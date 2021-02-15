// File: c:/ddc/Java/Actor7A/Job.java
// (C) OntoOO Inc 2005 Apr
// Date: Sat Sep 15 20:56:28 2018

package actor7A;

import java.util.*;
import java.io.*;

import fol.Formula;

// Job is the super class for the utility task NullJob, ShowAlertTrace &
// for Dispatcher and its subclasses, as well as for the classes 
// that capture actions specified inside dispatchers; see the example below.  
public abstract class Job {
    static final protected int updateInterval = 200; // 0.1 secs
    protected ActorBase actor = null;
    public ActorBase getActor() { return actor; }
    protected int priority = 5;
    public void setPriority(int i) { priority = i; }
    public int getPriority() { return priority; }
    protected String actorName = "unknown";
    public Job() {}
    public Job(ActorBase actor) { 
	this.actor = actor; 
	actorName = actor.getName();
    }
    abstract public boolean execute();
    abstract public String ascii();
} // end Job

/*
How to change the priority of a launched job with class name jobName:

- get the PriorityQueue taskQueue in actor with actor.getTaskQueue()
- iterate through taskQueue to get taskI so that
  taskI.getJobClassName() equals jobName
- delete taskI from the taskQueue with taskQueue.deleteTask(taskI)
- fetch the job from taskI with taskI.getJob()
- change the priority of job with job.setPriority(pNew)
- change the priority of taskI with taskI.setPriority(pNew)
- reinsert taskI in taskQueue withh actor.insertTask(taskI)
*/

class NullJob extends Job {
    NullJob(ActorBase actor) { 
	super(actor); 
	priority = 1;
    }
    private int cnt = 0;
    public boolean execute() {
	cnt = actor.getCnt();
	actor.nullJobCheck(cnt);
	// other stuff here
	try {
	    Thread.sleep(updateInterval);
	} catch (InterruptedException ignore) {}
	return true;
    } // end execute()

    public String ascii() { return "NullJob cnt: " + cnt; }
} // end NullJob 


class ShowAlertTrace extends Job {
    ShowAlertTrace(ActorMeta actor) { 
	super(actor); 
	priority = 1;
    }
    public boolean execute() {
	ActorMeta actorMeta = (ActorMeta) actor;
	Actor actor0 = actorMeta.getActor();

	Vector alertTrace = actor0.getAlertTrace();
	int lng = alertTrace.size();
	actor.addTrace
	    ("AnalyzeAlertTrace: for: " + actor0.getName() +
	     " alertTrace size:" + lng
	     );
	StringBuffer sb = new StringBuffer("AnalyzeAlertTrace: <b>alerts:</b>");
	for (int i = 0; i < lng; i++) {
	    Alert alert = (Alert) alertTrace.elementAt(i);
	    Dispatcher d = alert.getDispatcher();
	    Formula trigger = d.getTrigger();
	    String jobName = d.getJob().getClass().getName();
	    boolean launched = alert. getLaunched();
	    sb.append(
		"<br>" +
		trigger.html() + " " +
		jobName + " " +
		launched);
	}
	actor.addTrace(sb.toString());
	return false; // end job
    } // end execute()

    public String ascii() { return "ShowAlertTrace"; }
} // end ShowAlertTrace 

// add other subclasses here:

class DoProcessNodes extends Job {
    private Actor myActor = null;
    private Vector<Node> successorNodes;
    private State fetchNodeS;
    int lng = 0;
    int nx = 0; // index into successorNodes
    DoProcessNodes(Actor actor, Vector<Node> successorNodes, State state) {
	super(actor); 
	this.successorNodes = successorNodes;
	fetchNodeS = state;
	priority = 11;
	myActor = actor;
	lng = successorNodes.size();
    }
   public boolean execute() {
       if ( lng <= nx ) {
	   fetchNodeS.wakeUp();
	   return false; // finished
       }
       Node n = successorNodes.elementAt(nx++);
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
       return true; // repeat
   } // end execute()
    public String ascii() { return "DoProcessNodes"; }
} // end DoProcessNodes

