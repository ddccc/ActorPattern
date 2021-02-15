// File: c:/ddc/Java/ActorX/Try.java
// Date: Sat Sep 15 21:03:20 2018
// (C) OntoOO/ Dennis de Champeaux

/* 
   This file is an example how to create actors and how to start them.
   Usually a Try function will stop them.  Actors stop themselves in this
   example.  The Try-thread checks here periodically whether an actor is 
   still running; if not it proceeds with showing some stats and terminates.

   Infrastructure for agent-to-agent messaging is shown as well.
   Actor.run() has code how to respond to incoming messages.  
   ActorBase has the message queue and the associated operations.
 */

package actorX;

import java.io.*;
import java.util.*;


// import java.util.Random;

public class Try  {
public static int actorCnt = 0;
private static Thread myThread = null;
public static void decreaseActorCnt() {
	actorCnt--;
        myThread.interrupt();
}

         
public static void main(String[] args) throws IOException {
    myThread = Thread.currentThread();
    System.out.println("Matching:::");

    // Actor actorB = new Actor("Bob", true);
    // Actor actorA = new Actor("Alice", false);
    Actor actorB = new Actor("BertBob", true);
    Actor actorA = new Actor("BertAlice", false);
       try { myThread.sleep(1000); }
       catch (InterruptedException e) {}
    actorB.start();
    actorA.start();
    while ( 1 < actorCnt ) {
        try { myThread.sleep(1000); }
	catch (InterruptedException e) {}
    }
    System.out.println("Exit Try");
    System.exit(0);
} // end of main
 

// Miscellaneous infrastructure for messaging

// Example:
public static HashMap hm = new HashMap();
public static synchronized void addHS(String name, Actor actor) {
     hm.put(name, actor);
}

    // Example:
public static void relayInvite(Invitation in) {
    String to = in.getTo();
    Actor actor = (Actor) hm.get(to);
    System.out.println("--> relayInvite from " + in.getFrom() + 
                        " to " + to + " query " + in.getOutQuery() );
     // actor.receiveInvite(from, query);
    actor.putQueue(in);
}
    // Example:
public static void relayResponse(Exchange ex) {
    String to = ex.getTo();
    Actor actor = (Actor) hm.get(to);
    System.out.println("--> relayResponse from " + ex.getFrom() + 
		       " to " + to + " inQuery " + ex.getInQuery() +
		       " outQuery " + ex.getOutQuery());
    // actor.receiveResponse(from, inQuery, reply, outQuery);
    actor.putQueue(ex);
    actor.wakeUp();
}

} // end of Try


// Some example infrastructure for messaging between actors that
// can use the message queue in ActorBase.

abstract class Message {
    private String from;
    private String to;
    protected String purpose;
    Message (String from, String to) {
	this.from = from;
	this.to = to;
    }
    
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getPurpose() { return purpose; }
} // end Message

class Invitation extends Message {
    private int outQuery;
    public Invitation (String from, String to, int outQuery) {
	super(from, to);
	this.outQuery = outQuery;
	purpose = "Invitation";
    }
    public int getOutQuery() { return outQuery; }
}

class Exchange extends Message {
    private int inQuery;
    private float reply;
    private int outQuery;
    public Exchange (String from, String to, int inQuery,
		     float reply, int outQuery) {
	super(from, to);
	this.inQuery = inQuery;
	purpose = "Exchange";
	this.reply = reply;
	this.outQuery = outQuery;
    }
    public int getInQuery() { return inQuery; }
    public float getReply() { return reply; }
    public int getOutQuery() { return outQuery; }
}
