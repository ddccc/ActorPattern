// File: c:/ddc/Java/Actor8/Job.java
// (C) OntoOO Inc 2005 Apr

package actor8;

import java.util.*;
import java.io.*;

import fol.Formula;

public abstract class Job {
    static final protected int updateInterval = 200; // 0.2 secs
    protected ActorBase actor = null;
    public ActorBase getActor() { return actor; }
    protected int priority = 3;
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
	priority = 2;
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
	StringBuffer sb = 
	    new StringBuffer("AnalyzeAlertTrace: <b>alerts:</b>");
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

// add other subclasses here

/*
class DoInsertNode extends Job {
    Actor myActor = null;
    Node n = null;
    DoInsertNode(Actor actor, Node ny) {
	super(actor); 
	n = ny;
        priority = 11;
	myActor = (Actor) actor;
    } 
    public boolean execute() {
	System.out.println("DoInsertNode ... ...");
	myActor.insertOpenNode(n);
	myActor.getBestNodeS().wakeUp();
	return false;
    } // end execute()
    public String ascii() { return "DoInsertNode"; }
} // end DoInsertNode
*/


class DoEvalCandidate extends Job {
    private Actor myActor = null;
    private String candidate;
    private State state;
    // state = candidateEvaluated1
    DoEvalCandidate(Actor actor, String candidate, State state) {
	super(actor);
	myActor = (Actor) actor;
	this.candidate = candidate;
	this.state = state;
    }
    public boolean execute() {
	myActor.addTrace("DoEvalCandidate: candidate: " + candidate +
			 " " + state.bool());
	// just trigger the monitors associated with candidateEvaluated1
	state.wakeUp();
	return false;
    } // end execute()
    public String ascii() { return "DoInsertNode"; }

} // end DoEvalCandidate

class DoInviteCandidate extends Job {
    Actor myActor = null;
    private String candidate;
    private int query;
    private State state;
    // state = wait1
    DoInviteCandidate(Actor actor, String candidate, int query, State state) {
	super(actor);
	myActor = (Actor) actor;
	this.candidate = candidate;
	this.query = query;
	this.state = state;
    }
    public boolean execute() {
	myActor.addTrace("DoInviteCandidate: candidate: " + candidate +
			 " query: " + query);
	// setup waiter
	State wait1 = myActor.getWait1();
	myActor.setWaitTask(new WaitTask(wait1, 3000));

	// do the invite
	Invitation in = new Invitation(myActor.getName(), candidate, query);

	Message.relayInvite(in);
	return false;
    } // end execute()
    public String ascii() { return "DoInsertNode"; }

} // end DoInviteCandidate
