// File: c:/ddc/Java/Actor2B/Monitor.java
// (C) OntoOO Inc 2005 Apr / Fri Aug 04 13:45:12 2017

package actor2B;

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

class MonitorGoDown extends Monitor {
    public MonitorGoDown (Actor actor, State testVal) { 
	super(actor, testVal); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	int zeroCnt = actor.getSquareCnt();
	Stack inStack = actor.getInstack();
	// if ( inStack.empty() || 100000 < actor.getWorkCnt() ) {
	if ( inStack.empty()  ) {
	    actorMonitored.addTrace("MonitorGoDown: Finish!! " + 
				    actor.getWorkCnt() + " " + zeroCnt);
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	
	    return;
	}

	Stack outStack = actor.getOutstack();
	outStack.push(inStack.pop());
	// Tile t = (Tile) outStack.peek();
	// actorMonitored.addTrace("MonitorGoDown: i " + t.getI() + " j " + t.getJ());
	state.setIntValue(1); 
	state.wakeUp(); // wakes up monitorTestVal
    } // end check()
} // end MonitorGoDown

class MonitorTestVal extends Monitor {
     private State incrementQ;
     private State goDown;
     public MonitorTestVal (Actor actor, State testVal) { 
	super(actor, testVal); 
	incrementQ = actor.getIncrementQ();
	goDown = actor.getGoDown();
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Stack outStack = actor.getOutstack();	
	/* // testing
	  Tile t = (Tile) outStack.peek();
	  actorMonitored.addTrace("MonitorTestVal: i " + 
				  t.getI() + " j " + t.getJ() + " v " + t.getVal()); }
	*/
	int val = state.getIntValue();
	Tile t = (Tile) outStack.peek();
	Seq row = t.getRow();
	Seq col = t.getCol();
	Arr arr = t.getArr();
	// check that val is acceptable in this tile:
	boolean notOK = row.inRegion(val) || col.inRegion(val) || arr.inRegion(val);
	if ( notOK ) {
	    //	addTrace("testTile1 : i " + t.getI() + " j " + t.getJ() + " v " + val);
	    // state.setStringValue("incrementQ");
	    incrementQ.wakeUp();
	    return;
	}
	//   addTrace("testTile2 : i " + t.getI() + " j " + t.getJ()+ " v " + val +
	//	     " workCnt " + workCnt);
	t.setVal(val); // commit to this value, for now 
	goDown.wakeUp(); 
    } // end check()
} // end MonitorTestVal

class MonitorIncrementQ extends Monitor {
    private State goUp;
    public MonitorIncrementQ (Actor actor, State testVal) { 
	super(actor, testVal); 
	goUp = actor.getGoUp();
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	int val = state.getIntValue();
	// Tile t = (Tile) outStack.peek();
	// addTrace("incrementQ : i " + t.getI() + " j " + t.getJ() + " v " + val);
	if ( 9 <= val ) { // need to backtrack
	    // state.setStringValue("goUp");
	    goUp.wakeUp();
	    return;
	} 
	state.setIntValue(++val); // try next value
	// if ( 99990 < workCnt )
	//   addTrace("incrementQx : i " + t.getI() + " j " + t.getJ() + " v " + val);
	state.wakeUp(); 
    } // end check()
} // end MonitorIncrementQ

class MonitorGoUp extends Monitor {
    private State testVal;
    public MonitorGoUp (Actor actor, State incrementQ) { 
	super(actor, incrementQ);
	testVal = actor.getTestVal();
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Stack outStack = actor.getOutstack();
	if ( outStack.empty() ) {
	    actorMonitored.addTrace("MonitorGoUp: Fail!!");
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	 
	    return;
	}
	Stack inStack = actor.getInstack();
	inStack.push(outStack.pop());
	Tile t = (Tile) outStack.peek();
	int val = t.getVal();
	t.setVal(0); // unassign again
	testVal.setIntValue(val); // next value up will be tried
	state.wakeUp(); // wakes up monitorIncrementQ
    } // end check()
} // end MonitorGoUp


class MonitorCheckActions extends Monitor {
    public MonitorCheckActions (ActorMeta actor, State work) { 
	super(actor, work);
    }
    public void check() {
	check0();
	ActorMeta mactor = (ActorMeta) actorMonitored;
	int newWorkCnt = mactor.getActor().getWorkCnt();
	if ( newWorkCnt == state.getIntValue() ) {
	    state.setTrue();
	    state.wakeUp();
	} else 
	    state.setIntValue(newWorkCnt);
    } // end check()
} // end MonitorCheckActions

class MonitorCheckProgress extends Monitor {
    public MonitorCheckProgress (ActorMeta actor) { 
	super(actor);
    }
    public void check() {
	check0();
	ActorMeta mactor = (ActorMeta) actorMonitored;
	Actor actor = mactor.getActor();
	int squareZeroCnt = actor.getSquareCnt();
	int workCnt = actor.getWorkCnt();
	State work = mactor.getWork();  
	if ( work.bool() ) {
	    work.setFalse();
	    // State nextAction = mactor.getNextAction();
	    // nextAction.wakeUp();
	    mactor.addTrace("*********** WakeUp squareZeroCnt: " + squareZeroCnt + 
			    //	 " noChangeCnt: " + noChangeCnt +
			 " workCnt: " + workCnt);
	}

    } // end check()
} // end MonitorCheckActions

