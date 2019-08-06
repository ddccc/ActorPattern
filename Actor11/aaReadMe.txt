File: c:/ddc/Java/Actor11/aaReadMe.txt
Date: Sun Mar 18 16:51:26 2018


			PDA with concurrent goals

Here how to compile and and execute examples in a Windows setting
=================================================================

To compile a source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent11 like:
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
  java actor11.Try
or alternatively pipe the output in a text file like:
  java actor11.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 
===================================================================

We envisioned a scenario where a background task gets interrupted
because another task must take over for a short while, after which the
background task should resume.  This was quite easy to achieve.  Two
states trigger two monitors.  One monitor creates the back ground job,
which executes iterations.  Its assigned task manager checks when
iterating whether a job with a higher priority is available.  The
second monitor creates a job with a higher priority.  Hence the first
job gets suspended and the job with a higher priority runs.  When that
one terminates it creates a timer, which, when it expires, will cause
the creation of a similar high priority job.  The role of the timer
could be replaced by an external event that the agent responds to.

This example shows that a PDA contains two types of processes. The
priorities are fixed for the monitors with their runmonitors.  The
priorities of the dispatchers and jobs with their tasks are chosen and
can even subsequently be manipulated by the PDA itself.
