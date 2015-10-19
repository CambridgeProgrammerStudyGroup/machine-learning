#!/usr/bin/env python

import sys

from character_set import (CHARACTER_SET_SIZE, CHARACTER_TO_VALUE,
                           VALUE_TO_CHARACTER)


MEMORY_SIZE = 30000


class SegmentationFault(Exception):
    pass


class Break(Exception):
    pass


class Continue(Exception):
    pass


def _increment_data_pointer():
    global memory_index
    memory_index += 1
    if memory_index >= MEMORY_SIZE:
        raise SegmentationFault


def _decrement_data_pointer():
    global memory_index
    memory_index -= 1
    if memory_index < 0:
        raise SegmentationFault


def _increment_val():
    memory[memory_index] = (memory[memory_index] + 1) % CHARACTER_SET_SIZE


def _decrement_val():
    memory[memory_index] = (memory[memory_index] - 1) % CHARACTER_SET_SIZE


def _output_val():
    sys.stdout.write(VALUE_TO_CHARACTER[memory[memory_index]])


def _input_val():
    while True:
        user_input = raw_input("Enter a single character:")
        if len(user_input) == 1:
            memory[memory_index] = CHARACTER_TO_VALUE[user_input]
            break


def _while_non_zero():
    if memory[memory_index] == 0:
        raise Break


def _end_while():
    if memory[memory_index] != 0:
        raise Continue


COMMANDS = {">": _increment_data_pointer,
            "<": _decrement_data_pointer,
            "+": _increment_val,
            "-": _decrement_val,
            ".": _output_val,
            ",": _input_val,
            "[": _while_non_zero,
            "]": _end_while}


def _find_braces(program_string):
    find_opening_brace = {}
    find_closing_brace = {}

    opening_braces = []
    for (position, character) in enumerate(program_string):
        if character == "[":
            opening_braces.append(position)

        elif character == "]":
            try:
                opening_brace_position = opening_braces.pop()
            except IndexError:
                opening_brace_position = None

            find_opening_brace[position] = opening_brace_position

            if opening_brace_position is not None:
                find_closing_brace[opening_brace_position] = position

    if len(opening_braces) != 0:
        for opening_brace_position in opening_braces:
            find_closing_brace[opening_brace_position] = None

    return find_opening_brace, find_closing_brace


def _initialise_memory():
    global memory, memory_index
    memory = [0] * MEMORY_SIZE
    memory_index = 0


def bf_interpreter(program_string):
    _initialise_memory()
    # Generate mappings to lookup location of matching braces.
    find_opening_brace, find_closing_brace = _find_braces(program_string)

    program_position = 0
    while True:
        try:
            instruction = program_string[program_position]
        except IndexError:
            # We've reached the end of the program.
            break

        try:
            COMMANDS[instruction]()
        except KeyError:
            # Brainfuck just ignores any characters that are not in it's operator set.
            pass
        except SegmentationFault:
            # Treat this as a valid way to trigger program exit.
            break
        except Break:
            program_position = find_closing_brace[program_position]
        except Continue:
            program_position = find_opening_brace[program_position]

        if program_position is None:
            # i.e. program is syntactically invalid as there is no matching
            #      opening/closing brace to go to - exit.
            break
        else:
            program_position += 1


if __name__ == "__main__":
    bf_interpreter(sys.argv[1])
