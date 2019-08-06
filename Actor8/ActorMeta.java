// File: c:/ddc/Java/Actor8/ActorMeta.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006

package actor8;

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

    static final private int updateInterval = 200; // 0.2 secs
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
	int previousZeroCnt = 81;
	int noChangeCnt = 0;

	while ( again ) {
	    cnt++; 
	    addTrace("ActorMeta.run().cnt: " + cnt);
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
		addTrace("Actor: ** Waiting for threads to stop ...");
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("ActorMeta: Stopped run of Actor: " + name);


    } // end stop

    protected void nullJobCheck(int cnt) {
	int actorWorkCnt = actor.getWorkCnt();
	addTrace("ActorMeta/ actorWorkCnt: " + actorWorkCnt);
    }
} // end ActorMeta
