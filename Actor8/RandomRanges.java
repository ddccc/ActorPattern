// File:c:/ddc/Java/Actor8/RandomRanges.java
// Date: Fri Oct 27 17:56:31 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.io.*;
import java.util.Random;

public class RandomRanges { 
    public static void main(String[] args) throws IOException {
	// String name = "Bob"; // 0
        String name = "Alice"; // 1
	System.out.println("RandomRanges::: for " + name);
	String dir = "C:/ddc/Java/Actor8/ranges/";
	String fileName = dir + name;
	createRange("P-file for " + name, fileName+"P.txt", false);
	createRange("Q-file for " + name, fileName+"Q.txt", false);
	createRange("R-file for " + name, fileName+"R.txt", true);
    } // end main

    private static void createRange(String label, String fileName, boolean partner) {
	PersonalityRange pr = new PersonalityRange();
	PersonalityAspect[] allAspects = pr.getAllAspects();
	int numberOfFeatures = PersonalityFeature.numberOfFeatures;
	for (int i = 0; i < numberOfFeatures; i++) {
		PersonalityAspect pa = allAspects[i];
		float fx = (float) ( partner ? getRatio() : 1.0 );
		pa.setWeight(fx);
		fx = getRatio();
		pa.setValue(fx);
	}
	CreateRanges.writeRangeFile(label, fileName, pr);
    } // end createRange
    
    private static Random random = new Random(1);
    private static float getRatio() {
	int r = random.nextInt(11);
	return (float) (r/10.0) ;
    }

} // end RandomRanges

