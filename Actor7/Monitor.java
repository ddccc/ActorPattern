// File: Thu Aug 02 08:59:01 2018
// (C) OntoOO Inc 2005 Apr

package actor7;

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
	// state = bestNodeAvailable
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	Node nx = actor.getBestNode();
	if ( null == nx )  { // cannot happen 
	    /*
	    actorMonitored.addTrace
		("MonitorBestNodeAvailable fails +++++++++++");
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	 
	    myRunMonitor.stop();
	    */
	    return;
	}
	if ( nx.isGoal() ) {
	    actor.report();
	    nx.printSolution();
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	 
	    myRunMonitor.stop();
	    return;
	}
	// actor.addClosedNodes(nx);
	System.out.print("\nExpanding : ");
	nx.printNode();
	// actor.expandNode(nx);
	actor.bestNode = nx;
        state.wakeUp();
    } // end check()
} // end MonitorBestNodeAvailable


class MonitorExpandNode extends Monitor {
    // Monitor expand the best node and generates to alerst
    public MonitorExpandNode (ActorBase actor) { 
	super(actor); 
	// state = bestNodeS;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Node nx = actor.bestNode;
	if ( null == nx ) return;
	Node ny;
	if ( nx.getBoat() ) {
	    ny = nx.moveRight(1, 0); actor.checkNode(ny);
	    ny = nx.moveRight(0, 1); actor.checkNode(ny);
	    ny = nx.moveRight(1, 1); actor.checkNode(ny);
	    ny = nx.moveRight(2, 0); actor.checkNode(ny);
	    ny = nx.moveRight(0, 2); actor.checkNode(ny);
	} else {
	    ny = nx.moveLeft(1, 0); actor.checkNode(ny);
	    ny = nx.moveLeft(0, 1); actor.checkNode(ny);
	    ny = nx.moveLeft(1, 1); actor.checkNode(ny);
	    ny = nx.moveLeft(2, 0); actor.checkNode(ny);
	    ny = nx.moveLeft(0, 2); actor.checkNode(ny);
	}
	// state.wakeUp();
    } // end check()



} // end MonitorExpandNode
