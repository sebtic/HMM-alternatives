# Adapted version of FindJNI from cmake 2.6-patch 2
# - Find JNI java libraries.
# This module finds if Java is installed and determines where the
# include files and libraries are. It also determines what the name of
# the library is. This code sets the following variables:
#
#  JNI_INCLUDE_DIRS      = the include dirs to use
#  JNI_LIBRARIES         = the libraries to use
#  JAVA_AWT_LIBRARY      = the path to the jawt library
#  JAVA_JVM_LIBRARY      = the path to the jvm library
#  JAVA_INCLUDE_PATH     = the include path to jni.h
#  JAVA_INCLUDE_PATH2    = the include path to jni_md.h
#  JAVA_AWT_INCLUDE_PATH = the include path to jawt.h
#

# OVERRIDED_JAVA_HOME est le répertoire du JDK

message( STATUS "Searching for JNI compiler and linker flags" )

SET(JAVA_AWT_LIBRARY_DIRECTORIES
  "${OVERRIDED_JAVA_HOME}/jre/lib/i386"
  "${OVERRIDED_JAVA_HOME}/jre/lib/amd64"
  "${OVERRIDED_JAVA_HOME}/jre/lib/ppc"
  )
  
message( STATUS "JAVA_AWT_LIBRARY_DIRECTORIES set to ${JAVA_AWT_LIBRARY_DIRECTORIES}" )

SET(JAVA_JVM_LIBRARY_DIRECTORIES)
FOREACH(dir ${JAVA_AWT_LIBRARY_DIRECTORIES})
  SET(JAVA_JVM_LIBRARY_DIRECTORIES
    ${JAVA_JVM_LIBRARY_DIRECTORIES}
    "${dir}"
    "${dir}/client"
    "${dir}/server"
    )
ENDFOREACH(dir)


SET(JAVA_AWT_INCLUDE_DIRECTORIES
  "${OVERRIDED_JAVA_HOME}/include"
  )

FOREACH(JAVA_PROG "${JAVA_RUNTIME}" "${JAVA_COMPILE}" "${JAVA_ARCHIVE}")
  GET_FILENAME_COMPONENT(jpath "${JAVA_PROG}" PATH)
  FOREACH(JAVA_INC_PATH ../include ../java/include ../share/java/include)
    IF(EXISTS ${jpath}/${JAVA_INC_PATH})
      SET(JAVA_AWT_INCLUDE_DIRECTORIES ${JAVA_AWT_INCLUDE_DIRECTORIES} "${jpath}/${JAVA_INC_PATH}")
    ENDIF(EXISTS ${jpath}/${JAVA_INC_PATH})
  ENDFOREACH(JAVA_INC_PATH)
  FOREACH(JAVA_LIB_PATH
    ../lib ../jre/lib ../jre/lib/i386
    ../java/lib ../java/jre/lib ../java/jre/lib/i386
    ../share/java/lib ../share/java/jre/lib ../share/java/jre/lib/i386)
    IF(EXISTS ${jpath}/${JAVA_LIB_PATH})
      SET(JAVA_AWT_LIBRARY_DIRECTORIES ${JAVA_AWT_LIBRARY_DIRECTORIES} "${jpath}/${JAVA_LIB_PATH}")
    ENDIF(EXISTS ${jpath}/${JAVA_LIB_PATH})
  ENDFOREACH(JAVA_LIB_PATH)
ENDFOREACH(JAVA_PROG)

FIND_LIBRARY(JAVA_AWT_LIBRARY jawt
    PATHS ${JAVA_AWT_LIBRARY_DIRECTORIES}
)
IF(WIN32)
  SET( JAVA_JVM_LIBRARY jvm )
  LINK_DIRECTORIES( "${OVERRIDED_JAVA_HOME}/lib" )
ELSE()
  FIND_LIBRARY(JAVA_JVM_LIBRARY NAMES jvm JavaVM
    PATHS ${JAVA_JVM_LIBRARY_DIRECTORIES}
  )
ENDIF()

# add in the include path
SET(JAVA_INCLUDE_PATH ${JAVA_AWT_INCLUDE_DIRECTORIES})

string( TOLOWER "${CMAKE_SYSTEM_NAME}" ALTERNATIVE_OS)
SET(JAVA_INCLUDE_PATH2 ${CMAKE_MODULE_PATH}/../jni_md/${ALTERNATIVE_OS})

SET(JAVA_AWT_INCLUDE_PATH ${JAVA_INCLUDE_PATH})

MARK_AS_ADVANCED(
  JAVA_AWT_LIBRARY
  JAVA_JVM_LIBRARY
  JAVA_AWT_INCLUDE_PATH
  JAVA_INCLUDE_PATH
  JAVA_INCLUDE_PATH2
)

SET(JNI_LIBRARIES
  ${JAVA_AWT_LIBRARY}
  ${JAVA_JVM_LIBRARY}
)

SET(JNI_INCLUDE_DIRS
  ${JAVA_INCLUDE_PATH}
  ${JAVA_INCLUDE_PATH2}
  ${JAVA_AWT_INCLUDE_PATH}
)
