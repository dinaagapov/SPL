cmake_minimum_required(VERSION 3.21)
project(sp3_try)

set(CMAKE_CXX_STANDARD 11)
set(Boost_INCLUDE_DIR /cygdrive/c/cygwin64/usr/include)

find_package(Boost REQUIRED COMPONENTS system)
include_directories(${Boost_INCLUDE_DIRS})

add_executable(sp3_try src/echoClient.cpp include/echoClient.h src/ConnectionHandler.cpp include/ConnectionHandler.h src/WriteMessage.cpp include/WriteMessage.h src/ReadMessage.cpp include/ReadMessage.h)
target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})

