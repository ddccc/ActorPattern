// File: c:/ddc/Java/ActorX/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sun Mar 18 17:01:54 2018

package actor11;

import java.util.*;
import java.io.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    /* 
      // parser: text -> Formula 
      private Parser parser = new Parser(false); // true for tracing
      public Parser getParser() { return parser; }
      // deduction infrastructure
      private Theory theory = new Theory(false); // true for tracing
      public Theory getTheory() { return theory; };
    */ 

    // Application specific items here

    // A sequence of state+monitor pairs produce enough info 
    // in the last monitor to generate an alert.
    // An alert can contain a test (an instance of a subclass of 
    // Dispatcher) and an action an instance of the class Job   

    // states here: an example:
         // private State againS = new State(); // go fetch a node 
    private State start1 = new State(); // for starting MonitorGoal1
    public State getStart1() { return start1; }
    private State start2 = new State(); // for starting MonitorGoal2
    

    // -------  alert generators
    // A last monitor in a monitor-state sequence calls an alert generator, 
    // like evalCandidate below.
    // An alert contains a condition (triggerAtom), its dispatcher and
    // an action (here doEvalCandidate) if the condition is OK.  
    // The alert can be seen as a Fodor's LOT expression/ an 
    // if-then-else rule on steroids.
    // The dispatcher is responsible for evaluating the condition (trigger)
    // to decide whether to launch the action or not; see the comment in
    // Dispatcher.
    // Use of the theorem prover is optional.  If not then the response is
    // reactive instead of after deliberation.
    // Another perspective is that the action is instead a goal that requires 
    // planning to obtain a plan/ conditional plan/ etc to acually change the world.
 
    /* Example alert generator::
    public void evalCandidate(String candidate, State state) {
	addTrace("evalCandidate candidate: " + candidate);
	// not using the theorem prover but a 'procedural attachment'
	String trigger = ""; 	
        Formula triggerAtom = Symbol.UNKNOWN;
	// activate if trigger is set 
	     try { triggerAtom = (Formula) parser.parse(trigger); }
	     catch ( Exception pe ) {
	       String messg = "checkNode() Parser Error of: " + trigger;
	       System.out.println(messg);
	       addTrace(messg);
	        return;
	     }  
	addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	// setCandidateChecked(candidate);
	Dispatcher dispatcher = new EvalCandidate(this, triggerAtom);
	// set priority with one or the other
  	   // dispatcher.init();
           // dispatcher.setPriority(77);
	DoEvalCandidate doEvalCandidate =
	    new DoEvalCandidate(this, candidate, state);
	dispatcher.setJob(doEvalCandidate);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
        // if using the theorem prover
	   // dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	addTrace("Actor: evalCandidate/alert.wakeUp ...");
	wakeUp();
    } // end evalCandidate
    */
   
    public void startGoal1(String goal, int cnt) {
	addTrace("startGoal1: " + goal + " " + cnt);
	Formula triggerAtom = Symbol.UNKNOWN; // no deduction
	Dispatcher dispatcher = new Goal1(this, triggerAtom);
	dispatcher.init();
	DoGoal1 doGoal1 = new DoGoal1(this, goal, cnt);
	dispatcher.setJob(doGoal1);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	addAlert(alert); // a queue
	addTrace("Actor: startGoal1/alert.wakeUp ...");
	wakeUp();
    } // startGoal1
    public void startGoal2(String goal, int cnt) {
	addTrace("startGoal1: " + goal + " " + cnt);
	Formula triggerAtom = Symbol.UNKNOWN; // no deduction
	Dispatcher dispatcher = new Goal2(this, triggerAtom);
	dispatcher.init();
	DoGoal2 doGoal2 = new DoGoal2(this, goal, cnt);
	dispatcher.setJob(doGoal2);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	addAlert(alert); // a queue
	addTrace("Actor: startGoal2/alert.wakeUp ...");
	wakeUp();
    } // startGoal1




	// ----- constructor
    public Actor(String name) { 
	super(name); 
	actorMeta = new ActorMeta("Super" + name, this);

	// other initializations
 
	// Create the monitors here

	// Keep an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);

	// other monitors here, for example:
	// +++++
	/*
           RunMonitor monitorFetchNode =
	      new RunMonitor(new MonitorFetchNode(this, bestNode), 100000);
	   againS.addConsumer(monitorFetchNode);
	   monitors.addElement(monitorFetchNode);
	*/
	RunMonitor monitorGoal1 =
	      new RunMonitor(new MonitorGoal1(this), 100000);
	start1.addConsumer(monitorGoal1);
	monitors.addElement(monitorGoal1);

	RunMonitor monitorGoal2 =
	      new RunMonitor(new MonitorGoal2(this), 100000);
	start2.addConsumer(monitorGoal2);
	monitors.addElement(monitorGoal2);


	// more monitors here
    }

    // -------- trace stuff ------------

    public void addTrace(String message) {
	this.addTrace0(cnt + " " + name + " " + message);
    }
    public void addTrace0(String message) { // or change as needed
	System.out.println(message);
    }


    // -------------------- core consciousnesss loop ------------------

    static final private int updateInterval = 200; // 0.2 secs
    private boolean again = true;

    public void start() {
	cnt = 0;

	// start the monitors
	rmTaskFocus.start();
	int lng = monitors.size();
	for (int i = 0; i < lng; i++) {
	    RunMonitor rm = (RunMonitor) monitors.elementAt(i);
	    rm.start();
	}
	
	// start the taskFocus
	taskFocus.start();

	// start the consciousness loop
	myThread = new Thread(this);
	again = true;
	myThread.start(); 
	actorMeta.start();
	
	start1.wakeUp(); // get the ball rolling
	start2.wakeUp(); // get the ball rolling
    } // end start

    public void run() {
	addTrace("Actor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	// application specific actions here

	while ( again ) {
	    cnt++; 
	    // addTrace("Actor.run().cnt: " + cnt);

	    /* Example taken from an application how the message queue
	       can be used:
	    Object message = fetchQueue();
	    if ( null != message ) {
		if ( message instanceof Invitation ) {
		    Invitation in = (Invitation) message;
		    invitations.addElement(in);	
		} else
		if ( message instanceof Exchange ) {
		    response = (Exchange) message;
		    wait1.wakeUp();
		}
	    }
	    */

	    // dispatch alerts, wrapped due to concurrency
	    synchronized ( alerts ) {
		while ( 0 < alerts.size() ) {
		    Alert alert = (Alert) alerts.removeFirst();
		    Dispatcher d = alert.getDispatcher();
		    addTrace("Actor: Found alert!! for: " +
			     d.getJob().getClass().getName() +
			     " trigger: " + d.getTrigger().html());
		    // add to a queue so that meta can observe things 
		    // decide whether to act on the alert
		    synchronized ( this ) { dispatchAlert(alert); }
		    /* check for the entity involved
		       add to a bounded list of memory with 
		       recent alerts;
		       check for alert looping and if so freakout/ 
		       generate a task to clean up, raise fear, 
		       excitation etc */
		}
	    }
	    // launch actions
	    synchronized ( actions ) {
		while ( 0 < actions.size() ) {
		    Job job = (Job) actions.removeFirst();
		    addTrace("Actor: Found job!! for: " +
			     job.getClass().getName());
		    // add to a queue so that meta can observe things
		    synchronized ( this ) { insertTask(new Task(job)); }
		}
	    }
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	addTrace("Actor: Stopping consciousness loop of Actor: " + name);
	myThread = null;
    } // end run

    public void stop() {
	addTrace("Actor.stop() " + name + 
		 " taskQueue.size(): " + taskQueue.size());
	actorMeta.stop();

	// stop the conscious loop 
	again = false; 
	wakeUp();

	// stop the monitors
	rmTaskFocus.stop();
	stopMonitors(monitors);
	taskFocus.stop();

	// check that they have stopped 
	boolean checkAgain = true;
	while ( checkAgain ) {
	    checkAgain = false;
	    if ( !stopped() ) checkAgain = true;
	    if ( !rmTaskFocus.stopped() ) checkAgain = true;
	    if ( notStoppedMonitor(monitors) ) checkAgain = true;
	    if ( null != taskFocus ) checkAgain = true; 
	    if ( checkAgain ) {
		addTrace("Actor: ** Waiting for threads to stop ...");
		try {
		    Thread.sleep(10);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("Actor: All Threads stopped");

	addTrace("Actor: Stopped run of Actor: " + name);
	/* // optional logging
	Vector assertions = alertEvents.getAssertions();
	lng = assertions.size();
	if ( 0 < lng ) {
	    addTrace("Actor: # assertions " + lng);
	    for (int i = 0; i < lng; i++) {
	        Atom assertion = (Atom) assertions.elementAt(i);
	        addTrace("Assertion: " + i + ": " + assertion.html());
	    }
	}
	lng = alertTrace.size();
	if ( 0 < lng ) {
	    addTrace("Actor: # alertTrace " + lng);
	    for (int i = 0; i < lng; i++) {
		Alert alert = (Alert) alertTrace.elementAt(i);
		boolean launched = alert.getLaunched();
		// Atom trigger = alert.getTrigger();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + alert.getDispatcher().getTrigger().html());
	    }
	}
	*/
	// additional logging here

    } // end stop()

    // Application specific definition of nullJobCheck::
    // which is called in NullJob,
    // which is used in ActorBase to define nj, nullTask and taskFocus
    // taskFocus is started in start() above
    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
         
	// addTrace("Actor nullJobCheck cnt: " + cnt);
    } // end nullJobCheck

} // end Actor

// Add here support classes as needed
