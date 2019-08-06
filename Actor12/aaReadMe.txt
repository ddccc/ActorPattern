File: c:/ddc/Java/Actor12/aaReadMe.txt
Date: Fri Jun 21 15:21:23 2019


			    Basic PDA functionality

Here how to compile and and execute examples in a Windows setting
=================================================================

To compile a source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor\
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

This application uses the abstract implementation of the A*-algorithm
in ActorAstar.
The Node class is specialized for PDDL encoding of domains and
problem instances.
The unification algorithm from the fol package is employed to match
the preconditions of actions against the current state.

