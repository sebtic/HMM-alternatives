# GCC/G++ compiler/linker flags
if(CMAKE_COMPILER_IS_GNUCC OR CMAKE_COMPILER_IS_GNUCXX)
  message( STATUS "Configuring compiler and linker flags" )

  # warnings
  add_definitions( -Wall -Wunused -Wshadow -Wconversion -Wfloat-equal -Wparentheses -Wundef -Wextra -pipe )

  if("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
    add_definitions( -Wuninitialized )
    add_definitions( -O2 )
  endif()

  if("${CMAKE_SYSTEM_PROCESSOR}" STREQUAL "x86_64")
      message( STATUS "Processor type is x86_64" )
      if( "${ALTERNATIVE_PROCESSOR}" STREQUAL "nocona" )
        add_definitions( "-march=nocona -mtune=nocona" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "core2" )
        add_definitions( "-march=core2 -mtune=core2" )
      endif()
  else()
      message( STATUS "Processor type is x86" )
      # optimization mode
      if( "${ALTERNATIVE_PROCESSOR}" STREQUAL "i386" )
        add_definitions( "-march=i386 -mtune=i386" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "k6" )
        add_definitions( "-march=k6 -mtune=k6 -mmmx -m3dnow" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "k6-2" )
        add_definitions( "-march=k6-2 -mtune=k6-2 -mmmx -m3dnow" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "athlon" )
        add_definitions( "-march=athlon -mtune=athlon -mmmx -m3dnow" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "athlon-4" )
        add_definitions( "-march=athlon-4 -mtune=athlon-4 -mmmx -msse -mfpmath=sse -m3dnow" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "k8" )
        add_definitions( "-march=k8 -mtune=k8 -mmmx -msse -mfpmath=sse -msse2 -m3dnow" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "k8-sse3" )
        add_definitions( "-march=k8-sse3 -mtune=k8-sse3 -mmmx -msse -mfpmath=sse -msse2 -msse3 -m3dnow" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "amdfam10" )
        add_definitions( "-march=amdfam10 -mtune=amdfam10 -mmmx -msse -mfpmath=sse -msse2 -msse3 -msse4a -m3dnow -mabm" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )

      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium" )
        add_definitions( "-march=pentium -mtune=pentium" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium-mmx" )
        add_definitions( "-march=pentium-mmx -mtune=pentium-mmx -mmmx" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentiumpro" )
        add_definitions( "-march=pentiumpro -mtune=pentiumpro -mmmx" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium2" )
        add_definitions( "-march=pentium2 -mtune=pentium2 -mmmx" )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium3" )
        add_definitions( "-march=pentium3 -mtune=pentium3 -mmmx -msse -mfpmath=sse" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium4" )
        add_definitions( "-march=pentium4 -mtune=pentium4 -mmmx -msse -mfpmath=sse" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "pentium-m" )
        add_definitions( "-march=pentium-m -mtune=pentium-m -mmmx -msse -mfpmath=sse -msse2" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "prescott" )
        add_definitions( "-march=prescott -mtune=prescott -mmmx -msse -mfpmath=sse -msse2 -msse3" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "nocona" )
        add_definitions( "-march=nocona -mtune=nocona -mmmx -msse -mfpmath=sse -msse2 -msse3" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      elseif( "${ALTERNATIVE_PROCESSOR}" STREQUAL "core2" )
        add_definitions( "-march=core2 -mtune=core2 -mmmx -msse -mfpmath=sse -msse2 -msse3 -mssse3" )
        # solve SIGSEGV of the JVM due to difference in stack alignment with SSE instructions
        add_definitions( -mstackrealign )
      endif()

  endif()

  message( STATUS "  Processor optimization: '${ALTERNATIVE_PROCESSOR}'" )

  if( "${ALTERNATIVE_PROCESSOR}" STREQUAL "generic" OR "${ALTERNATIVE_PROCESSOR}" STREQUAL "")

  else()
    if( ALTERNATIVE_PROCESSOR )
        set( ALTERNATIVE_EXTRA_PARAMS "${ALTERNATIVE_EXTRA_PARAMS}\nrequire.org.projectsforge.alternatives.requirements.Cpu=${ALTERNATIVE_PROCESSOR}\nalternatives.Cpu=${ALTERNATIVE_PROCESSOR}" )
    endif()
  endif()


  if("${CMAKE_BUILD_TYPE}" STREQUAL "Debug")
    add_definitions( -g3 )
  endif()
endif()
