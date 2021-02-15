// File: c:/ddc/Java/Actor12/Try.java
// Date: Fri Jun 21 15:40:19 2019
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

package actor12;

import java.io.*;
import java.util.*;
import fol.*; // 1st order logic theorem prover

// import java.util.Random;

public class Try  {
public static int actorCnt = 0;
private static Thread myThread = null;
public static void decreaseActorCnt() {
	actorCnt--;
        myThread.interrupt();
}

         
public static void main(String[] args) throws IOException {
    System.out.println("PDDL");
    Vector <PddlAction> actions = new Vector <PddlAction>();
    // Define three actions: name, params, preconditions, deletes & adds
    actions.addElement(new PddlAction("move", "?from ?to", 
				      "At-robby(?from)|Room(?from)|Room(?to)|", 
				      "At-robby(?from)|", 
				      "At-robby(?to)|"));
    System.out.println();
    actions.addElement(new PddlAction("pick", "?obj ?room ?gripper",
         "At-robby(?room)|" +
	 // "Ball(?obj)|" +      //Room(?room)|Gripper(?gripper)|" +
	 "At(?obj ?room)|Free(?gripper)|",
	 "At(?obj ?room)|Free(?gripper)|",
	 "Carry(?obj ?gripper)|"));
    System.out.println();
    actions.addElement(new PddlAction("drop", "?obj ?room ?gripper",
	 "At-robby(?room)|" +
	 // "Ball(?obj)|" + //Room(?room)|Gripper(?gripper)|" +
	 "Carry(?obj ?gripper)|",
	 "Carry(?obj ?gripper)|",
	 "At(?obj ?room)|Free(?gripper)|"));
    System.out.println();
    // Define initial state
    Vector<Atom> initialState = 
	PddlAction.atoms("Room(rooma)|Room(roomb)|" +
			 "Ball(ball1)|Ball(ball2)|" +
			 "Gripper(left)|Gripper(right)|" +
			 "At-robby(rooma)|Free(left)|Free(right)|" +
			 "At(ball1 rooma)|At(ball2 rooma)|",
			 "parse error initialState");
    System.out.println();
    // Define goal state
    Vector<Atom> goalState = 
	// PddlAction.atoms("At(ball1 roomb)|","parse error goalState");
	PddlAction.atoms("At(ball1 roomb)|At(ball2 roomb)|",
			 "parse error goalState");
    System.out.println();
    // create initial node
    Node initNode = new Node(actions, initialState, goalState);
    initNode.printNode();
    System.out.println(initNode.isGoal());
    Actor actor = new Actor(initNode);
    actor.start();

    /*
    Vector<Node> nodes = initNode.successors();
    System.out.println("|successors| " + nodes.size());
    System.exit(0);
    */


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
