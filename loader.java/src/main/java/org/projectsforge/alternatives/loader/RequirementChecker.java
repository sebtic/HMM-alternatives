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
 * An interface to manage requirement.
 * @author Sébastien Aupetit
 */
public interface RequirementChecker {
  /**
   * Check if the properties are verified.
   * @param parameter the checker parameter
   * @param prop the properties
   * @return true if the test is true, false otherwise
   */
  boolean check(String parameter, Properties prop);

  /**
   * Define the priority of the descriptor deduced from the properties.
   * @param parameter the checker parameter
   * @param properties the properties
   * @return the priority
   */
  int getPriority(String parameter, Properties properties);

  /**
   * Indicate if the instance can be cached by the RequirementCheckerManager.
   * class
   * @return true if cachable, false otherwise
   */
  boolean isInstanceReusable();
}
