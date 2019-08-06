// File: c:/ddc/Java/Actor6/Monitor.java
// (C) OntoOO Inc Sat Aug 12 21:22:08 2017

package actor6;

import java.util.*;
import java.io.*;
import fol.*;  // 1st order logic theorem prover

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


class MonitorInputAtom extends Monitor {
    public MonitorInputAtom (Actor actor, State inputAvailable) { 
	super(actor, inputAvailable); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Formula conjecture = actor.getConjecture();
	if ( !(conjecture instanceof Atom) ) return;
	Atom atomConjecture = (Atom) conjecture;
	System.out.println("MonitorInputAtom: " + 
			   atomConjecture.html() );
	if ( atomConjecture.equals(Symbol.TRUE) || 
	     atomConjecture.equals(Symbol.FALSE) || 
	     atomConjecture.equals(Symbol.UNKNOWN) ) {
	    System.exit(0);
	} 
	Theory theory = actor.getTheory();
	Symbol predicate = atomConjecture.getPredicate();
	// System.out.println("name: " + predicate.getName());
	Equivalence eq = theory.getDefinition(predicate);
	// System.out.println("eq: " + eq.html());
	if ( null != eq ) { // will expand this 
	    Atom atomDef = (Atom) eq.getLeft();
	    Formula target = 
		theory.replaceAtom(predicate, atomDef.getArgs(),
		     eq.getRight(), atomConjecture);
	    /*
	    ProofStep ps = prove(target, maxSteps - 1);
		return new ProofStepDefinition(atomConjecture,
					       ps.getResult(),
					       ps);
	    */
	    actor.addToStack(conjecture);
	    actor.setConjecture(target);
	    state.wakeUp();
	    return;
	}
	// Try connection graph?
	System.out.println("MonitorInputAtom: Try connection graph?");

    } // end check()
} // end MonitorInputAtom

class MonitorInputNotAtom extends Monitor {
    public MonitorInputNotAtom (Actor actor, State inputAvailable) { 
	super(actor, inputAvailable); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Formula conjecture = actor.getConjecture();
	if ( (conjecture instanceof Atom) ) return;
	System.out.println("MonitorInputNotAtom: " + 
			   conjecture.html() );
	Formula conjecture2 = Formula.insurer(conjecture);
	System.out.println("MonitorInputNotAtom: " + 
			   conjecture2.html() );
	if ( conjecture2 instanceof Atom ) {
	    actor.addToStack(conjecture);
	    actor.setConjecture(conjecture2);
	    state.wakeUp();
	    return;
	}
	Theory theory = actor.getTheory();
	Symbol predicate = theory.containsDefinition(conjecture2);
	if ( null != predicate ) {
	    System.out.println("predicate: " + predicate.html());
	    Equivalence eq = theory.getDefinition(predicate);
	    Atom atomDef = (Atom) eq.getLeft();
	    Formula conjecture3 = theory.replaceAtom(
		     predicate, atomDef.getArgs(),
		     eq.getRight(), conjecture2);
	    actor.addToStack(conjecture);
	    actor.setConjecture(conjecture3);
	    state.wakeUp();
	    return;
	}
	// Try connection graph?
	System.out.println("MonitorInputNotAtom: Try connection graph?");
    } // end check()
} // end MonitorInputNotAtom


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
