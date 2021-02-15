// File: c:/ddc/Java/Actor/Job.java
// (C) OntoOO Inc 2005 Apr 

package actor;

import java.util.*;
import java.io.*;

import fol.Formula;

public abstract class Job {
    static final protected int updateInterval = 200; // 0.2 secs
    protected ActorBase actor = null;
    public ActorBase getActor() { return actor; }
    protected int priority = 5; // default
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


class NullJob extends Job {
    NullJob(ActorBase actor) { 
	super(actor); 
	priority = 1;
    }
    private int cnt = 0;
    public boolean execute() {
	cnt = actor.getCnt();
	// int actorCnt = actor.getCnt();
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


class DoRow1 extends Job {
    private int i = 0;
    DoRow1(Actor actor, int i) { 
	super(actor); 
	this.i = i;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Seq rowI = square.getRow(i);
	rowI.complete1();
	if ( 0 == rowI.getZeroCnt() ) {
	    act.addTrace("Row1 complete: " + i); 
	    int p = i/3;
	    act.getArrState(p, 0).wakeUp();
	    act.getArrState(p, 1).wakeUp();
	    act.getArrState(p, 2).wakeUp();
	}
	return false; // end job
    } // end execute()

    public String ascii() { return "DoRow1 " + i; }

} // end DoRow1 


class DoRow2 extends Job {
    private int i = 0;
    DoRow2(Actor actor, int i) { 
	super(actor); 
	this.i = i;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Seq rowI = square.getRow(i);
	rowI.completeRow2(i);
	if ( 0 == rowI.getZeroCnt() ) {
	    act.addTrace("Row2 complete: " + i); 
	    int p = i/3;
	    act.getArrState(p, 0).wakeUp();
	    act.getArrState(p, 1).wakeUp();
	    act.getArrState(p, 2).wakeUp();
	}
	return false; // end job
    } // end execute()

    public String ascii() { return "DoRow2 " + i; }

} // end DoRow2


class DoCol1 extends Job {
    private int j = 0;
    DoCol1(Actor actor, int j) { 
	super(actor); 
	this.j = j;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Seq colJ = square.getCol(j);
	colJ.complete1();
	if ( 0 == colJ.getZeroCnt() ) {
	    act.addTrace("Col1 complete: " + j); 
	    int q = j/3;
	    act.getArrState(0, q).wakeUp();
	    act.getArrState(1, q).wakeUp();
	    act.getArrState(2, q).wakeUp();
	}
	return false; // end job
    } // end execute()

    public String ascii() { return "DoCol1 " + j; }

} // end DoCol1 


class DoCol2 extends Job {
    private int j = 0;
    DoCol2(Actor actor, int j) { 
	super(actor); 
	this.j = j;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Seq colJ = square.getCol(j);
	colJ.completeCol2(j);
	if ( 0 == colJ.getZeroCnt() ) {
	    act.addTrace("Col2 complete: " + j); 
	    int q = j/3;
	    act.getArrState(0, q).wakeUp();
	    act.getArrState(1, q).wakeUp();
	    act.getArrState(2, q).wakeUp();
	}
	return false;
    } // end execute()

    public String ascii() { return "DoCol2 " + j; }

} // end DoCol2 


class DoArr1 extends Job {
    private int p = 0; private int q = 0;
    DoArr1(Actor actor, int p, int q) { 
	super(actor); 
	this.p = p; this.q = q;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Arr arrPQ = square.getArr(p, q);
	arrPQ.complete1();
	if ( 0 == arrPQ.getZeroCnt() ) {
	    act.addTrace("Arr1 complete: " + p + " " + q); 
	    for (int z = 0; z < 9; z++) {
		act.getRowState(z).wakeUp();
		act.getColState(z).wakeUp();
	    }
	}
	return false; // end job
    } // end execute()

    public String ascii() { return "DoArr1 " + p + " " + q; }

} // end DoArr1 


class DoArr2 extends Job {
    private int p = 0; private int q = 0;
    DoArr2(Actor actor, int p, int q) { 
	super(actor); 
	this.p = p; this.q = q;
        priority = 6;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	Square square = act.getSquare();
	Arr arrPQ = square.getArr(p, q);
	arrPQ.complete2();
	if ( 0 == arrPQ.getZeroCnt() ) {
	    act.addTrace("Arr2 complete: " + p + " " + q); 
	    for (int z = 0; z < 9; z++) {
		act.getRowState(z).wakeUp();
		act.getColState(z).wakeUp();
	    }
	}
	return false; // end job
    } // end execute()

    public String ascii() { return "DoArr2 " +  p + " " + q; }

} // end DoArr2 


class DoSetTile extends Job {
    private int p = 0; private int q = 0; private int k = 0;
    private int i0 = 0; private int j0 = 0; 
    private Arr arr = null;

    DoSetTile(Actor actor, Arr arr, int pi, int qj, int k) { 
	super(actor); 
	this.arr = arr;
	this.p = pi; this.q = qj; this.k = k;
	i0 = 3 * arr.getIindex(); 
	j0 = 3 * arr.getJindex(); 
        priority = 7;
    }
    public boolean execute() {
	Actor act = (Actor) actor;
	int i = i0 + p; int j = j0 + q;
	act.addTrace("DoSetTile: i " + i + " j " + j + " -> k " + k);
	arr.getTile(p, q).setVal(k);
	act.getRowState(i).wakeUp();
	act.getColState(j).wakeUp();
	return false; // end job
    } // end execute()

    public String ascii() { return ("DoSetTile " + (i0 + p) + " " + (j0 + q)); }

} // end DoSetTile 
