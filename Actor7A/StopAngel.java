// File: c:/ddc/Java/Actor7A/StopAngel.java
// Date: Sat Sep 15 21:01:30 2018
// (C) OntoOO/ Dennis de Champeaux

package actor7A;

// This utility class can be used to stop an actor with:
//    StopAngel sa = new StopAngel(<actor to be stopped>);
//    sa.start();

public class StopAngel implements Runnable {
    static private boolean started = false;
    private Actor actor = null;
    public StopAngel(Actor actor) { this.actor = actor; }
    

    protected Thread myThread = null;
    public void start() {
	synchronized (this) { 
	    if ( started ) return; else started = true;
	}
	myThread = new Thread(this);
	myThread.start(); 
    }
    public void run() { actor.stop(); myThread = null; }

} // end StopAngel

// An alternative would call start() in the constructor ...
// ... and delete the started attribute.
