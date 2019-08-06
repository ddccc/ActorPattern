// File: c:/ddc/Java/Actor9/ActorMeta.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sun Mar 18 17:52:07 2018

package actor9;

import java.util.*;
import fol.*;


public class ActorMeta extends ActorBase {

    private Actor actor = null;
    ActorMeta(String name, Actor actor) { 
	super(name); 
	this.actor = actor;
    }

    public Actor getActor() { return actor; }

    protected void addTrace(String message) {
	actor.addTrace0(cnt + " " + name + " " + message);
    }

    // -------------------- core consciousnesss loop ------------------

    static final private int updateInterval = 400; // 0.4 secs
    private boolean again = true;

    public void start() {
	cnt = 0;

	// start the monitors

	// start the taskFocus
	taskFocus.start();
	// start the consciousness loop
	myThread = new Thread(this);
	again = true;
	myThread.start(); 
    }
    public void run() {
	addTrace("MetaActor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	int previousZeroCnt = 81;
	int noChangeCnt = 0;
	while ( again ) {
	    cnt++; 
	    addTrace("MetaActor.run().cnt: " + cnt);
	    // PDA specific code here

	    // addTrace("squareZeroCnt: " + squareZeroCnt + 
	    //          " noChangeCnt: " + noChangeCnt);
            if ( !Thread.interrupted() )
	       try {
		   Thread.sleep(updateInterval);
	       } catch (InterruptedException ignore) {}
	}
	addTrace("ActorMeta: $$$$$$$$$$ Stopping conscious loop for: " + name);
	myThread = null;
    }
    public void stop() {
	addTrace("MetaActor.stop() " + name + 
		 " taskQueue.size(): " + taskQueue.size());
	// stop the conscious loop 
	again = false; 
	wakeUp();
	// stop the monitors
	// rmTaskFocus.stop();
	stopMonitors(monitors);
	taskFocus.stop();

	// check 
	boolean checkAgain = true;
	while ( checkAgain ) {
	    checkAgain = false;
	    if ( !stopped() ) checkAgain = true;
	    // if ( !rmTaskFocus.stopped() ) checkAgain = true;
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

	addTrace("ActorMeta: Stopped run of Actor: " + name);

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
		Dispatcher d = alert.getDispatcher();
		boolean launched = alert.getLaunched();
		Formula trigger = d.getTrigger();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + trigger.html());
	    }
	}
    } // end stop


    protected void nullJobCheck(int cnt) {
	// customize as needed
	addTrace("META nullJobCheck cnt: " + cnt);
    }
} // end ActorMeta
