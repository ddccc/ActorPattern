// (C) OntoOO Inc 2005 Apr
package actor8;

import java.util.*;
import java.io.*;

public class RunMonitor implements Runnable {
    private Monitor monitor = null;
    private int updateInterval = 1000; // 1 secs
    private ActorBase actor = null;

    public RunMonitor(Monitor monitor, int interval) {
	this(monitor);
	updateInterval = interval;
    }

    public RunMonitor(Monitor monitor) {
	this.monitor = monitor;
	actor = monitor.getActorMonitored();
	monitor.setMyRunMonitor(this);
    }

    public Monitor getMonitor() { return monitor; }

    // core monitor loop
    private boolean again = true;
    private Thread myThread = null;
    public void start() { 
	myThread = new Thread(this);
	again = true;
	myThread.start(); 
    }
    public void stop() { 
	again = false; 
	wakeUp();
    }

    public void run() {
	actor.addTrace(
	      "RunMonitor: start for monitor: " + monitor.getClass().getName());
	if ( 10000 < updateInterval ) { // wait first
	    try {
		Thread.sleep(updateInterval);
	    } catch (InterruptedException ignore) {}
	}
	while ( again ) {
	    monitor.check();
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	// actor.addTrace("RunMonitor: stopped run of: " +  monitor.getClass().getName());
	myThread = null;
    }
    // wakeUp() assumes that the client will not hog the myThread
    public void wakeUp() { 
	// actor.addTrace("WakeUp of: " + monitor.getClass().getName());
	try { myThread.interrupt(); }
	catch (Exception ignore) {}
    }
    public boolean stopped() { return (null == myThread); }

} // end RunMonitor
