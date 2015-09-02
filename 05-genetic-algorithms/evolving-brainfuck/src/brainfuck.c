#include <stdio.h>

#include "brainfuck.h"

bool DEBUG = false;

void interpret(
	char* prog, int prog_length, 
	char* input, int input_length,	
	char* data, int data_length, // input var
	char* output, int output_length// input var
){
	int progi = 0;
	int inputi = 0;
	int outputi = 0;
	int datai = 0;
	bool valid = true;
	
	while (valid){
		
		// data pointer out of range
		if(datai >= data_length || datai < 0){break;}
		
		// prog index out of range
		if(progi >= prog_length || progi < 0){break;}


		char instruction = prog[progi];

		if(DEBUG){
			printf("[%i, %i, %i, %i, %i, %i, ...]\n", data[0], data[1], data[2], data[3], data[4], data[5]);
			printf("[%c] progi:%i, inputi:%i, outputi:%i, datai:%i\n",instruction,progi,inputi,outputi,datai);
		}
		

		switch(instruction){
			case '>': datai++; break;
			case '<': datai--; break;
			case '+': data[datai]++; break;
			case '-': data[datai]--; break;
			case '.': 
					if(outputi >= output_length){valid=false;break;}
					output[outputi] = data[datai]; 
					outputi++; 
					break;
			case ',': 
					if(inputi >= input_length){valid=false;break;}
					data[datai] = input[inputi]; 
					inputi++; 
					break;
			case ']':
					if(data[datai] != 0){
						int balance = 1;
						int pointer = progi-1;
						while(balance != 0){
							if(pointer < 0){printf("Could not find matching [ paren!\n");valid=false;break;}
							if(prog[pointer] == ']'){ balance++; }
							if(prog[pointer] == '['){ balance--; }
							pointer--;
						}
						if(pointer >= 0){ progi = pointer + 1; }
					}
					break;
			case '[':
					if(data[datai] == 0){
						int balance = 1;
						int pointer = progi + 1;
						while(balance != 0){
							
							if (pointer >= prog_length){printf("Could not find matching ] paren!\n");valid=false;break;}
							if(prog[pointer] == '['){ balance++; }
							if(prog[pointer] == ']'){ balance--; }
							pointer++;
						}
						progi = pointer + 1;
					}
					break;
			default:
				break;
					
		}

		progi++;
	}
}