cmake_minimum_required(VERSION 2.6)

PROJECT(alternatives-requirements-cpu-jni)

SET(CMAKE_MODULE_PATH ${PROJECT_BINARY_DIR}/../../../extracted/utils/cmake)

set( ALTERNATIVE_DEPENDENCIES alternatives-requirements-cpu-cpuid )

set(ALTERNATIVE_IS_JNI_LIBRARY "1")
set( JAVAH_CLASSES org.projectsforge.alternatives.requirements.CPUDetector )

INCLUDE( ${CMAKE_MODULE_PATH}/jnilibraryproject.cmake )
