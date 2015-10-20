"""
Just trying ...
-- jordan <jordan@teddy-net.com>
"""

import sys
import random
import re

class GenAlgo:
    """
    __alp -- alphabet for random characters
    __trg -- target string to approximate
    __tbl -- generation table
    __mtr -- mutation rate
    """

    __alp = ""
    __trg = ""
    __tbl = []
     
    def __charlist (self, rx):
        return [chr (n) for n in range (1,127) if re.match (rx, chr (n))]

    def __ranstr (self):
        "generate random string of length len(__trg)"
        return ''.join (random.choice (self.__alp)
                        for _ in xrange (len (self.__trg)))

    def __fitness (self, i):
        return sum (a != b for a, b in zip (i, self.__trg))

    def __ftt1st (self, tbl):
        "sort by fitness"
        return  sorted (tbl, None, self.__fitness)

    def __permute (self, w):
        "generate an random permutation of [0 .. w-1]"
        inx = range (0, w)
        iny = []
        for inx_top in reversed (range (0, w)):
            n = random.randint (0, inx_top)
            iny.append (inx [n])
            del inx [n]
        return iny

    def __mutate (self, s):
        "mutate single character in string s randomly"
        if random.random () <= self.__mtr:
            return s
        n = random.randint (0, len (s)-1)
        return s [:n-1] + random.choice (self.__alp) + s [n:]
        
    def __init__ (self, tab_size, mtr, rx_alp, text):
        self.__mtr = mtr
        self.__alp = self.__charlist (rx_alp)
        self.__trg = text
        self.__tbl = self.__ftt1st ([self.__ranstr ()
                                        for _ in xrange (tab_size)])
    def alphabet (self):
        return self.__alp

    def population (self):
        return self.__tbl

    def fitness (self):
        "fitness of the fittest of the population"
        return self.__fitness (self.__tbl [0])

    def xbreed (self):
        "next generation: replace unfittest half of population"
        hdim  = len (self.__tbl) / 2
        tlen  = len (self.__trg)
        top   =  self.__tbl [0:hdim]

        # split, reassemble, and mutate fittest half of population
        htlen = tlen / 2
        right = [self.__tbl [i] for i in self.__permute (hdim)]
        bottm = [self.__mutate (u [0:htlen] +
                                v [htlen:tlen]) for u, v in zip (top, right)]

        # replace unfittest half of population
        self.__tbl = self.__ftt1st (top + bottm)

    def evo (self, max):
        "evolution: repeat xbreed"
        n = 0
        while self.fitness () > 0 and n <= max:
            n += 1
            self.xbreed ()
        return n
        
def main ():
    g = GenAlgo (1000, 0.3, '[ \w]', "how much wood could a wood chuck chuck")
    n = g.evo (1000)
    print "count: ", n, " fitness: ", g.fitness ()
    exit ()

if __name__ == "__main__":
    main ()

# End











