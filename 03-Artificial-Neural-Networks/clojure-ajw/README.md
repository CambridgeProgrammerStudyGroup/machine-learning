# Contents

andrew.wood9@ntlworld.com

This is in the form of a Leiningen project.

1.	There are 3 approaches to the specified toy problems

perceptron
backprop

based on effors by Tanimoto, Fatvat and Wali (backprop only)

2. HCR 

The library Enclog is applied to the HCR problem. The code is adapted from an example in Wali.

## Usage

They work in the simple IDE Nightcode which has a built in copy of Leiningen 
   and runs from a standalone jar file.

The algorithms can all be accessed by modifiable templates in core.clj.

## Perceptron and Backprop

### Imperative

There is an imperative version of both based on a transliteration of the code in 

	Tanimoto: The Elements of Artificial intelligence in Common Lisp

The original well annotated lisp code is in the directory Tanimoto. 

The code is distinctly clunky but that has the advantage that it is easy to follow the algorithm. 

Perceptron: The original perceptron implementation had no bias and so could not handle zero inputs. This has been added.

### Functional

There is a working version - minor errors corrected -of the code presented in two blogs by FatVat. 

The original blogs are inthe directory html.

## Wali

Wali: Clojure for Machine Learning

has a version of backpropagation.

Unfortunately he breaks off his exposition with a trace of it failing to work with XOR
  leaving it to his readers to: 
     1. tinker with the learning parameters
     2. 'regularise' the deltas according to a mathematical formula.

I couldn't persudae it to work - it just gets jammed loping seemingly endlessly about its initial error - until I swapped out Wali's initial weight allocation with Tanimoto's. 


## Enclog

Enclog is a clojure wrapper of the java Encog neural network library.

It is a copy of the simple example (XOR) in Wali.

(enclog-hcr 0)

will take a specified numbr of examples from training.csv
process them for a set number of iterations / till a specified error
and then test of a specified number of examples from training.csv
(test.csv does not have labels).

It will then save the results of the test is good-0.csv and bad-0.csv so that the correctly and incorrectly classified images can be compared.

1000 / 0.1 generaly gives a success rate on test of 75%+.
 
Enclog appears not to have implemented learning-rate or momentum which makes experimentation somewhat limited.


 

