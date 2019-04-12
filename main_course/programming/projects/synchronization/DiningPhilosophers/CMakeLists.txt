cmake_minimum_required(VERSION 3.13)
set(CMAKE_CXX_STANDARD 17)

project(DiningPhilosophers)
find_package(Threads)
add_executable(DiningPhilosophers main.cpp config.h global.h)
target_link_libraries (DiningPhilosophers ${CMAKE_THREAD_LIBS_INIT})
