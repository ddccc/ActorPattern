// File: c:/ddc/Java/ActorAstar7A/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sat Jun 01 14:19:12 2019

package actor7A;

import java.util.*;
import java.io.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    /* 
      The parser and theory are used inside the deliberate/decide
      module.
      The assertions attribute in theory has been used in some examples
      as a log, supporting the avoidance of cognitive loops. 
    */
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
    private State fetchNodeS = new State(); // go fetch a node 
    public State getFetchNodeS() { return fetchNodeS; }


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
    // planning to obtain a plan/ conditional plan/ etc to actually change the world.
 
    public void processSuccessors(Vector<Node> successorNodes, State state) {
	// addTrace("processSuccessors # successors: " + successorNodes.size());
	// not using the theorem prover but a 'procedural attachment'
	// String trigger = ""; 	
        Formula triggerAtom = Symbol.UNKNOWN;
	// activate if trigger is set 
	/*
	try { triggerAtom = (Formula) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "processSuccessors() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}  
	*/
	Dispatcher dispatcher = new ProcessNodes(this, triggerAtom);
	DoProcessNodes doProcessNodes =
	    new DoProcessNodes(this, successorNodes, state);
	dispatcher.setJob(doProcessNodes);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
        // if using the theorem prover
	   // dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	// addTrace("Actor: processSuccessors/alert.wakeUp ...");
	wakeUp();
    } // end processSuccessors()

    private Node goalNode = null;
    public void setGoalNode(Node n) { goalNode = n; }
    public Node getGoalNode() { return goalNode; }

	// ----- constructor
    public Actor(Node startNode) { 
	super("A*"); 
	actorMeta = new ActorMeta("A*Meta", this);

	// other initializations
	// put a node in the priority queue:
	Node.addNodeO(startNode);
 
	// Create the monitors here

	// Keep an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);

	// other monitors here:
	RunMonitor monitorFetchNode =
	    new RunMonitor(new MonitorFetchNode(this), 100000);
	fetchNodeS.addConsumer(monitorFetchNode);
	monitors.addElement(monitorFetchNode);



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
    } // end start

    public void run() {
	addTrace("Actor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	// application specific actions here

	fetchNodeS.wakeUp(); // process the start node

	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);

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
	if ( null != taskFocus ) taskFocus.stop();

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
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("Actor: All Threads stopped");

	addTrace("Actor: Stopped run of Actor: " + name);
	/* // optional logging
	int lng;
	// Vector assertions = alertEvents.getAssertions();
	Vector assertions = theory.getAssertions();
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
         
	addTrace("Actor nullJobCheck cnt: " + cnt);
    } // end nullJobCheck

} // end Actor

// Add here support classes as needed
