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
import java.util.StringTokenizer;
import org.projectsforge.alternatives.loader.Loggers;
import org.projectsforge.alternatives.loader.RequirementChecker;

/**
 * Abstract requirement checker managing OR relationship in the parameter
 * string.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractRequirementChecker implements RequirementChecker {

  /**
   * Implement an OR check between tokens of the parameter. The tokens are
   * separated by a comma.
   * 
   * @param parameter
   *          the list of tokens
   * @param properties
   *          the properties of the descriptor
   * @return true if at least a token validate the check
   */
  @Override
  public final boolean check(final String parameter, final Properties properties) {

    final StringTokenizer tokenizer = new StringTokenizer(parameter, ",");

    boolean result = false;

    while (tokenizer.hasMoreTokens() && !result) {
      result = result || checkAParameter(tokenizer.nextToken(), properties);
    }
    return result;
  }

  /**
   * Check if the token is valid.
   * 
   * @param token
   *          the token
   * @param properties
   *          the properties of the descriptor
   * @return true if the check is valid
   */
  public abstract boolean checkAParameter(String token, Properties properties);

  /**
   * Perform a strict checking between current and the property propertyName
   * from properties.
   * 
   * @param token
   *          the checking mode
   * @param properties
   *          the properties of the descriptor
   * @param propertyName
   *          the property name
   * @param current
   *          the value of the property to compare against
   * @return true if the property has the value current
   */
  public final boolean strictCheckOnly(final String token, final Properties properties,
      final String propertyName, final String current) {
    final String descriptor = properties.getProperty(propertyName, "");
    if ("strict".equals(token)) {
      return current.matches(descriptor);
    } else {
      Loggers.getRequirementCheckerLogger().warn(
          "Only strict checking is supported for " + propertyName + ": '" + token
              + "' mode was provided.");
      return false;
    }
  }
}
