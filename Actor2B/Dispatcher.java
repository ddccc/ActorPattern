// File: c:/ddc/Java/Actor2B/Dispatcher.java
// (C) OntoOO Inc 2005 Apr

package actor2B;

import java.util.*;
import java.io.*;
import fol.*;

// The classes Alert & Dispatcher have been changed in 2018

public abstract class Dispatcher extends Job {
    /**
       An instance of a subclass is a job that decides whether an alert 
       is ignored or not.  The mechanism is doing a limited number of 
       resolutions in a connection graph theorem prover - which lives in the 
       fol package.

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

// add subclasses here



	/* We could try proving that the tile is still zero with:
	cg = theory.setUpCGforConjecture(trigger); 
        The formula "SetTile(309 2 9)" would need a procedural attachment 
        that checks whether Tile(2, 9) == 0, see below
  	 */

/*
class SetTile extends Dispatcher {
    private Actor myActor = null;
    private Atom myTrigger = null;
    public SetTile(Actor ac, Atom t) {
	super(ac, t);
	myActor = (Actor)actor;
	myTrigger = (Atom) trigger;
    }

    // trigger ~=~ SetTile(309 2 9)

    //       An instance will simply launch the job without deliberation
    public void init() {
	priority = 10;
    }

    public boolean execute() {
	actor.addTrace("SetTile: execute(): *DO* launch trigger = " + trigger.html());
	Vector triggerArgs = myTrigger.getArgs();
	IntSymbol xs = (IntSymbol) triggerArgs.elementAt(1);
	IntSymbol ys = (IntSymbol) triggerArgs.elementAt(2);
	int x = xs.getValue();
	int y = ys.getValue();
	Square square = myActor.getSquare();
	Tile txy = square.getTile(x, y);
	if ( 0 != txy.getVal() ) {
	    System.out.println("^^^^^^^^^^^^^ Tile " + x + " " + y + " is set ^^^^^^^^^^^");
            return false; // terminate this task
	}

	// Atom newAssertion = trigger;
	// Job job = alert.getJob();
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false; // terminate this task
    } // end execute

} // end  SetTile
*/


