public class MinHeap<E extends Comparable<? super E>> { 

	private E[] Heap;   // Pointer to heap array 
	private int size;             // Maximum size of heap 
	private int n;                // # of things in heap 

	public MinHeap(E[] h, int num, int max) { 
		Heap = h;
		n = num;
		size = max;
		buildheap(); 
	} 

	public int heapsize() { 
		return n; 
	} 

	public boolean isLeaf(int pos) { 
		return (pos >= n/2) && (pos < n); 
	} 

	public int leftchild(int pos) { 
		assert pos < n/2 : "Position has no left child"; 
		return 2*pos + 1; 
	} 

	public int rightchild(int pos) {
		assert pos < (n-1)/2 : "Position has no right child"; 
		return 2*pos + 2; 
	} 

	public int parent(int pos) { 
		assert pos > 0 : "Position has no parent"; 
		return (pos-1)/2; 
	}

	public void buildheap() { 
		for (int i=n/2-1; i>=0; i--)
			siftdown(i); 
	}

	private void siftdown(int pos) { 
		assert (pos >= 0) && (pos < n): "Illegal heap position"; 
		while (!isLeaf(pos)) { 
			int j = leftchild(pos); 
			if ((j<(n-1)) && 
					(Heap[j].compareTo(Heap[j+1]) 
							>= 0)) 
				j++; // index of child w/ greater value 
			if (Heap[pos].compareTo(Heap[j])  
					< 0) 
				return; 
			swap(pos, j); 
			pos = j;  // Move down 
		} 
	}

	private void swap(int i, int j) {
		E temp = Heap[i];
		Heap[i] = Heap[j];
		Heap[j] = temp;
	}

	public E removeMin() { 
		assert n > 0 : "Removing from empty heap"; 
		swap(0, --n); 
		if (n != 0) siftdown(0); 
		return Heap[n]; 
	} 
	public void insert(E val) { 
		assert n < size : "Heap is full"; 
		int curr = n++; 
		Heap[curr] = val; 
		// Siftup until curr parent's key > curr key 
		while ((curr != 0)  && 
				(Heap[curr]. 
						compareTo(Heap[parent(curr)]) 
						< 0)) { 
			swap(curr, parent(curr)); 
			curr = parent(curr); 
		} 
	}
}