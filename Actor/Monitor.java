// File: c:/ddc/Java/Actor/Monitor.java
// (C) OntoOO Inc 2005 Apr
package actor;

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
		      (Task)taskQueue.poll() : 
		      actorMonitored.getNullTask() );
	    }
	    actorMonitored.setTaskFocus(task0);
	    task0.start();
	    actorMonitored.addTrace
		("MonitorTaskFocus: new task= " + task0.getJobClassName());
	}
    } // end check()
} // end MonitorTaskFocus

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
	if ( 0 == zeroCnt ) {
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


class MonitorRow extends Monitor {
    private int i = 0;
    public MonitorRow (Actor actor, int i) { 
	super(actor); 
	this.i = i;
    }
    public void check() {
	if ( stopped ) return;
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.addTrace("MonitorRow: checking row: " + i);
	int zeroCnt = actor.getSquare().getRow(i).getZeroCnt();
	if ( 0 == zeroCnt ) {
	    stopped = true;
	    myRunMonitor.stop();
	    return;
	}
	if ( 1 == zeroCnt ) {
	    actor.row1(i);
	    return;
	}
	if ( 2 == zeroCnt ) {
	    actor.row2(i);
	    return;
	}
    } // end check()

} // end MonitorRow


class MonitorCol extends Monitor {
    private int i = 0;
    public MonitorCol (Actor actor, int i) { 
	super(actor); 
	this.i = i;
    }
    public void check() {
	if ( stopped ) return;
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.addTrace("MonitorCol: checking col: " + i);
	int zeroCnt = actor.getSquare().getCol(i).getZeroCnt();
	if ( 0 == zeroCnt ) {
	    stopped = true;
	    myRunMonitor.stop();
	    return;
	}
	if ( 1 == zeroCnt ) {
	    actor.col1(i);
	    return;
	}
	if ( 2 == zeroCnt ) {
	    actor.col2(i);
	    return;
	}
    } // end check()

} // end MonitorCol


class MonitorArr extends Monitor {
    private int p = 0; private int q = 0;
    public MonitorArr (Actor actor, int p, int q) { 
	super(actor); 
	this.p = p;
	this.q = q;
    }
    public void check() {
	if ( stopped ) return;
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.addTrace("MonitorArr: checking arr: " + p + " " + q );
	int zeroCnt = actor.getSquare().getArr(p, q).getZeroCnt();
	if ( 0 == zeroCnt ) {
	    stopped = true;
	    myRunMonitor.stop();
	    return;
	}
	if ( 1 == zeroCnt ) {
	    actor.arr1(p, q);
	    return;
	}
	if ( 2 == zeroCnt ) {
	    actor.arr2(p, q);
	    return;
	}
    } // end check()

} // end MonitorArr

/* There are 81 instances created (9 subarrays and for each subarray the values 1-9).
   A monitor will kill it self if the zeroCnt for the subarray is zero or if the 
   k-value has been assigned already in the initial array, or when it finds the 
   unique tile that will get the k-value.
 */
class MonitorArrBlock extends Monitor {
    private int p = 0; private int q = 0; private int k = 0;
    private Arr arr = null;
    private int i0 = 0; private int j0 = 0; 
    public MonitorArrBlock (Actor actor, int p, int q, int k) { 
	super(actor); 
	this.p = p;
	this.q = q;
	this.k = k;
	Square square = actor.getSquare();
	arr = square.getArr(p, q);
	i0 = 3 * arr.getIindex(); 
	j0 = 3 * arr.getJindex(); 
    }
    public void check() {
	if ( stopped ) return;
	check0();
	Actor actor = (Actor) actorMonitored;
	// actor.addTrace("MonitorArrBlock: checking arrState: " + p + " " + q ); 
	if ( 0 == arr.getZeroCnt() ) {
	    stopped = true;
	    myRunMonitor.stop();
	    return;
	}
	if ( arr.inRegion(k) ) {
	    stopped = true;
	    myRunMonitor.stop();
	    return;
	}
	boolean [][] tileBools = new boolean[3][3];
	arr.block(k, i0, j0, tileBools);
	int falseCnt = 0;
	int pi = 0; int qj = 0;
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) 
		if ( !tileBools[p][q] ) {
		    falseCnt++;
		    pi = p; qj = q;
		} 
	if ( 1 == falseCnt ) { // success!!!
	    actor.addTrace("MonitorArrBlock: zCnt: " + 
			   actor.getSquare().getZeroCnt() + " " +
			   pi + " " + qj + " -> " + k);
	    actor.setTile(arr, pi, qj, k);
	    stopped = true;
	    myRunMonitor.stop();
	}
    } // end check()

} // end MonitorArrBlock
