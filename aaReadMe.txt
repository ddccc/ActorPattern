File: c:/ddc/Java/ActorPattern/aaReadMe.txt
Date: Mon Aug 05 17:10:58 2019

The directory layout for running, say, Actor5:

Your directory with:
   fol // the directory with the first order theorem prover
   Gent5.bat // Window script to compile the content of Actor5
             // or the equivalent script for unix
   Actor5 // the directory with the content of Actor5
The file aaReadMe.txt inside Actor5 describes how to procede.

The file ActorX contains the core classes for using the PDA actor
pattern.
The file ActorAstar has the ActorX classes plus infrastructure for
using the A* algorithm.
One of these is the start-point to develop a new application.

There are numerous files with examples, see also their
aaReadMe.txt files for details and how to execute their Try.main().
Actor5   A simple HelloWorld actor
Actor2   Depth first search solver for Suduko puzzles
Actor2B  Ditto, with faster version
ActorAstar A sub pattern: PDA + A* for best first search
Actor3A  ActorAstar customized for solving Sudoku puzzles
Actor7A  ActorAstar customized for solving the missionary & cannibals
         problem  
Actor    Emulating human tactics for solving Sudoku puzzles
Actor6   A preprocessor for a theorem prover with human tactics
Actor8   A dating simulator with two actors exchanging messages in
         order to agree to a meet&greet meeting  
Actor9   A 'pondering' actor for learning a simple sequence
Actor10A ActorAstar customized for a robotic actor solving Sokoban
         puzzles 
Actor11  Actor with concurrent goals with different priorities
Actor12  ActorAstar customized for solving a problem formulated using
         PDDL notation
