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
import org.projectsforge.alternatives.loader.Loggers;
import org.projectsforge.alternatives.loader.RequirementChecker;

/**
 * A requirement checker class that verifies if the current processor can
 * execute a library.
 * 
 * @author Sébastien Aupetit
 */
public class Cpu implements RequirementChecker {
  /** The detected processor identifier. */
  private transient int detected;

  /**
   * The constructor.
   */
  public Cpu() {
    if (System.getProperty("alternatives.Cpu") != null) {
      detected = CPUDetector.convert(AccessController.doPrivileged(new PrivilegedAction<String>() {
        @Override
        public String run() {
          return System.getProperty("alternatives.Cpu");
        }
      }));
    } else {
      detected = CPUDetector.detectCPU();
      if ((detected == CPUDetector.I386) || (detected == CPUDetector.I486)) {
        Loggers
            .getRequirementCheckerLogger()
            .error(
                "The current CPU is detected as a i386 or a i486."
                    + " Those models are unsupported. "
                    + "If the detection failed, you must define the alternatives.Cpu system property with the correct CPU model.");
        throw new IllegalStateException("Failed detection or unsupported CPU detected.");
      }
    }
  }

  @Override
  public boolean check(final String parameter, final Properties prop) {
    final int provided = CPUDetector.convert(prop.getProperty("alternatives.Cpu"));

    return CPUDetector.isCompatible(detected, provided);
  }

  @Override
  public int getPriority(final String parameter, final Properties properties) {
    final int provided = CPUDetector.convert(properties.getProperty("alternatives.Cpu"));

    return CPUDetector.getPriority(detected, provided);
  }

  @Override
  public boolean isInstanceReusable() {
    return true;
  }
}
