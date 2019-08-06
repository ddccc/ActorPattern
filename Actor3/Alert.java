// File: Mon Jul 30 18:17:09 2018
// (C) OntoOO Inc 2005 Apr

package actor3;

import java.util.*;
import java.io.*;

// The classes Alert & Dispatcher have been changed in 2018

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

