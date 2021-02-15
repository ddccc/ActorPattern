// File: c:/ddc/Java/Actor11/Monitor.java
// Date: Sat Sep 15 21:09:04 2018
// (C) OntoOO/ Dennis de Champeaux

package actor11;

import java.util.*;
import java.io.*;


public abstract class Monitor  {
    protected int previousInt = 0;
    protected float previousFloat = 0;
    protected String previousString = "";
    protected Object previousObject = null;

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
	    actorMonitored.addTrace("MonitorTaskFocus: new task= " + 
				    task0.getJobClassName());
	}
    } // end check()
} // end MonitorTaskFocus


class MonitorGoal1 extends Monitor {
    public MonitorGoal1 (Actor actor) { 
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.startGoal1("goal1", 5);
    } // end check()
} // end  MonitorGoal1

class MonitorGoal2 extends Monitor {
    public MonitorGoal2 (Actor actor) { 
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.startGoal2("goal2", 10);
    } // end check()
} // end  MonitorGoal2


/* // Example::
// This monitor keeps an eye on the zeroCnt; if zero it triggers termination;
// if it has changed it triggers the monitors registered at squareState
class MonitorSquare extends Monitor {
    public MonitorSquare (Actor actor, State squareState, int startZeroCnt) { 
	super(actor, squareState); 
	// state = squareState;
	previousInt = startZeroCnt;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Square square = actor.getSquare();
	int zeroCnt = square.getZeroCnt();
	actorMonitored.addTrace("MonitorSquare: square.zeroCnt: " + zeroCnt);
	if ( 0 == zeroCnt ) { // A solutio is found; actor will terminate.
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	    
	    return;
	}
	if ( zeroCnt < previousInt ) {
	    previousInt = zeroCnt;
	    state.setIntValue(zeroCnt);
	    state.wakeUp();
	}

    } // end check()
} // end MonitorSquare
*/

