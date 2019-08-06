// File: c:/ddc/Java/Actor8/CreateRanges.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.io.*;


// import java.util.Random;

public class CreateRanges {
         
public static void main(String[] args) throws IOException {
    System.out.println("CreateRanges:::");
    // PersonalityRange pr = new PersonalityRange();
    PersonalityRange pr = 
	// createPersonalityRange("||||||   Create desired profile for partner HebeR", true);
	// createPersonalityRange("||||||   Create profile for BertBobR", true);
	createPersonalityRange("||||||   Create profile for BertAliceR", true);
    System.out.println();
    pr.printPersonalityRange();
    writeRangeFile("Desired profile for partner BertAliceR", "BertAliceR.txt", pr);
    // writeRangeFile("Profile for BertBobR", "BertBobR.txt", pr);
} // end of main
 
    static public PersonalityRange 
	createPersonalityRange(String purpose, boolean partner) {
	System.out.println(purpose);
	PersonalityRange pr = new PersonalityRange();
	PersonalityAspect[] allAspects = pr.getAllAspects();
	int numberOfFeatures = PersonalityFeature.numberOfFeatures;
	InputStreamReader is = new InputStreamReader(System.in);
	BufferedReader br = new BufferedReader(is);
	for (int i = 0; i < numberOfFeatures; i++) {
	    PersonalityAspect pa = allAspects[i];
	    PersonalityFeature pf = PersonalityFeature.allFeatures[i];
	    String left = pf.getLeft(); String right = pf.getRight(); 
	    System.out.println("\n" + allAspects[i].ascii());
	    System.out.println(left + 
		     " |------------------------| " + right +
		     "    already done: " + i + " out of: " + numberOfFeatures);
	    if (partner) {
		float weight = getFloat(br, "provide importance: [1.0-0.0] :: ");
		pa.setWeight(weight);
	    }
	    float value = getFloat(br, 
			  "provide index: [1.0-0.0] left is 1.0, right = 0.0 :: ");
	    pa.setValue(value);
	    System.out.println(allAspects[i].ascii());
	    // if ( 1 == i ) break;
	}
	return pr;
    } // end createPersonalityRange

    static public float getFloat(BufferedReader br, String explanation) {
	float fx = -1;
	boolean again = true;
	while ( again ) {
	    again = false;
	    System.out.print(explanation);
	    try { 
		String str = br.readLine();
		fx = Float.parseFloat(str);
		if ( fx < 0 || 1 < fx ) 
		    throw new IllegalArgumentException("0 <= input <= 1");
	    } catch ( IllegalArgumentException e ) {
		System.out.println("Error: " + e);
		again = true;
	    }
	    catch ( Exception e ) {
		System.out.println("Error: " + e);
		again = true;
	    }
	}
	return fx;
    } // end getFloat

    static public void writeRangeFile(String label, String fileName,
				      PersonalityRange pr) {
	int numberOfFeatures = PersonalityFeature.numberOfFeatures;
	PersonalityAspect[] allAspects = pr.getAllAspects();
	
	File file = new File(fileName);
	try {
	    FileOutputStream fos = new FileOutputStream(file);
	    PrintWriter pw = new PrintWriter(fos);
	    pw.println(label);
	    for (int i = 0; i < numberOfFeatures; i++) {
		PersonalityAspect pa = allAspects[i];
		String line = i + " " + pa.getWeight() + " " + pa.getValue();
		pw.println(line);
	    }
	    pw.flush();
	    pw.close();
	    fos.close();
	} catch ( Exception e ) {
	    System.out.println("Error in: writeRangeFile " + e);
	}
     } // end writeRangeFile

} // end of CreateRanges















