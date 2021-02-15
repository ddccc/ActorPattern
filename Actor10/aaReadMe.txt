File: c:/ddc/Java/Actor10/aaReadMe.txt
Date: Sun Mar 18 16:51:26 2018


			    Sokoban

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent10 like:
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
===================================================================

The 'robotic' agent lives in a grid with 2 or 3 boxes that have to be
pushed (no pulling) to 2 or 3 goal locations.  There are numerous
obstacles.  A human learns fast that pushing a box in a corner (when
not a goal location) is a dead-end. Edge locations are usually
dead-ends too.  These features allow for the design of learning
opportunities in a fairly simple setting, but which are still beyond
what we wanted to achieve: solving the puzzles using the A* algorithm
(without reopening closed nodes).  Certain grid locations were marked
as no-go for box locations, which cuts-down on dead-end nodes,
although obstacles in the grid can still lead to dead-end states.  The
A* algorithm permits to recover from dead-ends at the abstract
planning level. One of our traces showed that even one-step look-ahead
to recognize a dead-end (in our Sokoban test domain) is not
sufficient.

We used for the Sokoban domain only one monitor that looks for an open
node. If a node is obtained it starts the node expansion process.  If
not, it checks whether there are still pending alerts, actions or
elements in the task queue.  If so, it will wait for another trigger;
otherwise no solution is found.  Node expansion is a multistep
process: generating feasible box moves from a box's perspective,
filtering out moves from the robot's perspectives, and rejecting
configurations that have been encountered earlier. A configuration
that survives these checks is fed via an alert into a deliberation
function and subsequently into a job module that adds a new
configuration into the sorted collection of open nodes.  The
deliberation module investigates whether the new configuration is
actually the solution (deviating from the A* algorithm), and if so
causes the termination.

Perhaps the trickiest operation (potentially relevant from a robotic
perspective) was deciding whether a move possible from a
box-perspective could be executed: was there a path to the particular
box edge from the current robot location? A depth first search
algorithm between two locations on the grid solved this task.   (Some
discretization of continuous space is required to apply this
technique.)

