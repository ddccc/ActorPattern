// File: c:/ddc/Java/Actor/Actor.java
// Date: Thu Jan 26 19:45:31 2006
package actor;

import java.util.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // Application specific sudoku items
    private Square square = null; // the item in the 'world' to be worked on
    public Square getSquare() { return square; }

    // state/monitor sequences generate alerts

    // state for the square to trigger monitors that depend on any assignment to a tile 
    private State squareState = new State();

    // arrays for the states of each row, column and subarray
    private State [] rowStates = new State[9];
    private State [] colStates = new State[9];
    private State [][] arrStates = new State[3][3];
    public State getSquareState() { return squareState; }
    public State getRowState(int i) { return rowStates[i]; }
    public State getColState(int j) { return colStates[j]; }
    public State getArrState(int p, int q) { return arrStates[p][q]; }


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

    public void row1(int i) {
	String trigger = "Row1(" + cnt + " " + i + ")"; // PC atomic formula 
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "row1() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger row1:</b> " + triggerAtom.html());
	// A dispatcher itself is a job whose task is to launch a job, like doRow1,
	// when appropriate/ approved
	Dispatcher dispatcher = new Row1(this, triggerAtom);  
	// dispatcher.setActor(this);
	DoRow1 doRow1 = new DoRow1(this, i); // subclass of Job
	dispatcher.setJob(doRow1);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doRow1);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // add to a queue of alerts
	addTrace("Actor: row1/alert.wakeUp ...");
	wakeUp();
    } // end row1

    public void row2(int i) {
	String trigger = "Row2(" + cnt + " " + i + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "row2() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger row2:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new Row2(this, triggerAtom);
	// dispatcher.setActor(this);
	DoRow2 doRow2 = new DoRow2(this, i);
	dispatcher.setJob(doRow2);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doRow2);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();

	addAlert(alert); // a queue
	addTrace("Actor: row2/alert.wakeUp ...");
	wakeUp();
    } // end row2

    public void col1(int i) {
	String trigger = "Col1(" + cnt + " " + i + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "col1() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger col1:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new Col1(this, triggerAtom);
	// dispatcher.setActor(this);
	DoCol1 doCol1 = new DoCol1(this, i);
	dispatcher.setJob(doCol1);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doCol1);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // a queue
	addTrace("Actor: col1/alert.wakeUp ...");
	wakeUp();
    } // end col1

    public void col2(int i) {
	String trigger = "Col2(" + cnt + " " + i + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "col2() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger col2:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new Col2(this, triggerAtom);
	// dispatcher.setActor(this);
	DoCol2 doCol2 = new DoCol2(this, i);
	dispatcher.setJob(doCol2);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doCol2);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // a queue
	addTrace("Actor: col2/alert.wakeUp ...");
	wakeUp();
    } // end col1

    public void arr1(int p, int q) {
	String trigger = "Arr1(" + cnt + " " + p + " " + q + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "arr1() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger arr1:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new Arr1(this, triggerAtom);
	// dispatcher.setActor(this);
	DoArr1 doArr1 = new DoArr1(this, p, q);
	dispatcher.setJob(doArr1);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doArr1);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // a queue
	addTrace("Actor: arr1/alert.wakeUp ...");
	wakeUp();
    } // end arr1

    public void arr2(int p, int q) {
	String trigger = "Arr2(" + cnt + " " + p + " " + q + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "arr2() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger arr2:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new Arr2(this, triggerAtom);
	// dispatcher.setActor(this);
	DoArr2 doArr2 = new DoArr2(this, p, q);
	dispatcher.setJob(doArr2);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doArr2);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // a queue
	addTrace("Actor: arr2/alert.wakeUp ...");
	wakeUp();
    } // end arr2
    
    public void setTile(Arr arr, int pi, int qj, int k) {
	if ( square.getZeroCnt() <= 0 ) return; // ad hoc optimization
	int ixi = arr.getIindex();
	int ixj = arr.getJindex();
	String trigger = "SetTile(" + cnt + " " + (3*ixi + pi) + " " + (3*ixj + qj) + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "setTile() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger setTile:</b> " + 
		 triggerAtom.html() + " -> " + k);
	// SetTile looks in the 'world' to check that the tile is not yet set
	// Still the job doSetTile invokes a method deep down in Tile that
	// has a synchronized method that will not set the tile if it is set already
	// (to prevent the zeroCnt counter to be decreased again).
	// See the comment in SetTile for an alternative way to do a pre-check.
	Dispatcher dispatcher = new SetTile(this, triggerAtom);
	// dispatcher.setActor(this);
	DoSetTile doSetTile = new DoSetTile(this, arr, pi, qj, k);
	dispatcher.setJob(doSetTile);
	// Alert alert = new Alert(this, triggerAtom, dispatcher, doSetTile);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.init();
	addAlert(alert); // add to a queue
	// addTrace("Actor: setTile/alert.wakeUp ...");
	wakeUp();
    } // end setTile

	// ----- constructor
    public Actor(Square square) { 
	super("Sudoku"); 
	actorMeta = new ActorMeta("Meta", this);
	this.square = square;
	square.setActor(this);

	// Init this state with the initial # of tiles to be assigned
	squareState.setIntValue(square.getZeroCnt());
	
	// Create states that trigger the monitors associated with rows, 
	// columns and subarrays:
	for ( int i = 0; i < 9; i++ ) {
	    Seq rowI = square.getRow(i);
	    rowStates[i] = new State(rowI.getZeroCnt());
	}
	for ( int i = 0; i < 9; i++ ) {
	    Seq colI = square.getCol(i);
	    colStates[i] = new State(colI.getZeroCnt());
	}
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) {
		Arr arrPQ = square.getArr(p, q);
		arrStates[p][q]= new State(arrPQ.getZeroCnt());
	    }
 
	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus); 
	
	// keeps an eye on the zeroCnt.  If zero it triggers termination
	// If the count has decreased it uses squareState to trigger other
	// tile-specific monitors
	int startZeroCnt = squareState.getIntValue();
	Monitor msq = new MonitorSquare(this, squareState, startZeroCnt);
	RunMonitor monitorSquare = new RunMonitor(msq, 50);
	monitors.addElement(monitorSquare);
	
	// Creates row-specific monitors that are triggered by row specific states
	for ( int i = 0; i < 9; i++ ) {
	    Monitor mI = new MonitorRow(this, i);
	    RunMonitor monitorRowI = new RunMonitor(mI, 10000000);
	    // RunMonitor monitorRowI = new RunMonitor(mI, 50);
	    // mI.setMyRunMonitor(monitorRowI);
	    rowStates[i].addConsumer(monitorRowI);
	    // squareState.addConsumer(monitorRowI);
	    monitors.addElement(monitorRowI);
	}
	// Creates column-specific monitors that are triggered by column specific states
	for ( int i = 0; i < 9; i++ ) {
	    Monitor mI = new MonitorCol(this, i);
	    RunMonitor monitorColI = new RunMonitor(mI, 10000000);
	    // RunMonitor monitorColI = new RunMonitor(mI, 50);
	    // mI.setMyRunMonitor(monitorColI);
	    colStates[i].addConsumer(monitorColI);
	    // squareState.addConsumer(monitorColI);
	    monitors.addElement(monitorColI);
	}
	// Creates subarray-specific monitors that are triggered by subarray specific states
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) {
		Monitor mPQ = new MonitorArr(this, p, q);
		RunMonitor monitorArrPQ = new RunMonitor(mPQ, 10000000);
		// RunMonitor monitorArrPQ = new RunMonitor(mPQ, 50);
		// mPQ.setMyRunMonitor(monitorArrPQ);
		arrStates[p][q].addConsumer(monitorArrPQ);
		// squareState.addConsumer(monitorArrPQ);
		monitors.addElement(monitorArrPQ);
	    }
	// Creates tile & candidate value specific monitors that are triggered by
	// squareState
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) 
		for ( int k = 1; k < 10; k++ ){
		    Monitor mPQ = new MonitorArrBlock(this, p, q, k);
		    RunMonitor monitorArrPQ = new RunMonitor(mPQ, 10000000);
		    // mPQ.setMyRunMonitor(monitorArrPQ);
		    // squareState.addConsumer(monitorArrPQ);
		    arrStates[p][q].addConsumer(monitorArrPQ);
		    monitors.addElement(monitorArrPQ);
	    }

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
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	addTrace("Actor: Stopping consciousness loop of Actor: " + name);
	myThread = null;
    } // end run

    public void stop() {
	addTrace("Actor.stop() " + name + 
		 " taskList.size(): " + taskQueue.size());
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

	addTrace("Actor: Stopped run of Actor: " + name);
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

    } // end stop()

    // Application specific definition of nullJobCheck::
    // which is called in NullJob,
    // which is used in ActorBase to define nj, nullTask and taskFocus
    // taskFocus is started in start() above
    protected void nullJobCheck(int cnt) {
	if ( square.getZeroCnt() <= 0 ) return; // ad hoc optimization
	addTrace("Actor nullJobCheck cnt: " + cnt);
	// The block call here has been replaced by 81 instances of MonitorArrBlock
        // (9 sub arrays and 9 values to be set). 
	// square.block();  // not needed
	// square.last();   // last is another implementation of block, not needed
    } // end nullJobCheck

} // end Actor
