// File: c:/ddc/Java/Actor8/PrintRange.java
// Date: Tue Oct 24 19:36:23 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.io.*;


// import java.util.Random;

public class PrintRange {
         
public static void main(String[] args) throws IOException {
    System.out.println("PrintRange:::");
    String dir = "C:/ddc/Java/Actor8/ranges/";
    PersonalityRange pr = new PersonalityRange(dir + "HebeP.txt");
    pr.printPersonalityRange();

} // end main

} // end PrintRange
