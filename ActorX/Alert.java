// File:" c:/ddc/Java/ActorX/Alert.java
// Date: Sat Sep 15 20:54:56 2018
// (C) Dennis de Champeaux/ OntoOO Inc 2018

package actorX;

import java.util.*;
import java.io.*;


// An alert containing its dispatcher gets injected in an agent's alerts container.
// The agent extracts the dispatcher and launches it in the task queue.  
public class Alert {
    private ActorBase actor;
    private Dispatcher dispatcher;
    private boolean launched = false;

    public Alert(ActorBase actor, Dispatcher dispatcher) {
	this.actor = actor;
	this.dispatcher = dispatcher;

    }
    public ActorBase getActor() { return actor; }
    public Dispatcher getDispatcher() { return dispatcher; }
    public void setLaunched(boolean b) { launched = b; }
    public boolean getLaunched() { return launched; }

} // end Alert 
