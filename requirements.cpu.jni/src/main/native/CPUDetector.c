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

#include <org_projectsforge_alternatives_requirements_CPUDetector.h>
#include <libcpuid/libcpuid.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_projectsforge_alternatives_requirements_CPUDetector
 * Method:    detectCPU
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_projectsforge_alternatives_requirements_CPUDetector_detectCPU
  (JNIEnv * jenv, jclass c)
{
  (void) c;
  (void) jenv;
  struct cpu_raw_data_t raw;
  struct cpu_id_t id;
  int processor = org_projectsforge_alternatives_requirements_CPUDetector_I386;

  if (cpuid_get_raw_data(&raw) == 0 && cpu_identify(&raw, &id) == 0)
  {
    if (id.vendor == VENDOR_AMD)
    {
      processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM;

      if (id.flags[CPU_FEATURE_MMX])
      {
        if (id.flags[CPU_FEATURE_3DNOW])
          processor = org_projectsforge_alternatives_requirements_CPUDetector_K6_2_AND_3;
        else
          processor = org_projectsforge_alternatives_requirements_CPUDetector_K6;
      }

      if (id.flags[CPU_FEATURE_3DNOWEXT])
      {
        if (id.flags[CPU_FEATURE_SSE])
          processor = org_projectsforge_alternatives_requirements_CPUDetector_ATHLON_4;
        else
          processor = org_projectsforge_alternatives_requirements_CPUDetector_ATHLON;
      }

      if (id.flags[CPU_FEATURE_SSE2] || id.flags[CPU_FEATURE_LM])
      {
        if (id.flags[CPU_FEATURE_PNI])
          processor = org_projectsforge_alternatives_requirements_CPUDetector_K8_SSE3;
        else
          processor = org_projectsforge_alternatives_requirements_CPUDetector_K8;
      }

      if (id.flags[CPU_FEATURE_SSE4A])
        processor = org_projectsforge_alternatives_requirements_CPUDetector_AMDFAM10;
    }
    else
    {
      switch (id.family)
      {
      case 4:
        processor = org_projectsforge_alternatives_requirements_CPUDetector_I486;
        break;
      case 5:
        processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM;
        if (id.flags[CPU_FEATURE_MMX])
          processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM_MMX;
        break;
      case 6:
        if (id.flags[CPU_FEATURE_LM])
          processor = org_projectsforge_alternatives_requirements_CPUDetector_CORE2;
        else
        {
          if (id.flags[CPU_FEATURE_PNI])
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PRESCOTT;
          else if (id.flags[CPU_FEATURE_SSE2])
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM_M;
          else if (id.flags[CPU_FEATURE_SSE])
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM3;
          else if (id.flags[CPU_FEATURE_MMX])
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM2;
          else
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUMPRO;
        }
        break;
      case 15:
        if (id.flags[CPU_FEATURE_PNI])
        {
          if (id.flags[CPU_FEATURE_LM])
            processor = org_projectsforge_alternatives_requirements_CPUDetector_NOCONA;
          else
            processor = org_projectsforge_alternatives_requirements_CPUDetector_PRESCOTT;
        }
        else
          processor = org_projectsforge_alternatives_requirements_CPUDetector_PENTIUM4;
        break;
      default:
        processor = org_projectsforge_alternatives_requirements_CPUDetector_I386;
      }
    }
  }
  return processor;
}

#ifdef __cplusplus
}
#endif

