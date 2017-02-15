message( STATUS "Configuring alternatives dependencies" )

set( alternatives_extracted_generic_include_directory ${PROJECT_BINARY_DIR}/../../../extracted/generic/include )
set( alternatives_extracted_generic_library_directory ${PROJECT_BINARY_DIR}/../../../extracted/generic/lib  )
set( alternatives_extracted_generic_config_directory ${PROJECT_BINARY_DIR}/../../../extracted/generic/config  )

set( alternatives_extracted_debug_include_directory ${PROJECT_BINARY_DIR}/../../../extracted/debug/include )
set( alternatives_extracted_debug_library_directory ${PROJECT_BINARY_DIR}/../../../extracted/debug/lib  )
set( alternatives_extracted_debug_config_directory ${PROJECT_BINARY_DIR}/../../../extracted/debug/config  )

set( alternatives_extracted_release_include_directory ${PROJECT_BINARY_DIR}/../../../extracted/release/include )
set( alternatives_extracted_release_library_directory ${PROJECT_BINARY_DIR}/../../../extracted/release/lib  )
set( alternatives_extracted_release_config_directory ${PROJECT_BINARY_DIR}/../../../extracted/release/config  )

include( FindPkgConfig )

set( main_includes_directories ${main_includes_directories} ${alternatives_extracted_generic_include_directory} )
set( main_link_directories ${main_link_directories} ${alternatives_extracted_generic_library_directory} )
if("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
  set( main_includes_directories ${main_includes_directories} ${alternatives_extracted_release_include_directory} )
  set( main_link_directories ${main_link_directories} ${alternatives_extracted_release_library_directory} )
else()
  set( main_includes_directories ${main_includes_directories} ${alternatives_extracted_debug_include_directory} )
  set( main_link_directories ${main_link_directories} ${alternatives_extracted_debug_library_directory} )
endif()

file(TO_NATIVE_PATH ${alternatives_extracted_generic_config_directory} config_path_generic )
if("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
  file(TO_NATIVE_PATH ${alternatives_extracted_release_config_directory} config_path_debugrelease )
else()
  file(TO_NATIVE_PATH ${alternatives_extracted_debug_config_directory} config_path_debugrelease )
endif()

set(ENV{PKG_CONFIG_PATH} "$ENV{PKG_CONFIG_PATH}:${config_path_generic}:${config_path_debugrelease}" )
message( STATUS "ENV{PKG_CONFIG_PATH} set to $ENV{PKG_CONFIG_PATH}" )

# with dockcross, the PKG_CONFIG_PATH is unsetted by the wrapper then we need to use alternative environment variable
set(ENV{PKG_CONFIG_PATH_i686_w64_mingw32_static} "$ENV{PKG_CONFIG_PATH_i686_w64_mingw32_static}:${config_path_generic}:${config_path_debugrelease}" )
set(ENV{PKG_CONFIG_PATH_x86_64_w64_mingw32_static} "$ENV{PKG_CONFIG_PATH_x86_64_w64_mingw32_static}:${config_path_generic}:${config_path_debugrelease}" )

if(ALTERNATIVE_DEPENDENCIES)
  message( STATUS "Checking main alternative dependencies" )
  foreach( alt ${ALTERNATIVE_DEPENDENCIES} )
    message( STATUS " - checking ${alt}..." )
    execute_process(
    COMMAND ${PKG_CONFIG_EXECUTABLE} ${alt} "--cflags"
    OUTPUT_VARIABLE DEPS_CFLAGS    
    RESULT_VARIABLE failed)
    if(failed)
        message( FATAL_ERROR "An error occurred while executing pkg-config of the dependency" )
    endif()

    execute_process(
    COMMAND ${PKG_CONFIG_EXECUTABLE} ${alt} "--libs"
    OUTPUT_VARIABLE DEPS_LDFLAGS
    RESULT_VARIABLE failed)
    if(failed)
        message( FATAL_ERROR "An error occured while executing pkg-config of the dependency" )
    endif()

    string(REGEX REPLACE "[\r\n]" " " DEPS_CFLAGS "${DEPS_CFLAGS}" )
    string(REGEX REPLACE " +$" "" DEPS_CFLAGS "${DEPS_CFLAGS}" )
    string(REGEX REPLACE "[\r\n]" " " DEPS_LDFLAGS "${DEPS_LDFLAGS}" )
    string(REGEX REPLACE " +$" "" DEPS_LDFLAGS "${DEPS_LDFLAGS}" )

    set( main_compiler_flags "${main_compiler_flags} ${DEPS_CFLAGS}" )
    set( main_link_flags "${main_link_flags} ${DEPS_LDFLAGS}" )
    message( STATUS "  Compiler flags: ${DEPS_CFLAGS}" )
    message( STATUS "  Linker flags: ${DEPS_LDFLAGS}" )

    set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\ndependency.${alt}.org.projectsforge.alternatives.dependencies.Compiler=strict\ndependency.${alt}.org.projectsforge.alternatives.dependencies.Linker=strict" )
    if( ALTERNATIVE_WEAK_COMPATIBILITY_FOR_MODE )
    else()
  set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\ndependency.${alt}.org.projectsforge.alternatives.dependencies.Mode=strict" )
    endif()

  endforeach()
elseif()
    message( STATUS "No alternatives dependencies are defined" )
endif()
