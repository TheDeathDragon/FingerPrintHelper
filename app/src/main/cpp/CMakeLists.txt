
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("fingerprinthelper")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections -fvisibility=hidden -fvisibility-inlines-hidden -Os")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")
SET_TARGET_PROPERTIES(${TARGET_NAME} PROPERTIES LINK_FLAGS "-Wl,--gc-sections")

add_library( # Sets the name of the library.
             fingerprinthelper

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             fingerprinthelper.cpp )

target_link_libraries( # Specifies the target library.
             fingerprinthelper
        )
