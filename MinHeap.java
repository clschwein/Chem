/**
 * This class implements an array-based heap structure to store elements in a
 * minimum to maximum order (minimum is at the top).
 * 
 * @author Nate Kibler (nkibler7)
 * @author Chris Schweinhart (schwein)
 * @param <E> - the type of element to store in this Heap, i.e. Reaction
 */
public class MinHeap<E extends Comparable<? super E>> { 

	private E[] Heap;   // Pointer to heap array 
	private int size;   // Maximum size of heap 
	private int n;      // # of things in heap 

	/**
	 * Creates a new MinHeap object from the given array.
	 * @param h - the array of elements to sort into a MinHeap
	 * @param num - number of elements in the array
	 */
	public MinHeap(E[] h, int num) { 
		Heap = h;
		n = num;
		size = h.length;
		buildheap(); 
	} 

	/**
	 * Returns the size of the heap.
	 * @return - the number of elements in the heap
	 */
	public int heapsize() { 
		return n; 
	} 

	/**
	 * Determines if the given position points to a place that is on the lowest level
	 * of the binary tree structure that is implemented.
	 * @param pos - the position to check
	 * @return true if a "leaf", false otherwise
	 */
	private boolean isLeaf(int pos) { 
		return (pos >= n/2) && (pos < n); 
	} 

	/**
	 * Returns the position of the child on the left of the provided index position.
	 * @param pos - the index position of the parent to find the left child for
	 * @return the index position of the left child
	 */
	private int leftchild(int pos) { 
		assert pos < n/2 : "Position has no left child"; 
		return 2*pos + 1; 
	} 

	/**
	 * Returns the position of the parent element, if one exists.
	 * @param pos - the index position of the element to find the parent of
	 * @return the index position of the parent
	 */
	private int parent(int pos) { 
		assert pos > 0 : "Position has no parent"; 
		return (pos-1)/2; 
	}

	/**
	 * Builds the heap.
	 */
	private void buildheap() { 
		for (int i=n/2-1; i>=0; i--)
			siftdown(i); 
	}

	/**
	 * If the element has a child and that child is less than the element itself in comparison,
	 * siftDown() swaps the element and its child until the element is less than its child element(s).
	 * @param pos - the position of the element to sift down
	 */
	private void siftdown(int pos) { 
		assert (pos >= 0) && (pos < n): "Illegal heap position"; 
		while (!isLeaf(pos)) { 
			int j = leftchild(pos); 
			if ((j<(n-1)) && 
					(Heap[j].compareTo(Heap[j+1]) 
							>= 0)) 
				j++; // index of child w/ lesser value 
			if (Heap[pos].compareTo(Heap[j])  
					< 0) 
				return; 
			swap(pos, j); 
			pos = j;  // Move down 
		} 
	}

	/**
	 * Swaps the elements at positions i and j.
	 * @param i - the index position of one element to be swapped
	 * @param j - the index position of the other element to be swapped
	 */
	private void swap(int i, int j) {
		E temp = Heap[i];
		Heap[i] = Heap[j];
		Heap[j] = temp;
	}

	/**
	 * Method to remove an element in the middle of the heap.
	 * Used to update the heap without re-making it from scratch.
	 * @param val the value to be removed
	 */
	public void remove(E val) {
		assert n > 0 : "Removing from empty heap";
		int idx = find(val);
		if (idx == -1)
			return;
		swap(idx, --n);
		buildheap();
	}
	
	/**
	 * Returns the index position of the given element in the array.
	 * @param val - the element to find
	 * @return index position of the element, -1 if not found
	 */
	private int find(E val) {
		for (int i = 0; i < Heap.length; i++) {
			if (val.equals(Heap[i]))
				return i;
		}
		return -1;
	}
	
	/**
	 * Removes the minimum element from the heap and sifts down if necessary.
	 * @return the element that was removed
	 */
	public E removeMin() { 
		assert n > 0 : "Removing from empty heap"; 
		swap(0, --n); 
		if (n != 0) siftdown(0); 
		return Heap[n]; 
	} 
	
	/**
	 * Inserts another element into the heap and shifts it to its proper position.
	 * @param val - the element to insert
	 */
	public void insert(E val) { 
		assert n < size : "Heap is full"; 
		int curr = n++; 
		Heap[curr] = val;
		// Siftup until curr parent's time < curr time 
		while ((curr != 0)  && 
				(Heap[curr]. 
						compareTo(Heap[parent(curr)]) 
						< 0)) { 
			swap(curr, parent(curr)); 
			curr = parent(curr); 
		} 
	}
}