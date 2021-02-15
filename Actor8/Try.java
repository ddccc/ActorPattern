// File: c:/ddc/Java/Actor8/Try.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.io.*;
import java.util.*;


// import java.util.Random;

public class Try  {
public static Vector males = new Vector(); 
public static Vector females = new Vector(); 
public static Vector removedActors = new Vector(); 
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
    System.out.println("|removedActors|= " + removedActors.size());
    printActor(actorB);
    System.out.println("B::averageWeight: " + actorB.averageWeight());
    System.out.println("B::averageValueDiff: " + actorB.averageValue());
    System.out.println("B::averagePQDiff: " + actorB.averagePQDiff());
    printActor(actorA);
    System.out.println("A::averageWeight: " + actorA.averageWeight());
    System.out.println("A::averageValueDiff: " + actorA.averageValue());
    System.out.println("A::averagePQDiff: " + actorA.averagePQDiff());
    System.exit(0);
} // end of main
 
public static void printActor(Actor actor) {
    System.out.println("Actor : " + actor.getName()); 
    System.out.println("gender: " + actor.getGender());
    System.out.println("candidateChecked: " + actor.getCandidateChecked());
    System.out.println("candidateQueryCnt: " + actor.getCandidateQueryCnt());
    System.out.println("candidateResponseScore: " + 
		       actor.getCandidateResponseScore());
    Invitation in = actor.invitation;
    if ( null != in ) 
	System.out.println("inName: " + in.getFrom());
    System.out.println("inviting: " + actor.inviting);
} // end printActor

    /* Initializations:
       actor.setCandidateChecked(null);
       actor.setCandidateQueryCnt(0);
       actor.setCandidateResponseScore(0);
       actor.response = null;
       actor.inviting = false;
       actor.waitThread = null;
     */

private static Random random = new Random(0);
public static int getRandom97() { return random.nextInt(97); }

} // end of Try

