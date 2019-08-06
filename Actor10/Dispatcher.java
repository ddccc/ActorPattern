// File: c:/ddc/Java/Actor10/Dispatcher.java
// Date: Wed Apr 25 15:38:29 2018

package actor10;

import java.util.*;
import java.io.*;
import fol.*;

// The classes Alert & Dispatcher have been changed in 2018

public abstract class Dispatcher extends Job {
    /**
       An instance of a subclass of Dispatcher is a job that decides 
       whether an alert is ignored or not.  The mechanism available is 
       doing a limited number of resolutions in a connection graph 
       theorem prover - which lives in the fol package.

       When the alert is honored an action, pre-specified in the alert,
       will be launched as another job.

       {Note: In a more advanced approach the alert would not contain a 
       pre-specified action but instead just a goal to be achieved.  
       A planning operation would be required to construct the sequence 
       of actions to be subsequently executed.}

       Making launch decisions in terms of predicate calculus reasoning is 
       essentially different from what goes on in the sensory realm where 
       low level states are observed by monitors, which can impact states 
       in turn, or - after data enrichments - can produce alerts.

       Instances of subclasses are created in ActorBase by 
       dispatchAlert(Alert alert)
       
       @see ActorBase
     */

    public Dispatcher(ActorBase ab, Formula t) {
	super(ab);
	trigger = t;
    }

    protected Alert alert = null; 
    protected Formula trigger = null;
    protected Theory theory = null;
    protected Job job = null;

    // public Dispatcher() {}
    protected void setAlert(Alert alert) { 
	this.alert = alert; 
    }
    protected void setJob(Job j) { 
	job = j;
    }
    protected Job getJob() { return job; }

    protected Formula getTrigger() { return trigger; } 

    protected void setTheory(Theory theory) { this.theory = theory; }
    protected int resolveMax = 5;
    protected int resolveCnt = 0;
    abstract public void init();
    // abstract public boolean launchQ(Theory alertEvents, Atom trigger);
    public String ascii() { return this.getClass().getName(); }

} // end Dispatcher


class CheckNode extends Dispatcher {
    private Actor myActor = null;
    private Atom myTrigger = null;
    private Node nc = null;
    private Theory theory = null; 
    private Parser parser = null;
    private State state = null;
    public CheckNode(Actor ac, Formula t, Node nodeCandidate, State againS) {
	super(ac, t);
	myActor = (Actor)actor;
	// myTrigger = (Atom) trigger;
	nc = nodeCandidate;
	parser = myActor.getParser();
	state = againS;
	theory = new Theory(false);
    }

    public void init() { priority = 8; }
    // trigger ~=~ Input(abcd)
    public boolean execute() { 
	// modify/extend with model checking of the trigger
	if ( myActor.problemSolvedQ() ) return false; 
	// System.out.println("CheckNode Dispatcher Entering for: " +
	//		   "node: " + nc.getMyCnt());
	
	Grid grid = nc.getGrid();
	if ( grid.isSolution() ) {
	    myActor.setProblemSolved();
	    myActor.addTrace("CheckNode:: Found solution");
	    System.out.println("||||||||||||| SOLUTION found " +
			       "openNodes #: " + myActor.numberOfOpenNodes());
	    nc.printSolution();
	    StopAngel stopAngel = new StopAngel(myActor);
	    stopAngel.start();	 
	    return false;
	}
	
	// just launch without further deliberation::
            System.out.println("||||||||||||| CheckNode launch node # " +  
			       nc.getMyCnt() );
	    myActor.addAction(job);
	    myActor.wakeUp();
	    alert.setLaunched(true);
	    return false;
	
	/* // not using any deliberation
	Vector foundAssertions = nc.getFoundAssertions();
	int lng = foundAssertions.size();
	// lng = 0;
	if ( 0 == lng ) { // skip testing
	    myActor.addNode(nc); // key map
            System.out.println(" ||||||||||||| CheckNode launch");
	    myActor.addAction(job);
	    myActor.wakeUp();
	    alert.setLaunched(true);
	    return false;
	}
	Formula factsF = grid.getFactsF();
	theory.addAxiom(factsF);
	System.out.println("CheckNode Dispatcher Axiom Entered");

	for (int i = 0; i < lng; i++) {
	    Atom at = Symbol.UNKNOWN;
	    String atS = (String) foundAssertions.elementAt(i);
	    try { at = (Atom) parser.parse(atS); }
	    catch ( Exception pe ) {
		String messg = "CheckNode.execute() Parser Error of: " + atS;
		System.out.println(messg);
		System.exit(0);
	    }
	    theory.addAssertion(at);
	}
	System.out.println("CheckNode Dispatcher pre-proof");
	// System.out.println();

	myActor.addTrace("Trigger: " + trigger.html());
	ProofStep ps = theory.prove(trigger, 5);
	myActor.addTrace("ps: " + ps.html());
	myActor.addTrace("ps: " + ps.getResult().html());
	if ( Symbol.TRUE == ps.getResult() ) {
	    // theory.addAssertion(myTrigger);
	    System.out.println("CheckNode Dispatcher proof OK " +
			       " Blocking node " + nc.getMyCnt());
	    // state.wakeUp();
	    return false;
	}
	// myActor.alertEvents.addAssertion(myTrigger);
	// Job job = alert.getJob();
	System.out.println("CheckNode Dispatcher proof fails");

	myActor.addNode(nc); // key map
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false;
	*/
    }
} // end CheckNode



