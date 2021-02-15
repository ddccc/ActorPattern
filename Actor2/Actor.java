// File: c:/ddc/Java/Actor2/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006
package actor2;

import java.util.*;
import fol.*;  // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // Application specific sudoku items
    public String sync = "sync";

    private Square square = null; // the item in the 'world' to be worked on
    public Square getSquare() { return square; }
    public int getSquareCnt() { return square.getZeroCnt(); }

    private Stack inStack = null;
    private Stack outStack = null;
    public Stack getInstack() { return inStack; }
    public Stack getOutstack() { return outStack; }

    private int workCnt = 0;
    public int getWorkCnt() { return workCnt; }
    public void incrementWorkCnt() { workCnt++; }

    // states
    private State nextAction = new State(); // triggers monitor
    public State getNextAction() { return nextAction; }


    // -------  alert generators:

    /* // not used in this version
    */

	// ----- constructor
    public Actor(Square square) { 
	super("Sudoku"); 
	actorMeta = new ActorMeta("Meta", this);

	// other initializations
	this.square = square;
	square.setActor(this);

	// set the stacks
	inStack = square.getInstack();
	outStack = square.getOutstack();
	
	// create the  monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus);

	// other monitors here

	RunMonitor monitorGoDown = 
	    new RunMonitor(new MonitorGoDown(this, nextAction), 10000000);
	nextAction.addConsumer(monitorGoDown);
	monitors.addElement(monitorGoDown);

	RunMonitor monitorTestVal = 
	    new RunMonitor(new MonitorTestVal(this, nextAction), 100000000);
	nextAction.addConsumer(monitorTestVal);
	monitors.addElement(monitorTestVal);

	RunMonitor monitorIncrementQ = 
	    new RunMonitor(new MonitorIncrementQ(this, nextAction), 100000000);
	nextAction.addConsumer(monitorIncrementQ);
	monitors.addElement(monitorIncrementQ);

	RunMonitor monitorGoUp = 
	    new RunMonitor(new MonitorGoUp(this, nextAction), 100000000);
	nextAction.addConsumer(monitorGoUp);
	monitors.addElement(monitorGoUp);

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
	// myThread.setPriority(Thread.NORM_PRIORITY-1);
	again = true;
	myThread.start(); 
	actorMeta.start();
    } // end start

    public void run() {
	addTrace("Actor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	nextAction.setStringValue("goDown"); // get the ball rolling
	nextAction.wakeUp(); // get the ball rolling
	while ( again ) {
	    cnt++; 
	    // addTrace("Actor.run().cnt: " + cnt);

	    // dispatch alerts, wrapped due to concurrency
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
		 " taskQueue size: " + getTaskQueueSize());
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

	addTrace("Actor: Stopped run of Actor: " + name + " workCnt: " + workCnt);
	/*
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
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + alert.getDispatcher().getTrigger().html());
	    }
	}
	*/
	// square.show();
    } // end stop

    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
	// addTrace("Actor nullJobCheck cnt: " + cnt);
	/*
	addTrace("Actor/ workCnt: " + workCnt +
		 " cnt " + cnt + 
		 " zeroCnt " + square.getZeroCnt() );
	*/
    } // end nullJobCheck

} // end Actor
