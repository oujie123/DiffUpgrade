cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(bzip2 BZSOURCE)

#native-lib 是给so库取的名字，随便取就行
add_library( native-lib
             SHARED
             native-lib.cpp
             bspatch.c
             ${BZSOURCE})

include_directories(bzip2)

find_library( log-lib
              log )

target_link_libraries( native-lib
                       ${log-lib} )