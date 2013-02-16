Chem
====

CS 3114 Project 1

Stochastic simulation for the first project of CS 3114.
The Chem.java file contains source code for the main method, initialization method,
and output methods.  The MinHeap.java file contains source code for the MinHeap<E>
data type with an array-based implementation.  The Reaction.java file contains
the source code for the Reaction class and member fields/methods.  The ReactionType.java
file contains the source code for the enumerated type ReactionType.

The design philosophy of this project was to make simulation runtime as fast as possible.
This means that the initialization has a lot of overhead, and isn't super efficient, but
it is needed to set up member variables effectively.  Overall, the number of runs you have
only effects the simulation runtime, and not initialization.

Group Members:
Chris Schweinhart (schwein)
Nate Kibler (nkibler7)
