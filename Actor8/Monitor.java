// File: c:/ddc/Java/Actor8/Monitor.java
// (C) OntoOO Inc 2005 Apr

package actor8;

import java.util.*;
import java.io.*;

// A monitor delegates its periodic execution to its RunMonitor

public abstract class Monitor  {
    protected int previousInt = 0;
    protected float previousFloat = 0;
    protected String previousString = "";
    protected ActorBase actorMonitored = null;
    protected boolean stopped = false;
    protected RunMonitor myRunMonitor = null;
    
    protected void setMyRunMonitor(RunMonitor rm) {
	myRunMonitor = rm;
    }

    /* A state can register a down stream effect; if used, check() can do:
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
    abstract public void check(); // This has the meat of a monitor
    protected int lastCnt = 0; 
    public int getLastCnt() { return lastCnt; }
    protected int previousCnt = 0; 

    protected void check0() { 
	// This is just default functionality
	previousCnt = lastCnt;
	lastCnt = actorMonitored.getCnt();
    } // end check

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
	    actorMonitored.addTrace
		("MonitorTaskFocus: new task= " + task0.getJobClassName());
	}
    } // end check()
} // end MonitorTaskFocus

class MonitorHasInvitation extends Monitor {
    public MonitorHasInvitation (ActorBase actor, State hasInvitation) {
	super(actor, hasInvitation); 
	// state = hasInvitation
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	// initializations first
	actor.setCandidateChecked(null);
	actor.setCandidateQueryCnt(0);
	actor.setCandidateResponseScore(0);
	actor.response = null;
	actor.inviting = false;
	Vector invitations = actor.getInvitations();
	if ( !invitations.isEmpty() ) {
	    actor.addTrace("HasInvitation invitations # " + invitations.size());
	    state.wakeUp();
	}
    }
} // end MonitorHasInvitation

class MonitorHasNoInvitation extends Monitor {
    public MonitorHasNoInvitation (ActorBase actor, State hasNoInvitation) {
	super(actor, hasNoInvitation); 
	// state = hasNoInvitation
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	Vector invitations = actor.getInvitations();
	if ( invitations.isEmpty() ) state.wakeUp();
    }
} // end MonitorHasInvitation

class MonitorFindCandidate extends Monitor {
    public MonitorFindCandidate (ActorBase actor, State findCandidate) {
	super(actor, findCandidate); 
	// state = findCandidate
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	boolean gender = actor.getGender();
	Vector candidates = (gender ? Try.females : Try.males);
	Vector rejectedCandidates = actor.getRejectedCandidates();
	String candidate = null;
	for (int i = 0; i < candidates.size(); i++) {
	    String candidateI = (String) candidates.elementAt(i);
	    if ( rejectedCandidates.contains(candidateI) ) continue;
	    candidate = candidateI; 
	    actor.addTrace(actor.getName() + " finds: " + candidate);
	    break;
	}
	if ( null == candidate ) {
	    state.setStringValue("");
	    state.setIntValue( 1 + state.getIntValue() );
	} else state.setStringValue(candidate); 
	state.wakeUp();
    }
} // end MonitorFindCandidate

class MonitorFailureExit extends Monitor {
    public MonitorFailureExit (ActorBase actor) {
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State findCandidate = actor.getFindCandidate();
	String candidate = findCandidate.getStringValue();
	if ( !candidate.equals("") ) return;
	int failCnt = findCandidate.getIntValue();
	if ( failCnt < 5  ) return; 
	actor.addTrace("Member failure exit: " + actor.getName());
	StopAngel sa = new StopAngel(actor); sa.start();
    }
} // end MonitorFailureExit

class MonitorFailureWait extends Monitor {
    public MonitorFailureWait (ActorBase actor, State noCandidate) {
	super(actor, noCandidate); 
	// state = noCandidate
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State findCandidate = actor.getFindCandidate();
	String candidate = findCandidate.getStringValue();
	if ( !candidate.equals("") ) return;
	int failCnt = findCandidate.getIntValue();
	if ( 5 <= failCnt  ) return; 
	actor.addTrace("FailureWait member: " + actor.getName() + " cnt: " + failCnt);
	try { Thread.sleep(300); }
	catch (InterruptedException e) {}
	state.wakeUp();
    }
} // end MonitorFailureWait

class MonitorEvaluateCandidate1 extends Monitor {
    public MonitorEvaluateCandidate1 (ActorBase actor, State candidateEvaluated1) {
	super(actor, candidateEvaluated1); 
	// state = candidateEvaluated1
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State findCandidate = actor.getFindCandidate();
	String candidate = findCandidate.getStringValue();
	actor.addTrace("MonitorEvaluateCandidate1 name: " + actor.getName() + 
		       " candidate: " + candidate);
	if ( candidate.equals("") ) return;
	actor.setCandidateChecked(candidate);
	actor.evalCandidate(candidate, state);
    }
} // end  MonitorEvaluateCandidate1

class MonitorRejectCandidate1 extends Monitor {
    public MonitorRejectCandidate1 (ActorBase actor, State noCandidate) {
	super(actor, noCandidate); 
	// state = noCandidate
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State candidateEvaluated1 = actor.getCandidateEvaluated1();
	boolean b = candidateEvaluated1.bool();
	if ( b ) return;
	State findCandidate = actor.getFindCandidate();
	String candidate = findCandidate.getStringValue();
	actor.addRejectedCandidate(candidate);
	actor.addTrace("MonitorRejectCandidate1: " + candidate);
	state.wakeUp();
    }
} // end MonitorRejectCandidate1


class MonitorAcceptCandidate1 extends Monitor {
    public MonitorAcceptCandidate1 (ActorBase actor) {
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State candidateEvaluated1 = actor.getCandidateEvaluated1();
	boolean b = candidateEvaluated1.bool();
	if ( !b ) return;
	boolean female = !actor.getGender();
	if ( female ) {
	    try { Thread.sleep(500); }
	    catch (InterruptedException e) { }
	    Vector invitations = actor.getInvitations();
	    if ( !invitations.isEmpty() ) { // pursue invitation instead
		actor.addTrace("MonitorAcceptCandidate1: " + 
			       "pursue invitation instead");
		State hasInvitation = actor.getHasInvitation();
		// To test the expiration of an invitation uncomment:
		// try { Thread.sleep(5000); }
		// catch (InterruptedException e) { }
		hasInvitation.wakeUp(); 
		return;
	    }
	}
	State findCandidate = actor.getFindCandidate();
	String candidate = findCandidate.getStringValue();
	actor.addTrace("MonitorAcceptCandidate1: " + candidate);
	actor.setCandidateChecked(candidate);
	actor.setCandidateQueryCnt(0);
	actor.setCandidateResponseScore(0);
	int query = Try.getRandom97();
	actor.setCandidateQuery(query); // not used
	// actor.wait = 0;
	actor.response = null;
	actor.inviting = true;
	state = actor.getWait1();
	actor.setWaitTask(new WaitTask(state, 3000));
	actor.inviteCandidate(candidate, query, state);
    }
} // end MonitorAcceptCandidate1

class MonitorWait extends Monitor {
    public MonitorWait (ActorBase actor, State wait2) {
	super(actor, wait2); 
	// state = wait2
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	WaitTask wt = actor.getWaitTask();
	wt.cancel();
	actor.incrementWorkCnt();
	String candidate = actor.getCandidateChecked();
	actor.addTrace("MonitorWait candidate: " + candidate);
	State wait1 = actor.getWait1();
	boolean expired = wait1.bool();
	if ( expired ) { // wait time expired
	    actor.addTrace("MonitorWait expired ... ... ... ...");
	    actor.addRejectedCandidate(candidate);
	    actor.setCandidateChecked(null);
	    actor.inviting = false;
	    State noCandidate = actor.getNoCandidate();
	    noCandidate.wakeUp();
	    return;
	}
	// not expired
	state.wakeUp();
    }
} // end MonitorWait

class MonitorWait2 extends Monitor {
    public MonitorWait2 (ActorBase actor, State phase2) {
	super(actor, phase2); 
	// state = phase2
    }
    public void check() {
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	actor.addTrace("MonitorWait2 DID wake up ... ... ...");
	String candidate = actor.getCandidateChecked();
	actor.incrementCandidateQueryCnt();
	Exchange ex = actor.response; 
	int queryP = ex.getInQuery();
	float candidateP = ex.getReply();
	PersonalityRange prR = actor.getPrR();
	PersonalityAspect[] selfAspectsR = prR.getAllAspects();
	PersonalityAspect selfAspectRI = selfAspectsR[queryP];
	float selfR = selfAspectRI.getValue();
	float selfW = selfAspectRI.getWeight();
	float score = 0;
	if ( 0.5 < selfR && 0.5 < candidateP ) { 
	    score += (candidateP - selfR) * selfW;
	}
	else
	if ( 0.5 > selfR && 0.5 > candidateP ) { 
	    score += (selfR - candidateP) * selfW;
	}
	else
	if ( 0.5 <= selfR && candidateP <= 0.5 ) {
	    score -= (selfR - candidateP) * selfW;
	}
	else
	if ( 0.5 >= selfR && candidateP >= 0.5 ) {
	    score += (selfR - candidateP) * selfW;
	}
	System.out.println("$$$ queryP: " + queryP + 
		   " candidateP: " + candidateP +
		   " selfR: " + selfR + " selfW: " + selfW + 
		   " score: " + score 
		   );
	if ( 0 <= score ) actor.incrementCandidateResponseScore();
	else actor.decrementCandidateResponseScore();
	actor.addTrace("MonitorWait2 candidateResponseScore: " +
		       actor.getCandidateResponseScore());

	int candidateQueryCnt = actor.getCandidateQueryCnt();
	actor.addTrace("MonitorWait2 received response from: " 
		       + candidate + " candidateQueryCnt: " + 
		       actor.getCandidateQueryCnt() );
	state.setIntValue(candidateQueryCnt);
	if ( 5 < candidateQueryCnt ) { // jump out met 
	    if ( 0 <= actor.getCandidateResponseScore() )
		state.setTrue(); else state.setFalse();
	    // state.wakeUp();
	    // return;
	} 
	System.out.println("MonitorWait2 statebool: " + state.bool());
	state.wakeUp();
    }
} // end MonitorWait2

class MonitorRejectCandidate2 extends Monitor {
    public MonitorRejectCandidate2 (ActorBase actor, State noCandidate) {
	super(actor, noCandidate); 
	// state = noCandidate
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State phase2 = actor.getPhase2();
	int candidateQueryCnt = phase2.getIntValue();
	if ( candidateQueryCnt <= 5 ) return;
	boolean b = phase2.bool();
	if ( b ) return;
	String candidate = actor.getCandidateChecked();
	actor.addRejectedCandidate(candidate);
	actor.setCandidateChecked(null);
	state.wakeUp();
    }
} // end MonitorRejectCandidate2

class MonitorMeetAndGreet extends Monitor {
    public MonitorMeetAndGreet (ActorBase actor) {
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State phase2 = actor.getPhase2();
	int candidateQueryCnt = phase2.getIntValue();
	if ( candidateQueryCnt <= 5 ) return;
	boolean b = phase2.bool();
	if ( !b ) return;
	// found a promissing partner !
	String candidate = actor.getCandidateChecked();
	Try.removedActors.addElement(candidate);
	actor.addTrace("MeetAndGreet 1: " + candidate);
	Actor c = (Actor) Message.hm.get(candidate);
	c.stop();
	String name = actor.getName();
	Try.removedActors.addElement(name);
	actor.addTrace("MeetAndGreet 2: " + name);
	// actor.stop(); // Can NOT self stop, thus use another party::
	StopAngel sa = new StopAngel(actor);
	sa.start();
    }
} // end MonitorMeetAndGreet


class MonitorPhase2 extends Monitor {
    public MonitorPhase2 (ActorBase actor) {
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State phase2 = actor.getPhase2();
	int candidateQueryCnt = phase2.getIntValue();
	// if ( candidateQueryCnt <= 5 ) return;
	if ( 5 < candidateQueryCnt ) return;
	// start next round of querying
	Exchange ex = actor.response; 
	int outQuery = ex.getOutQuery();
	PersonalityRange selfRangeP = actor.getPrP();
	PersonalityAspect[] selfAspectsP = selfRangeP.getAllAspects();
        PersonalityAspect selfAspectPI = selfAspectsP[outQuery];
	float selfP = selfAspectPI.getValue();
	int nextQuery = Try.getRandom97();
	actor.setCandidateQuery(nextQuery);
	String candidate = actor.getCandidateChecked();
	// from/ to/ outQuery/ selfP/ nextQuery
	ex = new Exchange(actor.getName(), candidate, 
				   outQuery, selfP, nextQuery);
	actor.response = null;
	// setup waiter
	State wait1 = actor.getWait1();
	actor.setWaitTask(new WaitTask(wait1, 3000));

	Message.relayResponse(ex);
    }
} // end MonitorPhase2

class MonitorEvaluateCandidate2 extends Monitor {
    public MonitorEvaluateCandidate2 (ActorBase actor, State candidateEvaluated2) {
	super(actor, candidateEvaluated2); 
	// state = candidateEvaluated2
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	Vector invitations = actor.getInvitations(); // not empty
	Invitation in = null;
	String candidate = null;
	for ( int i = 0; i < invitations.size(); i++ ) {
	    Invitation p = (Invitation) invitations.elementAt(i);
	    candidate = p.getFrom();
	    if ( actor.getRejectedCandidates().contains(candidate) ) continue;
	    if ( Try.removedActors.contains(candidate) ) continue;
	    in = p;
	    break;
	}
	// state.setStringValue(candidate);
	if ( null == in ) {
	    state.setFalse();
	    state.setStringValue("");
	    state.wakeUp();
	    return;
	}
	boolean candidateQ = actor.evaluateCandidate(candidate);
	actor.addTrace("EvaluateCandidate2 candidate: " + candidate + 
		       " bool: " + candidateQ);
	state.setStringValue(candidate);
	if ( candidateQ ) { 
	    state.setTrue(); 
	    actor.invitation = in;
	} else state.setFalse();
	state.wakeUp();
    }
} // end  MonitorEvaluateCandidate2

class MonitorRejectCandidate3 extends Monitor {
    public MonitorRejectCandidate3 (ActorBase actor, State noCandidate) {
	super(actor, noCandidate); 
	// state = noCandidate
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State candidateEvaluated2 = actor.getCandidateEvaluated2();
	String candidate = candidateEvaluated2.getStringValue();
	boolean b = candidateEvaluated2.bool();
	if (b) return;
	// not accepted:
	actor.addTrace("MonitorRejectCandidate3: " + candidate);
	if ( !candidate.equals("") ) actor.addRejectedCandidate(candidate);
	state.wakeUp();
    }
} // end MonitorRejectCandidate1

class MonitorAcceptInvitation extends Monitor {
    public MonitorAcceptInvitation (ActorBase actor) {
	super(actor); 
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	actor.incrementWorkCnt();
	State candidateEvaluated2 = actor.getCandidateEvaluated2();
	// String candidate = candidateEvaluated2.getStringValue();
	boolean b = candidateEvaluated2.bool();
	if (!b) return;
	// accepted:
	Invitation in = actor.invitation;
	String candidate = in.getFrom();
	actor.addTrace("MonitorAcceptInvitation " + candidate);
	actor.setCandidateChecked(candidate);
	int outQuery = in.getOutQuery();
	PersonalityRange selfRangeP = actor.getPrP();
	PersonalityAspect[] selfAspectsP = selfRangeP.getAllAspects();
        PersonalityAspect selfAspectPI = selfAspectsP[outQuery];
	float selfP = selfAspectPI.getValue();
	int nextQuery = Try.getRandom97();
	actor.setCandidateQuery(nextQuery);
	// from/ to/ outQuery/ selfP/ nextQuery
	Exchange ex = new Exchange(actor.getName(), candidate, 
				   outQuery, selfP, nextQuery);
	// actor.wait = 0;
	actor.response = null;
	// setup waiter
	State wait1 = actor.getWait1();
	actor.setWaitTask(new WaitTask(wait1, 3000));

	Message.relayResponse(ex);
    }
} // end AcceptInvitation
