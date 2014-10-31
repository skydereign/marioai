#! /c/Anaconda/python
from MarkovChain import *
mc = MarkovChain("./markov")
# To generate the markov chain's language model, in case it's not present
mc.generateDatabase("ThisThis string of Text. is This is a string of Text. a string of Text.It won't  is a string of Text. This is a string of Text. It won't generate string of Text. an interesting string of Text.database though.")
# To let the markov chain generate some text, execute
print mc.generateString() 