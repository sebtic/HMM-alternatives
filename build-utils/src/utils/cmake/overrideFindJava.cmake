# Adapted version of FindJNI from cmake 2.6-patch 2
# - Find Java
# This module finds if Java is installed and determines where the
# include files and libraries are. This code sets the following
# variables:
#
#  JAVA_RUNTIME    = the full path to the Java runtime
#  JAVA_COMPILE    = the full path to the Java compiler
#  JAVA_ARCHIVE    = the full path to the Java archiver
#
message( STATUS "Verifying Java availability" )

SET(JAVA_BIN_PATH
  "${OVERRIDED_JAVA_HOME}/bin"
  )

FIND_PROGRAM(JAVA_RUNTIME
  NAMES java
  PATHS ${JAVA_BIN_PATH}
)

FIND_PROGRAM(JAVA_ARCHIVE
  NAMES jar
  PATHS ${JAVA_BIN_PATH}
)

FIND_PROGRAM(JAVA_COMPILE
  NAMES javac
  PATHS ${JAVA_BIN_PATH}
)

MARK_AS_ADVANCED(
JAVA_BIN_PATH
JAVA_RUNTIME
JAVA_ARCHIVE
JAVA_COMPILE
)
