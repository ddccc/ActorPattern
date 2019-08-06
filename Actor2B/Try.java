package actor2B;

import java.io.*;


// import java.util.Random;

public class Try  {
         

    static private  int[][] arr = // new int[9][9];
    {
	{ 0, 0, 0, 0, 0, 7, 0, 0, 0 },
	{ 0, 3, 9, 0, 0, 4, 1, 0, 0 },
	{ 2, 0, 4, 6, 0, 0, 0, 0, 0 },
	{ 0, 0, 0, 0, 2, 0, 0, 0, 0 },
	{ 7, 0, 6, 0, 0, 0, 0, 0, 1 },
	{ 0, 0, 0, 0, 3, 0, 5, 7, 9 },
	{ 9, 2, 0, 0, 1, 0, 0, 3, 0 },
	{ 0, 4, 0, 8, 0, 0, 0, 5, 0 },
	{ 0, 0, 0, 0, 0, 0, 0, 0, 8 } };


    static private  int[][] arr2 = new int[9][9];
    static private  int[][] solution = new int[9][9];

public static void main(String[] args) throws IOException {
    System.out.println("Problem:::");
    show(arr);
    boolean ok = check(arr);
    if ( !ok ) {
	System.out.println("Check: " + ok);
	System.exit(0);
    }
    copyArr(arr, arr2); // arr2 for testing a unique solution
    int solveCnt = Sutils.solveCnt(arr2, 0, 0, 0, solution);
    if ( 1 != solveCnt ) {
	System.out.println("solveCnt = " + solveCnt);
	System.exit(0);
    }
    System.out.println("Solution:::");
    show(solution);
    ok = check(solution);
    if ( !ok ) {
	System.out.println("Check solution: " + ok);
	System.exit(0);
    }
    Square square = new Square();
    square.set(arr);
    // The stack contains initially unassigned tiles.
    // A tile has a sum count of the # of zeros in its row, column, arr.
    // Make a high-low ordering; thus a pop will get the next most constrained 
    // unassigned tile.
    square.makeInStack(); 

    Actor actor = new Actor(square);
    actor.start();

    try { Thread.sleep(10000); } // 10 sec
    catch (InterruptedException ignore) {}
    int waitCnt = 10;
    while ( !actor.stopped() ) {
	    try { Thread.sleep(1000); }
	    catch (InterruptedException ignore) {}
	    waitCnt++;
    }
    try { Thread.sleep(2000); } // 2 sec
    catch (InterruptedException ignore) {}
    System.out.println("waitCnt: " + waitCnt);
    square.show();
    /*
    ok = square.solve(solution); // solution is used for verification
    if ( !ok ) {
	square.showAll();
	System.out.println("Check square.solve(solution): " + ok);
	System.exit(0);
    }
    square.showAll();
    System.out.println("Solved by square.solve !");
    */
    // square.showAll();
    /*
    ok = square.solve(solution); // solution is used for verification
    if ( !ok ) {
	square.showAll();
	System.out.println("Check square.solve(solution): " + ok);
	System.exit(0);
    }
    System.out.println("Solved by square.solve !");
    Sutils.html(arr);
    */
} // end of main


     static public void show(int[][] arr) {
	 Sutils.show(arr);
     }

     static public boolean check(int[][] arr) {
	 return Sutils.check(arr);
     }
     static private boolean checkRow(int[][] arr, int i) {
	 return Sutils.checkRow(arr, i);
     }
     static public boolean checkColumn(int[][] arr, int j) {
	 return Sutils.checkColumn(arr, j);
     }
     static public boolean checkArr(int[][] arr, int i, int j) {
	 return Sutils.checkArr(arr, i, j);
     }
     
    static private void copyArr(int[][] arr, int[][] arr2) {
	Sutils.copyArr(arr, arr2);
    }


} // end of Try















