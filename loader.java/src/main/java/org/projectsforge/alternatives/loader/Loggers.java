/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of ALTERnatives.
 * 
 * SHS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SHS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Loggers.
 */
public final class Loggers {

  /** The loader logger. */
  private static org.slf4j.Logger loaderLogger = LoggerFactory.getLogger(Loader.class);

  /** The property file logger. */
  private static org.slf4j.Logger propertyFileLogger = LoggerFactory.getLogger(PropertyFile.class);

  /** The requirement checker manager logger. */
  private static org.slf4j.Logger requirementCheckerLogger = LoggerFactory
      .getLogger(RequirementChecker.class);

  /** The dependency checker manager logger. */
  private static org.slf4j.Logger dependencyCheckerLogger = LoggerFactory
      .getLogger(DependencyChecker.class);

  private static Logger utilsLogger = LoggerFactory.getLogger(Utils.class);

  /**
   * Gets the dependency checker manager logger.
   * 
   * @return the dependency checker manager logger
   */
  public static org.slf4j.Logger getDependencyCheckerLogger() {
    return Loggers.dependencyCheckerLogger;
  }

  /**
   * Gets the loader logger.
   * 
   * @return the loader logger
   */
  public static org.slf4j.Logger getLoaderLogger() {
    return Loggers.loaderLogger;
  }

  /**
   * Gets the property file logger.
   * 
   * @return the property file logger
   */
  public static org.slf4j.Logger getPropertyFileLogger() {
    return Loggers.propertyFileLogger;
  }

  /**
   * Gets the requirement checker manager logger.
   * 
   * @return the requirement checker manager logger
   */
  public static org.slf4j.Logger getRequirementCheckerLogger() {
    return Loggers.requirementCheckerLogger;
  }

  public static org.slf4j.Logger getUtilsLogger() {
    return Loggers.utilsLogger;
  }

  /**
   * Sets the dependency checker manager logger.
   * 
   * @param dependencyCheckerLogger
   *          the new dependency checker manager logger
   */
  public static void setDependencyCheckerLogger(final org.slf4j.Logger dependencyCheckerLogger) {
    Loggers.dependencyCheckerLogger = dependencyCheckerLogger;
  }

  /**
   * Sets the loader logger.
   * 
   * @param loaderLogger
   *          the new loader logger
   */
  public static void setLoaderLogger(final org.slf4j.Logger loaderLogger) {
    Loggers.loaderLogger = loaderLogger;
  }

  /**
   * Sets the property file logger.
   * 
   * @param propertyFileLogger
   *          the new property file logger
   */
  public static void setPropertyFileLogger(final org.slf4j.Logger propertyFileLogger) {
    Loggers.propertyFileLogger = propertyFileLogger;
  }

  /**
   * Sets the requirement checker manager logger.
   * 
   * @param requirementCheckerLogger
   *          the new requirement checker manager logger
   */
  public static void setRequirementCheckerLogger(final org.slf4j.Logger requirementCheckerLogger) {
    Loggers.requirementCheckerLogger = requirementCheckerLogger;
  }

  /**
   * Instantiates a new loggers.
   */
  private Loggers() {
  }
}
