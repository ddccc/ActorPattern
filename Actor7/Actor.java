// File: c:/ddc/Java/Actor7/Actor.java
// Date: Thu Jan 26 19:45:31 2006

package actor7;

import java.util.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    private Theory theory = new Theory(false);
    public Theory getTheory() { return theory; };

    // just stats
    private int workCnt = 0;
    public int getWorkCnt() { return workCnt; }
    public void incrementWorkCnt() { workCnt++; }

    private Vector vec = new Vector(); // sorted open nodes
    public void insertOpenNode(Node n) {
	synchronized (this) {
	    if ( checkIn(n, vec) ) return;
	    if ( checkIn(n, closedNodes) ) return;
	    System.out.print("new node: ");
	    n.printNode();
	    vec.addElement(n); 
	}
    } // end insertOpenNode
    public Node getBestNode() {
	synchronized (this) {
	    if ( vec.isEmpty() ) return null;
	    Node n = (Node) vec.lastElement(); // lowest F-value
	    vec.removeElementAt(vec.size() -1); // delete it
	    addClosedNodes(n);
	    return n;
	}
    } // end getBestNode

    private Vector closedNodes = new Vector(); 
    public void addClosedNodes(Node n) { closedNodes.addElement(n); }
    public int closedNodesSize() { return closedNodes.size(); }

    public Node bestNode = null;

    public void report() {
	System.out.println("\n#closed: " + closedNodes.size() + " " +
			   "#open: " + vec.size());
    }

    // states

    // This state notifies monitorBestNodeAvailable 
    // that a new node needs to be worked on
    private State bestNodeS = new State(); 
    public State getBestNodeS() { return bestNodeS; }

    // This state notifies monitorCheck that a solve attempt completed
    // and needs to be investigated for a failure or not. 
    // A non-failure entails a solution or a lower zero count, which need
    // not guarantee that a solution is possible in that configuration
    private State bestNodeAvailable = new State(); 

    public boolean checkIn(Node ny, Vector vec) {
	for (int i = 0; i < vec.size(); i++)
	    if ( ny.equals((Node) vec.elementAt(i))) return true;
	return false;
    }

    private void vecSort(Vector vec, int index1) { 
	// push the last entry down so that vec is reverse sorted
	if ( 0 == index1 ) return;
	Node s1 = (Node) vec.elementAt(index1);
	int f1 = s1.G();
	int index0 = index1-1;
	Node s0 = (Node) vec.elementAt(index0);
	int f0 = s0.G();
	if ( f1 <= f0 ) return;
	vec.setElementAt(s0, index1); vec.setElementAt(s1, index0); // swap
	vecSort(vec, index0);
    } // end vecSort

    // -------  alert generators:
    public void checkNode(Node ny) {
	int lc2 = ny.getLeftNumCannibals(); int rc2 = ny.getRightNumCannibals();
	int lm2 = ny.getLeftNumMissionaries(); int rm2 = ny.getRightNumMissionaries();
	String trigger = "||(" +
	    // lc2 < 0 || 3 < lc2 ||
	    "<(" + lc2 + " 0) <(3 " + lc2 + ") " +
	    // rc2 < 0 || 3 < rc2 ||
	    "<(" + rc2 + " 0) <(3 " + rc2 + ") " +
	    // lm2 < 0 || 3 < lm2 ||
	    "<(" + lm2 + " 0) <(3 " + lm2 + ") " +
	    // rm2 < 0 || 3 < rm2 )
	    "<(" + rm2 + " 0) <(3 " + rm2 + ") " +
	    //( 0<lm2 && lm2<lc2 )
	    "(&&(<(0 " + lm2 + ") <(" + lm2 + " " + lc2 + "))) " +
	    //( 0<rm2 && rm2<rc2 ) 
	    "(&&(<(0 " + rm2 + ") <(" + rm2 + " " + rc2 + "))) " +
	    ")";
	Formula triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Formula) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "checkNode() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new InsertNode(this, triggerAtom);
	// dispatcher.setActor(this);
	DoInsertNode doInsertNode =
	    new DoInsertNode(this, ny);
	dispatcher.setJob(doInsertNode);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.setTheory(theory);
	dispatcher.init();

	addAlert(alert); // a queue
	addTrace("Actor: insertNode/alert.wakeUp ...");
	wakeUp();
    } // end checkNode

	// ----- constructor
    public Actor(Node start) { 
	super("MaC"); 

	vec.addElement(start);

	actorMeta = new ActorMeta("Meta", this);

	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus);

	// This monitor checks whether a best node is available.
	// If so it triggers MonitorExpandNode

	RunMonitor monitorBestNodeAvailable =
	    new RunMonitor(new MonitorBestNodeAvailable(this, 
					bestNodeAvailable), 100000);
	bestNodeS.addConsumer(monitorBestNodeAvailable);
	monitors.addElement(monitorBestNodeAvailable);

	// This monitor expands the best node and launches alerts

	RunMonitor monitorExpandNode =
	    new RunMonitor(new MonitorExpandNode(this), 100000);
	bestNodeAvailable.addConsumer(monitorExpandNode);
	monitors.addElement(monitorExpandNode);

	// more monitors here
    }

    // -------- trace stuff ------------

    public void addTrace(String message) {
	this.addTrace0(cnt + " " + name + " " + message);
    }
    public void addTrace0(String message) {
	// System.out.println(message);
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
		 " taskQueue.size(): " + taskQueue.size());
	actorMeta.stop();

	// stop the conscious loop 
	again = false; 
	wakeUp();

	// stop the monitors
	rmTaskFocus.stop();
	stopMonitors(monitors);
	if (null != taskFocus)	taskFocus.stop();

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
