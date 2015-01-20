cmake_minimum_required(VERSION 2.6)

PROJECT(alternatives-requirements-cpu-cpuid)

SET(CMAKE_MODULE_PATH ${PROJECT_BINARY_DIR}/../../../extracted/utils/cmake)

set(ALTERNATIVE_IS_JNI_LIBRARY "0")

add_definitions( -DVERSION="0.1.0" -DHAVE_STDINT_H )

INCLUDE( ${CMAKE_MODULE_PATH}/configure.cmake )
INCLUDE( ${CMAKE_MODULE_PATH}/configure-project-main.cmake )
INCLUDE( ${CMAKE_MODULE_PATH}/configure-compiler-gcc.cmake )
INCLUDE( ${CMAKE_MODULE_PATH}/configure-alternative-dependencies.cmake )

INCLUDE( ${CMAKE_MODULE_PATH}/project-main.cmake )
INCLUDE( ${CMAKE_MODULE_PATH}/project-alternative.cmake )

