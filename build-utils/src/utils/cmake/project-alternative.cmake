# Generate alternatives config files
message( STATUS "Generating library.alternatives and ${CMAKE_PROJECT_NAME}.pc" )

string( TOLOWER "${CMAKE_SYSTEM_NAME}" ALTERNATIVE_OS)
message( STATUS "  Os is: ${ALTERNATIVE_OS}" )
set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\nrequire.org.projectsforge.alternatives.requirements.Os=strict" )

if( NOT ALTERNATIVE_ARCH )
  message( FATAL_ERROR "You must specify the ALTERNATIVE_ARCH variable." )
endif()

if("${ALTERNATIVE_ARCH}" STREQUAL "x86")
    set( ALTERNATIVE_ARCH "x86|i386" )
endif()
message( STATUS "  Arch is: ${ALTERNATIVE_ARCH}" )
set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\nrequire.org.projectsforge.alternatives.requirements.Arch=strict" )

if("${CMAKE_BUILD_TYPE}" STREQUAL "Debug")
  set( ALTERNATIVE_MODE "debug" )
  if( NOT ALTERNATIVE_PRIORITY)
    set( ALTERNATIVE_PRIORITY "0" )
  endif()
elseif("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
  set( ALTERNATIVE_MODE "release" )
  if( NOT ALTERNATIVE_PRIORITY)
    set( ALTERNATIVE_PRIORITY "0" )
  endif()
endif()
set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\nrequire.org.projectsforge.alternatives.requirements.Mode=any" )
message( STATUS "  Mode is: ${ALTERNATIVE_MODE}" )

if(CMAKE_COMPILER_IS_GNUCC OR CMAKE_COMPILER_IS_GNUCXX)
  set( ALTERNATIVE_COMPILER "gcc" )
  set( ALTERNATIVE_LINKER "gcc" )
endif()
message( STATUS "  Compiler is: ${ALTERNATIVE_COMPILER}" )
message( STATUS "  Linker is: ${ALTERNATIVE_LINKER}" )

message( STATUS "  priotity is: ${ALTERNATIVE_PRIORITY}" )

foreach( alt ${ALTERNATIVE_DEPENDENCIES} )
    set( ALTERNATIVE_DEPENDENCIES_REQUIRE "${ALTERNATIVE_DEPENDENCIES_REQUIRE} ${alt}" )
endforeach()

if( ALTERNATIVE_IS_JNI_LIBRARY EQUAL 1 )
  set( ALTERNATIVE_EXTRACT "library=${CMAKE_SHARED_LIBRARY_PREFIX}${CMAKE_PROJECT_NAME}${CMAKE_SHARED_LIBRARY_SUFFIX}" )
else()
  set( ALTERNATIVE_EXTRACT "extractdep=${CMAKE_SHARED_LIBRARY_PREFIX}${CMAKE_PROJECT_NAME}${CMAKE_SHARED_LIBRARY_SUFFIX}" )
endif()
message( STATUS "  ${ALTERNATIVE_EXTRACT}" )

set( PKGCONFIG_LINK_FLAGS "-l${CMAKE_PROJECT_NAME} ${PKGCONFIG_LINK_FLAGS}" )

message( STATUS "  pkg-config compile flags are: ${PKGCONFIG_COMPILE_FLAGS}" )
message( STATUS "  pkg-config link flags are: ${PKGCONFIG_LINK_FLAGS}" )

set( pkgconfig_path ${CMAKE_MODULE_PATH}/../pkg-config.pc )
set( libraryalternatives_path ${CMAKE_MODULE_PATH}/../library.alternatives )

configure_file(${pkgconfig_path} ${CMAKE_PROJECT_NAME}.pc)
configure_file(${libraryalternatives_path} library.alternatives )

set( main_sources_aux ${main_sources_aux} ${pkgconfig_path} ${libraryalternatives_path} )


