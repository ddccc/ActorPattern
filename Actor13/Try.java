// File: c:/ddc/Java/Actor13/Try.java
// Date: kSat Sep 07 20:17:58 2019
// (C) OntoOO/ Dennis de Champeaux

package actor13;

import java.io.*;
import java.util.*;


// import java.util.Random;

public class Try  {
    /*
      Purpose is to show how the state+monitors work with a blackboard
    */
         
public static void main(String[] args) throws IOException {
    Thread myThread = Thread.currentThread();
    System.out.println("Blackboard example:::");

    // Input to be parsed. 
    // The words in the sentence are between [...]
    StringBuffer sb = new StringBuffer();
    sb.append(" ");
    sb.append("[a]");
    sb.append("  ");
    sb.append("[aa]");
    sb.append(" ");
    sb.append("[ab]");
    sb.append(" ");
    sb.append("[abab]");
    sb.append(" ");
    sb.append("[abacabac]");
    sb.append(" ");
    sb.append("[abcabcabc]");
    sb.append(" ");
    sb.append("[abcdef]");
    sb.append(" ");
    sb.append("[aabbaabb]");
    sb.append(" ");
    sb.append("[aaaaaaaaaa]");
    sb.append(" ");
    sb.append("[dcba]");
    sb.append(" ");
    sb.append("[adcb]");
    sb.append(" ");
    

    System.out.println("Input: " + sb.toString());
    Actor actor = new Actor("Blackboard", myThread);
    BlackBoard bb = actor.getBlackBoard();
    bb.setInput(sb.toString());
    actor.start();
    try { myThread.sleep(10000); } // wait till interrupted
    catch (InterruptedException e) {}
    System.out.println();
    System.out.println("Output:\n" + bb.getOutput());
    System.out.println("Exit Try");
    System.exit(0);
} // end of main
 

// Miscellaneous infrastructure for messaging; not used here
/*
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
*/
} // end of Try

    /*
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
    */
