//helper class Node, separate because I want Test class to access it
public class Node {
	private int key;
	private Node left;
	private Node right;
		
	//constructor for Node
	public Node(int k) {
		key = k;
		left = null;
		right = null;
	}
	
	//getters and setters
	
	public Node getLeft() {
		return left;
	}
	public Node getRight() {
		return right;
	}
	
	public void setLeft(Node l) {
		left = l;
	}
	
	public void setRight(Node r) {
		right = r;
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int newK) {
		key = newK;
	}
}
