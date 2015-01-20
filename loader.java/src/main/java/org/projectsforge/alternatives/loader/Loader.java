/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of ALTERnatives.
 * 
 * ALTERnatives is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ALTERnatives is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ALTERnatives. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.HashSet;


import org.projectsforge.utils.path.JavaRegExPatchMatcher;
import org.projectsforge.utils.path.PathRepositoryFactory;
import org.projectsforge.utils.path.URISearcher;

/**
 * The loader class managing alternatives.
 * 
 * @author Sébastien Aupetit
 */
public final class Loader {

  /** The library path. */
  private static String libraryPath;

  /** The alternatives. */
  private static final Map<String, Alternative> alternatives = new Hashtable<String, Alternative>();

  private static final URISearcher uriSearcher;
  
  static {
    synchronized (Loader.class) {
      uriSearcher = new URISearcher();
      uriSearcher.setSearchPaths(PathRepositoryFactory.getDefaultPathRepository().getPaths());
    	
    	
      final boolean disabledBootstrap = AccessController
          .doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
              return Boolean.valueOf(System
                  .getProperty("alternatives.disablebootstraplib", "false"));
            }
          });

      Loader.configure();

      if (disabledBootstrap) {
        Loggers.getLoaderLogger().warn(
            "Assuming you set up the native library required environment.");
      } else {
        Loader.createLibraryDirectory();
        if (Loader.loadLibraryPathManagerJNILibrary()) {
          // Set up the library path
          LibraryPathManager.defineLibraryPath(Loader.getLibraryPath(), Loggers.getLoaderLogger());
        } else {
          Loader.logBootLoaderError();
          throw new UnsatisfiedLinkError("Cannot load the bootstrap native library.");
        }
      }
      Loader.reloadConfiguration();
    }
  }

  /**
   * Configure the library path. The value is defined by: the system property
   * alternatives.librarypath, the programmatically overrided library path or
   * automatically in the java.io.tmpdir directory.
   */
  private static void configure() {
    final String path = AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("alternatives.librarypath");
      }
    });

    if (path != null) {
      Loader.libraryPath = path;
    } else {
      Loader.libraryPath = AccessController.doPrivileged(new PrivilegedAction<String>() {
        @Override
        public String run() {
          return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
              + "alternatives-libpath-" + System.nanoTime();
        }
      });

    }
    Loggers.getLoaderLogger().info("Alternatives library path set to {}", Loader.libraryPath);
  }

  /**
   * Create the library path directory.
   * 
   * @throws IllegalStateException
   *           if the directory can not be created
   */
  private static void createLibraryDirectory() {
    // create the library directory and register it for deletion on exit
    final File libdir = new File(Loader.libraryPath);

    if (!AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      @Override
      public Boolean run() {
        if (libdir.mkdirs()) {
          libdir.deleteOnExit();
          Loggers.getLoaderLogger().info("{} will be deleted on exit.", Loader.libraryPath);
          return true;
        } else {
          return false;
        }
      }
    })) {
      throw new IllegalStateException("Can not create the directory: " + libdir.getAbsolutePath());
    }
  }

  /**
   * A function to get the list of known alternatives.
   * 
   * @return the alternatives
   */
  static Map<String, Alternative> getAlternatives() {
    return Loader.alternatives;
  }

  /**
   * Get a map of extracted files
   * 
   * @param libraryName
   *          the library name
   * @return the map of extracted files. The empty key "" refers to the main
   *         library file
   */
  public static Map<String, File> getExtractedFiles(final String libraryName) {
    synchronized (Loader.class) {
      final Alternative alternative = Loader.alternatives.get(libraryName);

      if (alternative == null) {
        Loggers.getLoaderLogger().error("Can not found the alternative library: " + libraryName);
        throw new UnsatisfiedLinkError("Can not found the alternative library: " + libraryName);
      }
      return Collections.unmodifiableMap(alternative.getExtractedFiles());
    }
  }

  /**
   * Get the library path.
   * 
   * @return the library path
   * @warning the returned content is valid only if the configure method has
   *          been called.
   */
  public static String getLibraryPath() {
    return Loader.libraryPath;
  }

  /**
   * Tests if a library is already loaded
   * 
   * @param libraryName
   *          the library name
   * @return true if already loaded
   */
  public static boolean isLoaded(final String libraryName) {
    synchronized (Loader.class) {
      final Alternative alternative = Loader.alternatives.get(libraryName);
      return alternative != null && alternative.isLoaded();
    }
  }

  private static void listAlternatives() {
    final List<String> altName = new ArrayList<String>(Loader.alternatives.keySet());
    Collections.sort(altName);
    for (final String alternative : altName) {
      Loggers.getLoaderLogger().info("Known alternatives: {} ({} version)", alternative,
          Loader.alternatives.get(alternative).getDescriptors().size());
    }
  }

  /**
   * A function to load an alternative library.
   * 
   * @param libraryName
   *          the library name
   * @return the absolute path to the library
   */
  public static String load(final String libraryName) {
    synchronized (Loader.class) {
      final Alternative alternative = Loader.alternatives.get(libraryName);

      if (alternative == null) {
        Loggers.getLoaderLogger().error("Can not found the alternative library: " + libraryName);
        throw new UnsatisfiedLinkError("Can not found the alternative library: " + libraryName);
      }
      return alternative.load();
    }
  }

  /**
   * Load the native LibraryPathManager JNI library.
   * 
   * @return true if a library has been successfully loaded
   */
  private static boolean loadLibraryPathManagerJNILibrary() {
	  JavaRegExPatchMatcher<HashSet<URL>> matcher = new JavaRegExPatchMatcher<HashSet<URL>>(Pattern.compile(".*library\\.alternatives$"), new HashSet<URL>());
	  uriSearcher.search(matcher);
    final Set<URL> bootlib = matcher.getMatchedPaths();

    final String operatingSystem = Utils.getCurrentOS();
    final String arch = Utils.getCurrentArch();
    final String distribution = Utils.getCurrentDistribution();
    final String osversion = Utils.getCurrentOSVersion();

    PropertyFile best = null;
    long bestPriority = Long.MIN_VALUE;

    for (final URL url : bootlib) {
      try {
        final PropertyFile propertyFile = new PropertyFile(url);

        if ("alternatives-loader-jni".equals(propertyFile.getName())
            && (propertyFile.getProperty("library") != null)) {
          final String osregex = propertyFile.getProperty("alternatives.Os", ".*");
          final String archregex = propertyFile.getProperty("alternatives.Arch", ".*");
          final String distributionregex = propertyFile.getProperty("alternatives.Distribution",
              ".*");
          final String osversionregex = propertyFile.getProperty("alternatives.OSVersion", ".*");
          final long priority = Long.parseLong(propertyFile.getProperty("priority", "0"));

          if (operatingSystem.matches(osregex) && arch.matches(archregex)
              && distribution.matches(distributionregex) && osversion.matches(osversionregex)
              && ((best == null) || (bestPriority < priority))) {
            best = propertyFile;
            bestPriority = priority;
          }
        }
      } catch (final IOException e) {
        Loggers.getLoaderLogger().debug("Ignoring property file " + url, e);
      } catch (final MissingPropertyException e) {
        Loggers.getLoaderLogger().debug(
            "Missing or empty name property for " + url + ". Ignoring property file.", e);
      }
    }

    if (best != null) {

      // extract the files and load the library
      try {

        // Step 1: extract necessary files
        for (final String fileName : best.getPropertiesStartingWith("extractfile")) {
          Utils.extractFile(best.getURL(), best.getProperty(fileName));
        }

        // Step 2: extract extra dependencies
        for (final String fileName : best.getPropertiesStartingWith("extractdep")) {
          Utils.extractFile(best.getURL(), best.getProperty(fileName));
        }

        // Step 3: extract the library and load it
        final String key = best.getProperty("library");
        if (key != null) {
          final File library = Utils.extractFile(best.getURL(), key);
          Loggers.getLoaderLogger().info("Loading native library: {}", library);
          try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              @Override
              public Object run() throws Exception {
                System.load(library.getAbsolutePath());
                return null;
              }
            });
          } catch (final PrivilegedActionException e) {
            throw e.getException();
          }
          Loggers.getLoaderLogger().info("Native library {} successfully loaded", library);
          return true;
        }
      } catch (final UnsatisfiedLinkError e) {
        Loggers.getLoaderLogger().error("Native library from " + best.getURL() + " failed to load",
            e);
        throw e;
      } catch (final Exception e) {
        Loggers.getLoaderLogger().error("An error occurred while extracting file", e);
        final UnsatisfiedLinkError newE = new UnsatisfiedLinkError(
            "An error occurred while extracting file");
        newE.initCause(e);
        throw newE;
      }
    }
    return false;
  }

  private static void logBootLoaderError() {
    Loggers.getLoaderLogger().error("Cannot load a bootstrap native library.");

    Loggers.getLoaderLogger().error(
        "os={} arch={} distribution={} osversion={}",
        new Object[] { Utils.getCurrentOS(), Utils.getCurrentArch(),
            Utils.getCurrentDistribution(), Utils.getCurrentOSVersion() });
    Loggers.getLoaderLogger().error("You must specify three things:");
    Loggers
        .getLoaderLogger()
        .error(
            "(1) -Dalternatives.disablebootstraplib=true to disable the attempts to load the bootstrap native library");
    Loggers
        .getLoaderLogger()
        .error(
            "(2) -Dalternatives.librarypath=path to specify the path where the native libraries will be extracted");
    Loggers.getLoaderLogger().error(
        "(3) the required environment variable allowing to load the extracted libraries.");

  }

  /**
   * Reload the configuration
   */
  public static void reloadConfiguration() {
    synchronized (Loader.class) {
      Loader.searchAndLoadAlternatives();
      Loader.resolveFromProperties();
      Loader.listAlternatives();
    }
  }

  private static void resolveFromProperties() {
    // Step : disable descriptors from properties
    Loggers.getLoaderLogger().debug(
        "Resolving alternatives from alternatives.resolve.* system properties");
    final Set<String> systemPropertyNames = AccessController
        .doPrivileged(new PrivilegedAction<Set<String>>() {
          @Override
          public Set<String> run() {
            return System.getProperties().stringPropertyNames();
          }
        });

    for (final String key : systemPropertyNames) {
      if (key.startsWith("alternatives.resolve.")) {
        final String libName = key.substring("alternatives.resolve.".length());
        final String uuid = AccessController.doPrivileged(new PrivilegedAction<String>() {
          @Override
          public String run() {
            return System.getProperty(key);
          }
        });

        final Alternative alternative = Loader.alternatives.get(libName);
        if (alternative != null) {
          alternative.resolve(uuid);
        }
      }
    }
  }

  private static void searchAndLoadAlternatives() {
    // Step 1: search for available alternatives
    Loggers.getLoaderLogger().debug("Searching for alternatives");
    JavaRegExPatchMatcher<HashSet<URL>> matcher = new JavaRegExPatchMatcher<HashSet<URL>>(Pattern.compile(".*library\\.alternatives$"), new HashSet<URL>());
    uriSearcher.search(matcher);
    final Set<URL> lib = matcher.getMatchedPaths();

    // Step 2: load all alternatives descriptions
    Loggers.getLoaderLogger().debug("Loading alternatives descriptors");
    for (final URL sr : lib) {
      Loggers.getLoaderLogger().debug("Loading {}", sr);
      try {
        final DescriptorPropertyFile dpf = new DescriptorPropertyFile(sr);

        Alternative alternative = Loader.alternatives.get(dpf.getName());
        if (alternative == null) {
          alternative = new Alternative(dpf.getName());
          Loader.alternatives.put(dpf.getName(), alternative);
        }
        if (!alternative.isLoaded()) {
          if (!alternative.contains(dpf)) {
            alternative.addDescriptorPropertyFile(dpf);
          } else {
            Loggers.getLoaderLogger().debug(
                "Ignoring already known descriptor for alternative: {}", dpf);
          }
        } else {
          Loggers.getLoaderLogger().debug("Ignoring descriptor for already loaded alternative: {}",
              dpf);
        }
      } catch (final IOException e) {
        Loggers.getLoaderLogger().debug("Alternatives can not be loaded", e);
      } catch (final MissingPropertyException e) {
        Loggers.getLoaderLogger().debug("Alternatives ignored", e);
      }
    }
  }

  private Loader() {
  }

}
