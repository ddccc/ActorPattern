File: c:/ddc/Java/Actor7/aaReadMe.txt
Date: Sat Sep 30 13:13:22 2017

		     MaC by best first search

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor7\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent7 like:
  echo GENT:7 --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor7
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor7.Try
or alternatively pipe the output in a text file like:
  java actor7.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 


----------------------------------------------------------------

Here an explanation what goes on in this PDA application version
================================================================


This implementation shows that the actor formalism (PDA) works for
best first search on the Missionaries & Cannibals (MaC) puzzle, which
is the 'outside world' from the Actor-PDA perspective.  Adherence to
the A* algorithm is complete with open and closed nodes.  Checking a
new node against the open & closed nodes is crucial to make progress
because an action can be reversed without making any progress.

The setup of the monitors is contrived because one monitor just
triggers the other one.

A most promissing open node (according to the G-function) is chosen
for evaluation by checking whether a solution is found.  If so the
sequence of start-end nodes is displayed.  Otherwise the next monitor
expands the node and generates alerts for each successor node.

These alerts generate dispatcher jobs, which each lead to scrutiny by
the decision/ deliberate module whether the node is legal, given the
constraints of the Mac puzzle (cannibals may not outnumber the
missionaries and the boat can hold at most 2 of them).  The dispatcher
invokes the decision/deliberation and if approved launches the job.
The job just recommends adding the new node to the set of open nodes.
A last check verifies whether the new node is not in the set of open
nodes and not in the set of closed nodes.  Wake-ups are generated for
the first monitor to see whether an open node is available.
