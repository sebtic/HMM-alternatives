set( java_home ${PROJECT_BINARY_DIR}/../../../java-home )

if( EXISTS ${java_home} )
    FILE( READ ${java_home} OVERRIDED_JAVA_HOME )
endif()

MESSAGE( STATUS "Java JDK is located at ${OVERRIDED_JAVA_HOME}" )


include( ${CMAKE_MODULE_PATH}/overrideFindJava.cmake )
include( ${CMAKE_MODULE_PATH}/overrideFindJNI.cmake )

message( STATUS "Configuring JNI support" )

include_directories( ${JNI_INCLUDE_DIRS} )
set( test_link_libraries ${test_link_libraries} ${JAVA_JVM_LIBRARY} )

set( javah_classpath ${PROJECT_BINARY_DIR}/../../../javah-classpath )
set(project_javah_path ${PROJECT_BINARY_DIR}/../../../native/javah )

FILE( MAKE_DIRECTORY "${project_javah_path}" )
include_directories( ${project_javah_path} )

if( EXISTS ${javah_classpath} )
    FILE( READ ${javah_classpath} JAVAH_CLASSPATH )
    message( STATUS "Processing the classes with javah:" )
    foreach( class ${JAVAH_CLASSES} )
        message( STATUS "  ${class}" )
        execute_process( COMMAND ${JAVA_BIN_PATH}/javah -d "${project_javah_path}" -classpath "${JAVAH_CLASSPATH}" "${class}"
                         RESULT_VARIABLE RetVal
                         )
        if( NOT ${RetVal} EQUAL 0)
            message( FATAL_ERROR "javah has failed" )
        endif()
    endforeach()
endif()
