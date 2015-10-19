#!/bin/bash
clang -g  src/main.c src/brainfuck.c src/GA.c -o brainfuck
./brainfuck $(cat programs/hello_world.bf) ""
