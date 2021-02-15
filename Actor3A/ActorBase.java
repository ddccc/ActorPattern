// File: c:/ddc/Java/Actor3A/ActorBase.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Sat Sep 15 20:53:38 2018

package actor3A;

import java.util.*;
import fol.*; // 1st order logic theorem prover

// The classes Actor and ActorMeta are subclasses, which gets customized 
public abstract class ActorBase implements Runnable {
    protected int cnt = 0; // used for tracing
    public int getCnt() { return cnt; }

    protected String name = "whatever ...."; // used for tracing
    public String getName() { return name; }
    ActorBase (String name) { this.name = name; }

    abstract protected void addTrace(String message);

    // Message queue
    protected LinkedList messageQueue = new LinkedList();
    protected synchronized void putQueue(Object o) { messageQueue.addLast(o); }
    protected synchronized Object fetchQueue() {
	return ( messageQueue.isEmpty() ? null : messageQueue.removeFirst() );
    }

    // ------ Monitors
    protected Vector monitors = new Vector(); // has the monitors
    public Vector getMonitors() { return monitors; }

    // --- Task stuff
    protected int tasksCnt = 0;
    protected int getTasksCnt() { return tasksCnt; }
    protected void setTasksCnt(int t) { tasksCnt = t; }
    protected PriorityQueue<Task> taskQueue = new PriorityQueue<Task>();
    // calls to insertTask must be synchronized like:
    // synchronized (actor) { insertTask(taskx); }
    public void insertTask(Task task) {
	tasksCnt++;
	taskQueue.add(task);
	addTrace("ActorBase: insertTask(): " + task.getJobClassName() + 
		 " and taskFocus.wakeUp " + taskQueue.size()
		 );
	try { taskFocus.wakeUp(); }
	catch (Exception ignore) {}

    } // end insertTask
    public void deleteTask(Task task) {
	if ( taskQueue.remove(task) ) tasksCnt--;
    }
    public PriorityQueue getTaskQueue() { return taskQueue; }
    public int getTaskQueueSize() { return taskQueue.size(); }

    // The nulljob runs when no other job is available.
    // NullJob is a subclass of Job.  It can be customized as needed.
    protected NullJob nj = new NullJob(this);
    protected Task nullTask = new Task(nj);
    public Task getNullTask() { return nullTask; }

    // taskFocus has the current task/activity
    protected Task taskFocus = nullTask;
    public void setTaskFocus(Task taskFocus) { this.taskFocus = taskFocus; }
    public Task getTaskFocus() { return taskFocus; }

    // PARSER, used for translating a text description of a condition into 
    // a PC expression that the theorem prover can handle.
    protected Parser parser = new Parser(false); // make true for tracing

    // --- alerts
    protected int alertsCnt = 0;
    protected int getAlertsCnt() { return alertsCnt; }
    protected LinkedList alerts = new LinkedList();
    public void addAlert(Alert alert) {
	synchronized ( alerts ) {
	    alertsCnt++;
	    alerts.add(alert); 
	}
    }

    // --- actions
    protected int actionsCnt = 0;
    protected int getActionsCnt() { return actionsCnt; }
    protected LinkedList actions = new LinkedList();
    public void addAction(Job job) { 
	synchronized ( actions ) {
	    actionsCnt++;
	    actions.add(job);
	} 
	/*
	int lng = actions.size();
	for ( int i = 0; i < lng; i++ ) {
	    Job jobI = (Job) actions.get(i);
	    addTrace("Job# " + i + " " + jobI.ascii());
	}
	*/
    }

 // --- alert trace
    protected Vector alertTrace = new Vector();
    public Vector getAlertTrace() { return alertTrace; }

    protected Theory alertEvents = new Theory();  // never used thusfar

    protected void dispatchAlert(Alert alert) {
	// check for the entity involved
	// add to a bounded list of memory with 
	// recent alerts;
	// check for alert looping and if so freakout/ 
	// generate a task to clean up, raise fear, 
	// excitation etc 
	// ActorSuper actor = alert.getActor();
	// focus = actor; // ? focusMonitor.wakeUp()

	Dispatcher dispatcher = alert.getDispatcher();
	insertTask(new Task(dispatcher));

	// anyway add the alert ...
	alertTrace.addElement(alert);
	// ... and have a look at it with something like:::
	// insertTask(new Task(new AnalyzeAlertTrace(this))); 
	// which is not available ...
    } // end dispatchAlert

    protected RunMonitor rmTaskFocus = null;

    public void wakeUpMonitorTaskFocus() { 
	if ( null == rmTaskFocus || rmTaskFocus.stopped() ) return;
	rmTaskFocus.wakeUp(); 
    }

    protected Thread myThread = null;
    public abstract void start();
    public abstract void run();
    protected abstract void nullJobCheck(int cnt);

    public void wakeUp() { 
	try { myThread.interrupt(); }
	catch (Exception ignore) {}
    }
    public boolean stopped() { return ( null == myThread ); }

    // These counters could be used ...
    private State tasksCounter = new State();
    public State getTasksCounter() { return tasksCounter; }
    private State alertsCounter = new State();
    public State getAlertsCounter() { return alertsCounter; }
    private State actionsCounter = new State();
    public State getActionsCounter() { return actionsCounter; }

    protected void nullJobCheckBase() {
	// not called in this version
	int x = tasksCounter.getIntValue();
	if ( x != tasksCnt ) {
	    tasksCounter.setIntValue(tasksCnt);
	    tasksCounter.wakeUp();
	}
	x = alertsCounter.getIntValue();
	if ( x != alertsCnt ) {
	    alertsCounter.setIntValue(alertsCnt);
	    alertsCounter.wakeUp();
	}
	x = actionsCounter.getIntValue();
	if ( x != actionsCnt ) {
	    actionsCounter.setIntValue(actionsCnt);
	    actionsCounter.wakeUp();
	}
	if ( 0 == (cnt%50)) 
	    addTrace("nullJobCheckBase actor: " + name + 
		     " tasksCnt: " + tasksCnt +
		     " alertsCnt: " + alertsCnt +
		     " actionsCnt: " + actionsCnt
		     );
    } // end nullJobCheckBase

    protected void stopMonitors(Vector monitors) {
	int lng = monitors.size();
	for (int i = 0; i < lng; i++) {
	    RunMonitor rm = (RunMonitor) monitors.elementAt(i);
	    rm.stop();
	} 
    } // end stopMonitors

    protected boolean notStoppedMonitor(Vector monitors) {
	// Returns true if at least one monitor has not stopped yet.
	boolean checkAgain = false;
	int lng = monitors.size();
	for (int i = 0; i < lng; i++) {
	    RunMonitor rm = (RunMonitor) monitors.elementAt(i);
	    if ( !rm.stopped() ) { 
		addTrace("Actor: RunMonitor NOT stopped: " + i + " " +
			 rm.getMonitor().getClass().getName());
		checkAgain = true; 
	    }
	}
	return checkAgain;
    } // end notStoppedMonitor

} // end ActorBase
