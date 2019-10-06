// File: c:/ddc/Java/Actor13/BlackBoard.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Mon Sep 09 14:48:01 2019

package actor13;

import java.util.*;
import java.io.*;

public class BlackBoard {
    private String input = null;
    private int inputLength = 0;
    public void setInput(String s) { 
	input = s; 
	inputLength = s.length();
	System.out.println("setInput: " + s + " " + inputLength);
    }
    private boolean inWord = false;
    public boolean getInWord() { return inWord; }
    public void setInWord(boolean b) { inWord = b; }
    private int index = 0;
    public int getIndex() { return index; }
    static final char NO_Char = '?'; 
    private char currentChar = NO_Char;
    public char getCurrentChar() { return currentChar; }
    public char nextChar() { 
	if ( inputLength <= index ) return NO_Char;
	currentChar = input.charAt(index++);
	//System.out.println("BlackBoard: " + (index-1) + " " + currentChar);
	return currentChar;
    } 
    private StringBuffer sb; // contains a word
    private int charCnt;
    public int getCharCnt() { return charCnt; }
    public void initWord() { 
	sb = new StringBuffer(); 
	charCnt = 0;
    }
    private int wordCnt = 0;
    public void incrementWordCnt() { wordCnt++; }
    public int getWordCnt() { return wordCnt; } 
    private Vector<Word> words = new Vector<Word>();
    public Vector<Word> getWords() { return words; }
    public Word makeWord() {
	if ( 0 == charCnt ) return null;
	Hashtable ht = new Hashtable();  
	// ht is used for counting how often a char occurs in a word 
	// System.out.println();
	// System.out.println("Input: " + sb.toString());
	for (int i = 0; i < charCnt; i++) {
	    String st = sb.substring(i,i+1);
	    Integer stCnt = (Integer) ht.get(st);
	    if ( null == stCnt ) ht.put(st, new Integer(1));
	    else ht.put(st, new Integer( 1 + stCnt.intValue()));
	}

	int minCnt = charCnt; // minCnt is the minimum number of reps
	Enumeration enumx = ht.elements();
	while ( enumx.hasMoreElements() ) {
	    Integer stCnt = (Integer) enumx.nextElement();
	    int stCnti = stCnt.intValue();
	    if ( stCnti < minCnt ) minCnt = stCnt;
	}
	// System.out.println("charCnt: " + charCnt + " minCnt: " + minCnt);
	Word word = new Word(sb, ht, charCnt, minCnt);
	words.addElement(word);
	return word;
    }
    public void insertChar(char nextChar) {
	sb.append(nextChar);
	charCnt++;
    }

    private StringBuffer output = new StringBuffer();
    public StringBuffer getOutput1() { return output; }
    public String getOutput() { return output.toString(); }
    public BlackBoard () {}
} // end BlackBoard

class Word {
    private StringBuffer sb = null;
    public StringBuffer getSb() { return sb; }
    Hashtable ht;
    private int charCnt;
    public int getCharCnt() { return charCnt; }
    private int minCnt;
    public int getMinCnt() { return minCnt; }
    public Word(StringBuffer s, Hashtable h, int c, int m) {
	sb = s; ht = h; charCnt = c; minCnt = m;
    }
    private String label = "";  // will get classifier info
    public void setLabel(String s) { label = s; }
    public String getLabel() { return label; }
    public int splitter() { // calculates the # of reps
	boolean split = false;
	int splitter = 1;
	for ( int i = 2; i <= minCnt; i++ ) { 
	    if ( 0 != (charCnt % i) ) continue;
	    int shift = charCnt/i; 
	    // System.out.println("fw i " + i + " shift " + shift);
	    boolean match = true;
	    for ( int j = 0; j < shift; j++ ) {
		if ( match ) {
		    for ( int k = 1; k < i; k++ ) {
			// System.out.print("fw j " + j + " k " + k);
			// System.out.println(" " + sb.charAt(j) + " " + 
			//		   sb.charAt(j+ k*shift));
			if ( sb.charAt(j) != sb.charAt(j + k*shift) ) {
			    match = false; break;
			}
		    }
		}
	    }
	    if ( match ) { split = true; splitter = i; }
	}
	String wordx = "[" + sb.toString() + 
		      " * " + splitter + "]";
	// System.out.println("|||||splitter() " + wordx);
	return splitter;
    } // end splitter

} // end Word
