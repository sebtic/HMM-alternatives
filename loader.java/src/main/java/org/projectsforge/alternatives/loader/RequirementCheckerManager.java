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
 * A utility class to manage a CACHE of RequirementChecker instances.
 * 
 * @author Sébastien Aupetit
 */
final class RequirementCheckerManager {

  /** The hash table of cached instances. */
  private static final Map<String, RequirementChecker> CACHE = new HashMap<String, RequirementChecker>();

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
  public static synchronized RequirementChecker getChecker(final String className)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    RequirementChecker checker = RequirementCheckerManager.CACHE.get(className);

    if (checker == null) {
      Loggers.getRequirementCheckerLogger().debug("Loading java class: {}", className);
      final Class<?> clazz = Class.forName(className);
      final Object instance = clazz.newInstance();
      if (instance instanceof RequirementChecker) {
        checker = (RequirementChecker) instance;

        if (checker.isInstanceReusable()) {
          RequirementCheckerManager.CACHE.put(className, checker);
        }
        return checker;
      } else {
        Loggers.getRequirementCheckerLogger().error(
            className + " does not implement the RequirementChecker interface");
        return null;
      }
    } else {
      return checker;
    }
  }

  private RequirementCheckerManager() {
    // nothing to do
  }
}
