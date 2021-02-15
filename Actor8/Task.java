// File: c:/ddc/Java/Actor8/Task.java
// (C) OntoOO Inc 2005 Apr

package actor8;

import java.util.*;
import java.io.*;


public class Task implements Runnable, Comparable  {
    private Job job = null;
    private ActorBase actor = null;
    private String jobClassName = null;
    private int priority = 0;
    public int getPriority() { return priority; }
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
    }
    public void stop() { 
	// actor.addTrace("Task: stopping run for job: " + jobClassName);
	repeat = false; 
	wakeUp();
    }

    public void run() {
	// actor.addTrace("Task: entering run for job: " + jobClassName);
	while ( repeat && again ) {
	    // Check whether there is a higher priority task
	    PriorityQueue taskQueue = actor.getTaskQueue();
	    synchronized ( actor ) {
		if ( 0 < taskQueue.size() ) {
		    Task task0 = (Task) taskQueue.peek();
		    // actor.addTrace("Task: " + 
		    //    jobClassName + " " + priority + " " +
		    //    task0.getJobClassName() + " " + task0.getPriority() );
		    if ( priority < task0.getPriority() ) {
			myThread = null;
			// actor.addTrace("Task: suspending run for job: " + 
			//                jobClassName);
			taskQueue.poll(); // remove first element 
			actor.insertTask(this);
			actor.setTaskFocus(task0);
			task0.start();
			return;
		    }
		}
	    }
	    try { again = job.execute(); }
	    catch (Exception ex) {
		actor.addTrace("########## Task: EXCEPTION for job: " + jobClassName);
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
	actor.wakeUpMonitorTaskFocus();

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


