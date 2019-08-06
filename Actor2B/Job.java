// (C) OntoOO Inc 2005 Apr
package actor2B;

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

// add other subclasses here

/*
class DoSetTile extends Job {
    private int p = 0; private int q = 0; private int k = 0;
    private int i0 = 0; private int j0 = 0; 

    private Arr arr = null;
    DoSetTile(Actor actor, Arr arr, int pi, int qj, int k) { 
	super(actor); 
	this.arr = arr;
	this.p = pi; this.q = qj; this.k = k;
	i0 = 3 * arr.getIindex(); 
	j0 = 3 * arr.getJindex(); 
        priority = 11;
    }
    public boolean execute() {
	System.out.println("block  i " + i0 + " j " + j0 + " k " + k);
	arr.getTile(p, q).setVal(k);
	return false;
    } // end execute()

    public String ascii() { return "DoSetTile " +  (i0 + p) + " " + (j0 + q); }

} // end DoSetTile 
*/
