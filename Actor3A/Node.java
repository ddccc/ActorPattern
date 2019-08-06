// File: c:/ddc/Java/Actor3A/Node.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Wed May 22 21:08:11 2019

package actor3A;

import java.util.*;
import java.io.*;

class Node implements Comparable {
    private static PriorityQueue<Node> openNodes = new PriorityQueue<Node>();
    public static void addNodeO(Node n) { 
	openNodes.offer(n); 
	System.out.println("addNodeO | | " + openNodes.size());
    }
    // getNodeO will remove the node also, when found
    public static Node getNodeO() { 
	Node nx = ( 0 == openNodes.size() ? null : openNodes.poll() );
	System.out.println("getNodeO | | " + openNodes.size() + " cnt " +
			   ( nx == null ? 0 : nx.getNodeCnt() ) );
	return nx;
	/*
			       return ( 0 == openNodes.size() ? null :
					    openNodes.poll() ); 
	*/
    }
    public static void deleteNodeO(Node n) { 
		openNodes.remove(n); 
		System.out.println("deleteNodeO | | " + openNodes.size());
	    }

    private static Hashtable<String, Node> allNodes = 
	new Hashtable<String, Node>(); // String must be customized
    public static void addNode(String k, Node n) { allNodes.put(k, n); }
    // getNode return null if no node found
    public static Node getNode(String k) { return allNodes.get(k); }
    public static void deleteNode(String k) { allNodes.remove(k); }

    // Constructor must be customized
    /* obj is the source that describes the state of the node
       need the set g & h
       need to construct a good key for the hashtable based only on obj
       need to construct a value for the hashCode() based only on obj
       need to construct equals based only on obj
     */
    static private int nodeCntN = 0;
    public Node(Square square, Node parent, int gVal) {
	this.square = square; this.parent = parent; g = gVal;
	StringBuffer sb = new StringBuffer();
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) 
		sb.append(square.v(i,j));
	key = sb.toString();
	h = square.getZeroCnt();
	nodeCnt = ++nodeCntN;
	System.out.println("New Node: " + nodeCnt);
    }
    private int nodeCnt = 0;
    public int getNodeCnt() { return nodeCnt; }
    // private Object nodeContent = null;
    // The operation applied to the parent to generate this node:
    // private Object operation; 
    private Node parent = null;
    public Node getParent() { return parent; }
    private Square square;
    public Square getSquare() { return square; }
    // private Key key = null;
    private String key = null;
    //  public Key getKey() { return key; }
    public String getKey() { return key; }
    private boolean isOpen = true;
    public void setOpen() { isOpen = true; } // not used
    public void setClosed() { isOpen = false; }
    public boolean getIsOpen() { return isOpen; }
    private int g = 0;
    private float h = 0; 
    public void setG(int x) { g = x; }
    public void setH(float x) { h = x; }
    public int G() { return g; }
    public float H() { return h; }
    public float F() { return G() + H(); }
    public boolean equals(Node n2) { return key.equals(n2.getKey()); }
    public int hashCode() { return key.hashCode(); }
    // public int compareTo(Node n2) { 
    public int compareTo(Object o2) {
	Node n2 = (Node) o2;
	return ( equals(n2) ? 0 : ( (this.F() - n2.F()) < 0 ? -1 : 1 ) ); }
    public boolean isGoal() { return (0 == square.getZeroCnt()); }
    public void processGoalNode(Actor actor) {
	actor.setGoalNode(this);
    }
    public Vector<Node> successors() {
	Vector<Node> out = new Vector<Node>();
	square.solve(); // try solving with the human type tactics
	h = square.getZeroCnt();
	if ( 0 == h ) {
	    int[][] arr = new int[9][9];
	    for ( int i = 0; i < 9; i++ )
		for ( int j = 0; j < 9; j++ ) arr[i][j] = square.v(i, j);
	    if ( Sutils.check(arr) ) { 
		System.out.println("FOUND A SOLUTION " + nodeCnt);
		Square s2 = square.cloneSquare();
		Node nx = new Node(s2, this, G());
		out.addElement(nx);
	    }
	    return out;
	}
	// Not solved yet
	// Identify a most constrained tile and create successor nodes
	Tile target = null; // identify a most constrained tile
	int a = 9; int b = 9; int c = 9;
	for ( int i = 0; i < 9; i++ )
	    for ( int j = 0; j < 9; j++ ) {
		int x;
		Tile t = square.getTile(i, j);
		if ( 0 != t.getVal() ) continue;
		int ax = t.getRow().getZeroCnt();
		int bx = t.getCol().getZeroCnt();
		if ( bx < ax ) { x = ax; ax = bx; bx = x; }
		int cx = t.getArr().getZeroCnt();
		if ( cx < ax ) { x = cx; cx = bx; bx = ax; ax = x; }
		else if ( cx < bx ) { x = cx; cx = bx; bx = x; }
		if ( ax < a ) { target = t; a = ax; continue; }
		if ( a < ax ) continue;
		if ( bx < b ) { target = t; b = bx; continue; }
		if ( b < bx ) continue;
		if ( cx < c ) { target = t; c = cx; }
	    }
	// Found the best tile.
	// now decide which values it can get given the current context 
	int i1 = target.getI(); // <i1,j1> are indices
	int j1 = target.getJ();
	Seq row = target.getRow(); // the row of this tile
	Seq col = target.getCol();
	Arr arr = target.getArr();
	for ( int k = 1; k < 10; k++ ) { // try this range
	    if ( row.inRegion(k) ) continue; // not acceptable
	    if ( col.inRegion(k) ) continue;
	    if ( arr.inRegion(k) ) continue;
	    System.out.println(
		    "New square i1 " + i1 + " j1 " + j1 + " k " + k);
	    Square s2 = square.cloneSquare();
	    Tile t = s2.getTile(i1, j1);
	    t.setVal(k); // set this tile with k
	    Node nx = new Node(s2, this, 1 + G());
	    out.addElement(nx);
	}
	return out;
    }
} // end Node 
/*
// Just a place holder; using String in this version.
abstract class Key {
} // end Key
*/
