// File: c:/ddc/Java/Actor10/Try.java
// Date: Wed Apr 25 15:46:01 2018
// (C) OntoOO/ Dennis de Champeaux

/* 
   This file is an example how to create actors and how to start them.
   Usually a Try function will stop them.  Actors stop themselves in this
   example.  The Try-thread checks here periodically whether an actor is 
   still running; if not it proceeds with showing some stats and terminates.
 */

package actor10;

import java.io.*;
import java.util.*;
import fol.*; // 1st order logic theorem prover

// import java.util.Random;

public class Try  {
    // private static Thread myThread = null;
         
public static void main(String[] args) throws IOException {
    //    myThread = Thread.currentThread();
    System.out.println("Sokoban:::");
    Grid grid = new Grid();
    grid.print();
    Actor actor = new Actor(grid);
    actor.start();

    try { Thread.sleep(3000); } // 3 sec
    catch (InterruptedException ignore) {}
    int waitCnt = 0;
    while ( !actor.stopped() ) {
	    try { Thread.sleep(1000); }
	    catch (InterruptedException ignore) {}
	    waitCnt++;
    }
    System.out.println("waitCnt: " + waitCnt);
    System.out.println("Exit Try");

} // end of main
 
    // The messaging infrastructure is not used 
public static HashMap hm = new HashMap();
public static synchronized void addHS(String name, Actor actor) {
     hm.put(name, actor);
}

public static void relayInvite(Invitation in) {
    String to = in.getTo();
    Actor actor = (Actor) hm.get(to);
    System.out.println("--> relayInvite from " + in.getFrom() + 
                        " to " + to + " query " + in.getOutQuery() );
     // actor.receiveInvite(from, query);
    actor.putQueue(in);
}

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

// Infrastructure for the 'robotic' application where a simple robot
// must push boxes to goal locations
class Grid {
    // 'wwww' is a grid wall element, 
    // an element with '#' must be avoided for a box,
    // an element with 'g' is a goal location for a box
    // The grid has the axis x horizontal (0, 1, .) & y vertical down (0, 1, .)
    // A (goal) pair like {4,1} has y = 4 and x = 1; see example below
    // The variable manx & many is the starting loc for the robot
    // facts are NOT used in this version
    // The number of boxes is equal to the number of goal locs

    /*// 1
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","#   ","    ","    ","    ","wwww"}, 
				  {"wwww","    ","wwww","wwww","wwww","wwww"},
				  {"wwww","    ","wwww","wwww","wwww","wwww"},
				  {"wwww"," g  ","wwww","wwww","wwww","wwww"}, 
				  {"wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {4,1} };
    private int [][] boxes = { {2,1} };
    private int manx = 3, many = 1;
    */
    /* //2
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","#   ","#   ","#   ","#   ","wwww"}, 
			  	  {"wwww","#   ","    ","    "," g  ","wwww"}, 
			  	  {"wwww","#   "," g  ","    ","#   ","wwww"}, 
				  {"wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,4}, {3,2} };
    private int [][] boxes = { {2,3}, {3,3} };
    private int manx = 1, many = 1;
    */
    //3
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","#   ","#   ","#   ","wwww","wwww"},
			  	  {"wwww"," g  ","    "," g  ","#   ","wwww"},
			  	  {"wwww","    ","    ","wwww","    ","wwww"},
			  	  {"wwww","#   ","#   ","#   ","#   ","wwww"},
				  {"wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,1}, {2,3} };
    private int [][] boxes = { {2,2}, {2,3} };
    private int manx = 1, many = 1;
    // */ 
    /* // 4
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","#   ","#   ","#   ","#   ","wwww","wwww"},
			  	  {"wwww","#   ","    ","    ","    ","#   ","wwww"},
			  	  {"wwww","#   ","wwww"," g  ","    "," g  ","wwww"},
			  	  {"wwww","#   ","#   ","#   ","#   ","#   ","wwww"},
				  {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,3}, {3,5} };
    private int [][] boxes = { {2,2}, {2,3} };
    private int manx = 1, many = 1;
    */
    /* // 5
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","wwww","wwww","#   ","#   ","wwww","wwww"},
			  	  {"wwww","#   ","    "," g  ","    ","wwww","wwww"},
			  	  {"wwww","#   ","    ","    ","    ","#   ","wwww"},
			  	  {"wwww","#   ","wwww"," g  ","    ","#   ","wwww"},
			  	  {"wwww","#   ","    ","#   ","#   ","#   ","wwww"},
				  {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,3}, {4,3} };
    private int [][] boxes = { {2,4}, {3,4} };
    private int manx = 1, many = 2;
    */ 
    /* // 6
    private String [][] grid1 = { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
			  	  {"wwww","wwww","#   ","    ","#   ","wwww","wwww"},
			  	  {"wwww","#   ","    ","wwww","    ","#   ","wwww"},
			  	  {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
			  	  {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
			  	  {"wwww","wwww","#   "," g  ","#   ","wwww","wwww"},
				  {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,3}, {4,3}, {5,3} };
    private int [][] boxes = { {3,2}, {3,3}, {3,4} };
    private int manx = 2, many = 1;
    */ 
    /* // 7
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","#   ","    ","    "," g  "," g  ","#   ","wwww"},
      {"wwww","#   ","    ","    ","    ","    ","#   ","wwww"},
      {"wwww","#   ","#   ","wwww","#   "," g  ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,4}, {2,5}, {4,5} };
    private int [][] boxes = { {3,2}, {3,3}, {3,4} };
    private int manx = 5, many = 3;
    */
    /* // 8
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","wwww"," g  ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","#   ","    ","wwww","    ","    ","#   ","wwww"},
      {"wwww","#   ","    ","wwww","    ","wwww","#   ","wwww"},
      {"wwww","#   "," g  ","    ","    ","    "," g  ","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,2}, {4,2}, {4,6} };
    private int [][] boxes = { {2,5}, {4,2}, {4,5} };
    private int manx = 6, many = 2;
    */
    /* // 9
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   "," g  ","    ","#   ","wwww","wwww"},
      {"wwww","    ","    ","wwww","    ","#   ","wwww"},
      {"wwww","    ","    ","    "," g  ","#   ","wwww"},
      {"wwww"," g  ","    ","    ","#   ","wwww","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,2}, {3,4}, {4,1} };
    private int [][] boxes = { {1,2}, {2,2}, {3,2} };
    private int manx = 1, many = 1;
    */
    /* // 10
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","wwww","#   ","#   ","#   ","#   ","wwww","wwww"},
      {"wwww","#   "," g  "," g  ","    ","    ","#   ","wwww"},
      {"wwww","#   ","wwww","    ","    ","    ","    ","wwww"},
      {"wwww","#   ","    ","#   ","wwww","#   "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,2}, {2,3}, {4,6} };
    private int [][] boxes = { {3,4}, {3,5}, {3,6} };
    private int manx = 5, many = 4;
    */
    /* // 11
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww"," g  ","    "," g  ","#   ","wwww","wwww"},
      {"wwww","    ","    ","    ","    ","#   ","wwww"},
      {"wwww","    ","    ","    ","wwww","#   ","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,1}, {1,3}, {4,3} };
    private int [][] boxes = { {1,3}, {2,1}, {2,4} };
    private int manx = 2, many = 1;
    */
    /* // 12
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    "," g  "," g  ","wwww","wwww"},
      {"wwww","#   ","wwww","    ","    ","wwww","wwww"},
      {"wwww","#   ","wwww","    ","    ","#   ","wwww"},
      {"wwww","#   ","    ","    ","wwww","#   ","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,3}, {1,4}, {5,3} };
    private int [][] boxes = { {2,3}, {4,3}, {5,3} };
    private int manx = 1, many = 1;
    */
    /* // 13
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    ","#   ","wwww","wwww","wwww"},
      {"wwww","    ","wwww","    ","wwww","wwww","wwww"},
      {"wwww"," g  "," g  ","    ","    ","#   ","wwww"},
      {"wwww","    ","    ","wwww","    ","    ","wwww"},
      {"wwww","#   ","    ","    ","    "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,1}, {3,2}, {5,5} };
    private int [][] boxes = { {3,1}, {3,3}, {4,4} };
    private int manx = 1, many = 5;
    */
    /* // 14
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","wwww","wwww","#   ","    ","#   ","wwww"},
      {"wwww"," g  ","    "," g  ","wwww","    ","wwww"},
      {"wwww","    ","    ","    ","    ","#   ","wwww"},
      {"wwww","#   ","    "," g  ","#   ","wwww","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,1}, {2,3}, {4,3} };
    private int [][] boxes = { {2,3}, {3,4}, {4,2} };
    private int many = 1, manx = 5;
    */
    /* // 15
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","#   "," g  ","    "," g  ","#   ","wwww","wwww"},
      {"wwww","#   ","wwww","    "," g  ","    ","#   ","wwww"},
      {"wwww","#   ","    ","#   ","#   ","#   ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,2}, {2,4}, {3,4} };
    private int [][] boxes = { {2,3}, {2,4}, {3,3} };
    private int many = 1, manx = 1;
    */
    /* // 16
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    "," g  ","#   ","wwww","wwww"},
      {"wwww","#   ","wwww"," g  ","    ","wwww","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
      {"wwww","#   ","    ","    ","    ","#   ","wwww"},
      {"wwww","#   ","#   ","wwww","#   ","#    ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,3}, {2,3}, {3,3} };
    private int [][] boxes = { {4,2}, {4,3}, {4,4} };
    private int many = 5, manx = 5;
    // */
    /* // 17
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    ","    "," g  "," g  ","wwww"},
      {"wwww","#   ","    ","    ","wwww","    ","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,4}, {2,5}, {4,3} };
    private int [][] boxes = { {2,4}, {3,3}, {4,4} };
    private int many = 4, manx = 5;
    */
    /* // 18
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww","wwww"},
      {"wwww","#   ","    ","    ","wwww"," g  ","#   ","wwww"},
      {"wwww","#   ","wwww","    ","    ","    ","#   ","wwww"},
      {"wwww","#   ","    "," g  ","wwww","#   ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,3}, {2,5}, {4,3} };
    private int [][] boxes = { {2,2}, {2,3}, {3,4} };
    private int many = 3, manx = 3;
    */
    /* //19
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","wwww","wwww"},
      {"wwww","#   ","    ","    ","    ","#   ","wwww"},
      {"wwww","wwww","    "," g  "," g  "," g  ","wwww"},
      {"wwww","#   ","    ","    ","wwww","    ","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,3}, {3,4}, {3,5} };
    private int [][] boxes = { {2,2}, {2,3}, {2,4} };
    private int many = 1, manx = 1;
    */
    // /*
    /*// 20
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","wwww","wwww","wwww","wwww"},
      {"wwww","    ","    ","#   ","wwww","wwww","wwww"},
      {"wwww","    ","    ","    ","#   ","wwww","wwww"},
      {"wwww"," g  ","    ","wwww","    ","#   ","wwww"},
      {"wwww","#   ","    "," g  ","    "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {4,1}, {5,3}, {5,5} };
    private int [][] boxes = { {3,1}, {3,3}, {4,4} };
    private int many = 3, manx = 4;
    */
    /* // 21
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    ","    "," g  ","wwww"},
      {"wwww","    ","    ","    ","    ","wwww"},
      {"wwww"," g  ","    ","wwww","    ","wwww"},
      {"wwww","#   ","    "," g  ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,4}, {3,1}, {4,3} };
    private int [][] boxes = { {2,1}, {2,2}, {2,3} };
    private int many = 2, manx = 4;
    */
    /* // 22
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    ","#   ","    ","#   ","wwww","wwww"},
      {"wwww","    ","wwww","    ","wwww","    ","#   ","wwww"},
      {"wwww"," g  ","    ","    "," g  ","    ","    ","wwww"},
      {"wwww","#   ","#   ","#   ","wwww","#   "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,1}, {3,4}, {4,6} };
    private int [][] boxes = { {2,5}, {3,3}, {3,4} };
    private int many = 4, manx = 1;
    */
    /* // 23
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","    ","#   ","wwww","wwww"},
      {"wwww","#   ","    ","wwww"," g  ","#   ","wwww"},
      {"wwww","#   ","    ","    ","    ","    ","wwww"},
      {"wwww","#   ","    ","    "," g  "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {2,4}, {4,4}, {4,5} };
    private int [][] boxes = { {2,4}, {3,4}, {4,3} };
    private int many = 2, manx = 1;
    */
    /* // 24  |path| = 20, open nodes 113, last node #326
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","wwww","wwww","wwww","#   "," g  ","#   ","wwww"},
      {"wwww","#   ","    ","    ","    ","    ","#   ","wwww"},
      {"wwww","    ","    ","    "," g  "," g  ","#   ","wwww"},
      {"wwww","#   ","#   ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,5}, {3,4}, {3,5} };
    private int [][] boxes = { {2,3}, {3,4}, {3,5} };
    private int many = 4, manx = 2;
    */
    /* // 25
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","wwww","    ","    "," g  ","#   ","wwww","wwww"},
      {"wwww","#   ","    ","    ","    ","    ","#   ","wwww"},
      {"wwww","    ","wwww"," g  ","    ","    ","#   ","wwww"},
      {"wwww","#   ","#   "," g  ","wwww","    ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };

    private int [][] goals = { {1,4}, {3,3}, {4,3} };
    private int [][] boxes = { {2,4}, {2,5}, {3,5} };
    private int many = 2, manx = 6;
    */
    /* // 26  |path| = 16, open nodes 65, last node #263
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","    "," g  ","    ","#   ","wwww","wwww"},
      {"wwww","    ","    ","    ","wwww","    ","wwww","wwww"},
      {"wwww"," g  ","wwww","    ","    ","    ","#   ","wwww"},
      {"wwww","    ","    "," g  ","wwww","    ","#   ","wwww"},
      {"wwww","#   ","#   ","#   ","wwww","#   ","#   ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {1,3}, {3,1}, {4,3} };
    private int [][] boxes = { {2,2}, {3,3}, {3,5} };
    private int many = 4, manx = 5;
    // */
    /* // 27 not solvable
    private String [][] grid1 = 
    { {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"},
      {"wwww","#   ","#   ","#   ","wwww","wwww","wwww"},
      {"wwww","#   ","wwww","    ","    ","#   ","wwww"},
      {"wwww","#   ","    "," g  ","    ","    ","wwww"},
      {"wwww","#   ","    ","wwww","wwww","    ","wwww"},
      {"wwww","#   "," g  ","    ","    "," g  ","wwww"},
      {"wwww","wwww","wwww","wwww","wwww","wwww","wwww"} };
    private int [][] goals = { {3,3}, {5,2}, {5,5} };
    private int [][] boxes = { {2,4}, {3,3}, {5,2} };
    private int many = 5, manx = 1;
    */
    // ----------------------------------------------------------------------

    // integers is used for finding a path for the robot through depth first search 
    private int [][] integers = null; 
    public int [][] getIntegers() { return integers; }
    public int [][] getGoals() { return goals; }
    public int [][] getBoxes() { return boxes; }
    public int getManx() { return manx; }
    public int getMany() { return many; }
    private String [][] myGrid = null;
    public String [][] getMyGrid() { return myGrid; }
    private int numRows;
    public int getNumRows() { return numRows; }
    public static final int WALL = 1000;
    public static final int FREE = 100;
    // key for characterizing the locs of the robot and the boxes to recognize
    // already encountered configurations/ nodes
    private String key = ""; 

    public String getKey() { return key; }
    public Grid() { 
	myGrid = grid1; // use the default 
	numRows = myGrid.length;
	// create the integer array for path existence checks 
	initIntegers();
	// setFactsF();
    }
    public Grid(int [][] bs, int mx, int my) { 
	this();
	boxes = bs; manx = mx; many = my;
	key = "manx" + manx + "many" + many + keyBoxes(boxes);
	initIntegers();
    }
    private void initIntegers() { // 
	int numCols = grid1[0].length;
	integers = new int[numRows][numCols];
	for (int i = 0; i < numRows; i++ )
	for (int j = 0; j < numCols; j++ )
	    integers[i][j] = ( grid1[i][j].equals("wwww") ? WALL : FREE );
	for (int i = 0; i < boxes.length; i++ ) { // mark boxes as not free
	    integers[boxes[i][0]][boxes[i][1]] = WALL;
	}
	// show(integers); System.exit(0);
    }
    public String getXY(int r, int c) {
	return myGrid[r][c];
    }
    public void setXY(int r, int c, String s) {
	myGrid[r][c] = s;
    }
    public boolean equals(Object o2) {
	if ( !(o2 instanceof Grid) ) return false;
	Grid g2 = (Grid) o2;
	if ( manx != g2.getManx() ) return false;
	if ( many != g2.getMany() ) return false;
	int [][] boxes2 = g2.getBoxes();
	int numBoxes = boxes.length;
	for (int i = 0; i < numBoxes; i++ ) {
	    int [] boxesi = boxes[i];
	    int [] boxes2i = boxes2[i];
	    if ( boxesi[0] != boxes2i[0] ) return false;
	    if ( boxesi[1] != boxes2i[1] ) return false;
	}
	return true;
    }
    private String keyBoxes(int [][] boxes) { // part of the key generator
	StringBuffer sb = new StringBuffer();
	int rows = boxes.length;
	for ( int i = 0; i < rows; i++ ) {
	    int[] rowi = boxes[i];
	    sb.append(rowi[0]); sb.append(rowi[1]);
	}
	return sb.toString();
    }
    public boolean hasPath(int fromx, int fromy, int tox, int toy) {
	numRows = myGrid.length;	
	int numCols = grid1[0].length;
	// make a copy from the integers array
	int [][] integersP = new int[numRows][numCols]; // make copy
	for (int i = 0; i < numRows; i++ )
	for (int j = 0; j < numCols; j++ )
	    integersP[i][j] = integers[i][j];

	// call recursive path finder ...
	/*
	boolean b = explore(integersP, 0, fromx, fromy, tox, toy);
	System.out.println("path " + b + " fx " + fromx + " fy " + fromy +
			   " tx " + tox + " ty " + toy);
	System.out.println();
	*/
	return explore(integersP, 0, fromx, fromy, tox, toy);
	// return b;
    }

    private boolean explore(int [][] integersP, int depth,
			     int fromx, int fromy, int tox, int toy) { 
	// space(depth);
	// System.out.println("A " + depth + " " + fromx + " " + fromy); 

	int d2 = depth+1;
	if ( fromx == tox && fromy == toy ) { //  found a path
	    integersP[fromy][fromx] = d2;
	    return true;
	}

	if ( integersP[fromy][fromx] == WALL ) {
	    return false;
	}

	if ( integersP[fromy][fromx] != FREE ) { // visited earlier
	    return false;
	}

	integersP[fromy][fromx] = d2;

	boolean b = explore(integersP, d2, fromx+1, fromy, tox, toy);
	if (b) return b;

	b = explore(integersP, d2, fromx-1, fromy, tox, toy);
	if (b) return b;

	b = explore(integersP, d2, fromx, fromy+1, tox, toy);
	if (b) return b;

	b = explore(integersP, d2, fromx, fromy-1, tox, toy);
	return b;
    } // end explore

    private void show(int numRows, int numCols, int [][] integersP) {
	for (int i = 0; i < numRows; i++ ) { // show layout
	    for (int j = 0; j < numCols; j++ )
		System.out.print(integersP[i][j] + " ");
	    System.out.println();
	}
    } // end show
    public void show(int [][] integers) {
	int numRows = integers.length;  int numCols = integers[0].length;
	show(numRows, numCols, integers);
    }

    private void space(int s) { // for tracing 
	if ( s <= 0 ) return;
	System.out.print("  ");
	space(s-1);
    }

    public void print() {
	System.out.println("GRID:::");
	for (int i = 0; i < numRows; i++ ) {
	    String [] rowi = myGrid[i];
	    for (int j = 0; j < rowi.length; j++) {
		String s = rowi[j];
		if ( j == manx && i == many ) {
		    if ( s.equals(" g  ") )
			System.out.print(" gM ");
		    else 
		    if ( s.equals("#   ") )
			System.out.print("#MM ");
		    else
			System.out.print(" MM "); 
		    continue;
		}
		if ( s.equals("wwww") ) {
		    System.out.print(s); continue;
		}
		if ( s.equals("#   ") ) {
		    System.out.print(s); continue;
		}
		boolean hasBox = hasBoxQ(i, j, boxes);
		if ( hasBox ) {
		    if ( s.equals(" g  ") ) 
			System.out.print(" gb ");
		    else System.out.print("  b ");
		} else {
		    if ( s.equals(" g  ") ) 
		 	 System.out.print(" g  ");
		    else System.out.print("    ");
		}
	    }
	    System.out.println();
	}
	/*
	show(integers);
	*/
    } // end printGrid

    public boolean hasBoxQ(int i, int j, int [][] boxes) {
	for (int p = 0; p < boxes.length; p++ ) 
	    if ( i == boxes[p][0] && j == boxes[p][1] ) return true;
	return false;
    }
    public boolean hasGoalQ(int i, int j, int [][] goals) {
	for (int p = 0; p < goals.length; p++ ) 
	    if ( i == goals[p][0] && j == goals[p][1] ) return true;
	return false;
    }    

    public boolean hasGoalQ(int i, int j) {
	return hasGoalQ(i, j, goals);
    }

    public boolean isSolution() {
	int rows = boxes.length;
	for ( int i = 0; i < rows; i++ )
	    if ( !isSolutioni(boxes[i])) return false;
	return true;
    }
    private boolean isSolutioni(int [] boxi) {
	int rows = goals.length;
	for ( int i = 0; i < rows; i++ ) {
	    int [] goalsi = goals[i];
	    if ( goalsi[0] == boxi[0] && goalsi[1] == boxi[1] ) return true;
	}
	return false;
    }
    public boolean isBlocked(int y, int x) {
	if ( integers[y][x] == Grid.WALL ) return true;
	if ( grid1[y][x].equals("#   ") )return true;
	if ( hasBoxQ(y, x, boxes) ) return true;
	return false;
    }
    public int numberBoxesOnGoal() {
	int cnt = 0;
	int rows = boxes.length;
	for ( int i = 0; i < rows; i++ )
	    if ( isSolutioni(boxes[i])) cnt++;
	return cnt;
    }

} // end grid
