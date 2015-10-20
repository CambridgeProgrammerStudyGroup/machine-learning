#!/usr/bin/env python

import string


CHARACTER_SET = string.lowercase  # change to string.printable for grater output variety
CHARACTER_SET_SIZE = len(CHARACTER_SET)
CHARACTER_TO_VALUE = {}
VALUE_TO_CHARACTER = {}


for index, printable_char in enumerate(CHARACTER_SET):
    CHARACTER_TO_VALUE[printable_char] = index
    VALUE_TO_CHARACTER[index] = printable_char
