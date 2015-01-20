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
package org.projectsforge.alternatives.dependencies;

import java.util.Properties;
import org.projectsforge.alternatives.loader.DependencyChecker;

/**
 * A dummy dependency checker that is always verified. This checker is useful to
 * disable the automatic adding of dependencies (Compiler and Linker).
 * 
 * @author Sébastien Aupetit
 */
public final class Dummy implements DependencyChecker {

  /**
   * Say the dependency property is always verified.
   * 
   * @param parameter
   *          the parameter of the dependency checker
   * @param sourceProperties
   *          the properties of the active Descriptor
   * @param dependsOnProperties
   *          the properties of the candidate Descriptor
   * @return true
   */
  @Override
  public boolean checkForSource(final String parameter, final Properties sourceProperties,
      final Properties dependsOnProperties) {
    return true;
  }

  /**
   * Indicate that the instance can be cached.
   * 
   * @return true
   */
  @Override
  public boolean isInstanceReusable() {
    return true;
  }

}
