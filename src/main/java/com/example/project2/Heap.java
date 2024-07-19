package com.example.project2;

public class Heap {
    HeapNode[] heapArray; // array to store heap nodes
    HeapNode[] newHeapArray; // temporary array for heap operations
    int[] frequencies; // array to store character frequencies
    int size; // current size of the heap
    int capacity; // maximum capacity of the heap
    int nonZeroCount; // number of non-zero frequency characters

    public Heap() {
    }

    public Heap(int[] frequencies) {
        this.frequencies = frequencies;
        this.capacity = frequencies.length;
        this.heapArray = new HeapNode[this.capacity];
        for (int i = 0; i < heapArray.length; i++) {
            heapArray[i] = new HeapNode();
        }
        int j = 0;
        for (int i = 0; i < capacity; i++) {
            if (frequencies[i] > 0) {
                heapArray[j].characterValue = i;
                heapArray[j].frequency = frequencies[i];
                j++;
            }
        }
        this.nonZeroCount = j;
        this.newHeapArray = new HeapNode[this.nonZeroCount];
        for (int i = 0; i < newHeapArray.length; i++) {
            newHeapArray[i] = new HeapNode();
            newHeapArray[i].characterValue = heapArray[i].characterValue;
            newHeapArray[i].frequency = heapArray[i].frequency;
        }
    }

    public void buildHeap(HeapNode[] newHeapArray, int i) {
        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (right < this.size && newHeapArray[right].frequency < newHeapArray[smallest].frequency) {
            smallest = right;
        }
        if (left < this.size && newHeapArray[left].frequency < newHeapArray[smallest].frequency) {
            smallest = left;
        }
        if (smallest != i) {
            HeapNode small = newHeapArray[i];
            newHeapArray[i] = newHeapArray[smallest];
            newHeapArray[smallest] = small;
            buildHeap(newHeapArray, smallest);
        }
    }

    public void buildingHeap(int[] frequencies) {
        int heap_size = this.nonZeroCount / 2;
        this.size = newHeapArray.length;
        for (int i = heap_size; i >= 0; i--)
            buildHeap(newHeapArray, i);
    }

    public void insert(HeapNode element) {
        int i = 0;
        if (isFull()) {
            System.out.println("full heap");
        } else {
            i = size++;
            newHeapArray[i] = element;
            while (i > 0 && newHeapArray[(int) Math.ceil(i / 2.0) - 1].frequency > newHeapArray[i].frequency) {
                HeapNode cur = newHeapArray[i];
                newHeapArray[i] = newHeapArray[(int) Math.ceil(i / 2.0) - 1];
                newHeapArray[(int) Math.ceil(i / 2.0) - 1] = cur;
                i = ((int) Math.ceil(i / 2.0) - 1);
            }
        }
    }

    public HeapNode deleteMin(int[] frequencies) {
        int i = 0, child;
        if (isEmpty()) {
            System.out.println("empty heap");
        }
        HeapNode min = new HeapNode();
        min.characterValue = newHeapArray[0].characterValue;
        min.frequency = newHeapArray[0].frequency;
        min.leftChild = newHeapArray[0].leftChild;
        min.rightChild = newHeapArray[0].rightChild;
        min.leftRow = newHeapArray[0].leftRow;
        min.rightRow = newHeapArray[0].rightRow;
        int s = (size - 1);
        HeapNode last = newHeapArray[s--];
        for (i = 0; i * 2 + 1 <= s; i = child) {
            child = i * 2 + 1;
            if (child < s && newHeapArray[child + 1].frequency < newHeapArray[child].frequency)
                child++;
            if (last.frequency > newHeapArray[child].frequency)
                newHeapArray[i] = newHeapArray[child];
            else
                break;
        }
        newHeapArray[i] = last;
        this.size = s + 1;
        return min;
    }

    public boolean isFull() {
        if (size < this.nonZeroCount)
            return false;
        else
            return true;
    }

    public boolean isEmpty() {
        if (size == 0)
            return true;
        else
            return false;
    }

    public void print() {
        for (int i = 0; i < this.size; ++i) {
            System.out.println("[" + this.newHeapArray[i].characterValue + "] = " + this.newHeapArray[i].frequency);
        }
    }
}



