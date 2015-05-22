import sys

with open("ANSWERS.txt") as af:
  answers = map(str.strip, af.readlines())
  for fn in sys.argv[1:]:
    with open(fn) as tf: 
      guesses = map(str.strip, tf.readlines())
      errors = sum( [0 if a == b else 1 for a,b in zip(answers, guesses)])
      print("Error for {:>25}: {:>5} ({:.2%})".format(fn, errors, float(errors)/len(guesses)))


