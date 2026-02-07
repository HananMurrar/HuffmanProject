package com.example.project2;

public class HeapNode {
    int frequency; // frequency of occurrence of the character
    int characterValue; // character represented by this node
    int leftRow; // row index of the left child in the heap
    int rightRow; // row index of the right child in the heap
    String huffmanCode; // huffman code for the character
    HeapNode leftChild; // left child node in the binary tree
    HeapNode rightChild; // right child node in the binary tree

    public HeapNode() {
        this.frequency = 0;
        this.characterValue = 0;
        this.leftRow = 0;
        this.rightRow = 1;
        this.huffmanCode = "";
        this.leftChild = this.rightChild = null;
    }
}
