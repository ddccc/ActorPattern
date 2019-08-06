// File: c:/ddc/Java/Actor2B/ActorMeta.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006

package actor2B;

import java.util.*;
import fol.*;


public class ActorMeta extends ActorBase {

    private Actor actor = null;
    public Actor getActor() { return actor; }
    private State work = new State(); // to resume a stalled process 
    public State getWork() { return work; }
    // private State progressQ = new State();
    // public State getProgressQ() { return progressQ; }

    // private State nextAction = null;
    // public State getNextAction() { return nextAction; }
  

    ActorMeta(String name, Actor actor) { 
	super(name); 
	this.actor = actor;

	// nextAction = actor.getNextAction();

	// set monitors
	RunMonitor checkActions = 
	    new RunMonitor(new MonitorCheckActions(this, work), 100);
	monitors.addElement(checkActions);

	RunMonitor checkProgress = 
	    new RunMonitor(new MonitorCheckProgress(this), 100);
	work.addConsumer(checkProgress);
	monitors.addElement(checkProgress);

    } // end ActorMeta


    public void addTrace(String message) {
	actor.addTrace0(cnt + " " + name + " " + message);
    }


    // -------------------- core consciousnesss loop ------------------

    static final private int updateInterval = 200; // 0.2 secs
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
	int previousZeroCnt = 81;
	int noChangeCnt = 0;
	while ( again ) {
	    cnt++; 
	    // addTrace("ActorMeta.run().cnt: " + cnt);
	    int squareZeroCnt = actor.getSquareCnt();
	    int workCnt = actor.getWorkCnt();
	    if ( squareZeroCnt != previousZeroCnt ) {
		previousZeroCnt = squareZeroCnt;
		noChangeCnt = 0;
	    }
	    else 
		noChangeCnt++;

	    if ( 15 < noChangeCnt ) { 
		StopAngel stopAngel = new StopAngel(actor);
		stopAngel.start();
	    }

	    // addTrace("squareZeroCnt: " + squareZeroCnt + " noChangeCnt: " + 
	    //          noChangeCnt);
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
		    Thread.sleep(10);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}

	addTrace("ActorMeta: Stopped run of Actor: " + name + 
		 " workCnt: " + actor.getWorkCnt());

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
		boolean launched = alert.getLaunched();
		// Atom trigger = alert.getTrigger();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + alert.getDispatcher().getTrigger().html());
	    }
	}
    } // end stop

    protected void nullJobCheck(int cnt) {
	int actorWorkCnt = actor.getWorkCnt();
	// addTrace("ActorMeta/ actorWorkCnt: " + actorWorkCnt);
    }
} // end ActorMeta
