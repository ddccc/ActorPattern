// File: c:/ddc/Java/Actor6/Actor.java
// Date: Sat Aug 12 21:13:03 2017

package actor6;

import java.util.*;
import fol.*;  // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    // states
    private State inputAvailable = new State(); // triggers monitor
    public State getInputAvailable() { return inputAvailable; } 

    private Theory theory = new Theory();
    public Theory getTheory() { return theory; };
    private Parser parser = new Parser(false);
    private String conjectureS = 
	      "Seteq(a a)";
	//      "(uq ?u Seteq(?u ?u))";
	//	"(uq ?u (uq ?v <->(Seteq(?u ?v) Seteq(?v ?u) )))";
	//      "(uq ?s (uq ?t (uq ?u ->(&&(Seteq(?s ?t) Seteq(?t ?u)) Seteq(?s ?u) ))))";
	/*
                "<->((<->((eq ?x1(uq ?y1 <->(P(?x1) P(?y1))))" +
		         "(<->((eq ?x2 Q(?x2)) (uq ?y2 P(?y2))))))" +
                    "(<->((eq ?x3(uq ?y3 <->(Q(?x3) Q(?y3))))" +
                	 "(<->((eq ?x4 P(?x4)) (uq ?y4 Q(?y4)))))))"; 
	*/
    private String definitionS = 
        "(uq ?s (uq ?t <->(Seteq(?s ?t) " +
	                 "(uq ?x <->(Inset(?x ?s) Inset(?x ?t) )) )))";

    Formula conjecture = null;
    public Formula getConjecture() { return conjecture; }
    public void setConjecture(Formula g) { conjecture = g; }

    private Stack conjectures = new Stack();
    public void addToStack(Formula f) { conjectures.push(f); }

    /*
	ProofStep ps = theory.prove(conjecture, 5);
	System.out.println("ps: " + ps.html());
    */

    // -------  alert generators:
    /*
    public void inputIs(String inString) {
	// test inString here
	String trigger = "Input(" + inString + ")";
	Atom triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Atom) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "inputId() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	Dispatcher dispatcher = new InString();
	dispatcher.setActor(this);
	DoInString doInString = new DoInString(this, inString);
	Alert alert = new Alert(this, triggerAtom, dispatcher, doInString);
	dispatcher.setAlert(alert);
	dispatcher.setTheory(theory);
	dispatcher.init();

	addAlert(alert); // a queue
	addTrace("Actor: inString/alert.wakeUp ...");
	wakeUp();
    } // end inputIs
    */
	// ----- constructor
    public Actor() { 
	super("Deduction"); 
	actorMeta = new ActorMeta("Meta", this);
	inputAvailable.setTrue();
	Formula out = null;
	// "Setq(a a)";
	// "(uq ?u (uq ?v <->(Seteq(?u ?v) Seteq(?v ?u) )))"
	try { out = parser.parse(conjectureS); }
	catch (Exception ex) {
	    System.out.println("Top: " + ex.getMessage());
	    ex.printStackTrace();
	}
	if ( null == out ) {
	    System.out.println("out == null");
	    System.exit(1);
	}
	conjecture = out;
	System.out.println("conjecture: " + out.html());
	//"(uq ?s (uq ?t <->(Seteq(?s ?t) " +
        //                 "(uq ?x <->(Inset(?x ?s) Inset(?x ?t) )) )))" ); }
	try { out = parser.parse(definitionS); }
	catch (Exception ex) {
	    System.out.println("Top: " + ex.getMessage());
	    ex.printStackTrace();
	}
	Universal definition = (Universal)out;
	System.out.println("definition: " + out.html());
	theory.addDefinition(definition);
	// create the  monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	monitors.addElement(rmTaskFocus);

	RunMonitor monitorInputAtom = 
	    new RunMonitor(new MonitorInputAtom(this, inputAvailable), 10000000);
	inputAvailable.addConsumer(monitorInputAtom);
	monitors.addElement(monitorInputAtom);
	RunMonitor monitorInputNotAtom = 
	    new RunMonitor(new MonitorInputNotAtom(this, inputAvailable), 10000000);
	inputAvailable.addConsumer(monitorInputNotAtom);
	monitors.addElement(monitorInputNotAtom);
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

    // static final private int updateInterval = 200; // 0.2 secs
    static final private int updateInterval = 20000; // 20 secs
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
	// myThread.setPriority(Thread.NORM_PRIORITY-1);
	again = true;
	myThread.start(); 
	actorMeta.start();
    }
    public void run() {
	addTrace("Actor: Entering consciousness loop of Actor: " + 
		 name + " cnt: " + cnt);
	inputAvailable.wakeUp(); // get the ball running
	
	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);
	    synchronized (alerts) {
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
    }

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
    } // end stop

    protected void nullJobCheck(int cnt) {
	// something here as needed for tracing
	// addTrace("Actor nullJobCheck cnt: " + cnt);
	// addTrace("Actor/ cnt " + cnt);
    } // end nullJobCheck

} // end Actor
