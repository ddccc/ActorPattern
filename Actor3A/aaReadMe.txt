File: c:/ddc/Java/Actor3A/aaReadMe.txt
Date: Wed May 22 18:45:16 2019


			    Basic PDA functionality

Here how to compile and and execute examples in a Windows setting
=================================================================

To compile a source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor3A\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named GENT like:
  echo GENT: --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor
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

--- 

This is an implementation/ elaboration of the A*-algorithm from
ActorAstar for solving Sudoku puzzles.
