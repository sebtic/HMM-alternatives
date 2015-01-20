set( testDisabled_path ${PROJECT_BINARY_DIR}/../../../testDisabled )

set( TESTDISABLED "false" )

if( EXISTS ${testDisabled_path} )
    FILE( READ ${testDisabled_path} TESTDISABLED )
endif()

if( ${TESTDISABLED} STREQUAL "false" )
    message( STATUS "Configuring sources directories for test project" )

    set( test_sources_path ${PROJECT_SOURCE_DIR}/test/native )
    set( test_includes_path ${PROJECT_SOURCE_DIR}/test/include )

    if( EXISTS ${test_includes_path} )
      set( test_includes_directories ${test_includes_directories} ${test_includes_path} )
    endif()

    aux_source_directory( ${test_sources_path} test_sources )
    
    string( TOLOWER "${CMAKE_SYSTEM_NAME}" OS )
    string( TOLOWER "${CMAKE_SYSTEM_PROCESSOR}" PROCESSOR )

    IF(EXISTS "${test_includes_path}-${OS}" )
      set( test_includes_directories ${test_includes_directories} ${test_includes_path}-${OS} )
    ENDIF()

    IF(EXISTS "${test_includes_path}-${OS}-${PROCESSOR}" )
      set( test_includes_directories ${test_includes_directories} ${test_includes_path}-${OS}-${PROCESSOR} )
    ENDIF()

    aux_source_directory( ${test_sources_path}-${OS} test_sources_os )
    aux_source_directory( ${test_sources_path}-${OS}-${PROCESSOR} test_sources_os_processor )
else()
  message( STATUS "Tests are disabled. Skipping tests" )
endif()
