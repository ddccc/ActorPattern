File: c:/ddc/Java/Actor3/aaReadMe.txt
Date: Tue Apr 26 17:32:16 2008

		     Sudoku by best first search

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor3\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent3 like:
  echo GENT:3 --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor3
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor3.Try
or alternatively pipe the output in a text file like:
  java actor3.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 

The first Sudoku puzzle configuration in Try.java should cause the Try
execution to terminate with the solution:
 8 6 1 3 9 7 2 4 5
 5 3 9 2 8 4 1 6 7
 2 7 4 6 5 1 8 9 3
 3 1 5 7 2 9 6 8 4
 7 9 6 5 4 8 3 2 1
 4 8 2 1 3 6 5 7 9
 9 2 8 4 1 5 7 3 6
 1 4 7 8 6 3 9 5 2
 6 5 3 9 7 2 4 1 8 

----------------------------------------------------------------

Here an explanation what goes on in this PDA application version
================================================================


This implementation shows that the actor formalism (PDA) works for
best first search on Sudoku puzzles, which is the 'outside world' from
the Actor-PDA perspective.  Adherence to the A* algorithm is
incomplete.  There is a set of open nodes (see Vector vec in Actor),
but no set of closed nodes.  There is a set of failed nodes, but a new
node is not checked against the set of open nodes, closed nodes and
failed nodes, because only a handful of nodes are generated in our
examples. 

The setup of the monitors is contrived because their parallel feature
is severely contricted through a kind of token regime.  

The actor takes a node that is still open (i.e. has unassigned
tiles).  It locates in such a node an unassigned tile T that is the most
constrained.  A successor node consists of a copy of the square with
the tile T given a value that is compatible with the row, column
and array associated with T.  A successor node is added to the set of
open nodes.  An open node is typically NOT solvable.

A most promissing open node (according to the F-function) is chosen
for evaluation by applying the solve-function that emulates human
tactics.  There are 3 possibilities:
-1- all tiles assigned but no solution, because a wrong 
    assignment was made earlier in Actor.expandNode(square) (which is
    invoked in monitorCheckSuccess)
-2- all tiles assigned and make up a solution
-3- some tiles are still unassigned
1 leads to close the node
2 leads to termination
3 leads to node expansion and the successors are added to the set of
open nodes

The monitors are triggered by token-like invocation:
state triggers    monitor               wakes up state
------------------------------------------------------------
bestNode          MBestNodeAvailable    bestNodeAvailable 
bestNodeAvailable MCheck                bestNodeChecked
bestNodeChecked   MCheckFinished
                  MCheckFailed          bestNode
                  MCheckSuccess         bestNode

This loop starts in the Actor.run() operation with a wakeUp() of
bestNodeSet.  The loop ends by a termination triggered in
MCheckFinished. 

Actors and meta actors have null jobs.  They can be given the
responsibility to keep an eye on the sanity of what is going and take
corrective action/ resetting/ reporting trouble to a higher authority,
etc.  In this version they just report the work count.

This version is NOT using alerts, jobs & tasks because no high level
deliberation is required.  Instead Actor.expandNode(square) is doing
all the changes in the 'outside world'.
