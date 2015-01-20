/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 *
 * This file is part of ALTERnatives.
 *
 * ALTERnatives is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * ALTERnatives is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ALTERnatives. If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id$
 */

#include <org_projectsforge_alternatives_loader_LibraryPathManager.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#ifdef __linux__
#include <dlfcn.h>
#endif

/**
 * Shared JNI library to update the library paths from where the native library can be loaded.
 * @author Sébastien Aupetit
 */

/** Static private variable to simplify the coding */
/** The JNIEnv pointer */
static JNIEnv * jenv = NULL;
/** The logger instance */
static jobject jlogger = 0;
/** The logger class */
static jclass loggerClass = 0;
/** The logger.info method */
static jmethodID jmethodDebug = 0;
/** The logger.info method */
static jmethodID jmethodInfo = 0;
/** The logger.error method */
static jmethodID jmethodError = 0;

/*#ifdef __linux__
 static int handlesCount = 0;
 static void ** handles = NULL;
 #endif*/

/** Throw a RuntimeException with the given message
 * @param message the message associeted to the exception
 */
static void throwJNIException(const char * message)
{
  jclass except = 0;
  ( *jenv )->ExceptionClear( jenv );
  except = ( *jenv )->FindClass( jenv, "java/lang/RuntimeException" );
  ( *jenv )->ThrowNew( jenv, except, message );
}

/** Prepare the logger to be used in the native code */
static int setUpLogger(void)
{
  loggerClass = ( *jenv )->GetObjectClass( jenv, jlogger );

  /** public void info(java.lang.Object);
   * Signature: (Ljava/lang/Object;)V
   */
  jmethodDebug = ( *jenv )->GetMethodID( jenv, loggerClass, "debug",
      "(Ljava/lang/String;)V" );
  if ( jmethodDebug == 0 )
  {
    throwJNIException( "An error occured in setUpLogger" );
    return 0;
  }

  jmethodInfo = ( *jenv )->GetMethodID( jenv, loggerClass, "info",
      "(Ljava/lang/String;)V" );
  if ( jmethodInfo == 0 )
  {
    throwJNIException( "An error occured in setUpLogger" );
    return 0;
  }

  jmethodError = ( *jenv )->GetMethodID( jenv, loggerClass, "error",
      "(Ljava/lang/String;)V" );
  if ( jmethodError == 0 )
  {
    throwJNIException( "An error occured in setUpLogger" );
    return 0;
  }

  return 1;
}

/** Log a message using the Java logger
 * @param message the message to be logged
 */
static void loggerDebug(const char * message)
{
  /* The string object will be collected by the GC later. No need to free it. */
  jstring jmessage = ( *jenv )->NewStringUTF( jenv, message );

  ( *jenv )->ExceptionClear( jenv );
  ( *jenv )->CallVoidMethod( jenv, jlogger, jmethodDebug, jmessage );
  if ( ( *jenv )->ExceptionOccurred( jenv ) )
  {
    ( *jenv )->ExceptionDescribe( jenv );
    ( *jenv )->ExceptionClear( jenv );
  }
}

/** Log a message using the Java logger
 * @param message the message to be logged
 */
static void loggerInfo(const char * message)
{
  /* The string object will be collected by the GC later. No need to free it. */
  jstring jmessage = ( *jenv )->NewStringUTF( jenv, message );

  ( *jenv )->ExceptionClear( jenv );
  ( *jenv )->CallVoidMethod( jenv, jlogger, jmethodInfo, jmessage );
  if ( ( *jenv )->ExceptionOccurred( jenv ) )
  {
    ( *jenv )->ExceptionDescribe( jenv );
    ( *jenv )->ExceptionClear( jenv );
  }
}

/** Log a message using the Java logger
 * @param message the message to be logged
 */
static void loggerError(const char * message)
{
  /* The string object will be collected by the GC later. No need to free it. */
  jstring jmessage = ( *jenv )->NewStringUTF( jenv, message );

  ( *jenv )->ExceptionClear( jenv );
  ( *jenv )->CallVoidMethod( jenv, jlogger, jmethodError, jmessage );
  if ( ( *jenv )->ExceptionOccurred( jenv ) )
  {
    ( *jenv )->ExceptionDescribe( jenv );
    ( *jenv )->ExceptionClear( jenv );
  }
}

/** Log a message using the Java logger
 * @param prefix the prefix part of the message
 * @param message the suffix part of the message
 */
static void loggerDebugWithPrefix(const char * prefix, const char * message)
{
  char * msg;
  if ( message != NULL )
  {
    msg = (char*) malloc( strlen( prefix ) + strlen( message ) + 1 );
    strcpy( msg, prefix );
    strcat( msg, message );
    loggerDebug( msg );
    free( msg );
  }
  else
  {
    loggerDebug( prefix );
  }
}

/** Log a message using the Java logger
 * @param prefix the prefix part of the message
 * @param message the suffix part of the message
 */
static void loggerInfoWithPrefix(const char * prefix, const char * message)
{
  char * msg;
  if ( message != NULL )
  {
    msg = (char*) malloc( strlen( prefix ) + strlen( message ) + 1 );
    strcpy( msg, prefix );
    strcat( msg, message );
    loggerInfo( msg );
    free( msg );
  }
  else
    loggerInfo( prefix );
}

/** Log a message using the Java logger
 * @param prefix the prefix part of the message
 * @param message the suffix part of the message
 */
static void loggerErrorWithPrefix(const char * prefix, const char * message)
{
  char * msg;
  if ( message != NULL )
  {
    msg = (char*) malloc( strlen( prefix ) + strlen( message ) + 1 );
    strcpy( msg, prefix );
    strcat( msg, message );
    loggerError( msg );
    free( msg );
  }
  else
    loggerError( prefix );
}

#ifdef WIN32
/** Implementation dependent constants */
/** The variable to update */
#define VARIABLE_NAME "PATH"
/** The path separator to use */
#define PATH_SEPARATOR ";"
#undef __linux__
#endif

#ifdef __linux__
/** Implementation dependent constants */
/** The variable to update */
#define VARIABLE_NAME "LD_LIBRARY_PATH"
/** The path separator to use */
#define PATH_SEPARATOR ":"
#endif

#ifdef __APPLE__
/** Implementation dependent constants */
/** The variable to update */
#define VARIABLE_NAME "DYLD_LIBRARY_PATH"
/** The path separator to use */
#define PATH_SEPARATOR ":"

#endif

#ifdef __cplusplus
extern "C"
{
#endif
  /*
   * Class:     org_projectsforge_alternatives_loader_LibraryPathManager
   * Method:    defineLibraryPath
   * Signature: (Ljava/lang/String;Lorg/apache/log4j/Logger;)V
   */
  JNIEXPORT void JNICALL Java_org_projectsforge_alternatives_loader_LibraryPathManager_defineLibraryPath
  (JNIEnv * _jenv, jclass _jclass, jstring _jpath, jobject _jlogger)
  {
    const char * path;
    char * PATH;
    char * tmp;

    (void) _jclass;

    jenv = _jenv;
    jlogger = _jlogger;

    setUpLogger();

    path = (*jenv)->GetStringUTFChars(jenv, _jpath, NULL);
    PATH = getenv(VARIABLE_NAME);

    loggerInfo( "Updating the " VARIABLE_NAME " environment variable");
    loggerDebugWithPrefix( "Old value: ", PATH);

    if (PATH == NULL)
    PATH="";

    tmp = (char*)malloc( strlen(VARIABLE_NAME)+1+strlen(PATH)+1+strlen(path)+1);
    strcpy(tmp,VARIABLE_NAME);
    strcat(tmp, "=");
    strcat(tmp, PATH );
    strcat(tmp,PATH_SEPARATOR);
    strcat(tmp,path);

    if (putenv(tmp) == 0)
    loggerInfo("Update successfull");
    else
    loggerError("Update failed");

    /* NB: we must not free the string since its content is not duplicated by putenv
     * see man 3 putenv
     * free(tmp);
     */

    (*jenv)->ReleaseStringUTFChars(jenv, _jpath, path);
    loggerDebugWithPrefix( "New value: ", getenv(VARIABLE_NAME));
  }

  /*
   * Class:     org_projectsforge_alternatives_loader_LibraryPathManager
   * Method:    prepareLibraryLoading
   * Signature: (Ljava/lang/String;Lorg/apache/log4j/Logger;)V
   */
  JNIEXPORT void JNICALL Java_org_projectsforge_alternatives_loader_LibraryPathManager_prepareLibraryLoading
  (JNIEnv * _jenv, jclass _jclass, jstring _jpath, jobject _jlogger)
  {
#ifdef __linux__
    const char * path;

    (void) _jclass;

    jenv = _jenv;
    jlogger = _jlogger;

    setUpLogger();

    path = (*jenv)->GetStringUTFChars(jenv, _jpath, NULL);

    loggerDebugWithPrefix( "Preparing library loading of: ", path );
    void * handle = dlopen(path, RTLD_LAZY|RTLD_GLOBAL);
    if (handle == NULL)
    {
      loggerErrorWithPrefix( "dlopen error: ", dlerror());
    }
    loggerDebugWithPrefix( "Library prepared to be loaded: ", path);

    (*jenv)->ReleaseStringUTFChars(jenv, _jpath, path);
#endif
  }

#ifdef __cplusplus
}
#endif
