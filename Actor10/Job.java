// File: c:/ddc/Java/Actor10/Job.java
// Date: Wed Apr 25 15:41:02 2018
package actor10;

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

class DoInsertNodeCandidate extends Job {
    private Node node = null;
    private State againS;
    private Actor myActor = null;
    DoInsertNodeCandidate(Actor actor, Node n, State s) {
	super(actor); 
	node = n;
	againS = s;
        priority = 8;
    } 
    public boolean execute() {
	myActor = (Actor) actor;
	int myCnt = node.getMyCnt();
	// System.out.println("DoInsertNodeCandidate myCnt " + myCnt);
	myActor.addTrace("DoInsertNodeCandidate myCnt " + myCnt);
        /* // this check is done in the dispatcher phase in checkNode
	if ( node.isSolution() ) {
	    myActor.addTrace("DoInsertNodeCandidate:: Found solution");
	    System.out.println("Found solution # open nodes: " +
			       myActor.numberOfOpenNodes());      
	    node.printSolution();
	    StopAngel stopAngel = new StopAngel(myActor);
	    stopAngel.start();	 
	    return false;
	}
        */
	myActor.insertNode(node);
	againS.wakeUp();
	return false;
    } // end execute()

 public String ascii() { return "DoInsertNodeCandidate"; }
} // end DoInsertNodeCandidate


