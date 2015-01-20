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
import java.util.StringTokenizer;
import org.projectsforge.alternatives.loader.DependencyChecker;
import org.projectsforge.alternatives.loader.Loggers;

/**
 * Abstract dependency checker managing OR relationship in the parameter string.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractDependencyChecker implements DependencyChecker {

  /**
   * Check if the token is a valid relationship between the descriptor.
   * 
   * @param token
   *          the token
   * @param sourceProperties
   *          the properties of the active Descriptor
   * @param dependsOnProperties
   *          the properties of the candidate Descriptor
   * @return true if the candidate Descriptor is compatible with the active
   *         Descriptor
   */
  public abstract boolean checkAParameterForSource(final String token,
      final Properties sourceProperties, final Properties dependsOnProperties);

  /**
   * Implement an OR check between tokens of the parameter. The tokens are
   * separated by a comma.
   * 
   * @param parameter
   *          the parameter of the dependency checker
   * @param sourceProperties
   *          the properties of the active Descriptor
   * @param dependsOnProperties
   *          the properties of the candidate Descriptor
   * @return true if the candidate Descriptor is compatible with the active
   *         Descriptor
   */
  @Override
  public final boolean checkForSource(final String parameter, final Properties sourceProperties,
      final Properties dependsOnProperties) {
    final StringTokenizer tokenizer = new StringTokenizer(parameter, ",");

    boolean result = false;

    while (tokenizer.hasMoreTokens() && !result) {
      result = result
          || checkAParameterForSource(tokenizer.nextToken(), sourceProperties, dependsOnProperties);
    }
    return result;
  }

  /**
   * Perform a strict checking between both descriptors using the property
   * propertyName.
   * 
   * @param token
   *          the check mode
   * @param sourceProperties
   *          the properties of the source descriptor
   * @param dependsOnProperties
   *          the properties of the destination descriptor
   * @param propertyName
   *          the property name
   * @return true if both descriptor are compatible
   */
  public final boolean strictCheckOnly(final String token, final Properties sourceProperties,
      final Properties dependsOnProperties, final String propertyName) {
    final String source = sourceProperties.getProperty(propertyName, "");
    final String depend = dependsOnProperties.getProperty(propertyName, "");

    if ("strict".equals(token)) {
      return source.matches(depend);
    } else {
      Loggers.getDependencyCheckerLogger().warn(
          "Dependency checking does not support '" + token + "' for " + propertyName + ".");
      return false;
    }
  }
}
