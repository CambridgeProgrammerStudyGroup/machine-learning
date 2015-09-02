#!/bin/bash
clang  src/main.c src/brainfuck.c -o brainfuck
./brainfuck $(cat programs/hello_world.bf) ""
