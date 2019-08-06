File: c:/ddc/Java/ActorX/aaReadMe.txt
Date: Sat Aug 03 15:28:45 2019


			    Basic PDA functionality

Here how to compile and and execute examples in a Windows setting
=================================================================

To compile a source -- assuming that it is in the directories:
    <your path here>\YourDirectory\ActorX\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named GENT like:
  echo GENT: --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\ActorX
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor.Try
or alternatively pipe the output in a text file like:
  java actor.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 

================
This directory contains the core classes for the PDA actor design
pattern. 
The file HowTo.txt gives advice how to create your own application
with one or more actors.  
There are numerous files with examples, see also their
aaReadMe.txt files::
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

 
