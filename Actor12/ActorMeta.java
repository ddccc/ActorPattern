// File: c:/ddc/Java/Actor12/ActorMeta.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Fri Jun 21 15:25:08 2019

package actor12;

import java.util.*;
import fol.*;

// See the class Actor how to structure your ActorMeta
public class ActorMeta extends ActorBase {

    private Actor actor = null;
    public Actor getActor() { return actor; }

    ActorMeta(String name, Actor actor) { 
	super(name); 
	this.actor = actor;

	// Create the monitors here

	// Keep an eye on the task focus
	// Activate rmTaskFocus if alerts create jobs
	// rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);

	// other monitors here,

    } // end ActorMeta

    protected void addTrace(String message) {
	actor.addTrace0(cnt + " " + name + " " + message);
    }

    // -------------------- core consciousnesss loop ------------------

    static final private int updateInterval = 400; // 0.4 secs
    private boolean again = true;

    public void start() {
	cnt = 0;

	// start the monitors
	/* 
	rmTaskFocus.start(); // if created above
	// if monitors are created above::
	int lng = monitors.size();
	for (int i = 0; i < lng; i++) {
	    RunMonitor rm = (RunMonitor) monitors.elementAt(i);
	    rm.start();
	}
	*/


	// start the taskFocus
	taskFocus.start();

	// start the consciousness loop
	myThread = new Thread(this);
	// myThread.setPriority(Thread.NORM_PRIORITY-1);
	again = true;
	myThread.start(); 
    } // end start

    public void run() {
	addTrace("ActorMeta: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	// PDA specific code here
	while ( again ) {
	    cnt++; 
	    addTrace("ActorMeta.run().cnt: " + cnt);
	    // PDA specific code here

	    // dispatch alerts - see Actor, if any

	    // launch actions  - see Actor, if any

	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	addTrace("ActorMeta: $$$$$$$$$$ Stopping conscious loop for: " + name);
	myThread = null;
    } // end run

    public void stop() {
	addTrace("ActorMeta.stop() " + name + 
		 " taskQueue.size(): " + taskQueue.size());
	// stop the conscious loop 
	again = false; 
	wakeUp();

	// stop the monitors
	// rmTaskFocus.stop(); // if used
	// stopMonitors(monitors); // if used
	if ( null != taskFocus ) taskFocus.stop();

	// check 
	boolean checkAgain = true;
	while ( checkAgain ) {
	    checkAgain = false;
	    if ( !stopped() ) checkAgain = true;
	    // if ( !rmTaskFocus.stopped() ) checkAgain = true; // if used
	    // if ( notStoppedMonitor(monitors) ) checkAgain = true; // if used
	    if ( null != taskFocus ) checkAgain = true; 
	    if ( checkAgain ) {
		addTrace("ActorMeta: ** Waiting for threads to stop ...");
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	} 

	addTrace("ActorMeta: Stopped run of Actor: " + name);
	/* // optional for logging
	Vector assertions = alertEvents.getAssertions();
	int lng = assertions.size();
	if ( 0 < lng ) {
	    addTrace("MetaActor: # assertions " + lng);
	    for (int i = 0; i < lng; i++) {
	        Atom assertion = (Atom) assertions.elementAt(i);
	        addTrace("Assertion: " + i + ": " + assertion.html());
	    }
	}
	lng = alertTrace.size();
	if ( 0 < lng ) {
	    addTrace("MetaActor: # alertTrace " + lng);
	    for (int i = 0; i < lng; i++) {
		Alert alert = (Alert) alertTrace.elementAt(i);
		boolean launched = alert.getLaunched();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + alert.getDispatcher().getTrigger().html());
	    }
	}
	*/
    } // end stop

    protected void nullJobCheck(int cnt) {
	// customize as needed
	addTrace("ActorMeta.nullJobCheck cnt: " + cnt);
    }
} // end ActorMeta
