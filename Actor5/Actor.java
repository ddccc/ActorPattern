// File: c:/ddc/Java/Actor5/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sat Mar 11 16:36:54 2017

package actor5;

import java.util.*;
import fol.*;  // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // states
    private State inputAvailable = new State(); // triggers monitor
    public State getInputAvailable() { return inputAvailable; } 

    private Theory theory = new Theory();


    // -------  alert generators:

    public void inputIs(String inString) {
	// test inString here
	String trigger = "Input(" + inString + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "inputId() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	Dispatcher dispatcher = new InString(this, triggerAtom);
	dispatcher.init();
	// dispatcher.setActor(this);
	DoInString doInString = new DoInString(this, inString);
	dispatcher.setJob(doInString);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.setTheory(theory);

	addAlert(alert); // a queue
	addTrace("Actor: inString/alert.wakeUp ...");
	wakeUp();
    } // end inputIs

	// ----- constructor
    public Actor() { 
	super("HelloWorld"); 
	actorMeta = new ActorMeta("Meta", this);

	String axiom1 = "Input(HelloWorld)";
	Atom axiom1ax = Symbol.UNKNOWN;
	try { axiom1ax = (Atom) parser.parse(axiom1); }
	catch ( Exception pe ) {
	    String messg = "inputId() Parser Error of: " + axiom1;
	    System.out.println(messg);
	    addTrace(messg);
	}
	if ( Symbol.UNKNOWN != axiom1ax )
	    theory.addAssertion(axiom1ax);

	// create the  monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	monitors.addElement(rmTaskFocus);

	RunMonitor monitorInputReady = 
	    new RunMonitor(new MonitorInputReady(this), 10000000);
	inputAvailable.addConsumer(monitorInputReady);
	monitors.addElement(monitorInputReady);

	// more monitors here
    }

    // -------- trace stuff ------------

    public void addTrace(String message) {
	this.addTrace0(cnt + " " + name + " " + message);
    }
    public void addTrace0(String message) {
	System.out.println(message);
    }


    // -------------------- core consciousnesss loop ------------------

    // static final private int updateInterval = 200; // 0.2 secs
    static final private int updateInterval = 20000; // 20 secs
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
	// myThread.setPriority(Thread.NORM_PRIORITY-1);
	again = true;
	myThread.start(); 
	actorMeta.start();

    }
    public void run() {
	addTrace("Actor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);

	    Object message = fetchQueue();
	    if ( null != message ) {
		if ( message instanceof Message ) {
		    Message in = (Message) message;
		    inputAvailable.setStringValue(in.getWord());
		    inputAvailable.wakeUp();
		}
	    }

	    synchronized (alerts) {
		while ( 0 < alerts.size() ) {
		    Alert alert = (Alert) alerts.removeFirst();
		    Dispatcher d = alert.getDispatcher();
		    addTrace("Actor: Found alert!! for: " +
			     d.getJob().getClass().getName() +
			     " trigger: " + d.getTrigger().html());
		    // add to a queue so that meta can observe things 
		    // decide whether to act on the alert
		    synchronized (this) { dispatchAlert(alert); }
		    /* check for the entity involved
		       add to a bounded list of memory with 
		       recent alerts;
		       check for alert looping and if so freakout/ 
		       generate a task to clean up, raise fear, 
		       excitation etc */
		}
	    }
	    synchronized ( actions ) {
		while ( 0 < actions.size() ) {
		    Job job = (Job) actions.removeFirst();
		    addTrace("Actor: Found job!! for: " +
			     job.getClass().getName());
		    // add to a queue so that meta can observe things
		    synchronized (this) { insertTask(new Task(job)); }
		}
	    }
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	addTrace("Actor: Stopping consciousness loop of Actor: " + name);
	myThread = null;
    }

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
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("Actor: All Threads stopped");

	addTrace("Actor: Stopped run of Actor: " + name);
	// /*
	Vector assertions = alertEvents.getAssertions();
	int lng = assertions.size();
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
		Dispatcher d = alert.getDispatcher();
		boolean launched = alert.getLaunched();
		Formula trigger = d.getTrigger();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + trigger.html());
	    }
	}
	// */

    } // end stop

    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
	// addTrace("Actor nullJobCheck cnt: " + cnt);
	// addTrace("Actor/ cnt " + cnt);
    } // end nullJobCheck

} // end Actor
