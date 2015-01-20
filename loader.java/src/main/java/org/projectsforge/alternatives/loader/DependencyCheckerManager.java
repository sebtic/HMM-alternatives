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

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to manage a cache of DependencyChecker instances.
 * 
 * @author Sébastien Aupetit
 */
final class DependencyCheckerManager {

  /** The manager instance. */
  private static DependencyCheckerManager manager;

  /**
   * A function to get the manager instance.
   * 
   * @return the manager instance
   */
  public static DependencyCheckerManager getManager() {
    if (DependencyCheckerManager.manager == null) {
      DependencyCheckerManager.manager = new DependencyCheckerManager();
    }
    return DependencyCheckerManager.manager;
  }

  /** The hash table of cached instances. */
  private transient final Map<String, DependencyChecker> cache = new HashMap<String, DependencyChecker>();

  /**
   * Dummy private constructor.
   */
  private DependencyCheckerManager() {
    // nothing to do
  }

  /**
   * A function to get an instance of the specified class name.
   * 
   * @param className
   *          the fully qualified class name
   * @return an instance of the class name or null if there is an error
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public synchronized DependencyChecker getChecker(final String className)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    DependencyChecker checker = cache.get(className);

    if (checker == null) {
      final Class<?> clazz = Class.forName(className);
      final Object instance = clazz.newInstance();
      if (instance instanceof DependencyChecker) {
        checker = (DependencyChecker) instance;
        if (checker.isInstanceReusable()) {
          cache.put(className, checker);
        }
        return checker;
      } else {
        Loggers.getDependencyCheckerLogger().error(
            "{} does not implement DependencyChecker interface", className);
        return null;
      }
    } else {
      return checker;
    }
  }
}
