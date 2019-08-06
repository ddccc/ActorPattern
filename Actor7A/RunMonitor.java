// File: c:/ddc/Java/Actor7A/RunMonitor.java
// Date: Sat Sep 15 20:58:32 2018
// (C) Dennis de Champeaux/ OntoOO Inc 2018

package actor7A;

import java.util.*;
import java.io.*;

// A wrapper that executes monitors by calling their check() operation.
// Mon Oct 08 15:15:18 2018 Upgrade: adds try-catch around monitor.check().
public class RunMonitor implements Runnable {
    private Monitor monitor = null;
    private int updateInterval = 1000; // 1 secs
    private ActorBase actor = null;
    private String monitorName = "";

    public RunMonitor(Monitor monitor, int interval) {
	this(monitor);
	updateInterval = interval;
    }

    public RunMonitor(Monitor monitor) {
	this.monitor = monitor;
	actor = monitor.getActorMonitored();
	monitor.setMyRunMonitor(this);
	monitorName = monitor.getClass().getName();
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
	actor.addTrace("RunMonitor: start for monitor: " + monitorName);
	if ( 10000 < updateInterval ) { // wait first
	    try {
		Thread.sleep(updateInterval);
	    } catch (InterruptedException ignore) {}
	}
	while ( again ) {
	    try { monitor.check(); }
	    catch (Exception ex) {
		actor.addTrace("########## RunMonitor: EXCEPTION for monitor: " + 
			       monitorName);
		actor.addTrace("########## RunMonitor: " + ex.getMessage());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		ex.printStackTrace(ps);
		actor.addTrace("########## RunMonitor: <br>" + 
			       "<pre>" +
			       baos.toString() +
			       "</pre>");
		again = false; break;
	    }
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	// actor.addTrace("RunMonitor: stopped run of: " +  monitorName);
	myThread = null;
    }
    // wakeUp() assumes that the client will not hog the myThread
    public void wakeUp() { 
	// actor.addTrace("WakeUp of: " + monitorName);
	try { myThread.interrupt(); }
	catch (Exception ignore) {}
    }
    // The stopped operation can be used to check whether the monitor crashed and
    // need a restart.
    public boolean stopped() { return (null == myThread); }

} // end RunMonitor
