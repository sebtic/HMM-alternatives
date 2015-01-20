/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SHS.
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.projectsforge.alternatives.loader.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class OverridingClassLoader.
 */
public class OverridingClassLoader extends URLClassLoader {

  public static boolean isOverridingClassLoader(final ClassLoader cl) {
    ClassLoader oldClassLoader;
    if (cl != null) {
      oldClassLoader = cl.getClass().getClassLoader();
    } else {
      oldClassLoader = null;
    }

    Class oclInSameClassLoader;
    try {
      oclInSameClassLoader = Class
          .forName("org.projectsforge.alternatives.classloader.OverridingClassLoader", false,
              oldClassLoader);

      return oclInSameClassLoader.isInstance(cl);
    } catch (final ClassNotFoundException e) {
      return cl instanceof OverridingClassLoader;
      // throw new IllegalStateException(e);
    }
  }

  /**
   * Instantiates a new universal swt class loader.
   * 
   * @param urls
   *          the urls
   * @param parent
   *          the parent
   */
  public OverridingClassLoader(final URL[] urls, final ClassLoader parent) {
    super(urls, parent);
  }

  /*
   * (non-Javadoc)
   * @see java.net.URLClassLoader#addURL(java.net.URL)
   */
  @Override
  public void addURL(final URL url) {
    super.addURL(url);
  }

  @Override
  protected String findLibrary(final String libname) {

    final URL url = getResource(System.mapLibraryName(libname));
    if (url == null) {
      return null;
    } else if ("jar".equals(url.getProtocol())) {
      try {
        return Utils.extractFileFromInput(url, System.mapLibraryName(libname)).getPath();
      } catch (final MalformedURLException e) {
        return null;
      } catch (final IOException e) {
        return null;
      }
    } else {
      return url.getPath();
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.ClassLoader#getResource(java.lang.String)
   */
  @Override
  public URL getResource(final String name) {
    URL url = findResource(name);
    if (url == null) {
      url = super.getResource(name);
    }
    return url;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.ClassLoader#getResources(java.lang.String)
   */
  @Override
  public Enumeration<URL> getResources(final String name) throws IOException {
    final Set<URL> urls = new HashSet<URL>();

    urls.addAll(Collections.list(findResources(name)));
    urls.addAll(Collections.list(super.getResources(name)));
    return Collections.enumeration(urls);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
   */
  @Override
  protected synchronized Class<?> loadClass(final String name, final boolean resolve)
      throws ClassNotFoundException {

    if (name.startsWith("org.projectsforge.alternatives.")) {
      // alternatives classes must not be overrided to forbid multiple
      // initialization and bad effects
      return super.loadClass(name, resolve);
    } else {
      try {
        final Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
          return findClass(name);
        } else {
          return clazz;
        }
      } catch (final Exception e) {
        try {
          return super.loadClass(name, resolve);
        } catch (final ClassNotFoundException e2) {
          return findSystemClass(name);
        }
      }
    }
  }

}
