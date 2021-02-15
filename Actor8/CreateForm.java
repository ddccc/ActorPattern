// File:c:/ddc/Java/Actor8/CreateForm.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.io.*;

public class CreateForm {
    public static void main(String[] args) throws IOException {
	System.out.println("CreateForm:::");
	boolean partner = true;
	String fileName = ( partner ? "partnerForm.txt" : "personForm.txt" );
	int numberOfFeatures = PersonalityFeature.numberOfFeatures;
	File file = new File(fileName);
	try {
	    FileOutputStream fos = new FileOutputStream(file);
	    PrintWriter pw = new PrintWriter(fos);
	    if ( partner ) {
		pw.println("Score for your desired partner for each personality feature with the");
		pw.println("index position on the scale and the importance of this feature.");
		pw.println("|---|---|---|---|---|---|---|---|---|---| importance [1.0 - 0.0]: ");
		pw.println("1  .9  .8  .7  .6  .5  .4  .3  .2  .1   0 ");
		pw.println("Circle own gender: female/ male");
	    } else {
		pw.println("Score yourself/ your acquaintance for each personality feature ");
		pw.println("with the index position on the scale:");
		pw.println("|---|---|---|---|---|---|---|---|---|---| ");
		pw.println("1  .9  .8  .7  .6  .5  .4  .3  .2  .1   0 ");
		pw.println("Circle own gender or the gender of the acquaintance: female/ male");
	    }
	    pw.println();

	    for (int i = 0; i < numberOfFeatures; i++) {
		PersonalityFeature pf = PersonalityFeature.allFeatures[i];
		String line = "|---|---|---|---|---|---|---|---|---|---| " +
		    (partner ? "importance [1.0 - 0.0]: " : "");
		pw.println(line + " " + i);
		String left = pf.getLeft(); String right = pf.getRight(); 
		int ll = left.length(); int lr = right.length();
		int z = 41 - (ll + lr);
		StringBuffer sb = new StringBuffer(left);
		for (int j = 0; j < z; j++) sb.append(' ');
		sb.append(right);
		line = sb.toString();
		pw.println(line);
		pw.println();
	    }
	    pw.flush();
	    pw.close();
	} catch ( Exception e ) {
	    System.out.println("Error in: CreateForm " + e);
	}
 
    } // end main
} // end CreateForm
