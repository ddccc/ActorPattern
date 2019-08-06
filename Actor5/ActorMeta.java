// File: c:/ddc/Java/Actor5/ActorMeta.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006

package actor5;

import java.util.*;
import fol.*;


public class ActorMeta extends ActorBase {

    private Actor actor = null;
    public Actor getActor() { return actor; }

    private State inputAvailable = null;
    public State getInputAvailable() { return inputAvailable; }

    ActorMeta(String name, Actor actor) { 
	super(name); 
	this.actor = actor;
	inputAvailable = actor.getInputAvailable();

	// set monitors
	RunMonitor monitorTerminateQ = 
	    new RunMonitor(new MonitorTerminateQ(this), 10000000);
	inputAvailable.addConsumer(monitorTerminateQ);
	monitors.addElement(monitorTerminateQ);

    } // end ActorMetA


    public void addTrace(String message) {
	actor.addTrace0(cnt + " " + name + " " + message);
    }


    // -------------------- core consciousnesss loop ------------------

    // static final private int updateInterval = 200; // 0.2 secs
    static final private int updateInterval = 20000; // 20 secs
    private boolean again = true;

    public void start() {
	cnt = 0;

	// start the monitors
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
    }
    public void run() {
	addTrace("ActorMeta: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	int noChangeCnt =0;

	while ( again ) {
	    cnt++; 
	    addTrace("ActorMeta.run().cnt: " + cnt);
	    if ( 15 < noChangeCnt ) { 
		StopAngel stopAngel = new StopAngel(actor);
		stopAngel.start();
	    }
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

	Vector assertions = alertEvents.getAssertions();
	int lng = assertions.size();
	if ( 0 < lng ) {
	    addTrace("ActorMeta: # assertions " + lng);
	    for (int i = 0; i < lng; i++) {
	        Atom assertion = (Atom) assertions.elementAt(i);
	        addTrace("Assertion: " + i + ": " + assertion.html());
	    }
	}
	lng = alertTrace.size();
	if ( 0 < lng ) {
	    addTrace("ActorMeta: # alertTrace " + lng);
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
	// addTrace("ActorMeta/ cnt: " + cnt);
    }
} // end ActorMeta
