// File c:/ddc/Java/Actor3A/Square.java
// Date Thu Jan 26 18:12:48 2006
package actor3A;

import java.util.*;

public class Square {
    static public boolean trace = false;
    protected Seq [] rows = new Seq[9];
    protected Seq [] cols = new Seq[9];
    protected Arr [][] arrs = new Arr [3][3];
    private int zeroCnt = 81;
    // private Actor actor = null;
    // public void setActor(Actor act) { actor = act; }
    Square() { init(); }
    public void init() {
	if (trace) System.out.println("Entering Square.init()");
	for ( int i = 0; i < 9; i++ ) {
	    rows[i] = new Seq();
	    rows[i].setIndex(this, i);
	    cols[i] = new Seq();
	    cols[i].setIndex(this, i);
	}
	for ( int i = 0; i < 3; i++ )
	    for ( int j = 0; j < 3; j++ ) {
		Arr arrij = new Arr();
		arrs[i][j] = arrij;
		arrij.setIndex(this, i, j);
	    }
	for ( int i = 0; i < 3; i++ )
	    for ( int j = 0; j < 3; j++ ) 
		arrs[i][j].setNeighbors();

	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) {
		Tile tile = new Tile(i, j, rows, cols, arrs);
		rows[i].setTile(j, tile);
		cols[j].setTile(i, tile);
		int p = i/3;
		int q = j/3;
		int i2 = i - p*3;
		int j2 = j - q*3;
		arrs[p][q].setTile(i2, j2, tile);
	    }
    }
    public Seq getRow(int i) { return rows[i]; }
    public Seq getCol(int j) { return cols[j]; }
    public Arr getArr(int i, int j) { return arrs[i][j]; }
    public Tile getTile(int i, int j) { return getRow(i).getTile(j); }
    public int getZeroCnt() { return zeroCnt; }
    public void decreaseZeroCnt() { zeroCnt--; }
    public void increaseZeroCnt() { zeroCnt++; }
    public int v(int i, int j) { return getTile(i, j).getVal(); }
    public void set(int[][] arr) {
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) {
		int val = arr[i][j];
		Tile t = getTile(i, j);
		t.setValInit(val);
	    }
	// do the zeroCnt stuff here
    }
 
    public Square cloneSquare() {
	Square s2 = new Square();
	s2.copy(this);
	return s2;
    }
    private void copy(Square s1) {
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) {
		Tile t1 = s1.getTile(i, j);
		int val = t1.getVal();
		Tile t = getTile(i, j);
		t.setValInit(val);
	    }
    }

    public void show() {
	for (int i = 0; i < 9; i++ ) {
	    for (int j = 0; j < 9; j++ ) 
		System.out.print(" " + v(i, j));
	    System.out.println();
	 }
	 System.out.println();
    }
    public void showAll() {
	System.out.println("showAll");
	show();
	System.out.println("ZeroCnt: " + zeroCnt);
	/*
	System.out.println("Rows");
	for ( int i = 0; i < 9; i++ ) {
	    Seq rowi = rows[i];
	    rowi.show();
	}
	System.out.println("Cols");
	for ( int i = 0; i < 9; i++ ) {
	    Seq coli = cols[i];
	    coli.show();
	}
	System.out.println("Arrs");
	for ( int i = 0; i < 3; i++ ) 
	    for ( int j = 0; j < 3; j++ ) {
		Arr arrij = arrs[i][j];
		arrij.show();
	}
	*/
    } // end showAll
    public void html(String fileName) {
	int [][] out = new int[9][9];
	for (int i = 0; i < 9; i++ ) 
	    for (int j = 0; j < 9; j++ ) 
		out[i][j] = v(i, j);
	Sutils.html(fileName, out);
    }

    public void solve() {
	while ( true ) {
	    if ( 0 == zeroCnt ) return;
	    int zeroCnt2 = zeroCnt;
	    completeRowColArr1();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    completeRowCol2();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    completeArr2();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    block();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    last();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    // more here
	    break;
	}
    }

    public boolean solve(int [][] solution) {
	while ( true ) {
	    if ( 0 == zeroCnt ) return check(solution);
	    int zeroCnt2 = zeroCnt;
	    completeRowColArr1();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    completeRowCol2();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    completeArr2();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    block();
	    if ( zeroCnt < zeroCnt2 ) continue;
	    last();
	    if ( zeroCnt < zeroCnt2 ) continue;


	    // more here
	    break;
	}
	// cannot solve
	System.out.println("***************** solve failure ****");
	System.out.println("Solution::");
	Sutils.show(solution);
	System.out.println("Current stage::");
	show(); 
	html("Fails.html");
	System.exit(0);
	return false;
    } // end solve
    
    private boolean check(int [][] solution) {
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) 
		if ( solution[i][j] != v(i, j) ) {
		    System.out.println("***************** Error solve ****");
		    System.out.println("Square i: " + i + " j: " + j);
		    showAll();
		    System.out.println("Solution:");
		    Sutils.show(solution);
		    // Sutils.html(Sutils.task); 
		    System.exit(1);
		    return false;
		}
	return true;
    } // end check

    private void completeRowColArr1() {
	for ( int i = 0; i < 9; i++ ) rows[i].complete1();
	for ( int i = 0; i < 9; i++ ) cols[i].complete1();
	for ( int i = 0; i < 3; i++ )
	    for ( int j = 0; j < 3; j++ ) arrs[i][j].complete1();
    }
    private void completeRowCol2() {
	for ( int i = 0; i < 9; i++ ) rows[i].completeRow2(i);
	for ( int i = 0; i < 9; i++ ) cols[i].completeCol2(i);
    }

    private void completeArr2() {
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) 
		arrs[p][q].complete2();
    }

    public void block() {
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) 
		arrs[p][q].block();
    } // end block

    public void last() {
	for ( int i = 0; i < 9; i++ ) 
	    for ( int j = 0; j < 9; j++ ) {
		getTile(i, j).last();
	    }
    } // end last

    /*
    public void notify(int i, int j) {
	actor.notify(i, j);
    }
    */

} // end of Square

class RegionValues {
    static boolean trace = Square.trace;

    protected boolean [] elements = new boolean[10];
    public void setVal(int idx, boolean b) { elements[idx] = b; }
    public void setIdx(int idx) { setVal(idx, true); }
    public void unSetIdx(int idx) { setVal(idx, false); }
    public boolean inRegion(int val) { return elements[val]; }
    protected int zeroCnt = 9;
    public int getZeroCnt() { return zeroCnt; }
    public void decreaseZeroCnt() { zeroCnt--; }
    public void increaseZeroCnt() { zeroCnt++; }
} // RegionValues


class Seq extends RegionValues { 
    static boolean trace = Square.trace;
    private int idx;
    private Tile [] tiles = new Tile[9];
    private Square square = null;
    public void setIndex(Square s, int i) { 
	square = s;
	idx = i; 
    }
    public int getIndex() { return idx; }
    public void setTile(int i, Tile t) { tiles[i] = t; }
    public Tile getTile(int i) { return tiles[i]; }
    public void complete1() {
	if ( 1 != zeroCnt ) return;
	int val = 0;
	int idx = 0;
	for (int i = 0; i < 9; i++) { 
	    if ( 0 == tiles[i].getVal() ) idx = i;
	    int val2 = i+1;
	    if ( !elements[val2] ) val = val2;
	}
	if (trace) System.out.print("complete1  ");
	tiles[idx].setVal(val);
    }
    public void completeRow2(int i) {
	if ( 2 != zeroCnt ) return;
	int p1 = i/3;
	int a = 0; int b = 0;
	int j1 = -1; int j2 = 0;
	for (int k = 0; k < 9; k++) { 
	    if ( 0 == tiles[k].getVal() ) {
		if ( -1 == j1 ) j1 = k; else j2 = k;
	    }
	    int val2 = k+1;
	    if ( !elements[val2] ) {
		if ( 0 == a ) a = val2; else b = val2; 
	    }
	}

	if ( square.cols[j1].inRegion(b) ||
	     square.cols[j2].inRegion(a) ) {
	    if (trace) System.out.print("completeRow2 A ");
	    tiles[j1].setVal(a);
	    tiles[j2].setVal(b);
	    return;
	}
	if ( square.cols[j1].inRegion(a) || 
	     square.cols[j2].inRegion(b) ) {
	    if (trace) System.out.print("completeRow2 B ");
	    tiles[j1].setVal(b);
	    tiles[j2].setVal(a);
	    return;
	}
	
	int q1 = j1/3; int q2 = j2/3; 
	Arr ap1q1 = square.arrs[p1][q1]; // to get the vertical neighbors
	Arr ap1q1V1 = ap1q1.getArrV1();
	Arr ap1q1V2 = ap1q1.getArrV2();
	Arr ap1q2 = ( q1 == q2 ? ap1q1 : square.arrs[p1][q2] );
	Arr ap1q2V1 = ( q1 == q2 ? ap1q1V1 : ap1q2.getArrV1() );
	Arr ap1q2V2 = ( q1 == q2 ? ap1q1V2 : ap1q2.getArrV2() );

	if ( ap1q1V1.mustCol(j1, b) || ap1q1V2.mustCol(j1, b) || 
	     ap1q2V1.mustCol(j2, a) || ap1q2V2.mustCol(j2, a) ) {
	    if (trace) System.out.print("completeRow2 A2 ");
	    tiles[j1].setVal(a);
	    tiles[j2].setVal(b);
	    return;
	} 
	     
	if ( ap1q1V1.mustCol(j1, a) || ap1q1V2.mustCol(j1, a) || 
	     ap1q2V1.mustCol(j2, b) || ap1q2V2.mustCol(j2, b) ) {
	    if (trace) System.out.print("completeRow2 A3 ");
	    tiles[j1].setVal(b);
	    tiles[j2].setVal(a);
	    return;
	} 

	if ( q1 == q2 ) return; // same arr's
	if ( ap1q2.inRegion(a) || 
	     ap1q1.inRegion(b) ) {
	    if (trace) System.out.print("completeRow2 C ");
	    tiles[j1].setVal(a);
	    tiles[j2].setVal(b);
	    return;
	}
	if ( ap1q1.inRegion(a) ||
	     ap1q2.inRegion(b) ) {
	    if (trace) System.out.print("completeRow2 D ");
	    tiles[j1].setVal(b);
	    tiles[j2].setVal(a);
	    return;
	}
    }

    public void completeCol2(int j) { 
	if ( 2 != zeroCnt ) return;
	int q1 = j/3;
	int a = 0; int b = 0;
	int i1 = -1; int i2 = 0;
	for (int k = 0; k < 9; k++) { 
	    if ( 0 == tiles[k].getVal() ) {
		if ( -1 == i1 ) i1 = k; else i2 = k;
	    }
	    int val2 = k+1;
	    if ( !elements[val2] ) {
		if ( 0 == a ) a = val2; else b = val2; 
	    }
	}

	if ( square.rows[i2].inRegion(a) ||
	     square.rows[i1].inRegion(b) ) { 
	    if (trace) System.out.print("completeCol2 A ");
	    tiles[i1].setVal(a);
	    tiles[i2].setVal(b);
	    return;
	}
	if ( square.rows[i1].inRegion(a) ||
	     square.rows[i2].inRegion(b) ) {
	    if (trace) System.out.print("completeCol2 B ");
	    tiles[i1].setVal(b);
	    tiles[i2].setVal(a);
	    return;
	}

	int p1 = i1/3; int p2 = i2/3;
	Arr ap1q1 = square.arrs[p1][q1]; // to get the vertical neighbors
	Arr ap1q1H1 = ap1q1.getArrH1();
	Arr ap1q1H2 = ap1q1.getArrH2();
	Arr ap2q1 = ( p1 == p2 ? ap1q1 : square.arrs[p2][q1] );
	Arr ap2q1H1 = ( p1 == p2 ? ap1q1H1 : ap2q1.getArrH1() );
	Arr ap2q1H2 = ( p1 == p2 ? ap1q1H2 : ap2q1.getArrH2() );

	if ( ap1q1H1.mustRow(i1, b) || ap1q1H2.mustRow(i1, b) || 
	     ap2q1H1.mustRow(i2, a) || ap2q1H2.mustRow(i2, a) ) {
	    if (trace) System.out.print("completeCol2 A2 ");
	    tiles[i1].setVal(a);
	    tiles[i2].setVal(b);
	    return;
	} 

	if ( ap1q1H1.mustRow(i1, a) || ap1q1H2.mustRow(i1, a) || 
	     ap2q1H1.mustRow(i2, b) || ap2q1H2.mustRow(i2, b) ) {
	    if (trace) System.out.print("completeCol2 A3 ");
	    tiles[i1].setVal(b);
	    tiles[i2].setVal(a);
	    return;
	} 

	if ( p1 == p2 ) return; // same arr's
	if ( ap2q1.inRegion(a) ||
	     ap1q1.inRegion(b) ) {
	    if (trace) System.out.print("completeCol2 C ");
	    tiles[i1].setVal(a);
	    tiles[i2].setVal(b);
	    return;
	}
	if ( ap1q1.inRegion(a) ||
	     ap2q1.inRegion(b) ) {
	    if (trace) System.out.print("completeCol2 D ");
	    tiles[i1].setVal(b);
	    tiles[i2].setVal(a);
	    return;
	}
    }

    public void show() {
	System.out.println(
	   "index: " + idx + " " +
	   "zeroCnt: " + zeroCnt);
	/*
	for ( int k = 1; k < 10; k++ ) {
	    boolean b = elements[k];
	    System.out.print( (b ? " " + k : "  ") );
	}
	System.out.println();
	*/
    }

} // end of Seq

class Arr extends RegionValues { 
    static boolean trace = Square.trace;
    private int ixi;
    private int ixj;
    private Tile [][] tiles = new Tile[3][3];
    private Square square = null;
    public void setIndex(Square s, int i, int j) { 
	square = s;
	ixi = i; ixj = j; 
    }
    public int getIindex() { return ixi; }
    public int getJindex() { return ixj; }
    public void setTile(int i, int j, Tile t) { tiles[i][j] = t; }
    public Tile getTile(int i, int j) { return tiles[i][j]; }

    Arr arrH1 = null; Arr arrH2 = null;
    Arr arrV1 = null; Arr arrV2 = null;
    public Arr getArrH1() { return arrH1; }
    public Arr getArrH2() { return arrH2; }
    public Arr getArrV1() { return arrV1; }
    public Arr getArrV2() { return arrV2; }

    public void setNeighbors() {
	int p = ixi; int p1 = 0; int p2 = 0;
	if ( 0 == p ) { 
	    p1 = 1; p2 = 2; 
	} else if ( 1 == p ) {
	    p1 = 0; p2 = 2; 
	} else {
	    p1 = 0; p2 = 1; 
	} 
	int q = ixj; int q1 = 0; int q2 = 0;
	if ( 0 == q ) { 
	    q1 = 1; q2 = 2; 
	} else if ( 1 == q ) {
	    q1 = 0; q2 = 2; 
	} else {
	    q1 = 0; q2 = 1; 
	} 
	arrH1 = square.arrs[p][q1];
	arrH2 = square.arrs[p][q2];
	arrV1 = square.arrs[p1][q];
	arrV2 = square.arrs[p2][q];
    }
    public void decreaseZeroCnt() { 
	super.decreaseZeroCnt();
	square.decreaseZeroCnt();
    }
    public void increaseZeroCnt() { 
	super.increaseZeroCnt();
	square.increaseZeroCnt();
    }
    public void complete1() {
	if ( 1 != zeroCnt ) return;
	int val = 0;
	int idx1 = 0;
	int idy1 = 0;

	for (int i = 0; i < 3; i++) 
	    for (int j = 0; j < 3; j++) 
		if ( 0 == tiles[i][j].getVal() ) {
		    idx1 = i;
		    idy1 = j;
		}
	for (int i = 1; i < 10; i++) { 
	    if ( !elements[i] ) { 
		val = i;
		break;
	    }
	}
	if (trace) System.out.print("CompleteArr1  ");
	tiles[idx1][idy1].setVal(val);
    }

    public void complete2() {
	if ( 2 != zeroCnt ) return;
	int i1 = -1; int i2 = 0;
	int j1 = -1; int j2 = 0;
	int p1 = 0; int p2 = 0; 
	int q1 = 0; int q2 = 0; 
	int a = 0; int b = 0;
	for ( int p = 0; p < 3; p++ ) 
	    for ( int q = 0; q < 3; q++ ) {
		Tile t = tiles[p][q];
		int val = t.getVal();
		if ( 0 == val ) {
		    if ( -1 == i1 ) {
			i1 = t.getI(); j1 = t.getJ();
			p1 = p; q1 = q;
		    } else {
			i2 = t.getI(); j2 = t.getJ();
			p2 = p; q2 = q;
		    }
		}
	    }
	for (int k = 1; k < 10; k++) { 
	    if ( !elements[k] ) {
		if ( 0 == a ) a = k; else b = k; 
	    }
	}
	if ( square.rows[i2].inRegion(a) ||
	     square.cols[j2].inRegion(a) ) {
	    if (trace) System.out.print("completeArr2 A ");
	    tiles[p1][q1].setVal(a);
	    tiles[p2][q2].setVal(b);
	    return;
	}
	if ( square.rows[i1].inRegion(b) ||
	     square.cols[j1].inRegion(b) ) {
	    if (trace) System.out.print("completeArr2 B ");
	    tiles[p1][q1].setVal(a);
	    tiles[p2][q2].setVal(b);
	    return;
	}
    } // complete2

    public void block() {
	boolean [][] tileBools = new boolean[3][3];
	int i0 = 3 * ixi; int j0 = 3 * ixj;
	for ( int k = 1; k < 10; k++ ) {
	    if ( 0 == zeroCnt ) return;
	    if ( elements[k] ) continue; // already in Arr
	    block(k, i0, j0, tileBools);
	    int falseCnt = 0;
	    int pi = 0; int qj = 0;
	    for ( int p = 0; p < 3; p++ ) 
		for ( int q = 0; q < 3; q++ ) 
		    if ( !tileBools[p][q] ) {
			falseCnt++;
			pi = p; qj = q;
		    } 
	    if ( 1 == falseCnt ) { // success!!!
		if (trace) System.out.print("block  ");
		tiles[pi][qj].setVal(k);
	    }
	}
    } //end block()
    public void block(int k, int i0, int j0, boolean [][] tileBools) {
	    for ( int p = 0; p < 3; p++ ) 
		for ( int q = 0; q < 3; q++ ) 
		    tileBools[p][q] = ( 0 < tiles[p][q].getVal() );

	    for ( int p = 0; p < 3; p++ ) {
		int ip = i0 + p;
		if ( square.rows[ip].inRegion(k) ||
		     arrH1.mustRow(ip, k) ||
		     arrH2.mustRow(ip, k) ) 
		    for ( int q = 0; q < 3; q++ ) 
			tileBools[p][q] = true;
	    }
	    for ( int q = 0; q < 3; q++ ) {
		int iq = j0 + q;
		if ( square.cols[iq].inRegion(k) ||
		     arrV1.mustCol(iq, k) ||
		     arrV2.mustCol(iq, k) ) 
		    for ( int p = 0; p < 3; p++ ) 
			tileBools[p][q] = true;
	    } 
    } // end block(int k, int i0, int j0, boolean [][] tileBools)



    public boolean mustRow(int i0, int k) {
	if ( elements[k] ) return false; // already present
	int ixi3 = 3 * ixi;
	int ixj3 = 3 * ixj;
	for ( int p = 0; p < 3; p++ ) {
	    int ip = ixi3 + p;
	    if ( i0 == ip ) continue; // ignore row
	    if ( square.rows[ip].inRegion(k) ) continue; // k cannot be here
	    for ( int q = 0; q < 3; q++ )
		if ( 0 == tiles[p][q].getVal() &&
		     !square.cols[ixj3 + q].inRegion(k) ) return false;
	}
	return true;
    } // end mustRow
    public boolean mustCol(int j0, int k) {
	if ( elements[k] ) return false; // already present
	int ixj3 = 3 * ixj;
	int ixi3 = 3 * ixi;
	for ( int q = 0; q < 3; q++ ) {
	    int iq = ixj3 + q;
	    if ( j0 == iq ) continue; // ignore col
	    if ( square.cols[iq].inRegion(k) ) continue; // k cannot be here
	    for ( int p = 0; p < 3; p++ )
		if ( 0 == tiles[p][q].getVal() &&
		     !square.rows[ixi3 + p].inRegion(k) ) return false;
	}
	return true;
    } // end mustCol

    public void show() {
	System.out.println(
	   "ixi: " + ixi + " " +
	   "ixj: " + ixj + " " +
	   "zeroCnt: " + zeroCnt);
    }
    /*
    public void notify(int i, int j) {
	square.notify(i, j);
    }
    */

} // end of Arr

class Tile implements Comparable {
    static boolean trace = Square.trace;
    private int val = 0;
    private int ixi = 0; // is square index 0-8
    private int ixj = 0; // is square index 0-8
    private Seq row = null;
    private Seq col = null;
    private Arr arr = null;
    private Arr arrH1 = null;
    private Arr arrH2 = null;
    private Arr arrV1 = null;
    private Arr arrV2 = null;

    public Seq getRow() { return row; }
    public Seq getCol() { return col; }
    public Arr getArr() { return arr; }

    private int sumZeroCnt = 0;
    public void calcSumZeroCnt() {
	sumZeroCnt = row.getZeroCnt() + col.getZeroCnt() + arr.getZeroCnt();
    }
    public int getSumZeroCnt() { return sumZeroCnt; }
    public int compareTo(Object o) {
	// order high to low
	Tile di2 = this;
	try { di2 = (Tile) o; } catch (ClassCastException ignore) {}
	int sumZeroCnt2 = di2.getSumZeroCnt();
	if ( sumZeroCnt2 < sumZeroCnt ) return -1;
	if ( sumZeroCnt < sumZeroCnt2 ) return 1;
	return 0;
    }

    Tile(int i, int j, Seq[] rows, Seq[] cols, Arr [][] arrs) {
	ixi = i; ixj = j; row = rows[i]; col = cols[j];
	int p = i/3; int q = j/3; arr = arrs[p][q];
	arrH1 = arr.getArrH1();
	arrH2 = arr.getArrH2();
	arrV1 = arr.getArrV1();
	arrV2 = arr.getArrV2();
    }
    public int getVal() { return val; }
    public int getI() { return ixi; }
    public int getJ() { return ixj; }

    public void setValInit(int z) { 
	if ( 0 == z ) return;
	row.setIdx(z);
	row.decreaseZeroCnt();
	col.setIdx(z);
	col.decreaseZeroCnt();
	arr.setIdx(z);
	arr.decreaseZeroCnt();
	val = z;
    }
    public void setVal(int z) { 
	if ( 0 == z ) {
	    row.unSetIdx(val);
	    row.increaseZeroCnt();
	    col.unSetIdx(val);
	    col.increaseZeroCnt();
	    arr.unSetIdx(val);
	    arr.increaseZeroCnt();
	} else {
	    if ( 0 < val ) { 
		System.out.println(
		   "######### Tile i: " + ixi + " j: " + ixj + " IS SET");
		return;
	    }
	    row.setIdx(z);
	    row.decreaseZeroCnt();
	    col.setIdx(z);
	    col.decreaseZeroCnt();
	    arr.setIdx(z);
	    arr.decreaseZeroCnt();
	    // arr.notify(ixi, ixj);
	    // System.out.println("Tile i: " + ixi + " j: " + ixj + " -> " + z);
	}
	val = z;
    } // end setVal

    public void last() {
	if ( 0 < val ) return;
	int inCnt = 0;
	boolean [] elementsX = new boolean[10];
	boolean b;
	for ( int k = 1; k < 10; k++ ) {
	    if ( arr.inRegion(k) ||
		 row.inRegion(k) || arrH1.mustRow(ixi, k) || arrH2.mustRow(ixi, k) || 
		 col.inRegion(k) || arrV1.mustCol(ixj, k) || arrV2.mustCol(ixj, k) 
		 ) {
		inCnt++;
		elementsX[k] = true;
	    }
	}
	if ( 8 != inCnt ) return; // cannot determine
	for ( int k = 1; k < 10; k++ ) 
	    if ( !elementsX[k] ) {
		if (trace) System.out.print("last  ");
		setVal(k);
		break;
	    }
    } // end last

} // end Tile







