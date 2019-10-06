File: c:/ddc/Java/Actor13/aaReadMe.txt
Date: Sat Aug 03 15:28:45 2019


			    Basic PDA functionality

Here how to compile and and execute examples in a Windows setting
=================================================================

To compile a source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor13\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named GENT13 like:
  echo GENT13: --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor13
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

This example shows how the state-monitors can work with a blackboard
to classify in a 'sentence' its 'words'.  These words are have
features that must be identified. 
