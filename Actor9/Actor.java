// File: c:/ddc/Java/Actor9/Actor.java
// Date: Sun Mar 18 17:01:54 2018

package actor9;

import java.util.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    private Theory theory = new Theory(false);
    public Theory getTheory() { return theory; };

    // Application specific items here

    // state/monitor sequences generate alerts

    // states here
         
    private State worldEvent = new State();
    public State getWorldEvent() { return worldEvent; }

    // -------  alert generators
    // A last monitor in a monitor-state sequence calls an alert generator, 
    // like row1.
    // An alert contains a condition (triggerAtom), its dispatcher and
    // an action (here doRow1) if the condition is OK.  
    // The alert can be seen as a Fodor's LOT expression/ an 
    // if-then-else rule of steroids.
    // The dispatcher is responsible for evaluating the condition (trigger)
    // to decide whether to launch the action or not; see the comment in
    // Dispatcher.
    // This PDA application is NOT using the theorem prover to evaluate the 
    // condition; see the comment in Row1.
    // Another perspective is that the action is instead a goal that requires 
    // planning to obtain a plan/ conditional plan/ etc to acually change the world

    private int eventCnt = 0;
    private int previousValue = 0;
    public void setPreviousValue(int p) { previousValue = p; }

    public void guessNextValue() {
	int actualValue = worldEvent.getIntValue();
	addTrace("guessNextValue() actualValue: " + actualValue);
	eventCnt++;
	String trigger = "<(" + eventCnt + " 25)";
	Formula triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Formula) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "guessNextValue() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("guessNextValue() new alert trigger: " + 
		 triggerAtom.html());
	Dispatcher dispatcher = new GuessNextValue(this, triggerAtom);
	dispatcher.init();
	DoGuessNextValue doGuessNextValue =
	    new DoGuessNextValue(this, previousValue, arr, bestGuess);
	dispatcher.setJob(doGuessNextValue);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.setTheory(theory);

	addAlert(alert); // a queue
	addTrace("Actor: guessNextValue()/alert.wakeUp ...");
	wakeUp();
    } // end guessNextValue()


    private int[][] arr = null;
    private int[] bestGuess = null; 
    public Thread worldThread = null;

	// ----- constructor
    public Actor(int[][] arr, int[] bestGuess, Thread worldThread) { 
	super("Ponder"); 
	actorMeta = new ActorMeta("MetaPonder", this);
	// other initializations
	this.arr = arr; 
	this.bestGuess = bestGuess; 
	this.worldThread = worldThread;
 
	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus); 

	// other monitors here
	RunMonitor monitorNewEvent =
	    new RunMonitor(new MonitorNewEvent(this), 100000);
	worldEvent.addConsumer(monitorNewEvent);
	monitors.addElement(monitorNewEvent);

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
	
	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);

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

	// optional logging
	int lng;
	/*  
	// Vector assertions = alertEvents.getAssertions();
	Vector assertions = theory.getAssertions(); // ++
	lng = assertions.size();
	if ( 0 < lng ) {
	    addTrace("Actor: # assertions " + lng);
	    for (int i = 0; i < lng; i++) {
	        Atom assertion = (Atom) assertions.elementAt(i);
	        addTrace("Assertion: " + i + ": " + assertion.html());
	    }
	}
	*/
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
	// */
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
