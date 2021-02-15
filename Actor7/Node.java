// File: c:/ddc/Java/Actor7/Node.java
// Date: Sat Sep 30 13:28:59 2017
package actor7;

public class Node implements Comparable  {
    public int compareTo(Object o2) { 
	if ( equals(o2) ) return 0;
	return compare(this, o2); 
    }
    public int compare(Object o1, Object o2) {
	if ( !(o1 instanceof Node) || 
	     !(o2 instanceof Node) ) return 0; // cannot happen
	Node s1 = (Node) o1;
	Node s2 = (Node) o2;
	if ( s1.equals(s2) ) return 0;
	int z1 = s1.G();
	int z2 = s2.G();
	if ( z1 != z2 ) return ( z1 < z2 ? -1 : 1 );
	return 1;
    } // end compare
    public int G() { return g; }
    public boolean equals(Node n) {
	if ( boat != n.getBoat() ) return false;
	if ( leftNumCannibals != n.getLeftNumCannibals() ) return false;
	if ( rightNumCannibals != n.getRightNumCannibals() ) return false;
	if ( leftNumMissionaries != n.getLeftNumMissionaries() ) return false;
	if ( rightNumMissionaries != n.getRightNumMissionaries() ) return false;
	return true;
    } // end equals

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
    private int g; Node previousNode;
    public Node(int lc, int rc, int lm, int rm, boolean b, int gv,
		Node pn) {
	// These checks will be done later
	// checkNum("lc",lc); checkNum("rc",rc); 
	// checkNum("lm",lm); checkNum("rm",rm); 
	leftNumCannibals = lc; rightNumCannibals = rc;
	leftNumMissionaries = lm; rightNumMissionaries = rm;
	boat = b; g = gv; previousNode = pn;
    } // end Node 
    private void checkNum(String s, int x) {
	if ( x < 0 || 3 < x ) {
	    System.out.println("**** checkNum " + s + " " + x);
	    System.exit(0);
	}
    }
    public Node moveRight(int c, int m) {
	// System.out.println("?? moveRight c: " + c + " m " + m + " "); 
	// printNode();
	// if ( !boat) return null;
	// int sum = c+m;
	// if ( 0 == sum || 2 < sum ) return null;
	int lc2 = leftNumCannibals - c;
	int rc2 = rightNumCannibals + c;
	int lm2 = leftNumMissionaries - m;
	int rm2 = rightNumMissionaries + m;
	/*
	if ( lc2 < 0 || 3 < lc2 ||
	     rc2 < 0 || 3 < rc2 ||
	     lm2 < 0 || 3 < lm2 ||
	     rm2 < 0 || 3 < rm2 ) return null;
	if ( ( 0<lm2 && lm2<lc2 ) ||
	     ( 0<rm2 && rm2<rc2 ) )  return null;
	// System.out.println("moveRight c: " + c + " m " + m);
	*/ 
	return new Node(lc2, rc2, lm2, rm2, false, g+1, this);
    } // end moveRight
    public Node moveLeft(int c, int m) {
	// System.out.println("?? moveLeft c: " + c + " m " + m + " "); 
	// printNode();
	// if ( boat) return null;
	// int sum = c+m;
	// if ( 0 == sum || 2 < sum ) return null;
	int lc2 = leftNumCannibals + c;
	int rc2 = rightNumCannibals - c;
	int lm2 = leftNumMissionaries + m;
	int rm2 = rightNumMissionaries - m;
	/*
	if ( lc2 < 0 || 3 < lc2 ||
	     rc2 < 0 || 3 < rc2 ||
	     lm2 < 0 || 3 < lm2 ||
	     rm2 < 0 || 3 < rm2 ) return null;
	if ( ( 0<lm2 && lm2<lc2 ) ||
	     ( 0<rm2 && rm2<rc2 ) )  return null;
	*/
	// System.out.println("moveLeft c: " + c + " m " + m); 
	return new Node(lc2, rc2, lm2, rm2, true, g+1, this);
    } // end moveLeft
    public boolean isGoal() {
	if ( boat) return false;
	if ( 3 != rightNumCannibals ) return false;
	if ( 3 != rightNumMissionaries ) return false;
	return true;
    } // end isGoal
    public void printNode() {
	System.out.println(
	     "Node lc " + leftNumCannibals + " lm " + leftNumMissionaries +
	     " rc " + rightNumCannibals + " rm " + rightNumMissionaries +
	     " b " + boat + " g " + g);
    } // end printNode
    public void printSolution() {
	System.out.print("solution node:");
	printNode();
	if ( null != previousNode ) previousNode.printSolution();
    }

} // end Node
