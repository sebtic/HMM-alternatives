# The test project is configured by:
# - test_includes: the list of includes search paths
# - test_link_directories: the list of libraries search paths
# - test_compiler_flags: the specific compiler flags
# - test_link_libraries: the link libraries
# - test_link_flags: the specific linker flags
# - test_sources: the source files
# - test_sources_aux: other source files

if( ${TESTDISABLED} STREQUAL "false" )
  message( STATUS "Configuring test project" )

  split_compilerflags( "${test_compiler_flags}" test_includes_extra test_real_compiler_flags )
  split_linkerflags( "${test_link_flags}" test_link_libraries_extra test_link_directories_extra test_real_link_flags )

  # Add include_directories
  if(test_includes_directories)
    include_directories( ${test_includes_directories} )
  endif()
  if(test_includes_directories_extra)
    include_directories( ${test_includes_directories_extra} )
  endif()

  # Add link_directories
  if(test_link_directories)
    link_directories( ${test_link_directories} )
  endif()
  if(test_link_directories_extra)
    link_directories( ${test_link_directories_extra} )
  endif()

  # Add the target
  if(test_sources)
    enable_testing()
    include(Dart)

    add_executable( test_${CMAKE_PROJECT_NAME} ${test_sources} ${test_sources_os} ${test_sources_os_processor} ${test_sources_aux} )
    link_directories( ${PROJECT_BINARY_DIR} )
    target_link_libraries(test_${CMAKE_PROJECT_NAME} ${CMAKE_PROJECT_NAME} )

    add_test(test test_${CMAKE_PROJECT_NAME})

    # Add specific compiler definitions
    if(test_real_compiler_flags)
      add_compiler_flags_to_target( test_${CMAKE_PROJECT_NAME} "${test_real_compiler_flags}" )
    endif()

    # Add specific link libraries from main project
    if(main_link_libraries)
      target_link_libraries( test_${CMAKE_PROJECT_NAME} ${main_link_libraries} )
    endif()
    if(main_link_libraries_extra)
      target_link_libraries( test_${CMAKE_PROJECT_NAME} ${main_link_libraries_extra} )
    endif()

    # Add specific link libraries from test project
    if(test_link_libraries)
      target_link_libraries( test_${CMAKE_PROJECT_NAME} ${test_link_libraries} )
    endif()
    if(test_link_libraries_extra)
      target_link_libraries( test_${CMAKE_PROJECT_NAME} ${test_link_libraries_extra} )
    endif()

    # Add specific linker flags
    if(test_real_link_flags)
      add_linker_flags_to_target( test_${CMAKE_PROJECT_NAME} ${test_real_link_flags} )
    endif()
    if( WIN32 )
      add_linker_flags_to_target( test_${CMAKE_PROJECT_NAME} -Wl,--add-stdcall-alias )
    endif()
  else()
    message( STATUS "No test to compile !" )
  endif()
endif()
