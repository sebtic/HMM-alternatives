# Build type need to be specified
if(NOT CMAKE_BUILD_TYPE)
  message(FATAL_ERROR "Choose the type of build, options are: Debug Release.")
endif(NOT CMAKE_BUILD_TYPE)

INCLUDE( ${CMAKE_MODULE_PATH}/macros.cmake )
