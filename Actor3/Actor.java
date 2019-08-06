// File: c:/ddc/Java/Actor3/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006

package actor3;

import java.util.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // Application specific sudoku items
    private int[][] solution = null;
    public int[][] getSolution() { return solution; }
    /* square has the node that will be worked on */
    private Square square = null;
    public Square getSquare() { return square; }
    public void clearSquare() { square = null; }
    public int getSquareCnt() { // zero when a solution is found
	synchronized(this) { 
	    // return ( null == square ? 1000 : square.getZeroCnt() ); }
	    return ( null == bestNode ? 1000 : bestNode.getZeroCnt() ); }
    }	

    private Square bestNode = null;
    public void setBestNode(Square square) { bestNode = square; }
    public Square getBestNode() { return bestNode; }

    // just stats
    private int workCnt = 0;
    public int getWorkCnt() { return workCnt; }
    public void incrementWorkCnt() { workCnt++; }

    private Vector vec = new Vector(); // sorted open nodes
    public void setSquare() { // fetch the node to work on
	synchronized (this) {
	    if ( null != square ) return;
	    if ( vec.isEmpty() ) return;
	    square = (Square) vec.lastElement(); // lowest F-value
	    vec.removeElementAt(vec.size() -1); // delete it
	}
    }
    // failedNodes has part of the closed nodes.
    // expanded nodes are also closed, but are not tracked in this implementation
    // Also: a new node is NOT checked against failed/closed/open nodes,
    // thus the set of failed nodes is not used.
    // Few nodes are encountered in this application
    private Vector failedNodes = new Vector(); 
    public void addFailedNode(Square square) { failedNodes.addElement(square); }
    public int failedNodesSize() { return failedNodes.size(); }

    // states

    // This state notifies monitorBestNodeAvailable 
    // that a new node needs to be worked on
    private State bestNodeS = new State(); 

    // This state notifies monitorCheck that a solve attempt completed
    // and needs to be investigated for a failure or not. 
    // A non-failure entails a solution or a lower zero count, which need
    // not guarantee that a solution is possible in that configuration
    private State bestNodeAvailable = new State(); 

    // This state has the outcome of monitorCheck and triggers:
    //    monitorCheckFinished, monitorCheckFailed and monitorCheckSuccess
    private State bestNodeChecked = new State(); 
    public State getBestNodeChecked() { return bestNodeChecked; }

    // Expand Node.  This action does NOT conform with the PDA design pattern.
    // Instead an alert should be generated, see setTile below.  Such an alert 
    // gets scrutinized in the Deliberate/Decide section and if approved causes a 
    // job to be generated that does the expandNode action.  Since there is
    // no choice a short cut is used instead.
    public void expandNode(Square square) {
	Tile target = null; // identify a most constrained tile
	int a = 9; int b = 9; int c = 9;
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) {
		int x;
		Tile t = square.getTile(i, j);
		if ( 0 != t.getVal() ) continue;
		int ax = t.getRow().getZeroCnt();
		int bx = t.getCol().getZeroCnt();
		if ( bx < ax ) { x = ax; ax = bx; bx = x; }
		int cx = t.getArr().getZeroCnt();
		if ( cx < ax ) { x = cx; cx = bx; bx = ax; ax = x; }
		else if ( cx < bx ) { x = cx; cx = bx; bx = x; }
		if ( ax < a ) { target = t; a = ax; continue; }
		if ( a < ax ) continue;
		if ( bx < b ) { target = t; b = bx; continue; }
		if ( b < bx ) continue;
		if ( cx < c ) { target = t; c = cx; }
	    }
	// found the best tile, 
	// now decide which values it can get given the current context 
	int i1 = target.getI(); // <i1,j1> are indices
	int j1 = target.getJ();
	Seq row = target.getRow(); // the row of this tile
	Seq col = target.getCol();
	Arr arr = target.getArr();
	for ( int k = 1; k < 10; k++ ) { // try this range
	    if ( row.inRegion(k) ) continue; // not acceptable
	    if ( col.inRegion(k) ) continue;
	    if ( arr.inRegion(k) ) continue;
	    addTrace("New square i1 " + i1 + " j1 " + j1 + " k " + k);
	    Square s2 = square.cloneSquare();
	    Tile t = s2.getTile(i1, j1);
	    t.setVal(k); // set this tile with k
	    s2.setG(1 + square.getG());  // increment distance from root node

	    // should check here whether s2 has been encountered already
	    // in: - set of open nodes, - failed nodes, - closed nodes

	    // s2.setHashIndex(); // for fast checking that squares are !=
	    // synchronized (this) { treeSet.add(s2); } // does not work

	    synchronized (this) { 
		vec.add(s2);  // add the node s2 to the set of open nodes
		vecSort(vec, vec.size()-1); // use insertion sort to push it down
		// addTrace("----- vec add: " + vec.size());
	    }
	    s2.setParent(square); // track how node expansion led to the solution
	    s2.setOperation("Assign i1: " + i1 + " j1: " + j1 + " k: " + k);
	}
	// should here add square to a set of closed nodes
    } // end expandNode

    private void vecSort(Vector vec, int index1) { 
	// push the last entry down so that vec is reverse sorted
	if ( 0 == index1 ) return;
	Square s1 = (Square) vec.elementAt(index1);
	int f1 = s1.F();
	int index0 = index1-1;
	Square s0 = (Square) vec.elementAt(index0);
	int f0 = s0.F();
	if ( f1 <= f0 ) return;
	vec.setElementAt(s0, index1); vec.setElementAt(s1, index0); // swap
	vecSort(vec, index0);
    } // end vecSort

    // -------  alert generators:
    public void trySolve(Square square, State state) {
	addTrace("trySolve square G & F: " + square.G() + " " +  square.F());
	// not using the theorem prover but a 'procedural attachment'
	String trigger = ""; 	
        Formula triggerAtom = Symbol.UNKNOWN;
	// activate if trigger is set 
	/*
	     try { triggerAtom = (Formula) parser.parse(trigger); }
	     catch ( Exception pe ) {
	       String messg = "checkNode() Parser Error of: " + trigger;
	       System.out.println(messg);
	       addTrace(messg);
	        return;
	     }  
	*/
	addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	// setCandidateChecked(candidate);
	Dispatcher dispatcher = new TrySolve(this, triggerAtom);
	// set priority with one or the other
  	   // dispatcher.init();
           // dispatcher.setPriority(77);
	DoTrySolve doTrySolve =
	    new DoTrySolve(this, square, state);
	dispatcher.setJob(doTrySolve);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
        // if using the theorem prover
	   // dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	addTrace("Actor: trySolve/alert.wakeUp ...");
	wakeUp();
    } // end trySolve

	// ----- constructor
    public Actor(Square square, int[][] solution) { 
	super("Sudoku"); 

	this.square = square;
	// square.setHashIndex(); // for fast checking that squares are !=
	this.solution = solution;
	square.setActor(this);

	actorMeta = new ActorMeta("Meta", this);

	/*
	System.out.println(
	   "getRowColArr2Cnt: " + square.getRowColArr2Cnt() +
	   " getRowCol3Cnt: " + square.getRowCol3Cnt());
	*/

	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus);

	// This monitor tries to solve the current square and when done
	// triggers monitorCheck through bestNodeAvailable
	RunMonitor monitorBestNodeAvailable =
	    new RunMonitor(new MonitorBestNodeAvailable(this, bestNodeAvailable), 100000);
	bestNodeS.addConsumer(monitorBestNodeAvailable);
	monitors.addElement(monitorBestNodeAvailable);

	// This monitor checks the current state of the square and 
	// sets the downstream state bestNodeCheckedOK with true when no 
	// contradiction was found or else with false, and triggers
	// monitorCheckFinished, monitorCheckFailed & monitorCheckSuccess
	RunMonitor monitorCheck =
	    new RunMonitor(new MonitorCheck(this, bestNodeChecked), 100000);
	bestNodeAvailable.addConsumer(monitorCheck);
	monitors.addElement(monitorCheck);

	// This monitor checks in case of no contradiction whether a solution
	// was found (zeroCnt is zero); and if so triggers a termination
	RunMonitor monitorCheckFinished =
	    new RunMonitor(new MonitorCheckFinished(this), 100000);
	bestNodeChecked.addConsumer(monitorCheckFinished);
	monitors.addElement(monitorCheckFinished);

	// This monitor recognizes a dead-end, fetches another open node and
	// triggers  monitorBestNodeAvailable via bestNodeSet
	RunMonitor monitorCheckFailed =
	    new RunMonitor(new MonitorCheckFailed(this, bestNodeS), 100000);
	bestNodeChecked.addConsumer(monitorCheckFailed);
	monitors.addElement(monitorCheckFailed);

	// This monitor recognizes that the node must be expanded, 
	// which adds to the collection of open nodes, fetches another 
	// open node and triggers  monitorBestNodeAvailable via bestNodeSet
	RunMonitor monitorCheckSuccess =
	    new RunMonitor(new MonitorCheckSuccess(this, bestNodeS), 100000);
	bestNodeChecked.addConsumer(monitorCheckSuccess);
	monitors.addElement(monitorCheckSuccess);

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

    // start(), run() and stop() have mostly only generic PDA functionality 

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

	bestNodeS.wakeUp(); // get the ball rolling

	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);

	    // no alerts are generated in this application
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
		    synchronized (this) { dispatchAlert(alert); }
		    /* check for the entity involved
		       add to a bounded list of memory with 
		       recent alerts;
		       check for alert looping and if so freakout/ 
		       generate a task to clean up, raise fear, 
		       excitation etc */
		}
	    }
	    // launch actions
	    // no actions are generated in this application
	    synchronized ( actions ) {
		while ( 0 < actions.size() ) {
		    Job job = (Job) actions.removeFirst();
		    addTrace("Actor: Found job!! for: " +
			     job.getClass().getName());
		    // add to a queue so that meta can observe things
		    synchronized (this) { insertTask(new Task(job)); }
		}
	    }
	    if ( !Thread.interrupted() )
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
	}
	addTrace("Actor: Stopping consciousness loop of Actor: " + name);
	myThread = null;

    } // end run()

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
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("Actor: All Threads stopped");

	addTrace("Actor: Stopped run of Actor: " + name + 
		 " workCnt: " + workCnt);

	/* // show what happened
	Vector assertions = alertEvents.getAssertions();
	int lng = assertions.size();
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
    } // end stop

    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
	// addTrace("Actor nullJobCheck cnt: " + cnt);
	addTrace("Actor/ workCnt: " + workCnt);
    } // end nullJobCheck(

} // end Actor
