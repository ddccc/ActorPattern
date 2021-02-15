File: c:/ddc/Java/Actor5/aaReadMe.txt
Date: Sat Aug 03 17:30:56 2019

		 HelloWorld in PDA

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor5\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent5 like:
  echo GENT5: --- Compile the Actor5 PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor5
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor5.Try
or alternatively pipe the output in a text file like:
  java actor5.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 


----------------------------------------------------------------


The World for this 'challenge' is a console for input and output (not
part of the PDA pattern) that takes as input a typed word. If the word
is "HelloWorld", it will be displayed (because the 'axiom'
"Input(HelloWorld)" has been added initially to the theorem prover in
the Deliberate/Decide module).   If the word is "stop" or "halt" the
session terminates.  If the word is anything else and not encountered
earlier it will be remembered in the theorem prover but not displayed.
If the word was remembered it will be displayed.

An infinite loop in the World reads a word. When the word is "halt"
causes termination, see below. Otherwise the word is send to the
actor's message queue, which wakes up the actor. The Actor's run
function takes the item from the queue, stores it in the state
inputAvailable and calls its wakeup function, which notifies the
monitors registered in this state.  The monitor monitorInputReady
fetches the word in the state and feeds it into the alert generator
inputIs. Two things happen.  A condition is generated that needs to be
approved by the Deliberate/Decide component; in this case the
translation of the string:
   "Input(<word provided>)"
conjecture in the format that the deductive equipment can process.
Secondly the job doInstring with its arguments is generated for
subsequent execution in the Act component in case the condition is
approved.  The condition is checked by the theorem prover.  If the
user types, say, "abc" the proof obligation is the translation of
"Input(abc)", which fails but causes the addition of the resulting
atomic formula to the list of assertions in the theorem prover.  Hence
when the user types in "abc" again, the proof will succeed.

The meta PDA is involved in this example. It has a monitor
monitorTerminateQ, which has also been registered in the state
inputAvailable; see Figure 2.

When the meta monitor wakes up it fetches also the word stored in the
state inputAvailable; and it just decides by itself (instead of
creating an alert) whether or not the word is "stop".  If not, nothing
happens.  Otherwise it executes a system exit that breaks the infinite
loop and terminates the process.  Would it be possible to create an
alert, obtain permission from the Deliberate/Decide module and
subsequently launch a job specified in the alert that does the
termination?  Certainly.  Another way to terminate the input loop is
to type "halt". This breaks the World's read-loop and terminates the
actor in a controlled way, showing, among others, the assertions added
to the theorem prover.

