package actor7;

import java.io.*;


// import java.util.Random;

public class Try  {
         
public static void main(String[] args) throws IOException {
    System.out.println("MAC:::");
    Node start = new Node(3, 0, 3, 0, true, 0, null);
    // Node goal = new Node(0, 3, 0, 3, false, 0, null);
    Actor actor = new Actor(start);
    // Actor actor = new Actor(goal);
    actor.start();

} // end of main

} // end of Try















