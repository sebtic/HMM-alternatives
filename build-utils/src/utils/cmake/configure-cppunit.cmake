if( ${TESTDISABLED} STREQUAL "false" )

    message( STATUS "Configuring the test project to use CPPUnit" )

    set( cppunit_sources ${CMAKE_MODULE_PATH}/../cppunit.cpp)

    # Add the main cppunit file to the test project
    set( test_sources_aux ${test_sources_aux} ${cppunit_sources} )

    #message( STATUS "pkg-config=${PKG_CONFIG_EXECUTABLE}" )
    #message( STATUS "ENV{PKG_CONFIG_PATH}=$ENV{PKG_CONFIG_PATH}" )
    execute_process(
        COMMAND ${PKG_CONFIG_EXECUTABLE} cppunit "--cflags"
        OUTPUT_VARIABLE DEPS_CFLAGS
        RESULT_VARIABLE failed)
    if(failed)
        message( FATAL_ERROR "An error occured while executing pkg-config of the dependency" )
    endif()

    execute_process(
        COMMAND ${PKG_CONFIG_EXECUTABLE} cppunit "--libs"
        OUTPUT_VARIABLE DEPS_LDFLAGS
        RESULT_VARIABLE failed)
    if(failed)
        message( FATAL_ERROR "An error occured while executing pkg-config of the dependency" )
    endif()

    string(REGEX REPLACE "[\r\n]" " " DEPS_CFLAGS "${DEPS_CFLAGS}" )
    string(REGEX REPLACE " +$" "" DEPS_CFLAGS "${DEPS_CFLAGS}" )
    string(REGEX REPLACE "[\r\n]" " " DEPS_LDFLAGS "${DEPS_LDFLAGS}" )
    string(REGEX REPLACE " +$" "" DEPS_LDFLAGS "${DEPS_LDFLAGS}" )

    set( test_compiler_flags "${test_compiler_flags} ${DEPS_CFLAGS}" )
    set( test_link_flags "${test_link_flags} ${DEPS_LDFLAGS}" )
    message( STATUS "  Test compiler flags: ${DEPS_CFLAGS}" )
    message( STATUS "  Test linker flags: ${DEPS_LDFLAGS}" )
endif()
