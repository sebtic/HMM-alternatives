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
 * ALTERnatives is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ALTERnatives. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.loader;

import java.util.Properties;

/**
 * An interface to manage dependencies.
 * @author Sébastien Aupetit
 */
public interface DependencyChecker {
  /**
   * Check if the dependsOnProperties are compatible with sourceProperties using
   * parameter.
   * @param parameter the parameter of the dependency checker
   * @param sourceProperties the properties of the active Descriptor
   * @param dependsOnProperties the properties of the candidate Descriptor
   * @return true if the candidate Descriptor is compatible with the active
   *         Descriptor
   */
  boolean checkForSource(String parameter, Properties sourceProperties,
      Properties dependsOnProperties);

  /**
   * Indicate if the instance can be cached by the DependencyCheckerManager
   * class.
   * @return true if cachable, false otherwise
   */
  boolean isInstanceReusable();
}
