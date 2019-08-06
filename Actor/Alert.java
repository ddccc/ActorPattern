// File: c:/ddc/Java/Actor/Alert.java
// Date: Tue Jul 10 21:42:52 2018
// (C) OntoOO Inc 2018
package actor;

import java.util.*;
import java.io.*;

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

