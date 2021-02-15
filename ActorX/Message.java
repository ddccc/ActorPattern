// File: c:/ddc/Java/ActorX/Message.java
// Date: Mon Nov 19 15:48:47 2018
// (C) OntoOO/ Dennis de Champeaux

/* This file has messaging infrastructure
   The example in actor8 uses this file.
 */

package actorX;

import java.io.*;
import java.util.*;

public abstract class Message {

    // Example registry for agents
    public static HashMap hm = new HashMap();
    public static synchronized void addHS(String name, Actor actor) {
	hm.put(name, actor);
    }

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

    /*
    // Example used in Actor8:
    public static void relayInvite(Invitation in) {
	String to = in.getTo();
	Actor actor = (Actor) hm.get(to);
	System.out.println("--> relayInvite from " + in.getFrom() + 
			   " to " + to + " query " + in.getOutQuery() );
	// actor.receiveInvite(from, query);
	actor.putQueue(in);
    }
    // Example used in Actor8::
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
    */
} // end Message

/*  Examples used in Actor8

class Invitation extends Message {
    private int outQuery;
    public Invitation (String from, String to, int outQuery) {
	super(from, to);
	this.outQuery = outQuery;
	purpose = "Invitation";
    }
    public int getOutQuery() { return outQuery; }
} // end Invitation

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
} // end Exchange


*/

