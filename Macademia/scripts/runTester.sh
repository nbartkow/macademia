#!/bin/bash

if [ ! -d bin ] ;then
    mkdir bin
fi

DISCO=./lib/disco-1.1.jar
rm -rf bin/*
javac -d bin/ -cp $DISCO src/edu/mac/macademia/* &&
java -cp bin/:$DISCO edu.mac.macademia.InterestComparer $@
