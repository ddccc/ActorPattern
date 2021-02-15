File: c:/ddc/Java/Actor2B/aaReadMe.txt
Date: Sat Aug 03 17:38:50 2019

		 Sudoku by depth first search in PDA

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor2B\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent2 like:
  echo GENT2: --- Compile the Actor2 PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor2B
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor2b.Try
or alternatively pipe the output in a text file like:
  java actor2b.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 

The Sudoku puzzle configuration in Try.java should cause the Try
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


This implementation shows that the actor formalism (PDA) works for
'dumb' depth first search on Sudoku puzzles.  The constraints on these
puzzles are so strong that intelligent backtracking (as required for
the knight tour on the chess board) is not necessary.

The setup with monitors is contrived in this application because their
parallel nature is severely constricted.  Four monitors are
responsible respectively for going down a stack with unassigned tiles,
checking that a candidate tile value does not conflict with other
tiles in its row, column and subarray, increasing a tile value if need
be, and going up the stack if a tile's value cannot be increased.
Since only one monitor can be active at the time there is an
underlying state machine with four states and eight transitions; two
exits and six internal transitions.  The monitors execute nearly
linear, small pieces of code that get triggered, executed and
suspended.  Each executing monitor is responsible for specifying in
the state nextAction which next one should take over.  This state,
simplified, notifies all four monitors with a condition that only one
can satisfy.  This turns out to be a fragile arrangement.  A setting
in which there is only concurrency can cause a monitor to miss a
notification (implemented by an interrupt), which breaks the control
chain.  A meta-actor, using two monitors, keeps an eye on whether the
four monitors are making progress. When a stagnation is observed
another notification wil kick start the right monitor.

This set up is a subidiom of the PDA-idiom that will work for other
depth first search problems.  

Smart backtracking (say for the knight tour) requires a modification
of this idiom because it demands a global analysis of the status quo
to back track to a shallower level.

Smart jumping in the knight tour - to avoid causing a tile to be
choked off - does not require changing the idiom because it can be
accomodated by a better check whether a jump is allowed beyond
reaching an not yet visited tile but also that a tile is not choked
off. 

This depth first search implementation for the Sudoku puzzle is not
using the decision component of the PDA and neither the action
component; the actions are inside the monitors.  

Termination occurs when all tiles have obtained an acceptable value.
The go-down monitor recognizes success when its input stack is empty.
A 'legal' Sudoku puzzle has a unique solution.  Still the go-up
monitor recognizes a failure when its output stack is empty (which
means that all possibilities have been exhausted).  The meta actor has
been instrumented to cause a termination as well when the four actor
monitors are stuck and restarting fails. 

Actors and meta actors have null jobs.  They can be given the
responsibility to keep an eye on the sanity of what is going and take
corrective action/ resetting/ reporting trouble to a higher authority,
etc.  In this version they just report the work count.


[From the paper, see the second design.]

The previous example used in a sub-PDA a black box with a depth first
search solver for Sudoku puzzles coded in Java. Here we show how depth
first search can be implemented also in a PDA. The World is here a
Sudoku square with a puzzle configuration in its tiles. The setup with
monitors is contrived in this application because their parallel
nature is severely constricted.  Four monitors are responsible
respectively for going down a stack with unassigned tiles, checking
that a candidate tile value does not conflict with other tiles in its
row, column and sub array, increasing a tile value if need be, and
going up the stack if a tile's value cannot be increased, see Figure
5.

Since only one monitor can be active at the time there is an
underlying state machine of the state nextAction takes for values
(goDown, testVal, incrementQ, and goUp) and eight transitions (two
exits and six internal transitions).

The monitors execute nearly linear, small pieces of code that get
triggered, executed and suspended.  Each executing monitor is
responsible for specifying in the state nextAction which monitor
should take over.  This state, simplified, notifies all four monitors
with a condition that only one can satisfy.  This turns out to be a
fragile arrangement.  Due to concurrency a monitor can miss a
notification (implemented by an interrupt), which breaks the control
chain.  A meta-PDA, using two monitors, keeps an eye on whether the
four monitors are making progress. When stagnation is observed another
notification will resume the control cycle.

A different design is shown in Figure 6 in which each monitor is
triggered by its own state.

These designs can be seen as a sub pattern of the PDA-pattern that
applies to other depth first search problems, like the knight tour and
the path finding algorithm in the robotic example below.

This particular depth first search implementations for the Sudoku
puzzle is not using the Deliberate/Decide component of the PDA and
neither the action component; actions are invoked inside the monitors.
Yet another design moves the testing done in monitorTestVal into a
dispatcher, which calls the Deliberate/Decide component and
subsequently invokes a job that sets a tile, or not.

Termination occurs when all tiles have obtained an acceptable value.
The go-down monitor recognizes success when its input stack is empty.
A 'legal' Sudoku puzzle has a unique solution.  Still the go-up
monitor can recognize a failure when its output stack is empty.  The
meta PDA has been instrumented in the first design to cause a
termination as well when the four actor monitors are stuck and
restarting fails.  This idiom can be employed when a system has
multiple approaches to tackle a problem, or even - more challenging -
for an on the fly failure analysis.

PDAs and meta PDAs have null jobs.  They can be given the
responsibility to keep an eye on the sanity of what is going and take
corrective action/ resetting/ reporting trouble to a higher authority,
etc.  In this version they just maintain a work count.

