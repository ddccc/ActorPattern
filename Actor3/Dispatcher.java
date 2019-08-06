// File: c:/ddc/Java/Actor3/Dispatcher.java
// (C) OntoOO Inc 2005 Apr

package actor3;

import java.util.*;
import java.io.*;
import fol.*;


public abstract class Dispatcher extends Job {
    /**
       An instance of subclass is a job that decides whether an alert 
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

class TrySolve extends Dispatcher {
       // An instance will simply launch the job without deliberation
    private Actor myActor = null;
    private Atom myTrigger = null;
    public TrySolve(Actor ac, Formula t) {
	super(ac, t);
	myActor = (Actor)actor;
	myTrigger = (Atom) trigger;
    }
    public void init() {
	priority = 10;
    }
    public boolean execute() {
	myActor.addTrace("TrySolve: execute(): trigger = " + trigger.html());
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false; // terminate this task
    } // end execute

} // end  TrySolve



