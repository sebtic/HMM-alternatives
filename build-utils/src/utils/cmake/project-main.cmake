# The main project is configured by:
# - main_includes: the list of includes search paths
# - main_link_directories: the list of libraries search paths
# - main_compiler_flags: the specific compiler flags
# - main_link_libraries: the link libraries
# - main_link_flags: the specific linker flags
# - main_sources: the source files
# - main_sources_aux: other source files

message( STATUS "Configuring main project" )

split_compilerflags( "${main_compiler_flags}" main_includes_extra main_real_compiler_flags )
split_linkerflags( "${main_link_flags}" main_link_libraries_extra main_link_directories_extra main_real_link_flags )

# Add include_directories
if(main_includes_directories)
  include_directories( ${main_includes_directories} )
endif()
if(main_includes_extra)
  include_directories( ${main_includes_extra} )
endif()

message( STATUS "Include paths: ${main_includes_directories} ${main_includes_extra}" )

# Add link_directories
if(main_link_directories)
  link_directories( ${main_link_directories} )
endif()
if(main_link_directories_extra)
  link_directories( ${main_link_directories_extra} )
endif()


# Add the target
if(main_sources)
  add_library( ${CMAKE_PROJECT_NAME} SHARED ${main_sources} ${main_sources_os} ${main_sources_os_processor} ${main_sources_aux})
else()
  message( FATAL_ERROR "No source files to compile" )
endif()

# Add specific compiler definitions
if(main_real_compiler_flags)
  add_compiler_flags_to_target( ${CMAKE_PROJECT_NAME} "${main_real_compiler_flags}" )
endif()

# Add specific link libraries
if(main_link_libraries)
  target_link_libraries( ${CMAKE_PROJECT_NAME} ${main_link_libraries} )
endif()
if(main_link_libraries_extra)
  target_link_libraries( ${CMAKE_PROJECT_NAME} ${main_link_libraries_extra} )
endif()

# Add specific linker flags
if(main_real_link_flags)
  add_linker_flags_to_target( ${CMAKE_PROJECT_NAME} ${main_real_link_flags} )
endif()
add_linker_flags_to_target( ${CMAKE_PROJECT_NAME} "-Wl,-O1,-rpath,." )
add_linker_flags_to_target( ${CMAKE_PROJECT_NAME} "-Wl,-O1,--as-needed" )
if( WIN32 )
  add_linker_flags_to_target( ${CMAKE_PROJECT_NAME} -Wl,--add-stdcall-alias )
endif()
