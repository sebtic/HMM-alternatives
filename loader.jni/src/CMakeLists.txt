cmake_minimum_required(VERSION 2.6)

PROJECT(alternatives-loader-jni)

SET(CMAKE_MODULE_PATH ${PROJECT_BINARY_DIR}/../../../extracted/utils/cmake)

set(ALTERNATIVE_IS_JNI_LIBRARY "1")
set( JAVAH_CLASSES org.projectsforge.alternatives.loader.LibraryPathManager )

if( NOT WIN32 )
  set( main_link_flags "${main_link_flags} -ldl" )
endif()
INCLUDE( ${CMAKE_MODULE_PATH}/jnilibraryproject.cmake )
