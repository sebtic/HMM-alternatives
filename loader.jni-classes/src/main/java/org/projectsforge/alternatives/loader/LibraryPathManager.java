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

import org.slf4j.Logger;

/**
 * Utility class to provide an uniform interface to a JNI function allowing to
 * configure the environment variables to load libraries from an arbitrary path.
 * 
 * @author Sébastien Aupetit
 */
public final class LibraryPathManager {
  /**
   * Ask to the JNI function to add the path to the path of allowed search and
   * load paths when loading native library.
   * 
   * @param path
   *          the path from where we would like to load native library
   * @param logger
   *          an instance of a logger to log action
   */
  static native void defineLibraryPath(final String path, final Logger logger);

  /**
   * Make all actions necessary to ensure that all is ready to load the library
   * or to need the library as a dependency.
   * 
   * @param path
   *          the absolute path to the library
   * @param logger
   *          an instance of a logger to log action
   */
  static native void prepareLibraryLoading(String path, final Logger logger);

  /**
   * Private dummy constructor to forbid the instantiation of the class.
   */
  private LibraryPathManager() {
    // nothing to do
  }
}
