// File: c:/ddc/Java/Actor7A/Node.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Wed May 22 21:08:11 2019

package actor7A;

import java.util.*;
import java.io.*;

class Node implements Comparable {
    private static PriorityQueue<Node> openNodes = new PriorityQueue<Node>();
    public static void addNodeO(Node n) { openNodes.offer(n); }
    // getNodeO will remove the node also, when found
    public static Node getNodeO() { return ( 0 == openNodes.size() ? null :
					    openNodes.poll() ); }
    public static void deleteNodeO(Node n) { openNodes.remove(n); }

    private static Hashtable<String, Node> allNodes = 
	new Hashtable<String, Node>(); // Key must be customized Key -> String
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
    // abstract public Node(Object obj);
    // private Object nodeContent = null;
    // The operation applied to the parent to generate this node:
    // private Object operation; 

    private int leftNumCannibals; 
    public int getLeftNumCannibals() { return leftNumCannibals; }
    private int rightNumCannibals; 
    public int getRightNumCannibals() { return rightNumCannibals; }
    private int leftNumMissionaries;
    public int getLeftNumMissionaries() { return leftNumMissionaries; }
    private int rightNumMissionaries;
    public int getRightNumMissionaries() { return rightNumMissionaries; }
    private boolean boat;
    public boolean getBoat() { return boat; }
    static private int nodeCntN = 0;
    public Node(int lc, int rc, int lm, int rm, boolean b, Node pn, int gv) {
	// These checks will be done later
	// checkNum("lc",lc); checkNum("rc",rc); 
	// checkNum("lm",lm); checkNum("rm",rm); 
	leftNumCannibals = lc; rightNumCannibals = rc;
	leftNumMissionaries = lm; rightNumMissionaries = rm;
	boat = b; g = gv; parent = pn;
	StringBuffer sb = new StringBuffer();
	sb.append(lc); sb.append(rc); 
	sb.append(lm); sb.append(rm); 
	sb.append(b);
	key = sb.toString();
	h = 0; // no good function available
	nodeCnt = ++nodeCntN;
	System.out.println("New Node: " +
			   "lc " + lc + " lm " + lm +
			   " rc " + rc + " rm " + rm +
			   "    " + nodeCnt);
    } // end Node 
    private int nodeCnt = 0;
    public int getNodeCnt() { return nodeCnt; }
    private Node parent = null;
    public Node getParent() { return parent; }
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
    public boolean equals(Node n2) {
	if ( boat != n2.getBoat() ) return false;
	if ( leftNumCannibals != n2.getLeftNumCannibals() ) return false;
	if ( rightNumCannibals != n2.getRightNumCannibals() ) return false;
	if ( leftNumMissionaries != n2.getLeftNumMissionaries() ) return false;
	if ( rightNumMissionaries != n2.getRightNumMissionaries() ) return false;
	return true;
    } // end equals
    public int hashCode() { return key.hashCode(); }
    public int compareTo(Object o2) { 
	Node n2 = (Node) o2;
	return ( equals(n2) ? 0 : ( (this.F() - n2.F()) < 0 ? -1 : 1 ) ); }
    public boolean isGoal() {
	if ( boat) return false;
	if ( 3 != rightNumCannibals ) return false;
	if ( 3 != rightNumMissionaries ) return false;
	return true;
    } // end isGoal
    public void processGoalNode(Actor actor) {
	actor.setGoalNode(this);
    }
    public Vector<Node> successors() { 
	Vector<Node> out = new Vector<Node>();
	boolean b2 = !boat;
	if ( boat ) {
	    proposeNodeR(out, 1, 0, b2);
	    proposeNodeR(out, 0, 1, b2);
	    proposeNodeR(out, 1, 1, b2);
	    proposeNodeR(out, 2, 0, b2);
	    proposeNodeR(out, 0, 2, b2);
	} else {
	    proposeNodeL(out, 1, 0, b2);
	    proposeNodeL(out, 0, 1, b2);
	    proposeNodeL(out, 1, 1, b2);
	    proposeNodeL(out, 2, 0, b2);
	    proposeNodeL(out, 0, 2, b2);
	}
	return out;
    }
    private void proposeNodeR(Vector<Node> out, int c, int m, boolean b2) {
	int lc2 = leftNumCannibals - c;
	int rc2 = rightNumCannibals + c;
	int lm2 = leftNumMissionaries - m;
	int rm2 = rightNumMissionaries + m;
	if ( 0 <= lc2 && lc2 <= 3 &&
	     0 <= rc2 && rc2 <= 3 &&
	     0 <= lm2 && lm2 <= 3 &&
	     0 <= rm2 && rm2 <= 3 && 
	     ( lm2 == 0 || lc2 <= lm2 ) &&
	     ( rm2 == 0 || rc2 <= rm2 ) )
	    out.addElement(new Node(lc2, rc2, lm2, rm2, b2, this, 1+G()));
	else { 
	    System.out.println("----- R Cancelled node -----------");
	}
    }
    private void proposeNodeL(Vector<Node> out, int c, int m, boolean b2) {
	int lc2 = leftNumCannibals + c;
	int rc2 = rightNumCannibals - c;
	int lm2 = leftNumMissionaries + m;
	int rm2 = rightNumMissionaries - m;
	if ( 0 <= lc2 && lc2 <= 3 &&
	     0 <= rc2 && rc2 <= 3 &&
	     0 <= lm2 && lm2 <= 3 &&
	     0 <= rm2 && rm2 <= 3 && 
	     ( lm2 == 0 || lc2 <= lm2 ) &&
	     ( rm2 == 0 || rc2 <= rm2 ) )
	    out.addElement(new Node(lc2, rc2, lm2, rm2, b2, this, 1+G()));
	else System.out.println("----- L Cancelled node -----------");
    }

    public void printNode() {
	System.out.println(
	     "Node lc " + leftNumCannibals + " lm " + leftNumMissionaries +
	     " rc " + rightNumCannibals + " rm " + rightNumMissionaries +
	     " b " + boat + " g " + g + " cnt " + nodeCnt);
    } // end printNode
    public void printSolution() {
	System.out.print("solution node:");
	printNode();
	if ( null != parent ) parent.printSolution();
    }


} // end Node 

// Just a place holder
// abstract class Key {
// } // end Key
