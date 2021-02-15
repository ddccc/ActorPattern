// File: c:/ddc/Java/Actor10A/Monitor.java
// Date: Thu Jun 13 13:57:15 2019
// (C) Dennis de Champeaux/ OntoOO 

package actor10A;

import java.util.*;
import java.io.*;


public abstract class Monitor  {
    protected int previousInt = 0;
    protected float previousFloat = 0;
    protected String previousString = "";
    protected Object previousObject = null;

    protected ActorBase actorMonitored = null;
    protected boolean stopped = false;
    protected RunMonitor myRunMonitor = null;
    
    protected void setMyRunMonitor(RunMonitor rm) {
	myRunMonitor = rm;
    }

    /*  state can register a down stream effect; if used, check() can do:
	state.wakeUp() to notify other monitors that depend on state
    */
    protected State state = null; 

    public Monitor (ActorBase actor, State state) { 
	actorMonitored = actor; 
	this.state = state;
    }
    public Monitor (ActorBase actor) { this(actor, null); }

    public ActorBase getActorMonitored() { return actorMonitored; }
    /* RunMonitor calls check() periodically.
       A state can also trigger check() to run via the wakeUp() feature
       in RunMonitor, as mentioned above
    */
    abstract public void check(); 
    protected int lastCnt = 0; 
    public int getLastCnt() { return lastCnt; }
    protected int previousCnt = 0; 

    protected void check0() {
	previousCnt = lastCnt;
	lastCnt = actorMonitored.getCnt();
    }

} // end Monitor

/* There is at most one task running, which is referred to in Actor by the taskFocus
   This monitor keeps an eye on the focus.  If no task is running it restarts the
   nulltask when the taskList is empty, otherwise it grabs the first task.
*/
class MonitorTaskFocus extends Monitor {
    public MonitorTaskFocus (ActorBase actor) { super(actor); }
    public void check() {
	check0();
	// actorMonitored.addTrace("MonitorTaskFocus: checking task focus ....");
	Task taskFocus = actorMonitored.getTaskFocus();
	if ( null == taskFocus ) {
	    PriorityQueue taskQueue = actorMonitored.getTaskQueue();
	    Task task0;
	    synchronized ( actorMonitored ) {
		task0 = 
		    ( 0 < taskQueue.size() ?
		      (Task) taskQueue.poll() :
		      actorMonitored.getNullTask() );
	    }
	    actorMonitored.setTaskFocus(task0);
	    task0.start();
	    actorMonitored.addTrace("MonitorTaskFocus: new task= " + 
				    task0.getJobClassName());
	}
    } // end check()
} // end MonitorTaskFocus

class MonitorFetchNode extends Monitor {
    public MonitorFetchNode(Actor actor, State hasSuccessorsS) {
	super(actor, hasSuccessorsS); 
	// state = hasSuccessorsS
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Node n = Node.getNodeO();
	if ( null == n ) {
	    actor.addTrace("MonitorFetchNode:: No node found");
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();
	    return;
	}
	n.setClosed();
	if ( n.isGoal() ) { 
	    n.processGoalNode(actor);
	    actor.addTrace("MonitorFetchNode:: Solution found ---- # " +
			   n.getNodeCnt());
	    StopAngel stopAngel = new StopAngel(actor);
	    stopAngel.start();	    
	    return;
	}
	Vector<Node> successorNodes = n.successors();
	state.setObj(successorNodes);
	state.wakeUp();
    } // end check
} // end  MonitorFetchNode


class MonitorNextNode extends Monitor {
    public MonitorNextNode(Actor actor, State hasSuccessorsS) {
    super(actor, hasSuccessorsS); 
       // state = hasSuccessorsS
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	Vector<Node> successorNodes = (Vector<Node>) state.getObj();
	// actor.addTrace("MonitorNextNode |successorNodes| = " + 
	//	          successorNodes.size());
	if ( 0 == successorNodes.size() ) {
	    State fetchNodeS = actor.getFetchNodeS();
	    fetchNodeS.wakeUp();
	    return;
	}
	Node n = successorNodes.firstElement();
	successorNodes.remove(0); // delete first element
	String key = n.getKey();
	Node n2 = Node.getNode(key);
	if ( null == n2 ) { // n is new node
	    Node.addNodeO(n); // add to the priority queue
	    Node.addNode(key, n); // add to the hash table
	} else { // existing node
	    if ( n.G() < n2.G() ) {
		// n has a shorter path to the start node
		Node.deleteNode(key); // delete
		if ( n2.getIsOpen() ) Node.deleteNodeO(n2);  // delete
		Node.addNodeO(n); // add to the priority queue
		Node.addNode(key, n);  // add to the hash table
	    } // otherwise ignore node n
	}
	state.wakeUp(); // go investigate next node
    } // end check
} // end MonitorNextNode

