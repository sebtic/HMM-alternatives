/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of ALTERnatives.
 * 
 * SHS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SHS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.classloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.projectsforge.alternatives.loader.Loader;
import org.projectsforge.utils.path.JavaRegExPatchMatcher;
import org.projectsforge.utils.path.PathRepositoryFactory;
import org.projectsforge.utils.path.URISearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ClassLoaderSwitcher which manages class loader switching to an
 * OverridedClassLoader.
 */
public class ClassLoaderSwitcher {

	/**
	 * The Class MainRunnable.
	 */
	private static class MainRunnable implements SerializableRunnable {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The class name. */
		private final String className;

		/** The args. */
		private final String[] args;

		/**
		 * Instantiates a new main runnable.
		 * 
		 * @param className
		 *            the class name
		 * @param args
		 *            the args
		 */
		public MainRunnable(final String className, final String[] args) {
			this.className = className;
			this.args = args;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Class<?> mainClazz;
			try {
				mainClazz = Thread.currentThread().getContextClassLoader()
						.loadClass(className);

				final Method method = mainClazz.getMethod("main",
						String[].class);
				method.setAccessible(true);
				method.invoke(null, (Object) args);
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			} catch (final SecurityException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			} catch (final NoSuchMethodException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			} catch (final IllegalArgumentException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException(
						"Can not run the main method of class " + className, e);
			}
		}
	}

	/** The LOGGER. */
	private static Logger LOGGER = LoggerFactory
			.getLogger(ClassLoaderSwitcher.class);

	/** The urls. */
	public static List<URL> urls = new ArrayList<URL>();

	static {
		// Step 1: search .classloaderswitcher files, load content and compute
		// URL
		// of extracted jar
		URISearcher uriSearcher = new URISearcher();
		uriSearcher.setSearchPaths(PathRepositoryFactory
				.getDefaultPathRepository().getPaths());
		JavaRegExPatchMatcher<HashSet<URL>> matcher = new JavaRegExPatchMatcher<HashSet<URL>>(
				Pattern.compile(".*\\.classloaderswitcher$"),
				new HashSet<URL>());
		uriSearcher.search(matcher);
		final Set<URL> lib = matcher.getMatchedPaths();

		for (final URL url : lib) {
			final Properties prop = new Properties();

			try {
				final InputStream is = url.openStream();
				try {
					try {
						prop.load(is);
					} catch (final IOException e) {
						ClassLoaderSwitcher.LOGGER.warn(
								"Can not read content of " + url, e);
					}
				} finally {
					try {
						is.close();
					} catch (final IOException e) {
						// ignoring
					}
				}
			} catch (final MalformedURLException e) {
				ClassLoaderSwitcher.LOGGER.warn("Can not open " + url, e);
			} catch (final IOException e) {
				ClassLoaderSwitcher.LOGGER.warn("Can not open " + url, e);
			}

			for (final Object key : prop.keySet()) {
				final String name = (String) key;
				if (name.startsWith("overrideclasses.")) {
					final String alternative = prop.getProperty(name);
					final Map<String, File> files = Loader
							.getExtractedFiles(alternative);
					for (final File file : files.values()) {
						if (file.getPath().toLowerCase().endsWith(".jar")) {
							try {
								ClassLoaderSwitcher.urls.add(file.toURI()
										.toURL());
							} catch (final MalformedURLException e) {
								ClassLoaderSwitcher.LOGGER
										.debug("Can not compute URL. Ignoring URL.",
												e);
							}
						}
					}
				}
			}
		}

		for (final URL url : PathRepositoryFactory.getDefaultPathRepository()
				.getPaths()) {
			ClassLoaderSwitcher.urls.add(url);
		}

		// Step 2: reset classes which manage loading of classes
		Loader.reloadConfiguration();

	}

	/**
	 * Try to execute the main method of the given class in a valid class
	 * loader.
	 * 
	 * @param clazz
	 *            the class defining the main method
	 * @param args
	 *            the arguments of the main method
	 * @return true if the clazz have been executed in a new class loader, false
	 *         otherwise <br>
	 *         <b>Usage:</b>
	 * 
	 *         <pre>
	 * class MyClass {
	 * 
	 * 	public static void main(String[] args) {
	 *     if (ClassLoaderSwitcher.main(MyClass.main, args)
	 *       return;
	 * 
	 *     // here is the main code
	 *   }
	 * }
	 * </pre>
	 */
	public static boolean main(final Class<?> clazz, final String[] args) {

		if (OverridingClassLoader.isOverridingClassLoader(clazz
				.getClassLoader())) {
			return false;
		} else {
			final MainRunnable runnable = new MainRunnable(clazz.getName(),
					args);
			ClassLoaderSwitcher.run(runnable);
			return true;
		}
	}

	/**
	 * Run.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public static void run(final SerializableRunnable runnable) {

		// Update the class loader of the thread
		ClassLoaderSwitcher.updateThreadsClassLoader();

		// Compute the class loader to use
		final ClassLoader ocl;
		if (OverridingClassLoader.isOverridingClassLoader(runnable.getClass()
				.getClassLoader())) {
			ocl = runnable.getClass().getClassLoader();
		} else {
			ocl = new OverridingClassLoader(
					ClassLoaderSwitcher.urls.toArray(new URL[0]), runnable
							.getClass().getClassLoader());
		}

		Object deserializedObject;

		try {
			// Serialize the object to an in memory object stream
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(baos);
			oos.writeObject(runnable);
			oos.close();

			// Deserialize the object using the new class loader
			final ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			final ObjectInputStream ois = new ObjectInputStream(bais) {
				@Override
				protected Class<?> resolveClass(final ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					try {
						final String name = desc.getName();
						return Class.forName(name, false, ocl);
					} catch (final ClassNotFoundException e) {
						return super.resolveClass(desc);
					}
				}
			};
			deserializedObject = ois.readObject();
			if (deserializedObject.getClass().getClassLoader() != ocl) {
				ClassLoaderSwitcher.LOGGER.error(
						"ClassLoader for {} is {} but must be {}",
						new Object[] { deserializedObject,
								deserializedObject.getClass().getClassLoader(),
								ocl });
				throw new IllegalStateException(
						"Can not switch the class loader of class "
								+ deserializedObject.getClass());
			}
		} catch (final ClassNotFoundException e) {
			ClassLoaderSwitcher.LOGGER
					.error("An error occurred while serializing and deserializing objects",
							e);
			throw new IllegalStateException(
					"An error occurred while serializing and deserializing objects",
					e);
		} catch (final IOException e) {
			ClassLoaderSwitcher.LOGGER
					.error("An error occurred while serializing and deserializing objects",
							e);
			throw new IllegalStateException(
					"An error occurred while serializing and deserializing objects",
					e);
		}

		Method method;
		try {
			method = deserializedObject.getClass().getMethod("run");

			method.setAccessible(true);
			method.invoke(deserializedObject);
		} catch (final SecurityException e) {
			throw new IllegalStateException(
					"An error occurred while invoking the run method", e);
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(
					"An error occurred while invoking the run method", e);
		} catch (final IllegalArgumentException e) {
			throw new IllegalStateException(
					"An error occurred while invoking the run method", e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(
					"An error occurred while invoking the run method", e);
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException(
					"An error occurred while invoking the run method", e);
		}
	}

	/**
	 * Update threads class loader.
	 */
	private synchronized static void updateThreadsClassLoader() {
		Thread[] threads;

		threads = Thread.getAllStackTraces().keySet().toArray(new Thread[0]);
		if (threads == null) {
			threads = ThreadUtilities.getAllThreads();
		}
		for (final Thread thread : threads) {
			final ClassLoader old = thread.getContextClassLoader();
			if (!OverridingClassLoader.isOverridingClassLoader(old)) {
				thread.setContextClassLoader(new OverridingClassLoader(
						ClassLoaderSwitcher.urls.toArray(new URL[0]), old));
			}
		}
	}
}
