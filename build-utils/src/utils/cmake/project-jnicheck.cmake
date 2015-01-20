if( ${TESTDISABLED} STREQUAL "false" )

    message( STATUS "Creating the JNI implementation checker" )

    # Define the list of keywords to replace by 0
    set( JNISUBSTITUTELIST
                           jbooleanArray
                           jbyteArray
                           jcharArray
                           jshortArray
                           jintArray
                           jlongArray
                           jfloatArray
                           jdoubleArray
                           jobjectArray
                           jobject
                           jclass
                           jthrowable
                           jstring
                           jarray
                           jint
                           jlong
                           jdouble
                           jboolean
                           jbyte
                           jchar )


    # Get the list of all generated JNI headers of the project
    file(GLOB_RECURSE javah_sources ${PROJECT_BINARY_DIR}/../../../native/javah/*.h)


    set(JNICHECK_COUNTER "0")
    foreach( javah_file ${javah_sources} )

      # read the content of the header
      file(READ ${javah_file} file_content)

      # define the required includes
      set(JNICHECK_INCLUDES "${JNICHECK_INCLUDES} #include \"${javah_file}\"\n" )

      # make the changes
      string(REGEX MATCHALL "JNIEXPORT[^;]*" OUT "${file_content}" )

      #string(REGEX REPLACE ".*\(JNIEXPORT.*;\).*" "\\1" OUT "${file_content}" )

      foreach( PROTOTYPE ${OUT} )
        string(REGEX REPLACE "JNIEXPORT.*JNICALL " "" PROTOTYPE "${PROTOTYPE}")
        string(REGEX REPLACE "JNIEnv ." "NULL" PROTOTYPE "${PROTOTYPE}")
        foreach( subst ${JNISUBSTITUTELIST} )
            string(REPLACE "${subst}" "0" PROTOTYPE "${PROTOTYPE}")
        endforeach()
        set( JNICHECK_MAIN "${JNICHECK_MAIN}\n ${PROTOTYPE};" )
      endforeach()

      math(EXPR JNICHECK_COUNTER ${JNICHECK_COUNTER}+1)
    endforeach()

    message( STATUS "  ${JNICHECK_COUNTER} files processed" )

    # generate the jnicheck.cpp source file
    configure_file(${PROJECT_BINARY_DIR}/../../../extracted/utils/jnicheck.cpp jnicheck.cpp )

    # add the target
    add_executable( jnicheck_${CMAKE_PROJECT_NAME} jnicheck.cpp )
    target_link_libraries(jnicheck_${CMAKE_PROJECT_NAME} ${CMAKE_PROJECT_NAME} )

    # Add specific link libraries from main project
    if(main_link_libraries)
      target_link_libraries( jnicheck_${CMAKE_PROJECT_NAME} ${main_link_libraries} )
    endif()
    if( WIN32)
      add_linker_flags_to_target( jnicheck_${CMAKE_PROJECT_NAME} -Wl,--add-stdcall-alias )
    endif()
else()
    message( STATUS "JNI implementation checking disabled" )
endif()
