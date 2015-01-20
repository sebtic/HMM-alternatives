message( STATUS "Configuring sources directories for main project" )

set( main_sources_path ${PROJECT_SOURCE_DIR}/main/native )
set( main_includes_path ${PROJECT_SOURCE_DIR}/main/include )

IF(EXISTS "${main_includes_path}" )
  set( main_includes_directories ${main_includes_directories} ${main_includes_path} )
ENDIF()

aux_source_directory( ${main_sources_path} main_sources )

string( TOLOWER "${CMAKE_SYSTEM_NAME}" OS )
string( TOLOWER "${CMAKE_SYSTEM_PROCESSOR}" PROCESSOR )

IF(EXISTS "${main_includes_path}-${OS}" )
  set( main_includes_directories ${main_includes_directories} ${main_includes_path}-${OS} )
ENDIF()

IF(EXISTS "${main_includes_path}-${OS}-${PROCESSOR}" )
  set( main_includes_directories ${main_includes_directories} ${main_includes_path}-${OS}-${PROCESSOR} )
ENDIF()

aux_source_directory( ${main_sources_path}-${OS} main_sources_os )
aux_source_directory( ${main_sources_path}-${OS}-${PROCESSOR} main_sources_os_processor )
