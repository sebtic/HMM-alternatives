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

import java.util.Hashtable;
import java.util.Map;
import org.projectsforge.alternatives.loader.Loader;

/**
 * Utility class to detect and manage the CPU of the current computer.
 * 
 * @author Sébastien Aupetit
 */
public final class CPUDetector {

  /** AMD k6 identifier/ */
  public final static int K6 = 20;
  /** AMD k6-2 and k6-3 identifier. */
  public final static int K6_2_AND_3 = 21;
  /** AMD Athlon identifier. */
  public final static int ATHLON = 22;
  /** AMD Athlon 4 identifier. */
  public final static int ATHLON_4 = 23;
  /** AMD k8 identifier. */
  public final static int K8 = 24;
  /** AMD k8 with sse3 support identifier. */
  public final static int K8_SSE3 = 25;
  /** AMD family 10th identifier. */
  public final static int AMDFAM10 = 26;

  /** The first identifiable AMD processor. */
  public final static int FIRST_AMD = CPUDetector.K6;
  /** The last identifiable AMD processor. */
  public final static int LAST_AMD = CPUDetector.AMDFAM10;

  /** Intel i386 identifier. */
  public final static int I386 = 1;
  /** Intel i486 identifier. */
  public final static int I486 = 2;
  /** Intel Pentium identifier. */
  public final static int PENTIUM = 3;
  /** Intel Pentium with MMX instruction set identifier. */
  public final static int PENTIUM_MMX = 4;
  /** Intel Pentium Pro identifier. */
  public final static int PENTIUMPRO = 5;
  /** Intel Pentium 2 identifier. */
  public final static int PENTIUM2 = 6;
  /** Intel Pentium 3 identifier. */
  public final static int PENTIUM3 = 7;
  /** Intel Pentium 4 identifier. */
  public final static int PENTIUM4 = 8;
  /** Intel Pentium M identifier. */
  public final static int PENTIUM_M = 9;
  /** Intel Prescott identifier. */
  public final static int PRESCOTT = 10;
  /** Intel Nocona identifier. */
  public final static int NOCONA = 11;
  /** Intel Core 2 identifier. */
  public final static int CORE2 = 12;

  /** The first identifiable Intel processor. */
  public final static int FIRST_INTEL = CPUDetector.I386;
  /** The last identifiable Intel processor. */
  public final static int LAST_INTEL = CPUDetector.CORE2;

  /** A conversion hash map between string and numeric identifiers. */
  private static Map<String, Integer> constants;

  static {
    Loader.load("alternatives-requirements-cpu-jni");

    CPUDetector.constants = new Hashtable<String, Integer>();

    CPUDetector.constants.put("k6", CPUDetector.K6);
    CPUDetector.constants.put("k6-2", CPUDetector.K6_2_AND_3);
    CPUDetector.constants.put("k6-3", CPUDetector.K6_2_AND_3);
    CPUDetector.constants.put("athlon", CPUDetector.ATHLON);
    CPUDetector.constants.put("athlon-4", CPUDetector.ATHLON_4);
    CPUDetector.constants.put("k8", CPUDetector.K8);
    CPUDetector.constants.put("k8-sse3", CPUDetector.K8_SSE3);
    CPUDetector.constants.put("amdfam10", CPUDetector.AMDFAM10);

    CPUDetector.constants.put("i386", CPUDetector.I386);
    CPUDetector.constants.put("i486", CPUDetector.I486);
    CPUDetector.constants.put("pentium", CPUDetector.PENTIUM);
    CPUDetector.constants.put("pentium-mmx", CPUDetector.PENTIUM_MMX);
    CPUDetector.constants.put("pentiumpro", CPUDetector.PENTIUMPRO);
    CPUDetector.constants.put("pentium2", CPUDetector.PENTIUM2);
    CPUDetector.constants.put("pentium3", CPUDetector.PENTIUM3);
    CPUDetector.constants.put("pentium4", CPUDetector.PENTIUM4);
    CPUDetector.constants.put("pentium-m", CPUDetector.PENTIUM_M);
    CPUDetector.constants.put("prescott", CPUDetector.PRESCOTT);
    CPUDetector.constants.put("nocona", CPUDetector.NOCONA);
    CPUDetector.constants.put("core2", CPUDetector.CORE2);
  }

  /**
   * Convert a string processor identifier into a numeric identifier.
   * 
   * @param cpuModel
   *          the string identifier
   * @return the numeric identifier
   */
  public static int convert(final String cpuModel) {
    if (CPUDetector.constants.containsKey(cpuModel)) {
      return CPUDetector.constants.get(cpuModel);
    } else {
      throw new IllegalArgumentException("Undefined CPU model:" + cpuModel);
    }
  }

  /**
   * Detect the current processor.
   * 
   * @return the numeric identifier of the current CPU
   */
  public static native int detectCPU();

  /**
   * Compute the priority of a provided processor compared with a detected
   * processor.
   * 
   * @param detected
   *          the detected processor identifier
   * @param provided
   *          the provided processor identifier
   * @return the priority
   */
  public static int getPriority(final int detected, final int provided) {
    if ((CPUDetector.FIRST_INTEL <= detected) && (detected <= CPUDetector.LAST_INTEL)) {
      // on est sur Intel
      if ((CPUDetector.FIRST_INTEL <= provided) && (provided <= CPUDetector.LAST_INTEL)) {
        // on favorise le plus évolué
        return 100 - (detected - provided);
      } else {
        // on favorise le plus évolué
        return provided;
      }
    } else if ((CPUDetector.FIRST_AMD <= detected) && (detected <= CPUDetector.LAST_AMD)) {
      // on est sur AMD
      if ((CPUDetector.FIRST_AMD <= provided) && (provided <= CPUDetector.LAST_AMD)) {
        // on favorise le plus évolué
        return 100 - (detected - provided);
      } else {
        // on favorise le plus évolué
        return provided;
      }
    }

    throw new IllegalStateException("Invalid processor detected");
  }

  /**
   * Indicate if the provided processor identifier is compatible with the
   * detected processor identifier.
   * 
   * @param detected
   *          the detected processor identifier
   * @param provided
   *          the provided processor identifier
   * @return true if compatible, false otherwise
   */
  public static boolean isCompatible(final int detected, final int provided) {
    if ((CPUDetector.FIRST_INTEL <= detected) && (detected <= CPUDetector.LAST_INTEL)) {
      // on est sur Intel
      if ((CPUDetector.FIRST_INTEL <= provided) && (provided <= CPUDetector.LAST_INTEL)) {
        // on a une version pour Intel
        return provided <= detected;
      } else {
        switch (provided) {
          case K6:
            return detected >= CPUDetector.PENTIUM_MMX;
          case K6_2_AND_3:
            return false;
          case ATHLON:
            return false;
          case ATHLON_4:
            return false;
          case K8:
            return false;
          case K8_SSE3:
            return false;
          case AMDFAM10:
            return false;
        }
      }
    } else if ((CPUDetector.FIRST_AMD <= detected) && (detected <= CPUDetector.LAST_AMD)) {
      // on est sur AMD
      if ((CPUDetector.FIRST_AMD <= provided) && (provided <= CPUDetector.LAST_AMD)) {
        return provided <= detected;
      } else {
        switch (provided) {
          case I386:
            return true;
          case I486:
            return true;
          case PENTIUM:
            return true;
          case PENTIUM_MMX:
            return true;
          case PENTIUMPRO:
            return true;
          case PENTIUM2:
            return true;
          case PENTIUM3:
            return detected >= CPUDetector.ATHLON_4;
          case PENTIUM4:
            return detected >= CPUDetector.K8;
          case PENTIUM_M:
            return detected >= CPUDetector.K8;
          case PRESCOTT:
            return detected >= CPUDetector.K8_SSE3;
          case NOCONA:
            return detected >= CPUDetector.AMDFAM10;
          case CORE2:
            return false;
        }
      }
    }

    throw new IllegalStateException("Invalid processor detected");
  }
}
