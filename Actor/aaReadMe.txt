File: c:/ddc/Java/Actor/aaReadMe.txt
Date: Tue Apr 08 19:49:09 2008/ 2019

			    Sudoku by PDA

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
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

Here an explanation what goes on in this PDA application version
================================================================

This implementation shows that the actor formalism (PDA) works for
an emulation of the >human approach< to Sudoku puzzles.  (A 'machine'
version using brute force depth first search is faster, easier to
implement and will solves all puzzles - also those that cannot be
solved without back tracking.)  { Why a solver that emulates the human
approach?  To assertain that a generated puzzle is human solvable. }  

A human develops over time a collection of tactics to find a tile that
must have a particular numeric value.  Iteration of these tactics will
find the last tile, assign its value and terminate the task.  { There
are more tactics than implemented in this emulation.  }  Available
tactics are: dealing with rows, columns and subarrays that have only a
single non-assigned tile; tactics that deal with two unassigned tiles
and attempts to show that one of them must have a specific value given
the constraints imposed by the context; yet another available tactic
applies to any unassigned tile in a subarray and checks whether a
unique candidate value is compatible with the constraints of the
context. { There are others. }
 
Monitors embody these tactics.  They reach into the world of a sudoku
array and if warranted invoke alert generators (for example
Actor.row1).  An alert (an instance of DoRow1, a subclass of Job)
yields a job that is wrapped by a dispatcher (an instance of Row1, a
subclass of Dispatcher which is also a subclass of Job).  A dispatcher
can request approval in the decide/deliberate phase, but in this
application all alert-jobs are silently approved and launched.  These
jobs invoke the sudoku specific tactics that aim to set a correct
value to an unassigned tile.

Notable is that these actions when finished give (a kind of) feedback
to the perception module by waking up specific states, which causes
subscribed monitors to get activated.  This feedback code is at this
point ad hoc.  Whether a generic mechanism must be added to the PDA
design pattern is an open issue.

This feedback mechanism realizes a 'circular' action pattern.  Row,
column and subarray monitors can activate the monitors that attempt to
set a specific value in a subarray >and<, simplified, the other way
around.  It turns out that this arrangement may fail in the current
version; reactivation stops before all tiles are assigned.  The meta
PDA comes to the rescue.  It checks periodically what the zeroCnt is
of the 9x9 array.  When the zero count does not change it increases
the noChangeCnt.  This will trigger activation of the row and state
states, which in turn activates the registered monitors.  If this does
not kick start the decrease of the zeroCnt then the meta PDA will
trigger other states that trigger the monitors that attempt to
set a specific value in a subarray.  

Psychological relevance cannot be claimed by this example: this PDA
example uses concurrency/ parallelism for applying the tactics that
follow the PDA pattern, which is not done by humans (and neither by a
brute force, depth first search version).

The perception part (P of PDA) consists of monitor-state chains.  An
example is the monitor MonitorSquare reaching into the 'world' of the
square-object that contains the puzzle and retrieves the zero count.  If
the zero count has changed (and is not zero) if will record the new
value in the state squareState and invokes its wakeUp() function.
The current version has not registered other monitors that can be
activated by squareState.

Another example of a monitor is an instance of MonitorRow (there are 9
of them).  Such a monitor for the i-th row has registered itself in a
state rowStates[i] and gets activated when that state changes.  The
monitor investigates the zeroCnt for its row.  If it has become zero
it stops its wrapper (an instance of RunMonitor).  If the count is one
it invokes the function actor.row1(i), which generates an alert.  An
alert is, in essence, an if-then-else rule.  Its condition is an
atomic predicate calculus expression that can be tested by a built-in
theorem prover in a dispatcher.  Another component of the alert is an
action that can be launched as a job.  The condition test is NOT done
by the dispatcher in this application with the doRow1 action.  Instead
a job, the action part doRow1, is always launched by a dispatch
wrapper that injects the job in a task queue.  The launched job, an
instance of DoRow1, is tasked to find and fill-in the single
unassigned position in a particular row.

Another alert generator is setTile that prepares to assign a tile to a
specific value.  The generated alert contains an instance of
DoSetTile, which is the next step to set a vacant position.  The
dispatcher for this alert, an instance of SetTile (a subclass of
Dispatcher) contains a substitute for testing the condition with a
theorem prover: it checks whether the target tile has been set
already.  Due to the inherent concurrency/ parallelism this can happen
in certain versions of this PDA application. (The function that
actually assigns the pertinent array entry checks again within a
synchronized block.)

A dispatcher operates in the Deliberate/Decide realm of the PDA
pattern.  The task it launches, if any, is in the Action realm.

The PDA pattern harbors ample concurrency/ parallelism.  The abstract
class ActorBase in Runnable and thus the two subclasses Actor and
ActorMeta have active instances.  A monitor has a wrapper that is an
instance of RunMonitor, which is a Runnable class.  The Dispatcher
class is a subclass of Job for which its instances gets wrapped by an
instance of Task which is also a Runnable class.  Decoupling the
functionality of monitors and jobs from how they get executed helps to
increase generic infrastructure and shrink the application specific
code to, preferably, semi-linear code.  The latter opens the door, in
the best of all worlds, to automatic code generation - ako 'learning'.

The Job class specifies the Java priority 5, which is the default.  
It is OK to specify a higher priority for a subclass of Job, like 
DoSetTile, because short linear code will not hog the processor.
When the Job subclass priorities are lower, they get starved and a 
solution may not be found.



