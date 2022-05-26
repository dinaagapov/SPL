cmake_minimum_required(VERSION 3.17)
project(SPL1)

set(CMAKE_CXX_STANDARD 14)

add_executable(SPL1 main.cpp Studio.h Action.h Customer.h Trainer.h Workout.h Workout.cpp Trainer.cpp Customer.cpp Studio.cpp Action.cpp)