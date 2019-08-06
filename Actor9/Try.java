// File: c:/ddc/Java/Actor9/Try.java
// Date: Mon Mar 19 15:51:48 2018

package actor9;

import java.io.*; 

// This is a 'random' example how to create an actor and work with it
// import java.util.Random;

public class Try  {
    // The main function is used to initialize a instance of Actor
    // with a sudoku problem and subsequently to start it up.
    static public int N = 5;
    static private  int[][] arr = new int[N][N];
    // arr[p][q] = #times that q followed p
    static private  int[] bestGuess = new int[N];
    // bestGuess[p] = index q so that (i) arr[p][i] <= arr[p][q]
    static public boolean again = true;

public static void main(String[] args) throws IOException {
    System.out.println("Actor9");
    Thread worldThread = Thread.currentThread();
    Actor actor = new Actor(arr, bestGuess, worldThread);
    State worldEvent = actor.getWorldEvent();
    int eventValue = 0;
    worldEvent.setIntValue(eventValue);
    actor.start();

    try { Thread.sleep(100); } // 3 sec
    catch (InterruptedException ignore) {}
    while ( again ) {
	System.out.println("\n--------- Try eventValue: " + eventValue);
	try { Thread.sleep(400); }
	catch (InterruptedException ignore) {}
	// Thread.yield(); // does not work
	eventValue = (++eventValue) % N;
	worldEvent.setIntValue(eventValue);
	worldEvent.wakeUp();
    }
    StopAngel sa = new StopAngel(actor); sa.start();
    try { Thread.sleep(2000); } // 2 sec
    catch (InterruptedException ignore) {}
    System.out.println("Exit Actor9");

} // end of main



} // end of Try















