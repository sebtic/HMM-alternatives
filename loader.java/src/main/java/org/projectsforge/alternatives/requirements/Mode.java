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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * A mode (debug,release) requirement checker class.
 * 
 * @author Sébastien Aupetit
 */
public class Mode extends AbstractRequirementChecker {

  /**
   * {@inheritDoc}
   * 
   * @see org.projectsforge.alternatives.requirements.AbstractRequirementChecker#checkAParameter(java.lang.String,
   *      java.util.Properties)
   */
  @Override
  public boolean checkAParameter(final String token, final Properties properties) {
    final String sysprop = AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("loader.requirements.Mode",
            System.getenv("loader_requirements_Mode"));
      }
    });
    if (sysprop == null) {
      return true;
    } else {
      final int index = sysprop.indexOf('#');
      if (index == -1) {
        return sysprop.equals(properties.getProperty("alternatives.Mode"));
      } else {
        final String action = sysprop.substring(0, index);
        final String value = sysprop.substring(index + 1);
        if ("prefered".equals(action)) {
          return true;
        } else if ("strict".equals(action)) {
          return value.equals(properties.getProperty("alternatives.Mode"));
        } else {
          return false;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.projectsforge.alternatives.loader.RequirementChecker#getPriority(java.lang.String,
   *      java.util.Properties)
   */
  @Override
  public int getPriority(final String parameter, final Properties properties) {
    final String sysprop = AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("loader.requirements.Mode");
      }
    });

    if (sysprop != null) {
      final int index = sysprop.indexOf('#');
      if (index == -1) {
        if (sysprop.equals(properties.getProperty("alternatives.Mode"))) {
          return 1;
        }
      } else {
        final String action = sysprop.substring(0, index);
        final String value = sysprop.substring(index + 1);
        if ("prefered".equals(action)) {
          if (value.equals(properties.getProperty("alternatives.Mode"))) {
            return 1;
          }
        } else if ("strict".equals(action)) {
          if (value.equals(properties.getProperty("alternatives.Mode"))) {
            return 1;
          }
        }
      }
    } else {
      if (properties.getProperty("alternatives.Mode").equals("debug")) {
        return 1;
      } else {
        return 0;
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.projectsforge.alternatives.loader.RequirementChecker#isInstanceReusable()
   */
  @Override
  public boolean isInstanceReusable() {
    return true;
  }

}
