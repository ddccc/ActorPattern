// File: c:/ddc/Java/ActorAstar/Node.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Wed May 22 21:08:11 2019

package actorAstar; // adjust

import java.util.*;
import java.io.*;

abstract class Node implements Comparable {
    private static PriorityQueue<Node> openNodes = new PriorityQueue<Node>();
    public static void addNodeO(Node n) { openNodes.offer(n); }
    // getNodeO will remove the node also, when found
    public static Node getNodeO() { return ( 0 == openNodes.size() ? null :
					    openNodes.poll() ); }
    public static void deleteNodeO(Node n) { openNodes.remove(n); }

    private static Hashtable<Key, Node> allNodes = 
	new Hashtable<Key, Node>(); // Key must be customized
    public static void addNode(Key k, Node n) { allNodes.put(k, n); }
    // getNode return null if no node found
    public static Node getNode(Key k) { return allNodes.get(k); }
    public static void deleteNode(Key k) { allNodes.remove(k); }

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
    private Node parent = null;
    public Node getParent() { return parent; }
    private Key key = null;
    public Key getKey() { return key; }
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
    abstract public boolean equals(Node n2); // Must be customized
    abstract public int hashCode();
    public int compareTo(Object o2) { 
        Node n2 = (Node) o2;
	return ( equals(n2) ? 0 : ( (this.F() - n2.F()) < 0 ? -1 : 1 ) ); }
    abstract public boolean isGoal(); // Must be customized
    public void processGoalNode(Actor actor) {
	actor.setGoalNode(this);
    }
    abstract public Vector<Node> successors(); // Must be customized

    abstract public void printNode();
    public void printSolution() {
	System.out.print("solution node:");
	printNode();
	if ( null != parent ) parent.printSolution();
    }

} // end Node 

// Just a place holder
abstract class Key {
} // end Key
