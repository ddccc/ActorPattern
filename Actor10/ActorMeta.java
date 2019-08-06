// File: c:/ddc/Java/Actor10/ActorMeta.java
// Date: Wed Apr 25 15:39:49 2018
package actor10;

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
	addTrace("ActorMeta: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	while ( again ) {
	    cnt++; 
	    addTrace("ActorMeta.run().cnt: " + cnt);
	    // PDA specific code here
            if ( !Thread.interrupted() )
	       try {
		   Thread.sleep(updateInterval);
	       } catch (InterruptedException ignore) {}
	}
	addTrace("ActorMeta: $$$$$$$$$$ Stopping conscious loop for: " + name);
	myThread = null;
    }
    public void stop() {
	addTrace("ActorMeta.stop() " + name + 
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
		addTrace("ActorMeta: ** Waiting for threads to stop ...");
	        try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}

	addTrace("ActorMeta: Stopped run of Actor: " + name);

    } // end stop


    protected void nullJobCheck(int cnt) {
	// customize as needed
	addTrace("ActorMeta.nullJobCheck cnt: " + cnt);
    }
} // end ActorMeta
