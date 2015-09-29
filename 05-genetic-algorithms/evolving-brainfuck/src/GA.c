#include <stdlib.h>
#include <limits.h>
#include <stdio.h>
#include <math.h>
#include <time.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>

#include "brainfuck.h"

#define ALPHABET "+-[]><.,"
#define TARGET "Hello, puny humans!"



float randf(){
	return (float)rand()/(float)RAND_MAX;
}

char choose(char* alphabet){
	return alphabet[rand() % strlen(alphabet)];
}

/**
 * Returns a null-terminate, allocated string bufer with an 
 * initialised candidate. This will need to be free'd after use.
 */
char* gen_candidate(int len){
	int alphabet_len = strlen(ALPHABET);

	char* cand = malloc(len+1);
	for (int i = 0; i < len; i++){
		cand[i] = choose(ALPHABET);
	}
	cand[len] = '\0';

	return cand;
}

char** gen_population(int size, int len){
	char** pop = malloc(size * sizeof(char*));
	for (int i = 0; i < size; i++){
		pop[i] = gen_candidate(len);
	}
	return pop;
}

int min(int a, int b){
	return (a>b)?b:a;
}

int distance(char* a, char* b, int len){
	int count = 0;
	for(int i = 0; i < len; i++){
		(a[i] != b[i])?count++:0;
	}
	return count;
}


float fitness(char* program, char* target, int iter_max){
	float fitness = 0.0;
	
	int data_length = 10000;
	char* data = calloc(data_length,sizeof(char));
	if(data == NULL){goto fitness_clean;}

	int output_length = 10000;
	char* output = calloc(output_length,sizeof(char));
	if(output == NULL){goto fitness_clean;}

	int input_length = 10000;
	char* input = calloc(input_length,sizeof(char));
	if(input == NULL){goto fitness_clean;}


	interpret(
		program, strlen(program), 
		input, input_length,
		data, data_length, 
		output, output_length,
		iter_max
	);

	// Null-terminate the ouput to be sure :)
	output[output_length-1] = '\0';

	float dist = (float) distance(target, output, strlen(target));
	int diff = abs((int)strlen(output) - (int)strlen(target));


	if(0.0 == dist){
		fitness = 1.0;
	}else{
		fitness = 1.0/(dist+diff);
	}
	
	printf("%s   ->   '%s' (%f)\n", program, output, fitness);

fitness_clean:
	free(input);
	free(data);
	free(output);

	return fitness;
}

/*
 * Returns a null terminated string that will need to be free'd
 */
char* breed(char* a, char* b){
	int cut_a = rand() % strlen(a);
	int cut_b = rand() % strlen(b);

	// if we want to preserve fixed-length strings, 
	// use this condition:
	// cut_a = cut_b;

	int len_a = cut_a;
	int len_b = strlen(b) - cut_b;

	int child_len = len_a + len_b+1;

	char* child = calloc(child_len, sizeof(char));

	strncpy(child, a, len_a);
	strncpy(child+len_a, b+cut_b, len_b);

	return child;

}

void mutate(char* candidate, float rate){
	for(int i = 0; i < strlen(candidate); i++){
		if(randf() < rate){
			candidate[i] = choose(ALPHABET);
		}
	}

}

void GA(){
	int popsize = 10;
	float* fitnesses = calloc(10, sizeof(float));

	srand(time(NULL));

	char** population = gen_population(popsize, 30);

	

	for(int i = 0; i < popsize; i++){
		float fit = fitness(population[i], TARGET, 100000);
		fitnesses[i] = fit; 
	}
	
	


	//printf("%s\n",breed("1234567890","1234567890"));
	
	free(fitnesses);
	return;
}




