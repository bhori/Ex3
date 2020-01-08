package utils;

import java.io.Serializable;
import java.util.ArrayList;

import dataStructure.Node;
import dataStructure.node_data;

public class MyMinHeap implements Serializable {

    private ArrayList<node_data> heap;

    public MyMinHeap() {
        heap = new ArrayList<node_data>();
    }

    public MyMinHeap(ArrayList<node_data> l) {
        heap = l;
        buildHeap();
    }

    public void buildHeap() {
        int i = heap.size() / 2;
        while (i >= 0) {
            minHeapify(i);
            i--;
        }
    }

    public node_data extractMin() {
        if (heap.size() <= 0) return null;
        node_data minValue = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.get(0).setInfo(""+0);
        heap.remove(heap.size() - 1);
        minHeapify(0);
        return minValue;
    }

    public String toString() {
        String s = "";
        for (node_data n : heap) {
            s += n + ",";
        }
        return s;
    }

    public void minHeapify(int i) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int smallest = i;

        if (left < heap.size()  && lessThan(left, smallest))
            smallest = left;

        if (right < heap.size()  && lessThan(right, smallest))
            smallest = right;
        if (smallest != i) {
            swap(smallest, i);
            minHeapify(smallest);
        }
    }
    public void minHeapifyUp(int i) {
    	int parent;
    	if(i==0)
    		return;
    	if((double)(i%2)!=0)
    		parent=(i-1)/2;
    	else parent=(i-2)/2;
    	if(lessThan(i, parent))
    		swap(parent,i);
    	 minHeapifyUp(parent);
    	
    	
    }
    private void swap(int i, int j) {
    	node_data t = heap.get(i);
        heap.set(i, heap.get(j));
        heap.get(i).setInfo(""+ i);
        heap.set(j, t);
        heap.get(j).setInfo(""+ j);
    }

    public boolean lessThan(int i, int j) {
        if(heap.get(i).getWeight()<heap.get(j).getWeight())
        	return true;
        return false;
    }
    public void changePriorety(int i, double weight) {
    	heap.get(i).setWeight(weight);
    	minHeapifyUp(i);
    }

   
}