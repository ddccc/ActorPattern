package actor5;

import java.io.*;


// import java.util.Random;

public class Try  {
         

public static void main(String[] args) throws IOException {
    System.out.println("Try actor5 Hello World");

    Actor actor = new Actor();
    actor.start();

    // State inputAvailable = actor.getInputAvailable();

    String string = "";
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    while ( true ) {
	System.out.println("Type some text and press 'Enter.'");
	string = reader.readLine();
	System.out.println("You typed: " + string);
	if ( string.equals("halt") ) break;
	actor.putQueue(new Message(string));
	actor.wakeUp();
    }
    actor.stop();

} // end of main

} // end of Try


class Message {
    private String word;
    Message (String word) {
	this.word = word;
    }
    public String getWord() { return word; }
} // end Message












