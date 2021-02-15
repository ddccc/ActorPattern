// File: c:/ddc/Java/Actor9/Job.java
// (C) OntoOO Inc 2005 Apr
// Date: Sun Mar 18 18:14:25 2018

package actor9;

import java.util.*;
import java.io.*;

import fol.Formula;

public abstract class Job {
    static final protected int updateInterval = 200; // 0.1 secs
    protected ActorBase actor = null;
    public ActorBase getActor() { return actor; }
    protected int priority = 5;
    public void setPriority(int i) { priority = i; }
    public int getPriority() { return priority; }
    protected String actorName = "unknown";
    public Job(ActorBase actor) { 
	this.actor = actor; 
	actorName = actor.getName();
    }
    abstract public boolean execute();
    abstract public String ascii();

} // end Job

class NullJob extends Job {
    NullJob(ActorBase actor) { 
	super(actor); 
	priority = 1;
    }
    private int cnt = 0;
    public boolean execute() {
	cnt = actor.getCnt();
	// int actorCnt = actor.getCnt();
	actor.nullJobCheck(cnt);
	// other stuff here
	try {
	    Thread.sleep(updateInterval);
	} catch (InterruptedException ignore) {}
	return true;
    } // end execute()

    public String ascii() { return "NullJob cnt: " + cnt; }
} // end NullJob 


class ShowAlertTrace extends Job {
    ShowAlertTrace(ActorMeta actor) { 
	super(actor); 
	priority = 1;
    }
    public boolean execute() {
	ActorMeta actorMeta = (ActorMeta) actor;
	Actor actor0 = actorMeta.getActor();

	Vector alertTrace = actor0.getAlertTrace();
	int lng = alertTrace.size();
	actor.addTrace
	    ("AnalyzeAlertTrace: for: " + actor0.getName() +
	     " alertTrace size:" + lng
	     );
	StringBuffer sb = new StringBuffer("AnalyzeAlertTrace: <b>alerts:</b>");
	for (int i = 0; i < lng; i++) {
	    Alert alert = (Alert) alertTrace.elementAt(i);
	    Dispatcher d = alert.getDispatcher();
	    Formula trigger = d.getTrigger();
	    String jobName = d.getJob().getClass().getName();
	    boolean launched = alert. getLaunched();
	    sb.append(
		"<br>" +
		trigger.html() + " " +
		jobName + " " +
		launched);
	}
	actor.addTrace(sb.toString());
	return false;
    } // end execute()

    public String ascii() { return "ShowAlertTrace"; }
} // end ShowAlertTrace 

// add other subclasses here:

class DoGuessNextValue extends Job {
    private Actor myActor = null;
    private int N = Try.N;
    private int[][]arr;
    private int[] bestGuessx;
    private int previousValue;
    DoGuessNextValue(Actor actor, int previousValue, 
		     int[][]arr, int[] bestGuess) {
	super(actor); 
	this.previousValue = previousValue;
	this.arr = arr; bestGuessx = bestGuess;
	priority = 11;
	myActor = (Actor) actor;
    }
    public boolean execute() {
	int bestGuess = bestGuessx[previousValue];
	myActor.addTrace("DoGuessNextValue previousValue: " + 
			 previousValue +
			 " bestGuess: " + bestGuess);
	int actualValue = myActor.getWorldEvent().getIntValue();
	if ( bestGuess == actualValue )
	    myActor.addTrace("   Guessed RIGHT bestGuess: " + bestGuess);
	else
	    myActor.addTrace("   Guessed Wrong bestGuess: " + bestGuess +
			     " actualValue: " + actualValue);
	// learn the input pattern
	arr[previousValue][actualValue]++; // increment the successor cnt
	int ix = -1; int j = 0;
	for (int i = 0; i < N; i++) 
	    if ( ix < arr[previousValue][i] ) {
		ix = arr[previousValue][i]; j = i;
	    }
	// store the index of the most frequent successor
	bestGuessx[previousValue] = j; 
	myActor.setPreviousValue(actualValue); // prepare for the next event
	// myActor.worldThread.interrupt(); // do it again
	return false;
    } // end execute()
    public String ascii() { return "DoGuessNextValue "; }
} // end DoGuessNextValue


