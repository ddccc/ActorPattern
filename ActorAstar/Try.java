// File: c:/ddc/Java/ActorAstar/Try.java
// Date: Sat Sep 15 21:03:20 2019
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

package actorAstar; // adjust

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
    System.out.println("<something");
    // Node start = new Node(3, 0, 3, 0, true, null, 0); // customize
    // Node goal = new Node(0, 3, 0, 3, false, null, 0);
    Actor actor = new Actor(start);
    // Actor actor = new Actor(goal);
    actor.start();

    // Customize as needed::::
    try { Thread.sleep(5000); } // 5 sec
    catch (InterruptedException ignore) {}
    int waitCnt = 10;
    while ( !actor.stopped() ) {
	    try { Thread.sleep(1000); }
	    catch (InterruptedException ignore) {}
	    waitCnt++;
    }
    try { Thread.sleep(2000); } // 2 sec
    catch (InterruptedException ignore) {}
    System.out.println("waitCnt: \n" + waitCnt);
    Node nx = actor.getGoalNode();
    nx.printSolution();

} // end of main

} // end of Try
