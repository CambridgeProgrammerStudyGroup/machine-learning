#include <stdbool.h>

#ifndef BRAINFUCK_H
#define BRAINFUCK_H

void interpret(
	char* prog, int prog_length, 
	char* input, int input_length,	
	char* data, int data_length, // input var
	char* output, int output_length// input var
);

#endif