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
package org.projectsforge.alternatives.requirements;

import java.util.Properties;
import org.projectsforge.alternatives.loader.Utils;

/**
 * A requirement checker for the OS.
 * 
 * @author Sébastien Aupetit
 */
public final class Os extends AbstractRequirementChecker {

  /**
   * Check if the current OS is compatible with the descriptor.
   * 
   * @param token
   *          the required mode to check
   * @param properties
   *          the properties of the descriptor
   * @return true if the check is true
   */
  @Override
  public boolean checkAParameter(final String token, final Properties properties) {
    return strictCheckOnly(token, properties, "alternatives.Os", Utils.getCurrentOS());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.projectsforge.alternatives.loader.RequirementChecker#getPriority(java.lang.String,
   *      java.util.Properties)
   */
  @Override
  public int getPriority(final String parameter, final Properties properties) {
    return 0;
  }

  /**
   * Indicate that the instance can be shared.
   * 
   * @return true
   */
  @Override
  public boolean isInstanceReusable() {
    return true;
  }

}
