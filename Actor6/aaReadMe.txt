File: c:/ddc/Java/Actor6/aaReadMe.txt
Date: Sat Apr 14 21:27:17 2018


			    Theorem prover manager

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor6\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent6 like:
  echo GENT: --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor6
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor6.Try
or alternatively pipe the output in a text file like:
  java actor6.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 
===================================================================

This directory contains Java source files for the PDA agent that
pre-processes a conjecture.  It tries to rewrite the input into a
conjunction and generates compressed mini-scope.  It tries to apply a
definition if available.  It uses a kind of subsumption algorithm to
generate compressed mini-scope.  If all high level operations fail
(many more are conceivable) then a resolution theorem prover is
invoked. 



