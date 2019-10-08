// File: c:/ddc/Java/Actor13/Monitor.java
// Date: Sun Sep 08 16:48:39 2019
// (C) Dennis de Champeaux/ OntoOO 

package actor13;

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

class MonitorFetchChar extends Monitor {
    public MonitorFetchChar (Actor actor, State nextCharS) { 
	super(actor, nextCharS); 
	// state = nextCharS;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	BlackBoard bb = actor.getBlackBoard();
	char nextChar = bb.nextChar();
	// System.out.println("MonitorFetchChar: " + nextChar);
	if ( BlackBoard.NO_Char == nextChar ) { 
	    State procSentenceS = actor.getProcSentenceS();
	    int lng = bb.getWordCnt(); // lng is the # words in the sentence
	    // System.out.println("||||| MonitorFetchChar:    bb.getWordCnt() " + lng);
	    synchronized(procSentenceS) { 
		procSentenceS.setIntValue(lng);
	    }
	    State finishS = actor.getFinishS();
	    finishS.wakeUp();
	    return;
	}
	state.wakeUp();
    } // end check
} // end MonitorFetchChar

class MonitorNextChar extends Monitor {
    public MonitorNextChar (Actor actor, State nextChar2S) { 
	super(actor, nextChar2S); 
	// state = nextChar2S;
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	BlackBoard bb = actor.getBlackBoard();
	char nextChar = bb.getCurrentChar();
	if ( ']' == nextChar ) { // the end of a word is reached
	    bb.setInWord(false);
	    Word word = bb.makeWord();
	    if ( null == word ) { // word is empty thus ignore
		actor.getProceedS().wakeUp();
		return;
	    }
	    bb.incrementWordCnt();
	    State processWordS = actor.getProcessWordS();
	    processWordS.setObj(word); // store info for the next monitor
	    processWordS.setIntValue(bb.getIndex());
	    processWordS.wakeUp();
	    return;
	}
	if ( bb.getInWord() ) { // process the next inside char
	    state.wakeUp();
	    return;
	}
	if ( '[' == nextChar ) { // start a new Word
	    bb.setInWord(true);
	    bb.initWord();
	    actor.getProceedS().wakeUp();
	    return;
	}
	// skip this char
	actor.getProceedS().wakeUp();
    } // end check
} // end MonitorNextChar

class MonitorWordChar extends Monitor {
    public MonitorWordChar (Actor actor, State proceedS) { 
	super(actor, proceedS); 
	// state = proceedS
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	BlackBoard bb = actor.getBlackBoard();
	char nextChar = bb.getCurrentChar(); // get it ..
	bb.insertChar(nextChar); // ... and put it back at the right place in bb
	state.wakeUp();
    } // end check
} // MonitorWordChar

class MonitorWord extends Monitor {
    public MonitorWord (Actor actor, State proceedS) { 
	super(actor, proceedS); 
	// state = proceedS
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State processWordS = actor.getProcessWordS();
	Word word = (Word) processWordS.getObj();
	int index = processWordS.getIntValue();
	// The actorw actor will classify the word
	// 1st arg is its name, 3th argument the state to notify when done
	ActorW actorw = new ActorW("actorw"+index, word, // create this actor ...
				  actor.getActorWS());
	actorw.start(); // ... and start it right away 
	state.wakeUp();
    } // end check
} // MonitorWordChar

class MonitorSentence extends Monitor {
    public MonitorSentence (Actor actor) { 
	super(actor); 
    }
    public void check() {  
	// generate the output with the word classifications
	check0();
	Actor actor = (Actor) actorMonitored;
	// State procSentenceS = actor.getProcSentenceS();

	BlackBoard bb = actor.getBlackBoard();
	Vector<Word> words = bb.getWords();
	int lng = words.size();
	// System.out.println("||||| MonitorSentence lng " + lng);
	
	// output will be stored here in the blackboard
	StringBuffer output = bb.getOutput1(); 
	for (int i = 0; i < lng; i++) {
	    Word word = words.elementAt(i);
	    StringBuffer sb = word.getSb();
	    String label = word.getLabel();
	    
	    String outx = "[" + sb.toString() + "] " +
		label + "\n";
	    output.append(outx);
	}
	// System.out.println("Output00:\n" + bb.getOutput()); 
	StopAngel stopAngel = new StopAngel(actor);
	stopAngel.start();
	actor.terminate();  // fast track termination by resuming the Try-thread
    } // end check
} // end MonitorSentence

class MonitorClassifiers extends Monitor {
    public MonitorClassifiers (Actor actor, State finishS) { 
	super(actor, finishS); 
	// state = finishS
    }
    public void check() {
	check0();
	Actor actor = (Actor) actorMonitored;
	State procSentenceS = actor.getProcSentenceS();
	State actorWS = actor.getActorWS();
	int lng = actorWS.getIntValue();
	synchronized(procSentenceS) { 
	    state.setIntValue(lng);
	}
	// System.out.println("||||| MonitorClassifiers lng " + lng);
	state.wakeUp(); // trigger monitorSentence if all actorw's are done
    } // end check
} // end MonitorClassifiers

class MonitorFinish extends Monitor {
    public MonitorFinish (Actor actor, State procSentenceS) { 
	super(actor, procSentenceS); 
	// state = procSentenceS
    }
    public void check() {  
	// push out the output sentence when # words equals finished # actorw's
	check0();
	Actor actor = (Actor) actorMonitored;
	int lng, lng2;
	boolean finished = false;
	synchronized(state) {
		lng = state.getIntValue();
		System.out.println("||||| MonitorFinish lng " + lng);
		if ( lng <= 0 ) return; // not yet set by monitorFetchChar
		State finishS = actor.getFinishS();
		lng2 = finishS.getIntValue();
		if ( lng == lng2 ) {
		    finished = true;
		    state.setIntValue(0);
		}
	    }
	if ( finished ) state.wakeUp(); // when done
    } // end check
} // end MonitorFinish



// ============================  Monitors for ActorW:

class MonitorWordClassifier extends Monitor {
    public MonitorWordClassifier(ActorW actor, State actorWS) { 
	super(actor, actorWS); 
	// state = actorWS) { 
    }
    public void check() {
	check0();
	ActorW actor = (ActorW) actorMonitored;
	Word word = actor.getWord();
	int charCnt = word.getCharCnt();
	int minCnt = word.getMinCnt();
	// System.out.println("MonitorWord " + charCnt + " " + minCnt);
	if ( 1 == minCnt ) { // all unique chars
	    State sortedqS = actor.getSortedqS();
	    sortedqS.wakeUp(); // test whether sorted, etc.
	    return;
	} else 
	if ( charCnt == minCnt ) {
	    word.setLabel(charCnt + " equal characters");
	} else {
	    int repCnt = word.splitter(); // find the repetition count
	    word.setLabel(repCnt + " of pattern " +
			  word.getSb().substring(0, charCnt/repCnt));
	}
	actor.addTrace("|||||Classifier done with: " + word.getLabel());
	synchronized(state) {
	    state.setIntValue(1+state.getIntValue());
	    // actor.addTrace("|||||Classifier actorWS " + state.getIntValue());
	    state.wakeUp();
	}
	StopAngel stopAngel = new StopAngel(actor);
	stopAngel.start();
    } // end check
} // end MonitorWordClassifier

class MonitorTestSorted extends Monitor {
    public MonitorTestSorted(ActorW actor, State actorWS) { 
	super(actor, actorWS); 
	// state = actorWS) {
    }
    public void check() {
	check0();
	ActorW actor = (ActorW) actorMonitored;
	Word word = actor.getWord();
	int charCnt = word.getCharCnt();
	if ( 1 == charCnt ) word.setLabel("is singleton");
	else {
	    StringBuffer sb = word.getSb();
	    boolean sorted = true;
	    for (int i = 1; i < charCnt; i++ ) 
		if ( sb.charAt(i) <= sb.charAt(i-1) ) { sorted = false; break; }
	    if ( sorted ) word.setLabel("is sorted");
	    else {
		sorted = true;
		for (int i = 1; i < charCnt; i++ ) 
		    if ( sb.charAt(i-1) <= sb.charAt(i) ) { sorted = false; break; }
		if ( sorted ) word.setLabel("is reverse sorted");
		else word.setLabel(charCnt + " unequal characters");
	    }
	}
	actor.addTrace("|||||TestSorted done with: " + word.getLabel());
	synchronized(state) {
	    state.setIntValue(1+state.getIntValue());
	    actor.addTrace("|||||Classifier actorWS " + state.getIntValue());
	}
	state.wakeUp();
	StopAngel stopAngel = new StopAngel(actor);
	stopAngel.start();
    } // end check
} // end MonitorTestSorted



