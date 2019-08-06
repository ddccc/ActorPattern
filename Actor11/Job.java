// File: c:/ddc/Java/Actor11/Job.java
// Date: Sun Mar 18 18:14:25 2018
// (C) OntoOO/ Dennis de Champeaux

package actor11;

import java.util.*;
import java.io.*;

import fol.Formula;

// Job is the super class for the utility task NullJob, ShowAlertTrace &
// for Dispatcher and its subclasses, as well as for the classes 
// that capture actions specified inside dispatchers; see the example below.  
public abstract class Job {
    static final protected int updateInterval = 200; // 0.1 secs
    protected ActorBase actor = null;
    public ActorBase getActor() { return actor; }
    protected int priority = 5;
    public void setPriority(int i) { priority = i; }
    public int getPriority() { return priority; }
    protected String actorName = "unknown";
    public Job() {}
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
	return false; // end job
    } // end execute()

    public String ascii() { return "ShowAlertTrace"; }
} // end ShowAlertTrace 

// add other subclasses here:

class DoGoal1 extends Job {
    private String goal1 = null;
    private int cnt = 0;
    private Actor myActor = null;
    DoGoal1(Actor actor, String g, int c) {
	super(actor); 
	goal1 = g;
	cnt = c;
        priority = 14;
	myActor = actor;
    } 
    public boolean execute() {
	cnt--;
	myActor.addTrace("DoGoal1 cnt: " + cnt +
			 " priority: " + priority);
	for (int i = 0; i < 3; i++) {
	    myActor.addTrace("DoGoal1 i: " + i);
	    try { Thread.sleep(100); }
	    catch (InterruptedException ignore) {}
	}
	// if ( 0 == cnt ) return false;
	// Expire2 timer = new Expire2(myActor.getStart1(), 1000, "DoGoal1");
	WaitTask wt = new WaitTask(myActor.getStart1(), 1000);
	return false;
    } // end execute
    public String ascii() { return "DoGoal1"; }
} // end DoGoal1



class DoGoal2 extends Job {
    private String goal2 = null;
    private int cnt = 0;
    private Actor myActor = null;
    DoGoal2(Actor actor, String g, int c) {
	super(actor); 
	goal2 = g;
	cnt = c;
        priority = 13;
	myActor = actor;
    } 
    public boolean execute() {
	cnt--;
	myActor.addTrace("DoGoal2 cnt: " + cnt +
			 " priority: " + priority);
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException ignore) {}
	if ( 0 == cnt ) { 
            myActor.addTrace("Goal2 terminates.....");
	    StopAngel sa = new StopAngel(myActor);
	    sa.start();
	    return false;
	}
	myActor.addTrace("AGAIN DoGoal2 cnt: " + cnt);
	return true;
    } // end execute
    public String ascii() { return "DoGoal2"; }
} // end DoGoal1


/* // Quite simple example:
class DoInString extends Job {
    private String inString = null;
    private Actor myActor = null;
    DoInString(Actor actor, String inString) {
	super(actor); 
	this.inString = inString;
        priority = 11;
	myActor = actor;
    } 
    public boolean execute() {
	// System.out.println("DoInString output: " + inString);
	myActor.addTrace("DoInString output: " + inString);
	return false;
    } // end execute()

    public String ascii() { return "DoInString "; }
} // end DoInString
*/
