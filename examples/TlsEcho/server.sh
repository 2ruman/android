#!/bin/bash

#Clean up all the class files
rm -rf bin/server

shopt -s globstar

#Compile the main java with all dependencies
javac -d bin/server -cp dist/bcprov-jdk18on-1.80.jar server/src/main/java/**/*.java

#Run the compiled main class
java -cp bin/server:dist/bcprov-jdk18on-1.80.jar truman.android.example.tls_echo.server.Main
