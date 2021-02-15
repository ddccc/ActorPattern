File: c:/ddc/Java/Actor8/aaReadMe.txt
Date: Sun Mar 18 16:51:26 2018


			    Basic PDA functionality

Here how to compile and and execute this example in a Windows setting
=====================================================================

To compile the source -- assuming that it is in the directories:
    <your path here>\YourDirectory\Actor8\
    <your path here>\YourDirectory\fol\

execute in YourDirectory a script named Gent8 like:
  echo GENT8: --- Compile the Actor PDA example -----------------
  set classpath0=%classpath%
  set classpath=%classpath%;<your path here>\YourDirectory\
  cd fol
  javac *.java
  cd ..\Actor8
  javac *.java
  cd ..
  set classpath=%classpath0%
  echo Finished

To execute the function Try in a shell: 
go to <your path here>\YourDirectory and execute:
  java actor9.Try
or alternatively pipe the output in a text file like:
  java actor9.Try > z.txt

You need to tinker with the script and with the names of your
directories in Unix. 
===================================================================

The other examples pertain to settings with a PDA for a single
agent. This example has PDA agents that represent males and females
who explore compatibility for setting up a meeting.  Each participant
has three vectors where each vector has 97 personality aspects, like
introvert-extrovert, liberal-conservative, pragmatic-idealistic,
etc. One of the vectors is a self-description, the 2nd one describes
the desired partner, and the 3rd one is the 'real' description of the
applicant provided by a person familiar with the applicant.

A participant starts the search by checking whether an invitation for
exploration is already available. If not, the description of a desired
partner is matched against self-descriptions of available
candidates. The simulator needs a design commitment whether both
genders can take the initiative or not. We may have deviated from
current reality that both genders equally take the initiative, but see
below. When a match succeeds, the candidate is sent an invitation for
one-on-one interactions in the second phase.  The candidate, when
available, will perform a similar test as the applicant.  When this
test also succeeds, both parties will query each other several rounds
against the 'real' features in the 3rd vector - a feature not
available in dating services as far as we know.  During these rounds
each of them has the option to back out.  After enough rounds of query
in the 2nd phase they can 'agree to meet in person' and this
terminates the simulation for them.

A participant's actor has here 8 states, 15 application specific
monitors, and 23 transitions (because three monitors have more than
one transition states).

The messaging between the participants is a major difference with the
preceding examples.  Sending a message and subsequently waiting for a
reply can get into trouble on a single core machine when the reply
arrives before the originating sender has started waiting.  A special
construct for this situation is actually available in Java:
SynchronousQueue. The side that arrives first (sender or waiter) can
be made to block for the other side (to poll or to offer).  We did not
pursue this solution to avoid blocking. Instead, simplified, we used
an interruptible timer.  A monitor gets activated by an incoming reply
or by the expiring timer.  A time-out will cancel the dialog.  This
solution obviates blocking by both parties, and conforms to the
FIPA-ACL asynchronous protocol. The waiting agent is not really
blocked because there can be another goal being pursued as we show in
another example below; the equivalent of pondering while an opponent
thinks about the next chess move.

The code for an agent worked fine on a single processor Pentium
machine, but failed on a four threads i3-machine.  Two participants
invited each other in parallel and both started waiting for each other
at the same time.  The fix used an asymmetry: the female waits a short
period before making an invitation and checks whether she has an
invitation.  If so she backs of from her invitation.  (There appears
to be a correspondence here with real life: males reach out, females
tend to wait for invitations.)

There are numerous parameters for tuning this simulation.  Random data
vectors for Alice & Bob experiments yielded distinctly different
behaviors for the data vectors obtained from a longtime married couple
and from a friend of them.

