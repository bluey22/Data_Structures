
public class Test {

	public static void main(String[] args) {
		//Test insertion and inorder traversal
		
		int[] keys = new int[]{15, 10, 20, 8, 12, 16, 25};
		int expected = 0;
		for(int j=0; j < keys.length; j++) {
			expected += keys[j];
		}
		
		//create an empty BST
		BinarySearchTree testBST = new BinarySearchTree();
		
		//fill empty tree
		for(int i = 0; i < keys.length; i++) {
			testBST.insert(keys[i]);
		}
		
		//test inorder and insertion
		//should return 8 10 12 15 16 20 25
		System.out.println("Inorder traversal:");
		testBST.createorder();
		testBST.printorder();
		
		System.out.println();
		
		//test Sum
		//should return 106
		System.out.println("Testing sum...");
		System.out.println("TEST VAL: " + testBST.sum(testBST.getBSTRoot()));
		System.out.println("EXPECTED VAL: "+expected);
		System.out.println();
		
		//testing search
		
		//test root
		Node searchTest  = testBST.search(testBST.getBSTRoot(), 15);
		System.out.println("search completed");
		if (searchTest != null) {
			System.out.println("FOUND NODE VAL: "+searchTest.getKey());
			System.out.println();
		}
		
		//test 25, right child of 20
		Node searchTest2  = testBST.search(testBST.getBSTRoot(), 25);
		System.out.println("search completed");
		if (searchTest2 != null) {
			System.out.println("FOUND NODE VAL: "+searchTest.getKey());
		}
		System.out.println();
		
		//test for something that is not in tree
		Node searchTest3  = testBST.search(testBST.getBSTRoot(), 13);
		System.out.println("search completed");
		if (searchTest3 != null) {
			System.out.println("FOUND NODE VAL: "+searchTest.getKey());
		}
		System.out.println();
		
		//testing delete	
		int deleteValue = 0;
		//let's delete 16
		deleteValue = testBST.delete(16);
		System.out.println("DELETE "+deleteValue);
		//print the tree after
		testBST.createorder();
		testBST.printorder();
		System.out.println();
		
		//deleting root, 15
		deleteValue = testBST.delete(15);
		System.out.println("DELETE "+deleteValue);
		//print the tree after
		testBST.createorder();
		testBST.printorder();
		System.out.println();
		
		//testing kthSmallest
		//3rd smallest
		Node kTest = null;
		kTest = testBST.kthSmallest(3);
		
		//4th smallest
		kTest = testBST.kthSmallest(4);
		
		//5th smallest
		kTest = testBST.kthSmallest(5);
		
		//test for out of bounds
		kTest = testBST.kthSmallest(0);
		kTest = testBST.kthSmallest(6);
	}

}
