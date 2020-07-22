//Shiri Kullock, id 312530686, user name shirikullock
//Yaara Federman, id 205946312, user name yaaraf

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */

public class FibonacciHeap{
	private HeapNode min;
	private int size;
	private int numOfTrees;
	private int numOfMarked;
	private HeapNode leftMost;
	private static int totalLinks = 0;
	private static int totalCuts = 0;
	
	/**
	 * public FibonacciHeap()
	 * the constructor  
	 */
	public FibonacciHeap() {
		this.min = null;
		this.size = 0;
		this.numOfTrees = 0;
		this.numOfMarked = 0;
		this.leftMost = null;
	}

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean isEmpty() {
		return (this.size == 0);
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
	 */
	public HeapNode insert(int key) {    
		HeapNode curr = new HeapNode(key);
		if (this.isEmpty()) {
			this.min = curr;
			this.leftMost = curr;
		} else {
			curr.right = this.leftMost;
			curr.left = this.leftMost.left;
			this.leftMost.left = curr;
			this.leftMost = curr;
			curr.left.right = curr;
			if (curr.key < this.min.key) {
				this.min = curr;
			}
		}
		this.size ++;
		this.numOfTrees ++;
		return curr;
	}

	/**
	 * public void deleteMin()
	 * Deletes the node containing the minimum key.
	 *
	 */
	public void deleteMin() {
		if (this.size == 1) {
			this.min = null;
			this.leftMost = null;
			this.size --;
			this.numOfTrees --;
			return;
		}
		deleteForReal(min);
		double numerator = Math.log10(this.size);
		double gold = 0.5*(1+Math.sqrt(5));
		double denominator = Math.log10(gold);
		int arrSize = (int) (numerator/denominator)+1;
		HeapNode[] arr = new HeapNode[arrSize];
		HeapNode curr = this.leftMost;
		this.leftMost.left.right = this.leftMost.left;
		this.leftMost.left = this.leftMost;
		for (int i=0; i<this.numOfTrees; i++) {
			HeapNode next = curr.right;
			toBucket(curr, arr);
			curr = next;
		}
		fromBucket(arr);
		this.size -- ;
	}

	/**
	 * public HeapNode findMin()
	 * Returns the node of the heap whose key is minimal. 
	 *
	 */
	public HeapNode findMin() {
		return this.min;
	} 

	/**
	 * public void meld (FibonacciHeap heap2)
	 * Meld the heap with heap2
	 *
	 */
	public void meld (FibonacciHeap heap2) {
		if (heap2.isEmpty()) {
			return;
		}
		if (this.isEmpty()) {
			this.min = heap2.min;
			this.leftMost = heap2.leftMost;
			this.size = heap2.size;
			this.numOfTrees = heap2.numOfTrees;
			this.numOfMarked = heap2.numOfMarked;
			return;
		}
		this.leftMost.left.right = heap2.leftMost;
		heap2.leftMost.left.right = this.leftMost;
		HeapNode tmp = this.leftMost.left;
		this.leftMost.left = heap2.leftMost.left;
		heap2.leftMost.left= tmp;
		this.size += heap2.size;
		this.numOfTrees += heap2.numOfTrees;
		this.numOfMarked += heap2.numOfMarked;
		if (heap2.min.key < this.min.key) {
			this.min = heap2.min;
		}
	}

	/**
	 * public int size()
	 * Returns the number of elements in the heap
	 *   
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int[] countersRep()
	 * Returns a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
	 * 
	 */
	public int[] countersRep() {
		double numerator = Math.log10(this.size);
		double gold = 0.5*(1+Math.sqrt(5));
		double denominator = Math.log10(gold);
		int arrSize = (int) (numerator/denominator)+1;
		int[] arr = new int[arrSize];
		HeapNode curr = this.leftMost;
		do {
			arr[curr.rank] ++;
			curr = curr.right;
		} while (curr != this.leftMost);
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 * Deletes the node x from the heap. 
	 *
	 */
	public void delete(HeapNode x) {    
		if (x != min) {
			int delta = x.key - min.key + 1;
			decreaseKey(x, delta);
		}
		deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 * The function decreases the key of the node x by delta. The structure of the heap should be updated
	 * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {    
		x.key -= delta;
		if (x.key < min.key) {
			this.min = x;
		}
		if (x.parent != null) {
			if (x.key < x.parent.key) {
				cascadingCut(x);
			}
		}
	}

	/**
	 * public int potential() 
	 *
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked
	 * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
	 */
	public int potential() {    
		return (this.numOfTrees + 2*this.numOfMarked);
	}

	/**
	 * public static int totalLinks() 
	 *
	 * This static function returns the total number of link operations made during the run-time of the program.
	 * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
	 * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
	 * in its root.
	 */
	public static int totalLinks() {    
		return totalLinks;
	}

	/**
	 * public static int totalCuts() 
	 *
	 * This static function returns the total number of cut operations made during the run-time of the program.
	 * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
	 */
	public static int totalCuts() {    
		return totalCuts;
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k) 
	 *
	 * This static function returns the k minimal elements in a binomial tree H.
	 * The function should run in O(k(logk + deg(H)). 
	 */
	public static int[] kMin(FibonacciHeap H, int k) { 
		int[] arr = new int[k];
		if (k==0) {
			return arr;
		}
		arr[0] = H.min.getKey();
		if (k==1) {
			return arr;
		}
		FibonacciHeap heapOfSmallest = new FibonacciHeap();
		HeapNode minimal = findMinimalChild(H.min);
		heapOfSmallest.insert(minimal.getKey()).copyOfMe = minimal;
		for (int i=1;i<k;i++) {
			minimal = heapOfSmallest.findMin();
			arr[i] = minimal.getKey();
			HeapNode minimalChild = findMinimalChild(minimal.copyOfMe);
			HeapNode minimalBrother = findMinimalBrother(minimal.copyOfMe);
			if (minimalChild != null) {
				heapOfSmallest.insert(minimalChild.getKey()).copyOfMe = minimalChild;
			}
			if (minimalBrother != null) {
				heapOfSmallest.insert(minimalBrother.getKey()).copyOfMe = minimalBrother;
			}
			heapOfSmallest.deleteMin();
		}
		return arr; 
	}
	
	/**
	 * private static HeapNode findMinimalChild(HeapNode parent)
	 * 
	 * Returns node with the minimal key amongst parent's children
	 */
	private static HeapNode findMinimalChild(HeapNode parent) {
		if (parent.child == null) {
			return null;
		}
		HeapNode curr = parent.child;
		HeapNode minimal = curr;
		do {
			curr = curr.right;
			if (minimal.getKey() > curr.getKey()) {
				minimal = curr;
			}
		} while (curr != parent.child);
		return minimal;
	}
	
	/**
	 * private static HeapNode findMinimalBrother(HeapNode bro)
	 * Returns node with the minimal key amongst bro's siblings
	 */
	private static HeapNode findMinimalBrother(HeapNode bro) {
		if (bro.right == bro) {
			return null;
		}
		HeapNode curr = bro.right;
		HeapNode minimal = null;
		while (curr != bro) {
			if (minimal == null && curr.key > bro.key) {
				minimal = curr;
			}
			if (minimal != null && curr.key < minimal.key && curr.key > bro.key) {
				minimal = curr;
			}
			curr = curr.right;
		}
		return minimal;
	}

	/**
	 * private void cascadingCut(HeapNode curr)
	 * cuts under the inv that each node can loose only one child
	 */
	private void cascadingCut(HeapNode curr) {
		HeapNode next = curr.parent;
		cut(curr);
		curr = next;
		
		while ((curr.parent != null) && (curr.mark)) { //curr is a not root and is marked
			next = curr.parent;
			cut(curr);
			curr = next;
		}
		
		if (curr.parent != null) {
			curr.mark = true;
			this.numOfMarked ++;
		}
	}
	
	/**
	 * private void cut(HeapNode x)
	 * cuts node x
	 */
	private void cut(HeapNode x) {
		if (x.right == x) {
			x.parent.child = null;
		} else {
			if (x.parent.child == x) {
				x.parent.child = x.right;
			}
			x.right.left = x.left;
			x.left.right = x.right;
		}
		
		x.parent.rank --;
		x.parent = null;
			
		x.right = this.leftMost;
		x.left = this.leftMost.left;
		this.leftMost.left.right = x;
		this.leftMost.left = x;
		this.leftMost = x;
		this.numOfTrees ++;
		if (x.mark) {
			this.numOfMarked -= 1;
			x.mark = false;
		}
		totalCuts ++;
	}
	
	/**
	 * private void deleteForReal(HeapNode curr)
	 * deletes the min - all the connections to and from it
	 */
	private void deleteForReal(HeapNode curr) {
		if (curr.rank == 0) { //if curr has no childs
			curr.left.right = curr.right;
			curr.right.left = curr.left;
			if (curr == this.leftMost) {
				leftMost = curr.right;
			}
		} else { //if curr has childs
			HeapNode node = curr.child;
			do { //makes curr childs' parent null
				node.parent = null;
				node = node.right;
			}
			while (node != curr.child);
			curr.left.right = curr.child; //inserts curr's child as roots
			curr.child.left.right = curr.right;
			curr.right.left = curr.child.left;
			curr.child.left = curr.left;
			if (curr == this.leftMost) {
				this.leftMost = curr.child;
			}
		}
		this.numOfTrees += curr.rank - 1;
		this.min = null; //deletes curr forever
		curr.child = null; 
		curr.right = null;
		curr.left = null;
	}
	
	/**
	 * private void toBucket(HeapNode curr, HeapNode[] arr)
	 * performs successive link on curr
	 */
	private void toBucket(HeapNode curr, HeapNode[] arr) {
		curr.right.left = curr.right;
		curr.right = curr;
		HeapNode root = null;
		while (arr[curr.rank] != null) {
			int tmpRank = curr.rank;
			root = link(curr, arr[curr.rank]);
			arr[tmpRank] = null;
			curr = root;
		}
		arr[curr.rank] = curr;
	}
	
	/**
	 * private void fromBucket(HeapNode[] arr) 
	 * gathers all linked trees to a heap
	 */
	private void fromBucket(HeapNode[] arr) {
		HeapNode node = null;
		this.numOfTrees = 0;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] != null) {
				if (node == null) {
					node = arr[i];
					this.leftMost = node;
					this.min = node;
				} else {
					node.right = arr[i];
					arr[i].left = node;
					node = node.right;
					if (node.key < this.min.key) {
						this.min = node;
					}
				}
				this.numOfTrees++;				
			}
		}
		this.leftMost.left = node;
		node.right = this.leftMost;
	}
	
	/**
	 * private static HeapNode link(HeapNode tree1, HeapNode tree2)
	 * connects two trees of the same rank
	 */
	private static HeapNode link(HeapNode tree1, HeapNode tree2) {
		HeapNode smaller = tree1;
		HeapNode bigger = tree2;
		if (tree1.key > tree2.key) {
			smaller = tree2;
			bigger = tree1;
		}
		if (smaller.rank != 0) {
			smaller.child.left.right = bigger;
			bigger.left = smaller.child.left;
			bigger.right = smaller.child;
			smaller.child.left = bigger;			
		}
		smaller.child = bigger;
		bigger.parent = smaller;
		smaller.rank++;
		totalLinks++;
		return smaller;
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap
	 * (for example HeapNode), do it in this file, not in 
	 * another file 
	 *  
	 */
	public class HeapNode {

		public int key;
		private int rank;
		private boolean mark;
		private HeapNode child;
		private HeapNode right;
		private HeapNode left;
		private HeapNode parent;
		private HeapNode copyOfMe;

		public HeapNode(int key) {
			this.key = key;
			this.rank = 0;
			this.mark = false;
			this.child = null;
			this.right = this;
			this.left = this;
			this.parent = null;
			this.copyOfMe = null;
		}

		public int getKey() {
			return this.key;
		}
	}
	
}