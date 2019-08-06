// File: c:/ddc/Java/ActorX/Dispatcher.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sat Sep 15 20:55:59 2018

package actorX;

import java.util.*;
import java.io.*;
import fol.*;

public abstract class Dispatcher extends Job {
    /**
       An instance of a subclass of Dispatcher is a job that decides 
       whether an alert is ignored or not.  The mechanism available is 
       doing a limited number of resolutions in a connection graph 
       theorem prover - which lives in the fol package.

       When the condition in the dispatcher is honored an action,
       pre-specified in the dispatcher, will be launched as another
       job.

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
    public String ascii() { return this.getClass().getName(); }
} // end Dispatcher


/* Example where deliberation is invoked 
class InString extends Dispatcher {
    private Actor myActor = null;
    private Formula myTrigger = null;
    public InString(Actor ac, Atom t) {
	super(ac, t);
	myActor = (Actor)actor;
	myTrigger = trigger;
    }

    public void init() { priority = 10; }
    // trigger ~=~ Input(abcd)
    public boolean execute() {
	myActor.addTrace("Trigger: " + myTrigger.html());
	ProofStep ps = theory.prove(myTrigger, 5);
	myActor.addTrace("ps: " + ps.html());
	myActor.addTrace("ps: " + ps.getResult().html());
	if ( Symbol.TRUE != ps.getResult() ) {
	    theory.addAssertion(myTrigger);
	    return false;
	}
	myActor.alertEvents.addAssertion(myTrigger); // only if an atom !! 
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false;
    }
} // end InString
*/
