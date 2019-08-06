// File c:/ddc/Java/Actor/Sutils.java
// Date Thu Jan 26 18:12:48 2006
package actor;

import java.io.*;

public class Sutils {

    static public void show(int[][] arr) {
	for (int i = 0; i < 9; i++ ) {
	    for (int j = 0; j < 9; j++ ) System.out.print(" " + arr[i][j]);
	    System.out.println();
	}
	System.out.println();
    }

    static public void html(int[][] arr) { html("sudoku.html", arr); }
    static public void html(String fileName, int[][] arr) {
	try { html0(fileName, arr); }
	catch (IOException ex) {
	    System.out.println("******* htl0 failure for: " + fileName);
	}
    }
    static public void html0(String fileName, int[][] arr) throws IOException {
	// write problem as an html file
	File ff = new File(fileName);
	FileWriter fw = new FileWriter(ff);
	PrintWriter pw = new PrintWriter(fw);
	pw.println("<html>");
	pw.println("<body><center>");
	pw.println("<table border>");
	for (int a = 0; a < 3; a++) {
	    pw.println("<tr>");
	    for (int b = 0; b < 3; b++) {
		pw.println("<td><table border>");
		for (int i = 0; i < 3; i++ ) {
		    pw.println("<tr>");
		    for (int j = 0; j < 3; j++ ) {
			int x = 3*a + i;
			int y = 3*b + j;
			int vv = arr[x][y];
			// &nbsp;
			pw.print("<td height=\"40\" width=\"40\" align=\"center\">" +
				 ( 0 == vv ? "&nbsp;" : "" + vv) + 
				 "</td>");
		    }
		    pw.println("</tr>");
		}
		pw.println("</table></td>");
	    }
	    pw.println("</tr>");
	}
	pw.println("</table>");
	pw.println("</center></body>");
	pw.println("</html>");
	pw.flush();
	pw.close();
    }

    static public void copyArr(int[][] arr, int[][] arr2) {
	for (int i = 0; i < 9; i++ ) {
	    for (int j = 0; j < 9; j++ ) arr2[i][j] = arr[i][j];
	} 
    }

    // Checks
     static public boolean check(int[][] arr) {
	 boolean out;
	 for (int i = 0; i < 9; i++ ) {
	     out = checkRow(arr, i);
	     if ( !out ) return false;
	 }
	 for (int j = 0; j < 9; j++ ) {
	     out = checkColumn(arr, j);
	     if ( !out ) return false;
	 }
	 for (int i = 1; i < 9; i = i + 3) {
	     for (int j = 1; j < 9; j = j + 3) { 
		 out = checkArr(arr, i, j);
		 if ( !out ) return false;
	     }
	 }
	 return true;
     }
     static public boolean checkRow(int[][] arr, int i) {
	 boolean [] arb = new boolean[10]; 
	 for (int j = 0; j < 9; j++ ) {
	     int val = arr[i][j];
	     if ( 0 == val ) continue;
	     if ( arb[val] ) return false;
	     arb[val] = true;
	 }
	 return true;
     }
     static public boolean checkColumn(int[][] arr, int j) {
	 boolean [] arb = new boolean[10]; 
	 for (int i = 0; i < 9; i++ ) {
	     int val = arr[i][j];
	     if ( 0 == val ) continue;
	     if ( arb[val] ) return false;
	     arb[val] = true;
	 }
	 return true;
     }
     static public boolean checkArr(int[][] arr, int i, int j) {
	 boolean [] arb = new boolean[10]; 
	 for ( int p = -1; p < 2; p++ ) {
	     for ( int q = -1; q < 2; q++ ) {
		 int val = arr[i+p][j+q];
		 if ( 0 == val ) continue;
		 if ( arb[val] ) return false;
		 arb[val] = true;
	     }
	 }
	 return true;
     }

    /*
      This will print all solutions given the configuration in arr and
      the assignable zero tiles in arr beyond [i,j]
    */
     static public void solveAll(int[][] arr, int i, int j) {
	 boolean out;
	 int val = arr[i][j];
	 if ( 0 == val ) {
	     for (int k = 1; k < 10; k++) {
		 arr[i][j] = k;
		 out = checkRow(arr, i);
		 if ( !out ) continue;
		 out = checkColumn(arr, j);
		 if ( !out ) continue;
		 int p = 1 + 3 * (i/3);
		 int q = 1 + 3 * (j/3);
		 out = checkArr(arr, p, q);
		 if ( !out ) continue;

		 if ( j < 8 ) // next column
		     solveAll(arr, i, j+1);
		 else
		 if ( i < 8 ) // next row
		     solveAll(arr, i+1, 0);
		 else // solution
		     show(arr);
	     }
	     arr[i][j] = 0;
	     return;
	 }
	 // tile has initial value
	 if ( j < 8 ) 
	     solveAll(arr, i, j+1);
	 else
	 if ( i < 8 ) 
	     solveAll(arr, i+1, 0);
	 else
	     // solution
	     show(arr);
     } // end solveAll

    /* 
       solveCnt calculates the number of solutions given the current 
       configuration and given the focus (i,j).  
       It will return cnt + the number of solutions.
    */
     static public int solveCnt(int[][] arr, int i, int j, int cnt,
				int[][] solution) {
	 boolean out;
	 int val = arr[i][j];
	 if ( 0 == val ) {
	     for (int k = 1; k < 10; k++) {
		 if ( 1 < cnt ) { 
		     System.out.println("*** solveCnt =>: " + cnt);
		     return cnt;
		 }
		 arr[i][j] = k;
		 out = checkRow(arr, i);
		 if ( !out ) continue;
		 out = checkColumn(arr, j);
		 if ( !out ) continue;
		 int p = 1 + 3 * (i/3);
		 int q = 1 + 3 * (j/3);
		 out = checkArr(arr, p, q);
		 if ( !out ) continue;

		 if ( j < 8 ) { // next column
		     cnt = solveCnt(arr, i, j+1, cnt, solution);
		 }
		 else
		 if ( i < 8 ) { // next row
		     cnt = solveCnt(arr, i+1, 0, cnt, solution);
		 }
		 else { // solution
		     // show(arr);
		     copyArr(arr, solution);
		     cnt++;
		 }
	     }
	     arr[i][j] = 0;
	     return cnt;
	 }
	 // tile has initial value
	 if ( j < 8 ) {
	     return solveCnt(arr, i, j+1, cnt, solution);
	 }
	 else
	 if ( i < 8 ) {
	     return solveCnt(arr, i+1, 0, cnt, solution);
	 }
	 else {
	     // solution
	     // show(arr);
	     copyArr(arr, solution);
	     return cnt + 1;
	 }
     } // end of solveCnt


} // end Sutils
