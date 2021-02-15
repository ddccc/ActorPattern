// File: c:/ddc/Java/Actor3/Monitor.java
// (C) OntoOO Inc 2005 Apr

package actor3;

import java.util.*;
import java.io.*;

// A monitor delegates its periodic execution to its RunMonitor

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

    /* A state can register a down stream effect; if used, check() can do:
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
    abstract public void check(); // This has the meat of a monitor
    protected int lastCnt = 0; 
    public int getLastCnt() { return lastCnt; }
    protected int previousCnt = 0; 

    protected void check0() { 
	// This is just default functionality
	previousCnt = lastCnt;
	lastCnt = actorMonitored.getCnt();
    } // end check

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

class MonitorBestNodeAvailable extends Monitor {
    // This monitor tries to solve the current square and when done
    // triggers monitorCheck through bestNodeAvailable
    public MonitorBestNodeAvailable (ActorBase actor, State bestNodeAvailable) { 
	super(actor, bestNodeAvailable); 
	// state = bestNodeAvailable;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	Square square = actor.getSquare();
	if ( null != square ) {
	    actor.clearSquare();
	    actor.setBestNode(square);
	    System.out.println();
	    actorMonitored.addTrace
		("MonitorBestNodeAvailable null != square: workCnt: " + 
		 actor.getWorkCnt() + " zeroCnt " + square.getZeroCnt() );
	    // square.show();
	    /*
	      solve() is the human tactics solver, there are 3 possibilities:
	      -- all tiles assigned but no solution, because a wrong 
	         assignment was made in Actor.expandNode()
	      -- all tiles assigned and a solution
	      -- some tiles are still unassigned
	    */
	    actor.trySolve(square, state);
	} else { // cannot happen with a legal puzzle
	    actorMonitored.addTrace
		("MonitorBestNodeAvailable fails +++++++++++");
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	 
	    myRunMonitor.stop();
	}
    } // end check()
} // end MonitorBestNodeAvailable

class MonitorCheck extends Monitor {
    // This monitor checks the current state of the square and 
    // sets the downstream state bestNodeCheckedOK with true when no 
    // contradiction was found or else with false, and triggers
    // monitorCheckFinished, monitorCheckFailed & monitorCheckSuccess
    public MonitorCheck (ActorBase actor, State bestNodeChecked) { 
	super(actor, bestNodeChecked); 
	// state = bestNodeChecked;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Square square = actor.getBestNode();
	if ( square.check() ) { // no contradiction and 0 <= zeroCnt
	    actorMonitored.addTrace("MonitorCheck: true");
	    state.setTrue();
	}
	else { // invalid solution
	    actorMonitored.addTrace("MonitorCheck: false");
	    state.setFalse();
	}
	state.wakeUp();
    } // end check()
} // end MonitorCheck

class MonitorCheckFinished extends Monitor {
    // This monitor checks in case of no contradiction whether a solution
    // was found (zeroCnt is zero); and if so triggers a termination
    public MonitorCheckFinished (ActorBase actor) { 
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State bestNodeChecked = actor.getBestNodeChecked();
	if ( !bestNodeChecked.bool() ) return;
	Square square = actor.getBestNode();
	if ( 0 == square.getZeroCnt() ) {
	    actorMonitored.addTrace
		("MonitorCheckFinished SUCCEEDS !!!!!!!!!!");
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	 
	    myRunMonitor.stop();
	}
    } // end check()
} // end MonitorCheckFinished

class MonitorCheckFailed extends Monitor {
    // This monitor recognizes a dead-end, fetches another open node and
    // triggers  monitorBestNodeAvailable via bestNodeSet
    public MonitorCheckFailed (ActorBase actor, State bestNodeS) { 
	super(actor, bestNodeS); 
	// state = bestNodeS;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State bestNodeChecked = actor.getBestNodeChecked();
	if ( bestNodeChecked.bool() ) return;
	Square square = actor.getBestNode();
	actorMonitored.addTrace
	    ("MonitorCheckFailed *** zeroCnt " + square.getZeroCnt());
	actor.addFailedNode(square);
	actor.clearSquare();
	actor.setSquare(); // fetch a node from the open nodes
	state.wakeUp();
    } // end check()
} // end MonitorCheckFailed

class MonitorCheckSuccess extends Monitor {
    // This monitor recognizes that the node must be expanded, 
    // which adds to the collection of open nodes, fetches another 
    // open node and triggers  monitorBestNodeAvailable via bestNodeSet
    public MonitorCheckSuccess (ActorBase actor, State bestNodeS) { 
	super(actor, bestNodeS); 
	// state = bestNodeS;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State bestNodeChecked = actor.getBestNodeChecked();
	if ( !bestNodeChecked.bool() ) return;
	Square square = actor.getBestNode();
	int zeroCnt = square.getZeroCnt();
	if ( 0 == zeroCnt ) {
	    myRunMonitor.stop();
	    return;
	}
	actorMonitored.addTrace("MonitorCheckSuccess !! zeroCnt: " + zeroCnt);
	/* delete state expandNode, 
	   delete MonitorExpandNode
	   add here : actor.expandNode(square);
	 */
	actor.expandNode(square);
	actor.clearSquare();
	actor.setSquare(); // fetch a node from the open nodes
	state.wakeUp();
    } // end check()
} // end MonitorCheckSuccess

