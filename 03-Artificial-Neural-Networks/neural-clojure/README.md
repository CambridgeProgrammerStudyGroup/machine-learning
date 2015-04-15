# Contents

This is in the form of a Leiningen project.

There are 3 modules

perceptron
backprop
enclog

## Usage

They work in the simple IDE Nightcode which has a built in copy of Leiningen 
   and runs from a standalone jar file.

The algorithms can all be accessed by modifiable templates in core.clj.

## Perceptron and Backprop

are based on a transliteration of the code in 

	Tanimoto: The Elements of Aritifical intelligence in Common Lisp

into clojure. The original well annotated lisp code is in Tanimoto. 

The code is distinctly clunky but that has the advantage that it is easy to follow the algorithm. 

The original perceptron implementation had no bias and so could not handle zero inputs.
This has been added.

## Enclog

Enclog is a clojure wrapper of the java Encog neural network library.

It is a copy of the simple example (XOR) in:

  Wali: Clojure for Machine Learning

Wali has his own version of backpropagation.
Unfortunately he breaks off his exposition with a trace of it failing to work with XOR
  leaving it to his readers to: 

    1. tinker with the learning parameters

          I found that it works a charm on individual examples but not sets 
          despite adjustments. Presumably I am missing something.

    2. 'regularise' the deltas according to a mathematical formula.

          This was beyond me. He refers the reader to his chapters on regression for
          clarification so maybe I'llcome back to it.

 

