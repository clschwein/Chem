/**
 * This class implements an array-based heap structure to store elements in a
 * minimum to maximum order (minimum is at the top).  Maintains order with all
 * methods, and implements removal of a non-root element.
 * 
 * @author Nate Kibler (nkibler7)
 * @author Chris Schweinhart (schwein)
 * @param <E> - the type of element to store in this Heap, i.e. Reaction
 */
public class MinHeap<E extends Comparable<? super E>> { 

	/**
	 * This array is the heap data structure, which keeps track of all
	 * the elements store in the heap.  The root is at index 0, with
	 * children being at 1 and 2.  In general for a node at index i,
	 * left child is at 2*i + 1 and right child is at 2*i + 2.
	 */
	private E[] heap;

	/**
	 * This integer represents the capacity of the heap.  In java, this
	 * isn't needed because it is stored in the Heap array, but having this
	 * variable makes the code easier to read and write.
	 */
	private int size;

	/**
	 * This integer represents the number of elements currently stored in
	 * the heap.  Will change dynamically based on removals or insertions.
	 */
	private int n;

	/**
	 * Creates a new MinHeap object from the given array.
	 * 
	 * @param h
	 * 			the array of elements to sort into a MinHeap
	 * @param num
	 * 			number of elements in the array
	 */
	public MinHeap(E[] h, int num) { 
		heap = h;
		n = num;
		size = h.length;
		buildheap(); 
	} 

	/**
	 * Returns the size of the heap.
	 * 
	 * @return
	 * 			the number of elements in the heap
	 */
	public int heapsize() { 
		return n; 
	} 

	/**
	 * Determines if the given position points to a node that is a leaf of
	 * the binary tree structure that is implemented.
	 * 
	 * @param pos
	 * 			the position to check
	 * @return
	 * 			true if a "leaf", false otherwise
	 */
	private boolean isLeaf(int pos) { 
		return (pos >= n/2) && (pos < n); 
	} 

	/**
	 * Returns the position of the child on the left of the provided index position.
	 * @param pos
	 * 			the index position of the parent to find the left child for
	 * @return
	 * 			the index position of the left child
	 */
	private int leftchild(int pos) { 
		assert pos < n/2 : "Position has no left child"; 
		return 2*pos + 1; 
	} 

	/**
	 * Returns the position of the child on the right of the provided index position.
	 * @param pos
	 * 			the index position of the parent to find the right child for
	 * @return
	 * 			the index position of the right child
	 */
	public int rightchild(int pos) {
		assert pos < (n-1)/2 : "Position has no right child"; 
		return 2*pos + 2; 
	} 

	/**
	 * Returns the position of the parent element, if one exists.
	 * 
	 * @param pos
	 * 			the index position of the element to find the parent of
	 * @return
	 * 			the index position of the parent
	 */
	private int parent(int pos) { 
		assert pos > 0 : "Position has no parent"; 
		return (pos-1)/2; 
	}

	/**
	 * Builds the heap, sorting it into its order.
	 */
	private void buildheap() { 
		for (int i=n/2-1; i>=0; i--)
			siftdown(i); 
	}

	/**
	 * If the element has a child and that child is less than the element itself
	 * in comparison, siftDown() swaps the element and its child until the element
	 * is less than its child element(s).
	 * 
	 * @param pos
	 * 			the position of the element to sift down
	 */
	private void siftdown(int pos) { 
		assert (pos >= 0) && (pos < n): "Illegal heap position"; 
		while (!isLeaf(pos)) { 
			int j = leftchild(pos); 
			if ((j<(n-1)) && 
					(heap[j].compareTo(heap[j+1]) 
							>= 0)) 
				j++; // index of child w/ lesser value 
			if (heap[pos].compareTo(heap[j])  
					< 0) 
				return; 
			swap(pos, j); 
			pos = j;  // Move down 
		} 
	}

	/**
	 * Swaps the elements at positions i and j.
	 * 
	 * @param i
	 * 			the index position of one element to be swapped
	 * @param j
	 * 			the index position of the other element to be swapped
	 */
	private void swap(int i, int j) {
		E temp = heap[i];
		heap[i] = heap[j];
		heap[j] = temp;
	}

	/**
	 * Method to remove an element in the middle of the heap.
	 * Used to update the heap without re-making it from scratch.
	 * 
	 * @param val
	 * 			the value to be removed
	 */
	public void remove(E val) {
		assert n > 0 : "Removing from empty heap";
		int idx = find(val);
		if (idx == -1) {
			return;
		}
		if (idx == (n - 1)) {
			n--;
			return;
		}
		swap(idx, --n);
		int parentIdx = parent(idx);
		if (heap[idx].compareTo(heap[parentIdx]) < 0) {
			int curr = idx;
			while ((curr != 0)  && 
					(heap[curr]. 
							compareTo(heap[parent(curr)]) 
							< 0)) { 
				swap(curr, parent(curr)); 
				curr = parent(curr); 
			}
		}
		else {
			if (!isLeaf(idx))
				siftdown(idx);
		}
	}

	/**
	 * Returns the index position of the given element in the array.
	 * Must be linear search since this isn't a binary search tree.
	 * 
	 * @param val
	 * 			the element to find
	 * @return
	 * 			index position of the element, -1 if not found
	 */
	private int find(E val) {
		for (int i = 0; i < heap.length; i++) {
			if (val.equals(heap[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the minimum element of the heap.
	 * 
	 * @return
	 * 			the element at the top of the heap
	 */
	public E getMin() { 
		return heap[0]; 
	} 

	/**
	 * Inserts another element into the heap and shifts it to its proper position.
	 * 
	 * @param val
	 * 			the element to insert
	 */
	public void insert(E val) { 
		assert n < size : "Heap is full"; 
		int curr = n; 
		heap[curr] = val;
		// Siftup until curr parent's time < curr time 
		while ((curr != 0)  && 
				(heap[curr]. 
						compareTo(heap[parent(curr)]) 
						< 0)) { 
			swap(curr, parent(curr)); 
			curr = parent(curr); 
		} 
		n++;
	}
}