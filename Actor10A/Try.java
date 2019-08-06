// File: c:/ddc/Java/Actor10/Try.java
// Date: Wed Apr 25 15:46:01 2018
// (C) OntoOO/ Dennis de Champeaux

/* 
   This file is an example how to create actors and how to start them.
   Usually a Try function will stop them.  Actors stop themselves in this
   example.  The Try-thread checks here periodically whether an actor is 
   still running; if not it proceeds with showing some stats and terminates.
 */

package actor10A;

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
    Actor actor = new Actor(new Node(grid, null, 0));
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
    System.out.println(
    "MM is robot, b is box, g is goal, gb is box at goal, # is no go for box");
    Node nx = actor.getGoalNode();
    if ( null != nx ) nx.printSolution();
    System.out.println("Exit Try");

} // end of main
 
} // end of Try



