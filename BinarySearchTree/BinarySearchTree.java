import java.util.ArrayList;
//so we can store the inorder search in an array list, then print that
public class BinarySearchTree { // working only with keys(int)
	private Node root;
	private int numItems;// keeps track of how many items are in the BST
	private ArrayList<Node> bstArrayList = new ArrayList<Node>();
	
	// Binary tree constructor - create empty binary tree
	public BinarySearchTree() {
		root = null;
		numItems = 0;
	}

	// getter to get BST root
	public Node getBSTRoot() {
		return root;
	}

	// getter to get the number of items in BST root
	public int getNumItems() {
		return numItems;
	}

	// insert - iterative implement
	public void insert(int key) {
		Node parent = null;// parent of trav
		Node trav = root;// node to traverse down BST

		while (trav != null) {
			parent = trav;
			if (key < trav.getKey()) {
				trav = trav.getLeft(); // if key is less, will be on left of tree
			} else {
				trav = trav.getRight();
			}
		}

		Node newChild = new Node(key);

		if (parent == null) { // trav == null to start, BST empty
			root = new Node(key);
		} else if (key < parent.getKey()) { // key smaller than parent - left child
			parent.setLeft(newChild);
		} else { // otherwise the inserted key should be a right child
			parent.setRight(newChild);
		}
		numItems += 1;
	}

	public void createorder() {//creates the inorder traversal
		bstArrayList.clear();//clears arraylist
		inorder(root);//appends nodes to arralist in inorder order
	}
	private void inorder(Node root) {// just puts it in inorder
		if (root.getLeft() != null) {// if left child not null, continue traversing left recursively
			inorder(root.getLeft());
		}
		bstArrayList.add(root);

		if (root.getRight() != null) {
			inorder(root.getRight());
		}
	}
	public void printorder() {//this is our print inorder method, which prints the inorder traversal
		for(int i=0; i<bstArrayList.size(); i++) {
			System.out.print(bstArrayList.get(i).getKey() + " "); 
		}
	}

	public int sum(Node root) { // returns int sum of BST keys
		// check for empty BST
		if (root == null) {
			return 0; // return 0 if empty
		}

		// recursively add right subtree
		// recursively add left subtree
		// add root
		// that is the sum

		return (root.getKey() + sum(root.getLeft()) + sum(root.getRight()));
	}

	public Node search(Node root, int k) { // takes a root of the BST and the key

		Node searchedNode = null; // node we want to return
		boolean done = false; // for while loop
		Node trav = root;// node for traversing, starting at root
		Node childL = null; // trav is our curr, this is left child of trav
		Node childR = null; // right child of trav
		Node parent = null;// node for comparison
							// ex. found node trav is the left/right child of parent
		// check if searched node is the root of the tree
		if (k == root.getKey()) {
			done = true;
			searchedNode = root;
			System.out.println("Node with key " + k + " is the root of the BST");
			return searchedNode;
		}

		while (done != true) {// while exists
			if (trav == null) {// checks for an empty BST
				done = true;
				continue;
			}

			parent = trav;
			childL = trav.getLeft();
			childR = trav.getRight();

			if (childL != null) {
				if (k == childL.getKey()) {// if keys match with left child, return node
					searchedNode = childL;
					done = true;
					continue;
				}
			}
			if (childR != null) {
				if (k == childR.getKey()) {
					searchedNode = childR; // if keys match with right child return node
					done = true;
					continue;
				}
			}

			if (k < trav.getKey()) {// if key is smaller than node key, go left
				trav = trav.getLeft();
			} else {// if key is bigger than node key, go right
				trav = trav.getRight();
			}
		}

		// now we either found our search or it doesn't exist, and we know whether it's
		// left or right
		// because we can check with parent, which we made lag two nodes behind

		// basically, parent is the parent of the searched node(if exists!)

		if (searchedNode == null) {
			System.out.println("Node with key " + k + " is not in the tree");
			return null; // means node we were searching for DNE
			// code below checks for whether or not the searched node is a left or right
			// child of the parent
		} else if (searchedNode.getKey() < parent.getKey()) {
			System.out.println("Node with key " + k + " is the left child of parent with key " + parent.getKey());
			return searchedNode;
		} else {
			System.out.println("Node with key " + k + " is the right child of parent with key " + parent.getKey());
			return searchedNode;
		}

	}

	public Node kthSmallest (int k) {
		//an inorder of a binary search tree
		//is in smallest to largest
		createorder();
		Node kthSmallest = null;
		if(k > bstArrayList.size() || k <= 0) {
			System.out.println(k+"th smallest term DNE"+"... try values above 0?");
			return kthSmallest;
		}
		
		kthSmallest = bstArrayList.get(k-1);
		System.out.println("The "+k+"th smallest term in the BST is "+kthSmallest.getKey());
		return kthSmallest;
	}

	public int delete(int key) {
		// Find node we want to delete and its parent
		Node parent = null;
		Node trav = root;

		while (trav != null && trav.getKey() != key) {
			parent = trav;
			if (key < trav.getKey()) {
				trav = trav.getLeft();
			}
			else {
				trav = trav.getRight();
			}
		}
		
		//delete node and return value
		//in this case returning the key
		if( trav == null) {//no key exists, return -1
			return -1;
		}
		else {
			int returnKey = trav.getKey();
			deleteNode(trav, parent);
			numItems -= 1;
			return returnKey;
		}
	}

	// helper method deleteNode
	private void deleteNode(Node toDelete, Node parent) {
		if (toDelete.getLeft() == null || toDelete.getRight() == null) {
			// deals with if our deleted node has no children or 1
			Node toDeleteChild = null;
			if (toDelete.getLeft() != null) {
				toDeleteChild = toDelete.getLeft();
			} else {
				toDeleteChild = toDelete.getRight();
			}

			if (toDelete == root) { // deals with if we are deleting the root
				root = toDeleteChild; // root will either be null or the one child - case 1 or 2
			} else if (toDelete.getKey() < parent.getKey()) {
				parent.setLeft(toDeleteChild);
			} else {
				parent.setRight(toDeleteChild);
			}
		} else { // case 3
					// inorder successor, smallest item in right subtree

			Node rParent = toDelete;// the parent of the replacement. need so we don't lose pointers
			Node replacer = toDelete.getRight();// replacer is eventually going to be the inorder succesor
			// go right then all the way left as possible
			while (replacer.getLeft() != null) {
				rParent = replacer;
				replacer = replacer.getLeft();
			}
			// replace key
			toDelete.setKey(replacer.getKey());

			// Recursively delete replacer's old node
			deleteNode(replacer, rParent);
		}
	}
}