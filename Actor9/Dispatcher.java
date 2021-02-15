// File: c:/ddc/Java/Actor9/Dispatcher.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sun Mar 18 18:00:44 2018

package actor9;

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

class GuessNextValue extends Dispatcher {
    private Actor myActor = null;
    private Atom myTrigger = null;
    public GuessNextValue(Actor ac, Formula t) {
	super(ac, t);
	myActor = (Actor) actor;
	myTrigger = (Atom) trigger;
    }
    public void init() { priority = 10; }
    // trigger ~=~ <(2 100)
    public boolean execute() {
	myActor.addTrace("Trigger: " + myTrigger.html());
	ProofStep ps = theory.prove(myTrigger, 5);
	// myActor.addTrace("ps: " + ps.html());
	myActor.addTrace("ps: " + ps.getResult().html());
	if ( Symbol.TRUE != ps.getResult() ) {
	    // theory.addAssertion(myTrigger);
	    Try.again = false; // stop actor !!!
	    return false;
	}
	// theory.addAssertion(myTrigger); //++
	// myActor.alertEvents.addAssertion(myTrigger);
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false;
    }
} // end GuessNextValue

