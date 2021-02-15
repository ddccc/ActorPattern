// File: c:/ddc/Java/Actor8/Actor.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006

package actor8;

import java.util.*;
import java.io.*;
import fol.*; // 1st order logic theorem prover

public class Actor extends ActorBase {

    private ActorMeta actorMeta = null;

    private Theory theory = new Theory(false);
    public Theory getTheory() { return theory; };

    private boolean gender;
    public boolean getGender() { return gender; }

    private String dir = "C:/ddc/Java/Actor8/ranges/";
    private PersonalityRange prP = null;
    public PersonalityRange getPrP() { return prP; }
    private PersonalityRange prQ = null;
    public PersonalityRange getPrQ() { return prQ; }
    private PersonalityRange prR = null;
    public PersonalityRange getPrR() { return prR; }
    private int numberOfFeatures = PersonalityFeature.numberOfFeatures;
    public float averageWeight() {
	float sum = 0;
	PersonalityAspect[] allAspects = prR.getAllAspects();
	for (int i = 0; i < numberOfFeatures; i++) {
	    PersonalityAspect pa = allAspects[i];
	    sum += pa.getWeight();
	}
	return sum/numberOfFeatures;
    } // end averageWeight
    public float averageValue() {
	float sum = 0;
	PersonalityAspect[] allAspects = prR.getAllAspects();
	for (int i = 0; i < numberOfFeatures; i++) {
	    PersonalityAspect pa = allAspects[i];
	    float value = pa.getValue() - 0.5f;
	    if (value < 0) value = -value;
	    sum += value;
	}
	return sum/numberOfFeatures;
    } // end averageWeight

    public float averagePQDiff() {
	float sum = 0;
	PersonalityAspect[] allAspectsP = prP.getAllAspects();
	PersonalityAspect[] allAspectsQ = prQ.getAllAspects();
	for (int i = 0; i < numberOfFeatures; i++) {
	    PersonalityAspect paP = allAspectsP[i];
	    float valueP = paP.getValue();
	    PersonalityAspect paQ = allAspectsQ[i];
	    float valueQ = paQ.getValue();
	    float diff = valueP - valueQ;
	    if (diff < 0) diff = -diff;
	    sum += diff;
	}
	return sum/numberOfFeatures;
    } // end averageWeight


    private Vector rejectedCandidates = new Vector(); // rejected or no reply 
    public Vector getRejectedCandidates() { return rejectedCandidates; }
    public void addRejectedCandidate(String candidate) { 
	rejectedCandidates.addElement(candidate);
    }
    private String candidateChecked = null;
    public void setCandidateChecked(String c) { candidateChecked = c; }
    public String getCandidateChecked() { return candidateChecked; }

    int candidateQueryCnt = 0; // # received responses
    public int getCandidateQueryCnt() { return candidateQueryCnt; }
    public void setCandidateQueryCnt(int x) { candidateQueryCnt = x; }
    public void incrementCandidateQueryCnt() { candidateQueryCnt++; }

    // not used
    private int candidateQuery = 0; // # aspect asked about
    public void setCandidateQuery(int x) { candidateQuery = x; }
    public int getCandidateQuery() { return candidateQuery; } 

    private int candidateResponseScore = 0;
    public void incrementCandidateResponseScore() { candidateResponseScore++; }
    public void decrementCandidateResponseScore() { candidateResponseScore--; }
    public void setCandidateResponseScore(int x) { candidateResponseScore = x; }
    public int getCandidateResponseScore() { return candidateResponseScore; }


    private Vector invitations = new Vector(); // has Pair-s
    public Vector getInvitations() { return invitations; }
    /*
    public void receiveInvite(String name, int query) {
	invitations.addElement(new Pair(name, query));
    }
    */
    public Invitation invitation = null;

    // public String synchroObject = "synchroObject";
    // public int wait = 0;

    public boolean inviting = false;

    // public Thread waitThread = null;

    /*
    public void receiveResponse(String name, int inQuery, 
				float reply, int outQuery) {
	response = new FourTuple(name, inQuery, reply, outQuery);
	addTrace("Received response from: " + name +
		 " inQuery " + inQuery +  " outQuery " + outQuery);
	// wait = 1;
    }
    public FourTuple response = null;
    */
    public Exchange response = null;

    // just stats
    private int workCnt = 0;
    public int getWorkCnt() { return workCnt; }
    public void incrementWorkCnt() { workCnt++; }

    // states
    private State noCandidate = new State(); 
    public State getNoCandidate() { return noCandidate; }

    private State hasInvitation = new State(); 
    public State getHasInvitation() { return hasInvitation; }
    private State hasNoInvitation = new State(); 
    private State findCandidate = new State(); 
    public State getFindCandidate() { return findCandidate; }
    private State candidateEvaluated1 = new State(); 
    public State getCandidateEvaluated1() { return candidateEvaluated1; } 
    private State wait1 = new State(); 
    public State getWait1() { return wait1; } 
    private State wait2 = new State(); 
    private State phase2 = new State(); 
    public State getPhase2() { return phase2; }
    private State candidateEvaluated2 = new State(); 
    public State getCandidateEvaluated2() { return candidateEvaluated2; } 
    // private State cancel = new State(); 
    // public State getCancel() { return cancel; } 


    // -------  alert generators:

    public void evalCandidate(String candidate, State state) {
	addTrace("evalCandidate candidate: " + candidate);
	// not using the theorem prover but a 'procedural attachment'
	String trigger = ""; 
	Formula triggerAtom = Symbol.UNKNOWN;
	/*
	try { triggerAtom = (Formula) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "checkNode() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	 */
	// setCandidateChecked(candidate);
	Dispatcher dispatcher = new EvalCandidate(this, triggerAtom, candidate);
	dispatcher.init();
	// dispatcher.setActor(this);
	DoEvalCandidate doEvalCandidate =
	    new DoEvalCandidate(this, candidate, state);
	dispatcher.setJob(doEvalCandidate);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	// dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	addTrace("Actor: evalCandidate/alert.wakeUp ...");
	wakeUp();
    } // end evalCandidate

    public void inviteCandidate(String candidate, int query, State state) {
	/*
	String trigger = "Invite(" + candidate + ")"; 
	Formula triggerAtom = Symbol.UNKNOWN;
	try { triggerAtom = (Formula) parser.parse(trigger); }
	catch ( Exception pe ) {
	    String messg = "checkNode() Parser Error of: " + trigger;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	    } */
	Formula triggerAtom = Symbol.TRUE; // unconditional approval 
	addTrace("Actor: <b>new alert trigger:</b> " + triggerAtom.html());
	Dispatcher dispatcher = new InviteCandidate(this, triggerAtom);
	dispatcher.init();
	// dispatcher.setActor(this);
	DoInviteCandidate doInviteCandidate =
	    new DoInviteCandidate(this, candidate, query, state);
	dispatcher.setJob(doInviteCandidate);
	Alert alert = new Alert(this, dispatcher);
	dispatcher.setAlert(alert);
	dispatcher.setTheory(theory);
	addAlert(alert); // a queue
	addTrace("Actor: evalCandidate/alert.wakeUp ...");
	wakeUp();
    } // end inviteCandidate

    private WaitTask waitTask = null;
    public void setWaitTask(WaitTask wt) { waitTask = wt; }
    public WaitTask getWaitTask() { return waitTask; }

	// ----- constructor
    public Actor(String name, boolean gender) { 
	super(name); 
	Message.addHS(name, this);
	this.gender = gender;
	if ( gender ) Try.males.addElement(name);
	else Try.females.addElement(name);
	prP = new PersonalityRange(dir + name + "P.txt");
	prQ = new PersonalityRange(dir + name + "Q.txt");
	prR = new PersonalityRange(dir + name + "R.txt");

	actorMeta = new ActorMeta("Meta " + name, this);

	/* Modify and uncomment for selective invitations, see 
	   also Dispatcher.InviteCandidate and above inviteCandidate()
	// adjust theory
	String axiomSt = "(uq ?x (Invite(?x)))";
	Formula axiom = Symbol.UNKNOWN;
	try { axiom = (Formula) parser.parse(axiomSt); }
	catch ( Exception pe ) {
	    String messg = "Actor() Parser Error of: " + axiomSt;
	    System.out.println(messg);
	    addTrace(messg);
	    return;
	}
	theory.addAxiom(axiom);
	*/

	// Create the monitors

	// keeps an eye on the task focus
	rmTaskFocus = new RunMonitor(new MonitorTaskFocus(this), 50);
	monitors.addElement(rmTaskFocus);

	RunMonitor monitorHasInvitation =
	    new RunMonitor(new MonitorHasInvitation(this, 
					hasInvitation), 100000);
	noCandidate.addConsumer(monitorHasInvitation);
	monitors.addElement(monitorHasInvitation);

	RunMonitor monitorHasNoInvitation =
	    new RunMonitor(new MonitorHasNoInvitation(this, 
					hasNoInvitation), 100000);
	noCandidate.addConsumer(monitorHasNoInvitation);
	monitors.addElement(monitorHasNoInvitation);

	RunMonitor monitorFindCandidate =
	    new RunMonitor(new MonitorFindCandidate(this, 
					findCandidate), 100000);
	hasNoInvitation.addConsumer(monitorFindCandidate);
	monitors.addElement(monitorFindCandidate);

	RunMonitor monitorFailureExit =
	    new RunMonitor(new MonitorFailureExit(this), 100000);
	findCandidate.addConsumer(monitorFailureExit);
	monitors.addElement(monitorFailureExit);

	RunMonitor monitorFailureWait =
	    new RunMonitor(new MonitorFailureWait(this,
						  noCandidate), 100000);
	findCandidate.addConsumer(monitorFailureWait);
	monitors.addElement(monitorFailureWait);

	RunMonitor monitorEvaluateCandidate1 =
	    new RunMonitor(new MonitorEvaluateCandidate1(this,
						  candidateEvaluated1), 100000);
	findCandidate.addConsumer(monitorEvaluateCandidate1);
	monitors.addElement(monitorEvaluateCandidate1);

	RunMonitor monitorRejectCandidate1 =
	    new RunMonitor(new MonitorRejectCandidate1(this,
						  noCandidate), 100000);
	candidateEvaluated1.addConsumer(monitorRejectCandidate1);
	monitors.addElement(monitorRejectCandidate1);

	RunMonitor monitorAcceptCandidate1 =
	    new RunMonitor(new MonitorAcceptCandidate1(this), 100000);
	candidateEvaluated1.addConsumer(monitorAcceptCandidate1);
	monitors.addElement(monitorAcceptCandidate1);

	RunMonitor monitorWait =
	    new RunMonitor(new MonitorWait(this, wait2), 100000);
	wait1.addConsumer(monitorWait);
	monitors.addElement(monitorWait);

	RunMonitor monitorWait2 =
	    new RunMonitor(new MonitorWait2(this, phase2), 100000);
	wait2.addConsumer(monitorWait2);
	monitors.addElement(monitorWait2);

	RunMonitor monitorRejectCandidate2 =
	    new RunMonitor(new MonitorRejectCandidate2(this,
						  noCandidate), 100000);
	phase2.addConsumer(monitorRejectCandidate2);
	monitors.addElement(monitorRejectCandidate2);

	RunMonitor monitorMeetAndGreet =
	    new RunMonitor(new MonitorMeetAndGreet(this), 100000);
	phase2.addConsumer(monitorMeetAndGreet);
	monitors.addElement(monitorMeetAndGreet);

	RunMonitor monitorPhase2 =
	    new RunMonitor(new MonitorPhase2(this), 100000);
	phase2.addConsumer(monitorPhase2);
	monitors.addElement(monitorPhase2);

	RunMonitor monitorEvaluateCandidate2 =
	    new RunMonitor(new MonitorEvaluateCandidate2(this,
						  candidateEvaluated2), 100000);
	hasInvitation.addConsumer(monitorEvaluateCandidate2);
	monitors.addElement(monitorEvaluateCandidate2);

	RunMonitor monitorRejectCandidate3 =
	    new RunMonitor(new MonitorRejectCandidate3(this,
						  noCandidate), 100000);
	candidateEvaluated2.addConsumer(monitorRejectCandidate3);
	monitors.addElement(monitorRejectCandidate3);

	RunMonitor monitorAcceptInvitation =
	    new RunMonitor(new MonitorAcceptInvitation(this), 100000);
	candidateEvaluated2.addConsumer(monitorAcceptInvitation);
	monitors.addElement(monitorAcceptInvitation);

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
	Try.actorCnt++;
	System.out.println("starting: " + getName());
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
	try { myThread.sleep(100); }
	catch (InterruptedException e) {}

	noCandidate.wakeUp();  // get the ball rolling

	while ( again ) {
	    cnt++; 
	    addTrace("Actor.run().cnt: " + cnt);

	    Object message = fetchQueue();
	    if ( null != message ) {
		if ( message instanceof Invitation ) {
		    Invitation in = (Invitation) message;
		    invitations.addElement(in);	
		} else
		if ( message instanceof Exchange ) {
		    response = (Exchange) message;
		    wait1.wakeUp();
		}
	    }

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
	if (null != taskFocus)	taskFocus.stop();
	stopMonitors(monitors);
	// 

	// check that they have stopped 
	boolean checkAgain = true;
	// boolean checkAgain = false;
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
		Dispatcher d = alert.getDispatcher();
		boolean launched = alert.getLaunched();
		Formula trigger = d.getTrigger();
	        addTrace("Trigger: " + i + ": " + 
			 "launched: " + launched + 
			 " " + trigger.html());
	    }
	}
	*/
	// square.show();
	Try.decreaseActorCnt();
    } // end stop

    protected void nullJobCheck(int cnt) {
	// addTrace("Actor nullJobCheck cnt: " + cnt);
	addTrace("Actor/ workCnt: " + workCnt);
    } // end nullJobCheck(

    public boolean evaluateCandidate(String candidate) {
	addTrace("Actor: ENTERING evaluateCandidate: " + candidate);
	try {
	Actor actorCandidate = (Actor) Message.hm.get(candidate);
	int numberOfFeatures = PersonalityFeature.numberOfFeatures;
	PersonalityRange prQCandidate = actorCandidate.getPrQ();
	PersonalityAspect[] candidateAspectsQ = prQCandidate.getAllAspects();
	PersonalityAspect[] selfAspectsR = prR.getAllAspects();
 
	// compare against own prR
	float score = 0;
	for (int i = 0; i < numberOfFeatures; i++) {
	    PersonalityAspect candidateAspectQI = candidateAspectsQ[i];
	    float candidateQ = candidateAspectQI.getValue();
	    PersonalityAspect selfAspectRI = selfAspectsR[i];
	    float selfR = selfAspectRI.getValue();
	    float selfW = selfAspectRI.getWeight();
	    if ( 0.5 < selfR && 0.5 < candidateQ ) { 
		score += (candidateQ - selfR) * selfW;
		continue;
	    }
	    if ( 0.5 > selfR && 0.5 > candidateQ ) { 
		score += (selfR - candidateQ) * selfW;
		continue;
	    }
	    if ( 0.5 <= selfR && candidateQ <= 0.5 ) {
		score -= (selfR - candidateQ) * selfW;
		continue;
	    }
	    if ( 0.5 >= selfR && candidateQ >= 0.5 ) {
		score += (selfR - candidateQ) * selfW;
		continue;
	    }
	}
	addTrace("Actor: evaluateCandidate " + candidate + " score= " + score);
	// check score against cutoff value
	return true; 
	} catch (Exception ex) {
	    addTrace("########## evaluateCandidate: " + 
			   ex.getMessage());
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    ex.printStackTrace(ps);
	    addTrace("########## evaluateCandidate: " +
			   baos.toString());
	    return true;
	}

    } // end evaluateCandidate

} // end Actor

