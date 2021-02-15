// File: c:/ddc/Java/Actor/StopAngel.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jan 26 19:45:31 2006
package actor;

// This utility class can be used to stop an actor:
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
