// File: c:/ddc/Java/Actor11/Task.java
// Date: Mon Mar 19 15:49:16 2018
// (C) OntoOO/ Dennis de Champeaux

package actor11;

import java.util.*;
import java.io.*;

// A task gets a job and will repeatedly execute it until it returns false.
// Execution can get suspended when a job with a higher priority is found
// in an actor's priorityQueue.
public class Task implements Runnable, Comparable {
    private Job job = null;
    private ActorBase actor = null;
    private String jobClassName = null;
    private int priority = 0;
    public int getPriority() { return priority; }
    public void setPriority(int p) { priority = p; }
    public String getJobClassName() { return jobClassName; }
    public int compareTo(Object t) { 
	return ((Task) t).getPriority() - priority; }

    public Task(Job job) { 
	this.job = job; 
	actor = job.getActor(); 
	jobClassName = job.getClass().getName();
	priority = job.getPriority();
    }
    private boolean again = true;
    private boolean repeat = true;
    private Thread myThread = null;
    public void start() { 
	// actor.addTrace("Task: starting run for job: " + jobClassName);
	myThread = new Thread(this);
	again = true;
	repeat = true;
	myThread.start(); 
    } // end start

    public void stop() { 
	// actor.addTrace("Task: stopping run for job: " + jobClassName);
	repeat = false; 
	wakeUp();
    } // end stop

    public void run() {
	// actor.addTrace("Task: entering run for job: " + job.ascii());
	while ( repeat && again ) {
	    priority = job.getPriority(); // could be changed
	    if ( 0 == priority ) break; // was killed
	    // Check whether there is a higher priority task
	    PriorityQueue taskQueue = actor.getTaskQueue();
	    synchronized ( actor ) {
		if ( 0 < taskQueue.size() ) {
		    Task task0 = (Task) taskQueue.peek();
		    actor.addTrace("Task: task0::" + 
		        jobClassName + " " + priority + " " +
		        task0.getJobClassName() + " " + task0.getPriority() );
		    if ( priority < task0.getPriority() ) {
			myThread = null;
			 actor.addTrace("Task: suspending run for job: " + 
			                jobClassName);
			taskQueue.poll(); // remove first element 
			actor.insertTask(this); // suspend
			actor.setTaskFocus(task0);
			task0.start();
			return;
		    }
		}
	    }
	    try { again = job.execute(); } // do the job
	    catch (Exception ex) {
		actor.addTrace("########## Task: EXCEPTION for job: " + 
			       jobClassName);
		actor.addTrace("########## Task: " + ex.getMessage());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		ex.printStackTrace(ps);
		actor.addTrace("########## Task: <br>" + 
			       "<pre>" +
			       baos.toString() +
			       "</pre>");
		again = false;
	    }
	}
	synchronized (this) { myThread = null; }
	// actor.addTrace("Task: exiting run for job: " + jobClassName);
	actor.setTaskFocus(null); 
	actor.wakeUpMonitorTaskFocus(); // get another task

    } // end of run()

    /** 
	The job executed by a task may do a Thread.sleep(updateInterval).
	wakeUp() wakes up such a sleeping task.  See the class NullJob 
	for a job that invokes Thread.sleep(updateInterval).
	Ignore if the job has finished already.
     */
    public void wakeUp() { 
	synchronized (this) {
	    if (null == myThread) return;
	    if (!myThread.interrupted() )
		try { myThread.interrupt(); }
		catch (Exception ignore) {} }
    } // end wakeUp

} // end of Task 


