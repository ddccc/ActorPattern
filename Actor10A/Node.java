// File: c:/ddc/Java/Actor10A/Node.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Thu Jun 13 13:58:06 2019

package actor10A;

import java.util.*;
import java.io.*;

public class Node implements Comparable {
    private static PriorityQueue<Node> openNodes = new PriorityQueue<Node>();
    public static void addNodeO(Node n) { openNodes.offer(n); }
    // getNodeO will remove the node also, when found
    public static Node getNodeO() { return ( 0 == openNodes.size() ? null :
					    openNodes.poll() ); }
    public static void deleteNodeO(Node n) { openNodes.remove(n); }

    private static Hashtable<String, Node> allNodes = 
	new Hashtable<String, Node>(); // Key must be customized
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
    public Node(Grid grid, Node parent, int gVal) {
	this.grid = grid; this.parent = parent; g = gVal;
	key = grid.getKey();
	calculateH(grid);
	nodeCnt = ++nodeCntN;
	System.out.println("New Node: " + nodeCnt);
    }
    private void calculateH(Grid grid) {
	int [][] boxes = grid.getBoxes();
	int [][] goals = grid.getGoals(); 
	int rows = boxes.length;
	for (int row1 = 0; row1 < rows; row1++) {
	    int minimum = 100;
	    int bx = boxes[row1][0]; int by = boxes[row1][1]; 
	    for (int row2 = 0; row2 < rows; row2++) {
		int gx = goals[row2][0]; int gy = goals[row2][1]; 
		int dist = abs(bx-gx) + abs(by-gy);
		if ( dist < minimum ) minimum = dist;
		
		// if ( bx == gx && by == gy ) {
		    // h = h-1; // heuristic bonus
		// System.out.println("|||||||| bonus bx " + bx + " by " + by);
		// } 
	    }
	    h = h + minimum;
	}
	if ( null != parent && 
	     numberBoxesOnGoal() > parent.numberBoxesOnGoal() ) h = h-10;
    } 
    private int numberBoxesOnGoal() { return grid.numberBoxesOnGoal(); }
    private int abs(int x) { return ( 0 <= x ? x : -x); }
    private int nodeCnt = 0;
    public int getNodeCnt() { return nodeCnt; }

    private Node parent = null;
    public Node getParent() { return parent; }
    private Grid grid = null;
    public Grid getGrid() { return grid; }
    private String key = null;
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
    public int compareTo(Object o2) { 
        Node n2 = (Node) o2;
	return ( equals(n2) ? 0 : ( (this.F() - n2.F()) < 0 ? -1 : 1 ) ); }
    public boolean isGoal() { return grid.isSolution(); }
    public void processGoalNode(Actor actor) {
	actor.setGoalNode(this);
    }
    public Vector<Node> successors() {
	Vector<Node> out = new Vector<Node>();
	int numberOfMoves = 0;

	int [][] boxes = grid.getBoxes();
	int [][] integers = grid.getIntegers();
	Vector boxmoves = new Vector();
	Vector boxmoves2 = new Vector();

	// vectori = (boxy/ boxx/ nesw1234) adjacent locs are free
	int rows = boxes.length;
	for (int i = 0; i < rows; i++) {
	    int [] boxi = boxes[i];
	    int by = boxi[0], bx = boxi[1];
	    if ( integers[by-1][bx] == Grid.FREE &&
		 integers[by+1][bx] == Grid.FREE ) {
		if ( grid.hasGoalQ(by+1, bx) )
		    boxmoves.add(new int[] {by, bx, 1}); // N->S
		else 
		    boxmoves2.add(new int[] {by, bx, 1}); // N->S
		if ( grid.hasGoalQ(by-1, bx) )
		    boxmoves.add(new int[] {by, bx, 3}); // S->N
		else
		    boxmoves2.add(new int[] {by, bx, 3}); // S->N
	    }
	    if ( integers[by][bx+1] == Grid.FREE &&
		 integers[by][bx-1] == Grid.FREE ) {
		if ( grid.hasGoalQ(by, bx+1) )
		    boxmoves.add(new int[] {by, bx, 4}); // W->E
		else
		    boxmoves2.add(new int[] {by, bx, 4}); // W->E
		if ( grid.hasGoalQ(by, bx-1) )
		    boxmoves.add(new int[] {by, bx, 2}); // E->W
		else
		    boxmoves2.add(new int[] {by, bx, 2}); // E->W
	    }
	}
	int lng2 = boxmoves2.size();
	for (int i = 0; i < lng2; i++) // promote moves to goal locs
	    boxmoves.add(boxmoves2.elementAt(i));
	// candidate box moves provided the start locs can be reached
	// System.out.println("MonitorGenerateMoves boxmoves: " + boxmoves.size());

	numberOfMoves = boxmoves.size();
	if ( 0 == numberOfMoves ) {
	    System.out.println("|-|-|-|-|-|-|-|- No box moves of node:: " + nodeCnt);
	    grid.print();
	    return out;
	}


	// -------------------------------------------------------------------

	int manx = grid.getManx(), many = grid.getMany();

	int proposedCnt = 0;
	for (int i = 0; i < boxmoves.size(); i++ ) {
	    // boolean atWall = false; boolean atWall2 = false; boolean inCorner = false;
	    int[] triple = (int[]) boxmoves.elementAt(i);
	    int by = triple[0], bx = triple[1], direction = triple[2];
	    System.out.println("i: " + i + 
			       " triple by: " + by + " bx " + bx + " dir " + direction);
	    // Vector assertions = new Vector();
	    if ( 1 == direction ) {
		if ( !grid.hasPath(manx, many, bx, by-1) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int by2 = by+1;
		if ( grid.isBlocked(by2, bx) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		Node n = proposeNode(this, direction, bx, by);
		if ( n == null ) numberOfMoves--; else {
		    proposedCnt++;
		    out.addElement(n);
		}
	    } else 
	    if ( 3 == direction ) {
		if ( !grid.hasPath(manx, many, bx, by+1) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int by2 = by-1;
		if ( grid.isBlocked(by2, bx) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		Node n = proposeNode(this, direction, bx, by);
		if ( n == null ) numberOfMoves--; else {
		    proposedCnt++;
		    out.addElement(n);
		}
	    } else 
	    if ( 2 == direction ) {
		if ( !grid.hasPath(manx, many, bx+1, by) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int bx2 = bx-1;
		if ( grid.isBlocked(by, bx2) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		Node n = proposeNode(this, direction, bx, by);
		if ( n == null ) numberOfMoves--; else {
		    proposedCnt++;
		    out.addElement(n);
		}
	    } else 
	    if ( 4 == direction ) {
		if ( !grid.hasPath(manx, many, bx-1, by) ) {
		    numberOfMoves--;
		    System.out.println("No path"); continue;
		}
		int bx2 = bx+1;
		if ( grid.isBlocked(by, bx2) ) {
		    numberOfMoves--;
		    System.out.println("Reject direction: " + direction);
		    continue;
		}
		Node n = proposeNode(this, direction, bx, by);
		if ( n == null ) numberOfMoves--; else {
		    proposedCnt++;
		    out.addElement(n);
		}
	    } else 
		System.out.println("****** node.succesors:: not recognized direction");
	}
	if ( 0 == proposedCnt || 0 == numberOfMoves ) {
	    System.out.println("||||||||||||||||||| Dead node #: " + nodeCnt);
	}
	return out;
	// System.exit(0);
    } // end successors

    public Node proposeNode(Node node, int direction, int bx, int by) {

	// Check here whether the box hits a wall 
	// direction 1: N->S, 3: S->N, 2: E->W, 4: W->E

	System.out.println();
	System.out.println("||||||||||||||||||| proposedNode #: " 
			   + nodeCnt);
	System.out.println("proposeNode by " + by + " bx " + bx + 
		 " direction " + direction);  

	Grid grid = node.getGrid();
	int [][] boxes = grid.getBoxes();
	int bx2 = bx, by2 = by, mx = bx, my = by;
	int [][] boxes2 = new int[boxes.length][2];
	switch (direction) {
	case 1: 
	    by2 = by + 1;
	    break;
	case 3:
	    by2 = by - 1; 
	    break;
	case 2:
	    bx2 = bx-1;
	    break;
	case 4:
	    bx2 = bx+1;
	    break;
	default:
	    System.out.println("*** Error in proposeNode: direction= " +
			       direction);
	    return null;
	}

	for (int i = 0; i < boxes.length; i++ ) {
	    int [] rowi = boxes[i];
	    if ( by == rowi[0] && bx == rowi[1] ) {
		boxes2[i] = new int [] { by2, bx2 };
	    } else boxes2[i] = rowi;
	}

	Grid grid2 = new Grid(boxes2, mx, my);
	Node nodeCandidate = new Node(grid2, node, 1 + g);
	return nodeCandidate;
    } // end proposeNode

    public void printNode() { grid.print(); }
    public void printSolution() {
	System.out.print("solution node: " + nodeCnt + " ");
	printNode();
	if ( null != parent ) parent.printSolution();
    }

} // end Node 

// Just a place holder
// abstract class Key {
// } // end Key
