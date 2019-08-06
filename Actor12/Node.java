// File: c:/ddc/Java/Actor12/Node.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Wed May 22 21:08:11 2019

package actor12; // adjust

import java.util.*;
import java.io.*;
import fol.*; // 1st order logic theorem prover

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
    /* 
       abstract public Node(Object obj);
       obj is the source that describes the state of the node
       need the set g & h
       need to construct a good key for the hashtable based only on obj
       need to construct a value for the hashCode() based only on obj
       --
       need to construct equals based only on obj
       the procedure for generating successor nodes
     */

    static public Vector <PddlAction> actions;
    static public Vector<Atom> goalState;
    private Vector<Atom> stateDescription;
    static private int nodeCntN = 0;
    private int nodeCnt;
    public Node(Vector <PddlAction> actions, Vector<Atom> initialState,
		Vector<Atom> goalState) {
	// used to create the first node
	this(initialState, null, 0);
	this.actions = actions;
	this.goalState = goalState;
    }
    private Vector<Atom> sort(Vector<Atom> in) {
	// sort the in sequence and removes duplicates
	int lng = in.size();
	if ( lng <= 1 ) return in;
	Atom lastElement = (Atom) in.remove(lng-1);
	Vector<Atom> in2 = sort(in);
	return insert(in2, lastElement, new Vector<Atom>());
    }
    private Vector<Atom> insert(Vector<Atom> in2, 
				Atom lastElement, Vector<Atom> tail) {
	int lng = in2.size();
	if ( 0 == lng ) {
	    in2.addElement(lastElement);
	    return append(in2, tail);
	}
	Atom last2 = (Atom) in2.remove(lng-1);
	int comp = last2.html().compareTo(lastElement.html());
	if ( 0 == comp ) {
	    in2.addElement(last2); // put back & remove lastElement!
	    return append(in2, tail);
	}
	if ( comp < 0 ) {
	    in2.addElement(last2); // put back
	    in2.addElement(lastElement); // insert
	    return append(in2, tail);
	}
	tail.addElement(last2);
	return insert(in2, lastElement, tail);
    }
    private Vector<Atom> append(Vector<Atom> in3, Vector<Atom> tail) {
	int lng = tail.size();
	if ( 0 == lng ) return in3;
	Atom last3 = (Atom) tail.remove(lng-1);
	in3.addElement(last3);
	return append(in3, tail);
    }

    public Node(Vector<Atom> atoms, Node parent, int gVal) {
	stateDescription = sort(atoms);
	this.parent = parent;
	g = gVal;
	StringBuffer sb = new StringBuffer();
	int lng = stateDescription.size();
	for (int i = 0; i < lng; i++) 
	    sb.append(stateDescription.elementAt(i).html());
	key = sb.toString();

	nodeCnt = ++nodeCntN;
	// System.out.println("New Node: " + nodeCnt);
    }

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
    public boolean equals(Node n2) { return key.equals(n2.getKey()); }
    public int hashCode() { return key.hashCode(); } 
    public int compareTo(Object o2) { 
        Node n2 = (Node) o2;
	return ( equals(n2) ? 0 : ( (this.F() - n2.F()) < 0 ? -1 : 1 ) ); 
    }
    public boolean isGoal() { 
	int lng = goalState.size(); int lng2 = stateDescription.size();
	for (int i = 0; i < lng; i++) {
	    Atom goali = goalState.elementAt(i);
	    boolean found = false;
	    for (int j = 0; j < lng2; j++) {
		Atom sdj = stateDescription.elementAt(j);
		if ( goali.html().equals(sdj.html()) ) {
		    found = true;
		    break;
		}
	    }
	    if ( !found ) return false;
	}
	return true;
    }

    public void processGoalNode(Actor actor) {
	actor.setGoalNode(this);
    }
    public Vector<Node> successors() { 
	Vector<Node> out = new Vector<Node>();
	int lngActions = actions.size();
	for (int ac = 0; ac < lngActions; ac++) {
	    PddlAction aci = actions.elementAt(ac);
	    // System.out.println("Action: " + aci.getName());
	    Vector<Node> out2 = generateNodes(0, aci,
		   new Vector <Substitution>());
	    // System.out.println("Action: " + aci.getName() +
	    //	       " # nodes " + out2.size());
	    out.addAll(out2);
	}					     
	return out; 
    }; // Must be customized

    private Vector<Node> generateNodes(int pridx, PddlAction aci,
	       Vector <Substitution> subs) {
	Vector<Atom> preconds = aci.getPreconds();
	Vector<Node> nodes = new Vector<Node>();
	if ( preconds.size() <= pridx ) {
	    if ( subs.isEmpty() ) return nodes;
	    Vector<Atom> deletes = aci.getDeletes();
	    int lng = deletes.size();
	    Vector <Atom> toBeDeleted = new Vector<Atom>();
	    for (int dx = 0; dx < lng; dx++) {
		Atom ax = deletes.elementAt(dx);
		ax = (Atom) ax.sublis(subs); 
		// remove ax from current stateDecription
		toBeDeleted.addElement(ax);
	    }
	    Vector <Atom> newState = new Vector <Atom>();
	    lng = stateDescription.size();
	    for (int i = 0; i < lng; i++) {
		Atom ai = stateDescription.elementAt(i);
		boolean found = false;
		int lng2 = toBeDeleted.size();
		for (int j = 0; j < lng2; j++) {
		    if ( ai.equals(toBeDeleted.elementAt(j)) ) {
			found = true; break;
		    }
		} 
		if ( !found ) newState.addElement(ai);
	    }
	    Vector<Atom> adds = aci.getAdds();
	    lng = adds.size();
	    for (int i = 0; i < lng; i++) {
		Atom ai = adds.elementAt(i);
		ai = (Atom) ai.sublis(subs);
		newState.addElement(ai);
	    }
	    // create new node and add to nodes
	    Node newNode = new Node(newState, this, g+1);
	    // newNode.printNode();
	    nodes.addElement(newNode);
	    return nodes;
	}
	// process next item in preconds[pridx]
	// check whether a match happens of preconds[pridx] of
	// an item in stateDescriptionJ (adjusted with  subs)
	// if a match extend subs and recurse with pridx+1 &
	// repeat with J+1
	Atom precond = (Atom)preconds.elementAt(pridx).sublis(subs);
	int lng = stateDescription.size();
	for (int i = 0; i < lng; i++) {
	    Atom sti = stateDescription.elementAt(i);
	    Vector subsi = precond.unify(sti);
	    // print here
	    if (null == subsi) {
		// System.out.println(sti.html() + 
		//    " no match with " + precond.html());
		continue;
	    }
	    // System.out.println(sti.html() + 
	    //	       " MATCH with " + precond.html());
	    Vector <Substitution> subsc = (Vector <Substitution>)subs.clone();
	    int lngs = subsi.size();
	    for (int j = 0; j < lngs; j++)
		subsc.addElement((Substitution)subsi.elementAt(j));
	    Vector nodesi = generateNodes(pridx+1, aci, subsc);
	    nodes.addAll(nodesi);
	}
	return nodes;
    }

    public void printNode(){
	System.out.println("Node " + nodeCnt);
	int lng = stateDescription.size();
	for (int i = 0; i < lng; i++)
	    System.out.print(stateDescription.elementAt(i).html() + " ");
	System.out.println();
    }
    public void printSolution() {
	System.out.print("solution node:");
	printNode();
	if ( null != parent ) parent.printSolution();
    }

} // end Node 

// Just a place holder
// abstract class Key {
// } // end Key

class PddlAction {
    static public Vector<Atom> atoms(String terms, String messg) {
	Parser parser = new Parser(false); // true for tracing
	Vector<Atom> out = new Vector<Atom>();
	StringTokenizer st = new StringTokenizer(terms, "|");
	while (st.hasMoreTokens()) {
	    String xyz = st.nextToken();
	    try { out.addElement((Atom)parser.parse(xyz)); }
	    catch (Exception ex) { 
		System.out.println(messg + xyz);
		System.exit(0);
	    }
	}
	// /*
	int lng = out.size();
	for ( int i = 0; i < lng; i++ )
	    System.out.println(i + " " + out.elementAt(i).html());
	// */
	return out;
    }
    private String name;
    public String getName() { return name; }
    private Vector<Variable> variables = new Vector<Variable>();
    private Vector<Atom> preconds;
    public Vector<Atom> getPreconds() { return preconds; }
    private Vector<Atom> deletes;
    public Vector<Atom> getDeletes() { return deletes; }
    private Vector<Atom> adds;
    public Vector<Atom> getAdds() { return adds; }
    public PddlAction(String name, String params,
		     String preconditions, String deleteItems, String addItems) {
	this.name = name;
	System.out.println("Name: " + name);
	// parse the params -> variables
	StringTokenizer st = new StringTokenizer(params);
	while (st.hasMoreTokens()) {
	    String param = st.nextToken();
	    if ( param.length() < 2 ) {
		System.out.println("PddlAction  param.length() < 2 " + param.length());
		System.exit(0);
	    }
	    if ( !param.startsWith("?") ) {
		System.out.println("PddlAction  !param.startsWith(\"?\") " + param);
		System.exit(0);
	    }
	    variables.addElement(new Variable(param.substring(1)));
	}
	// parse the preconditions
	preconds = atoms(preconditions, "parse error preconditions");
	// parse the delete items
	deletes = atoms(deleteItems, "parse error deleteItems");
	// parse the add items
	adds = atoms(addItems, "parse error addItems");

    } // end PddlParser
} // end PddlParser

