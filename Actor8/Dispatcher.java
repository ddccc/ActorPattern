// (C) OntoOO Inc 2018 Jan
package actor8;

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



class EvalCandidate extends Dispatcher {
    private Actor myActor = null;
    private String candidate;
    public EvalCandidate(Actor ac, Formula t, String can) {
	super(ac, t);
	myActor = (Actor)actor;
	candidate = can;
    }
    public void init() {
	priority = 10;
	// myActor = (Actor)actor;
    }
    public boolean execute() {
	/* Not calling the theorem prover !
	ProofStep ps = theory.prove(trigger, 5);
	// System.out.println("ps: " + ps.html());
	// System.out.println("ps: " + ps.getResult().html());
	if ( Symbol.TRUE == ps.getResult() ) {
	    // System.out.println("***** InsertNode refuse new node");
	    return false;
	}
	// System.out.println("***** InsertNode inserting new node");
	*/
	/* Instead call an ad hoc boolean procedure, store the
	   outcome in a state and use an action that triggers a 
           wakeUp of that state and thereby an associated monitor. 
	 */
	myActor.addTrace("EvalCandidate.execute: " + candidate);
	boolean b = myActor.evaluateCandidate(candidate);
	State state = myActor.getCandidateEvaluated1();
	if (b) state.setTrue(); else state.setFalse();
	// execute job 
	// System.out.println("***** EvalCandidate inserting new alert");
	// Job job = alert.getJob();
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false;
    } // end execute

} // end EvalCandidate

class InviteCandidate extends Dispatcher {
    private Actor myActor = null;
    public InviteCandidate(Actor ac, Formula t) {
	super(ac, t);
	myActor = (Actor)actor;
    }
    public void init() {
	priority = 10;
	// myActor = (Actor)actor;
    }
    public boolean execute() {
	String candidate = myActor.getCandidateChecked();
	// trivial proof here::
	ProofStep ps = theory.prove(trigger, 5);
	// System.out.println("ps: " + ps.html());
	// System.out.println("ps: " + ps.getResult().html());
	if ( Symbol.TRUE != ps.getResult() ) {
	    System.out.println("***** InviteCandidate fails");
	    myActor.addRejectedCandidate(candidate);
	    myActor.setCandidateChecked(null);
	    myActor.inviting = false;
	    State noCandidate = myActor.getNoCandidate();
	    noCandidate.wakeUp();
	    return false;
	}
	System.out.println("***** InviteCandidate approved");
	// execute job 
	// System.out.println("***** EvalCandidate inserting new alert");
	// Job job = alert.getJob();
	myActor.addAction(job);
	myActor.wakeUp();
	alert.setLaunched(true);
	return false;
    } // end execute

} // end InviteCandidate
