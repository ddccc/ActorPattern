// File: c:/ddc/Java/Actor10/Actor.java
// Date: Wed Apr 25 15:39:03 2018
package actor10;

import java.util.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // private Theory theory = new Theory(false);
    // public Theory getTheory() { return theory; };
    private Parser parser = new Parser(false);
    public Parser getParser() { return parser; }

    // Application specific items here
    // private Grid grid = null;
    
    // This is NOT a faithful implementation of the A* algorithm in the sense 
    // that a shorted path is NOT being looked for:
    // -- Termination is decided when a good candidate node is encountered
    // -- A closed node recreated with a better g-value is NOT revisited
 
    // sorted open nodes
    private PriorityQueue<Node> openNodes = new PriorityQueue<Node>(); 
    public PriorityQueue<Node> getOpenNodes() { return openNodes; }
    public void insertNode(Node nx) {
	synchronized( openNodes ) { openNodes.add(nx); }
    }
    public Node getFirstNode() { 
	synchronized( openNodes ) { return openNodes.poll(); }
    }
    public int numberOfOpenNodes() { return openNodes.size(); }

    // closed nodes
    private HashSet closedNodes = new HashSet();
    /*
    public void addNode(Node node) {
	String key = node.getKey();
	closedNodes.put(key, node);
    }
    */
    public void addKey(String key) {
	closedNodes.add(key);
    }

    public boolean nodeKnownQ(String key) { 
	return closedNodes.contains(key); 
    }
	
    private boolean problemSolved = false;
    public boolean problemSolvedQ() { return problemSolved; }
    public void setProblemSolved() { problemSolved = true; }

    // state/monitor sequences generate alerts

    // states here
         // private State squareState = new State();
    private State againS = new State(); // go fetch a node 

    /*
    private State bestNode = new State(); // result of fetch, can be null 
    public State getBestNode() { return bestNode; }

    private State moves = new State(); // has a list of candidate moves 
    */

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
 
    /* A*-like node expansion (initiated by the monitor fetch node) uses in sequence:
       -- generateMoves for potential box moves
       -- generateNodes for filtering out impossible moves
       -- proposeNode for checking whether he candidate node must be rejected after 
          all because the configuration has been encountered already
       -- checkNode which finally creates the alert that starts the deliberation 
          phase
       Note: the subclass of Dispatcher that generates the instances doing the 
       dispatching is CheckNode. 
          
 
     */

    private int numberOfMoves = 0;

    public void generateMoves(Node node) {
	System.out.println();
	System.out.println("||||||||||||||||||| GenerateMoves #: " + node.getMyCnt() +
			   " |openNodes| " + numberOfOpenNodes());
	Grid grid = node.getGrid();
	int [][] boxes = grid.getBoxes();
	int [][] integers = grid.getIntegers();
	Vector boxmoves = new Vector();
	Vector boxmoves2 = new Vector();

	// vectori = (boxy/ boxx/ nesw1234) adjacent locs are free
	int rows = boxes.length;
	for (int i = 0; i < rows; i++) {
	    int [] boxi = boxes[i];
	    int by = boxi[0], bx = boxi[1];
	    if ( integers[by-1][bx] == Grid.FREE &&
		 integers[by+1][bx] == Grid.FREE ) {
		if ( grid.hasGoalQ(by+1, bx) )
		    boxmoves.add(new int[] {by, bx, 1}); // N->S
		else 
		    boxmoves2.add(new int[] {by, bx, 1}); // N->S
		if ( grid.hasGoalQ(by-1, bx) )
		    boxmoves.add(new int[] {by, bx, 3}); // S->N
		else
		    boxmoves2.add(new int[] {by, bx, 3}); // S->N
	    }
	    if ( integers[by][bx+1] == Grid.FREE &&
		 integers[by][bx-1] == Grid.FREE ) {
		if ( grid.hasGoalQ(by, bx+1) )
		    boxmoves.add(new int[] {by, bx, 4}); // W->E
		else
		    boxmoves2.add(new int[] {by, bx, 4}); // W->E
		if ( grid.hasGoalQ(by, bx-1) )
		    boxmoves.add(new int[] {by, bx, 2}); // E->W
		else
		    boxmoves2.add(new int[] {by, bx, 2}); // E->W
	    }
	}
	int lng2 = boxmoves2.size();
	for (int i = 0; i < lng2; i++) // promote moves to goal locs
	    boxmoves.add(boxmoves2.elementAt(i));
	// candidate box moves provided the start locs can be reached
	addTrace("MonitorGenerateMoves boxmoves: " + boxmoves.size());
	// System.out.println("MonitorGenerateMoves boxmoves: " + boxmoves.size());
	numberOfMoves = boxmoves.size();
	if ( 0 == numberOfMoves ) {
	    System.out.println("|-|-|-|-|-|-|-|- No box moves of node:: " + node.getMyCnt());
	    System.out.println("alerts.size(): " + alerts.size());
	    System.out.println("actions.size(): " + actions.size());
	    System.out.println("taskQueueSize() " + getTaskQueueSize());
	    grid.print();
	    againS.wakeUp();
	    return;
	}
	node.setBoxmoves(boxmoves);
	generateNodes(node);
    } // end generateMoves

    public void generateNodes(Node node) {
	System.out.println();
	System.out.println("||||||||||||||||||| GeneratesNodes #: " + node.getMyCnt() +
			   " |openNodes| " + numberOfOpenNodes());
	Vector boxmoves = node.getBoxmoves();
	Grid grid = node.getGrid();
	int manx = grid.getManx(), many = grid.getMany();
	int [][] integers = grid.getIntegers();
	// System.out.println("MonitorGenerateNodes boxmoves #: " + boxmoves.size());
	// System.out.println("---------------------");
	int proposedCnt = 0;
	for (int i = 0; i < boxmoves.size(); i++ ) {
	    if ( problemSolved ) return;
	    // boolean atWall = false; boolean atWall2 = false; boolean inCorner = false;
	    int[] triple = (int[]) boxmoves.elementAt(i);
	    int by = triple[0], bx = triple[1], direction = triple[2];
	    System.out.println("i: " + i + 
			       " triple by: " + by + " bx " + bx + " dir " + direction);
	    // Vector assertions = new Vector();
	    if ( 1 == direction ) {
		if ( !grid.hasPath(manx, many, bx, by-1) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int by2 = by+1;
		if ( grid.isBlocked(by2, bx) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		if ( proposeNode(node, direction, bx, by) ) 
		    proposedCnt++;
		else numberOfMoves--;
	    } else 
	    if ( 3 == direction ) {
		if ( !grid.hasPath(manx, many, bx, by+1) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int by2 = by-1;
		if ( grid.isBlocked(by2, bx) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		if ( proposeNode(node, direction, bx, by) ) 
		    proposedCnt++;
		else numberOfMoves--;
	    } else 
	    if ( 2 == direction ) {
		if ( !grid.hasPath(manx, many, bx+1, by) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int bx2 = bx-1;
		if ( grid.isBlocked(by, bx2) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		if ( proposeNode(node, direction, bx, by) ) 
		    proposedCnt++;
		else numberOfMoves--;
	    } else 
	    if ( 4 == direction ) {
		if ( !grid.hasPath(manx, many, bx-1, by) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int bx2 = bx+1;
		if ( grid.isBlocked(by, bx2) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		if ( proposeNode(node, direction, bx, by) ) 
		    proposedCnt++;
		else numberOfMoves--;
	    } else 
		addTrace("****** MonitorGenerateNodes::  " +
			       "not recognized direction");
	}
	if ( 0 == proposedCnt || 0 == numberOfMoves ) {
	    System.out.println("||||||||||||||||||| Dead node #: " + node.getMyCnt());
	    againS.wakeUp();
	}

	// System.exit(0);
	
    } // end generateNodes

    public boolean proposeNode(Node node, int direction, int bx, int by) {

	if ( problemSolved ) return true;

	// Check here whether the box hits a wall 
	// direction 1: N->S, 3: S->N, 2: E->W, 4: W->E

	System.out.println();
	System.out.println("||||||||||||||||||| proposedNode #: " 
			   + node.getMyCnt());
	addTrace("proposeNode by " + by + " bx " + bx + 
		 " direction " + direction);  
	// System.out.println(
	//	 "proposeNode by " + by + " bx " + bx + " direction " + direction);  
	/*
	Vector nextAssertions = node.getNextAssertions();
	if ( 0 < nextAssertions.size() ) {
	    System.out.println("nextAssertions" + 
	                       nextAssertions.toString());
	    System.out.println("open nodes # " + openNodes.size());
	    // System.exit(0);
	    node.print();
	}
	*/

	Grid grid = node.getGrid();
	int [][] boxes = grid.getBoxes();
	int bx2 = bx, by2 = by, mx = bx, my = by;
	int [][] boxes2 = new int[boxes.length][2];
	switch (direction) {
	case 1: 
	    by2 = by + 1;
	    break;
	case 3:
	    by2 = by - 1; 
	    break;
	case 2:
	    bx2 = bx-1;
	    break;
	case 4:
	    bx2 = bx+1;
	    break;
	default:
	    System.out.println("*** Error in proposeNode: direction= " +
			       direction);
	    return false;
	}

	for (int i = 0; i < boxes.length; i++ ) {
	    int [] rowi = boxes[i];
	    if ( by == rowi[0] && bx == rowi[1] ) {
		boxes2[i] = new int [] { by2, bx2 };
	    } else boxes2[i] = rowi;
	}
	/*
	for (int i = 0; i < boxes2.length; i++ ) {
	    int [] rowi = boxes2[i];
	    System.out.println("proposeNode: " + rowi[0] + " " + rowi[1]);
	}
	*/

	Grid grid2 = new Grid(boxes2, mx, my);
	// System.out.println("grid2::: mx " + mx + " my " + my); 
	// grid2.print();
	/*
	if ( grid2.isSolution() ) {
	    System.out.println(" ||||||||||||| SOLUTION found");
	    grid2.print();
	    System.exit(0);
	}
	*/
	String key = grid2.getKey();
	synchronized(this) {
	    if ( nodeKnownQ(key) ) { // configuration already encountered
		System.out.println("|-|-|-|-|-|- configuration already found " + key);
		// grid2.print();
		// skip
		// againS.wakeUp();
		return false;
	    } else addKey(key);
	}

	// addTrace("proposeNode new grid ... :");
	// grid2.print();
	Node nodeCandidate = new Node(node, grid2, 1 + node.getG());
	nodeCandidate.setFoundAssertions(node.getNextAssertions());
	// nodeCandidate.print();
	// call checkNode alert generator
	checkNode(nodeCandidate);
	return true;
    } // end proposeNode


    // -------  alert generators
    // A last monitor in a monitor-state sequence calls an alert generator.
    // REWRITE the explanation
    // An alert contains a condition (triggerAtom), its dispatcher and
    // an action if the condition is OK.  
    // The alert can be seen as a Fodor's LOT expression/ an 
    // if-then-else rule of steroids.
    // The dispatcher is responsible for evaluating the condition (trigger)
    // to decide whether to launch the action or not; see the comment in
    // Dispatcher.
    // This PDA application is NOT using the theorem prover to evaluate the 
    // condition; see the comment in Row1.
    // Another perspective is that the action is instead a goal that requires 
    // planning to obtain a plan/ conditional plan/ etc to acually change the world

    public void checkNode(Node nodeCandidate) {
	addTrace("checkNode");
	System.out.println("||||||||||||||||||| Entering checkNode for: # " + 
			   nodeCandidate.getMyCnt());
	// nodeCandidate.print();
	// postpone this test to the dispatcher
	/*
	Grid grid = nodeCandidate.getGrid();
	if ( grid.isSolution() ) {
	    setProblemSolved();
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    System.out.println("||||||||||||| SOLUTION found " +
			       "openNodes #: " + numberOfOpenNodes());
	    nodeCandidate.printSolution();
	    StopAngel stopAngel = new StopAngel(this);
	    stopAngel.start();	 
	    return;
	}
	*/

	int priority = 9;

	// Atom triggerAtom = grid.getTriggerAtom(); // "No(trigger)"

	String trigger = "No(trigger)";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "checkNode() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    againS.wakeUp();
	    return;
	}

	// addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	Dispatcher dispatcher = 
	    new CheckNode(this, triggerAtom, nodeCandidate, againS);
	// dispatcher.init();
	dispatcher.setPriority(priority);
	// dispatcher.setActor(this);
	DoInsertNodeCandidate doCheckNodeCandidate =
	    new DoInsertNodeCandidate(this, nodeCandidate, againS);
	doCheckNodeCandidate.setPriority(priority);
	dispatcher.setJob(doCheckNodeCandidate);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	// dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	/*
	addTrace("Actor: checkNode/alert.wakeUp ...");
	System.out.println("1checkNode alerts.size() " + alerts.size());
	System.out.println("1checkNode actions.size() " + actions.size());
	System.out.println("1taskQueueSize() " + getTaskQueueSize() );
	*/
	wakeUp();
	Thread.yield();
	/*
	System.out.println("2checkNode alerts.size() " + alerts.size());
	System.out.println("2checkNode actions.size() " + actions.size());
	System.out.println("2taskQueueSize() " + getTaskQueueSize() );
	*/
    } // end checkNode

	// ----- constructor
    // public Actor(Square square) { 
    public Actor(Grid grid) { 
	super("Sokoban"); 
	actorMeta = new ActorMeta("MetaSokoban", this);
	addKey(grid.getKey());
	// grid.print();
	// put grid in a node
	Node node = new Node(null, grid, 0);
	node.print();
	insertNode(node); // open nodes

	// other initializations
 
	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	// monitors.addElement(rmTaskFocus); 

	// other monitors here
	RunMonitor monitorFetchNode =
	    new RunMonitor(new MonitorFetchNode(this), 100000);
	againS.addConsumer(monitorFetchNode);
	monitors.addElement(monitorFetchNode);

	// more monitors here
    }

    // -------- trace stuff ------------

    public void addTrace(String message) {
	this.addTrace0(cnt + " " + name + " " + message);
    }
    public void addTrace0(String message) { // or change as needed
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
	againS.wakeUp(); // get the ball running
	while ( again ) {
	    cnt++; 
	    // addTrace("Actor.run().cnt: " + cnt);

	    /* Example taken from an application how the message queue
	       can be used:
	    Object message = fetchQueue();
	    if ( null != message ) {
		if ( message instanceof Invitation ) {
		    Invitation in = (Invitation) message;
		    invitations.addElement(in);	
		} else
		if ( message instanceof Exchange ) {
		    synchronized(synchroObject) {
			if ( wait <= 1 ) {
			    response = (Exchange) message;
			    if ( 1 == wait ) {	
				wait = 2; 
				waitThread.interrupt();
			    } else wait = 2; 
			}
		    }
		}
	    }
	     */

	    // dispatch alerts, wrapped due to concurrency
	    synchronized ( alerts ) {
		while ( 0 < alerts.size() ) {
		    Alert alert = (Alert) alerts.removeFirst();
		    Dispatcher d = alert.getDispatcher();
		    /*
		    addTrace("Actor: Found alert!! for: " +
			     d.getJob().getClass().getName() +
			     " trigger: " + d.getTrigger().html());
		    */
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
		    // addTrace("Actor: Found job!! for: " + job.getClass().getName());
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
		 " taskQueue.size(): " + taskQueue.size());
	addTrace("alerts.size(): " + alerts.size());
	addTrace("actions.size(): " + actions.size());

	// System.out.println(taskQueue.toString());

	actorMeta.stop();

	// stop the conscious loop 
	again = false; 
	wakeUp();

	// stop the monitors
	rmTaskFocus.stop();
	stopMonitors(monitors);
	if ( null != taskFocus ) taskFocus.stop();

	// check that they have stopped 
	boolean checkAgain = true;
	int tryCnt = 0;
	while ( checkAgain ) {
	    tryCnt++;
	    if ( 10 < tryCnt ) {
		addTrace("Forced stop ...");
		break;
	    }
	    checkAgain = false;
	    if ( !stopped() ) checkAgain = true;
	    if ( !rmTaskFocus.stopped() ) checkAgain = true;
	    if ( notStoppedMonitor(monitors) ) checkAgain = true;
	    if ( null != taskFocus ) checkAgain = true; 
	    if ( checkAgain ) {
		addTrace("Actor: ** Waiting for threads to stop ... tryCnt: " + tryCnt);
		try {
		    Thread.sleep(updateInterval);
		} catch (InterruptedException ignore) {}
		// Thread.yield();
	    }
	}
	addTrace("Actor: All Threads stopped");

	addTrace("Actor: Stopped run of Actor: " + name);
	/* // optional logging
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
	*/
	// additional logging here

    } // end stop()

    // Application specific definition of nullJobCheck::
    // which is called in NullJob,
    // which is used in ActorBase to define nj, nullTask and taskFocus
    // taskFocus is started in start() above
    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
         
	addTrace("Actor nullJobCheck cnt: " + cnt);
    } // end nullJobCheck

} // end Actor

class Node implements Comparable {
    private static int cnt = 0;
    private static int getNextCnt() { cnt++; return cnt; }
    // nodes have unique configurations; a grid is tested to avoid duplicates
    private int myCnt = 0;
    public int getMyCnt() { return myCnt; } 
    private Node parent = null;
    private Grid grid = null;
    public Grid getGrid() { return grid; }
    private int [][] goals = null;
    // public int [][] getGoals() { return goals; }
    private int [][] boxes = null;
    // public int [][] getBoxes() { return boxes; }
    private int manx, many;
    private int g = 0;
    public int getG() { return g; } 
    public void setG(int x) { g = x; }
    public int G() { return g; }
    private int h = 0;
    public int H() { return h; } 
    public int numberBoxesOnGoal() { return grid.numberBoxesOnGoal(); }
    // /*
    private void calculateH() {
	int rows = boxes.length;
	for (int row1 = 0; row1 < rows; row1++) {
	    int minimum = 100;
	    int bx = boxes[row1][0]; int by = boxes[row1][1]; 
	    for (int row2 = 0; row2 < rows; row2++) {
		int gx = goals[row2][0]; int gy = goals[row2][1]; 
		int dist = abs(bx-gx) + abs(by-gy);
		if ( dist < minimum ) minimum = dist;
		
		// if ( bx == gx && by == gy ) {
		    // h = h-1; // heuristic bonus
		// System.out.println("|||||||| bonus bx " + bx + " by " + by);
		// } 
	    }
	    h = h + minimum;
	}
	if ( null != parent && 
	     numberBoxesOnGoal() > parent.numberBoxesOnGoal() ) h = h-10;
    } 
    // */
    /*
    private void calculateH() {
	int rows = boxes.length;
	for (int row1 = 0; row1 < rows; row1++) {
	    int bx = boxes[row1][0]; int by = boxes[row1][1];
	    int gx = goals[row1][0]; int gy = goals[row1][1];
	    h = h + abs(bx-gx) + abs(by-gy);
	}
    }
    */
    private int abs(int x) { return ( 0 <= x ? x : -x); }
    public int F() { return G() + 4*H(); }
    private String key = null;
    public String getKey() { return key; }
    private Vector nextAssertions = new Vector();
    public void setNextAssertions(Vector n) { nextAssertions = n; }
    public Vector getNextAssertions() { return nextAssertions; }
    public Vector foundAssertions = new Vector();
    public Vector getFoundAssertions() { return foundAssertions; }
    public void setFoundAssertions(Vector v) { foundAssertions = v; }

    public Node(Node p, Grid gr, int gx) {
	myCnt = getNextCnt();
	parent = p;
	grid = gr; g = gx;
	goals = grid.getGoals();
	boxes = grid.getBoxes();
	manx = grid.getManx(); many = grid.getMany();
	calculateH();
	key = grid.getKey();
    }
    public int compareTo(Object o2) { 
	if ( this.equals(o2) ) return 0;
	return compare(this, o2); 
    }
    public int compare(Object o1, Object o2) {
	Node n1 = (Node) o1;
	Node n2 = (Node) o2;
	int out = n1.F() - n2.F();
	return ( 0 != out ? out : n2.getMyCnt() - n1.getMyCnt() );
    }
    public boolean isSolution() { return grid.isSolution(); }

    Vector boxmoves = null;
    public void setBoxmoves(Vector b) { boxmoves = b; }
    public Vector getBoxmoves() { return boxmoves; }

    public void print() {
	System.out.println();
	System.out.println("Node: cnt " + myCnt + " G " + G() + " H "  + H() + 
			   " F " + F() + " " + key +
			   " parent# " + (null == parent? 0 : parent.getMyCnt()) );
	// System.out.println("factsF " + grid.getFactsF().html());
	// System.out.println("foundAssertions " + foundAssertions.toString());
	grid.print();
    }
    public void printSolution() {
	print();
	if ( null != parent )
	    parent.printSolution();	
    }
} // end of Node
