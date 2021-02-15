// File: c:/ddc/Java/Actor8/PersonalityRange.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.util.*;
import java.io.*;

public class PersonalityRange {
    static int numberOfFeatures = PersonalityFeature.numberOfFeatures;
    private PersonalityAspect[] allAspects = 
	new PersonalityAspect[numberOfFeatures];
    public PersonalityRange() {
	for (int i = 0; i < numberOfFeatures; i++)
	    allAspects[i] = new PersonalityAspect(i, 1, 1);
    } // end PersonalityRange()
    public PersonalityRange(String fileName) {
	this();
	File file = new File(fileName);
	try {
	    FileInputStream fis = new FileInputStream(file);
	    DataInputStream dis = new DataInputStream(fis);
	    BufferedReader br = 
		new BufferedReader(new InputStreamReader(dis));
	    String line = br.readLine();
	    if ( null == line ) throw new EOFException("1st line missing");
	    // pw.println(label);
	    String number; float fx;
	    for (int i = 0; i < numberOfFeatures; i++) {
		line = br.readLine();
		if ( null == line ) throw new EOFException("range line missing");
		PersonalityAspect pa = allAspects[i];
		StringTokenizer st = new StringTokenizer(line);
		if ( st.hasMoreTokens() ) 
		    number = st.nextToken(); // skip
		else throw new EOFException("range line item missing");
		if ( st.hasMoreTokens() ) {
		    number = st.nextToken();
		    fx = Float.parseFloat(number);
		    if ( fx < 0 || 1 < fx ) 
			throw new IllegalArgumentException("0 <= input <= 1");
		    pa.setWeight(fx);
		}
		else throw new EOFException("range line item missing");
		if ( st.hasMoreTokens() ) {
		    number = st.nextToken();
		    fx = Float.parseFloat(number);
		    if ( fx < 0 || 1 < fx ) 
			throw new IllegalArgumentException("0 <= input <= 1");
		    pa.setValue(fx);
		}
		else throw new EOFException("range line item missing");
	    }
	    br.close();
	    fis.close();
	} catch ( Exception e ) {
	    System.out.println("Error in: readRangeFile " + fileName +
			       "\n" + e);
	}
    } // end PersonalityRange(String fileName)


    public PersonalityAspect[] getAllAspects() {
	return allAspects;
    }
    public void printPersonalityRange() {
	for (int i = 0; i < numberOfFeatures; i++)
	    System.out.println(allAspects[i].ascii() + " " + i);
    }
    
} // end PersonalityRange
