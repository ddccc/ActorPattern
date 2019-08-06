// File: c:/ddc/Java/Actor5/Monitor.java
// (C) OntoOO Inc 2005 Apr

package actor5;

import java.util.*;
import java.io.*;


public abstract class Monitor  {
    protected int previousInt = 0;
    protected float previousFloat = 0;
    protected String previousString = "";
    protected ActorBase actorMonitored = null;
    protected boolean stopped = false;
    protected RunMonitor myRunMonitor = null;
    
    protected void setMyRunMonitor(RunMonitor rm) {
	myRunMonitor = rm;
    }

    /*  state can register a down stream effect; if used, check() can do:
	state.wakeUp() to notify other monitors that depend on state
    */
    protected State state = null; 

    public Monitor (ActorBase actor, State state) { 
	actorMonitored = actor; 
	this.state = state;
    }
    public Monitor (ActorBase actor) { this(actor, null); }

    public ActorBase getActorMonitored() { return actorMonitored; }
    /* RunMonitor calls check() periodically.
       A state can also trigger check() to run via the wakeUp() feature
       in RunMonitor, as mentioned above
    */
    abstract public void check(); 
    protected int lastCnt = 0; 
    public int getLastCnt() { return lastCnt; }
    protected int previousCnt = 0; 

    protected void check0() {
	previousCnt = lastCnt;
	lastCnt = actorMonitored.getCnt();
    }

} // end Monitor

/* There is at most one task running, which is referred to in Actor by the taskFocus
   This monitor keeps an eye on the focus.  If no task is running it restarts the
   nulltask when the taskList is empty, otherwise it grabs the first task.
*/
class MonitorTaskFocus extends Monitor {
    public MonitorTaskFocus (ActorBase actor) { super(actor); }
    public void check() {
	check0();
	// actorMonitored.addTrace("MonitorTaskFocus: checking task focus ....");
	Task taskFocus = actorMonitored.getTaskFocus();
	if ( null == taskFocus ) {
	    PriorityQueue taskQueue = actorMonitored.getTaskQueue();
	    Task task0;
	    synchronized ( actorMonitored ) {
		task0 = 
		    ( 0 < taskQueue.size() ?
		      (Task) taskQueue.poll() :
		      actorMonitored.getNullTask() );
	    }
	    actorMonitored.setTaskFocus(task0);
	    task0.start();
	    actorMonitored.addTrace
		("MonitorTaskFocus: new task= " + task0.getJobClassName());
	}
    } // end check()
} // end MonitorTaskFocus


class MonitorInputReady extends Monitor {
    public MonitorInputReady (Actor actor) { 
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State state = actor.getInputAvailable();
	String inString = state.getStringValue();
	System.out.println("MonitorInputReady inString: " + inString);
	actor.inputIs(inString); // create alert
	
    } // end check()
} // end MonitorInputReady

class MonitorTerminateQ extends Monitor {
    public MonitorTerminateQ (ActorMeta actor) { 
	super(actor); 
    }
    public void check() {
	check0();
	ActorMeta actor = (ActorMeta) actorMonitored;
	State state = actor.getInputAvailable();
	String inString = state.getStringValue();
	if ( !inString.equals("stop") ) return;
	/*
	StopAngel stopAngel = new StopAngel(actor);
	stopAngel.start();
	try { Thread.sleep(10000); }
	catch (InterruptedException ignore) {}
	*/
	System.out.println("System exit...");
	System.exit(0);
    } // end check()
} // end MonitorTerminateQ
