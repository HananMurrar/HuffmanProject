package com.example.project2;

import java.io.DataInputStream;
import java.io.InputStream;

public class Header {
    int numOfAdditionalZeros; // represents the number of additional zeros
    int headerSize; // represents the size of the header
    DataInputStream inputStream; // used for reading input
    HeapNode headerTree; // the root node of the header tree
    HeapNode root; // the root node of the huffman tree
    String fileExtension = ""; // stores the file extension
    String numberRepresentation = ""; // represents the binary representation of the header
    String characterRepresentation = ""; // represents the character representation of the header
    String content = ""; // stores content related to header operations
    String binaryString = ""; // represents the binary string
    String charactersString = ""; // // represents the characters string

    public Header() {
    }

    public Header(HeapNode root) {
        this.root = root;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String headerTree(HeapNode root) {
        if (root != null) {
            if (root.leftChild == null && root.rightChild == null) {
                numberRepresentation = numberRepresentation + "1";
                characterRepresentation = characterRepresentation + (char) root.characterValue;
                content = content + "1" + (char) root.characterValue;
                return content;
            }
            numberRepresentation += "0";
            content += "0";
            headerTree(root.leftChild);
            headerTree(root.rightChild);
        }
        return content;
    }

    public void readHeader() throws Exception {
        int x = inputStream.read();
        byte[] buffer = new byte[x];
        inputStream.read(buffer);
        for (int i = 0; i < x; i++)
            fileExtension += (char) buffer[i];
        int numInBytes = inputStream.read();
        int numInBits = inputStream.readInt();
        buffer = new byte[numInBytes];
        inputStream.read(buffer);
        String binaryCode = "";
        for (int i = 0; i < buffer.length; i++) {
            int c = buffer[i];
            if (c < 0)
                c = 256 + c;
            binaryCode = Integer.toBinaryString(c);
            while (binaryCode.length() < 8)
                binaryCode = "0" + binaryCode;
            binaryString += binaryCode;
            // to delete the added zeroes
            if (binaryString.length() > numInBits) {
                binaryString = binaryString.substring(0, numInBits);
            }
        }
        int numChar = inputStream.read();
        this.headerSize = numInBytes + numChar;
        buffer = new byte[numChar + 1];
        inputStream.read(buffer);
        for (int i = 0; i < buffer.length; i++) {
            charactersString += (char) buffer[i];
        }
        this.numOfAdditionalZeros = inputStream.read();
    }

    int j = 0, index = 0;

    public void buildHeaderTree(String binary, String characters, HeapNode headTree) {
        if (j == binary.length())
            return;
        // initialize the tree
        if (this.headerTree == null) {
            this.headerTree = new HeapNode();
            j++;
            buildHeaderTree(binary, characters, this.headerTree);
        } else {
            // if the character is 1 that means it is leafs
            if (binary.charAt(j) == '1') {
                HeapNode temp = new HeapNode();
                j++;
                // if the left null store the node on the left and store the value of byte there
                if (headTree.leftChild == null) {
                    temp.characterValue = characters.charAt(index++);
                    temp.frequency = 1;
                    headTree.leftChild = temp;
                    // if the left is not null store the node in the right and store the value of the byte there
                } else {
                    temp.characterValue = characters.charAt(index++);
                    temp.frequency = 1;
                    headTree.rightChild = temp;
                }
                // if the right null call the method
                if (headTree.rightChild == null)
                    buildHeaderTree(binary, characters, headTree);
                // if the character is zero that means the node is not leaf
            } else if (binary.charAt(j) == '0') {
                HeapNode temp = new HeapNode();
                j++;
                // if the left is null put the character node in the left of the current node
                if (headTree.leftChild == null) {
                    headTree.leftChild = temp;
                    if (headTree.leftChild != null)
                        buildHeaderTree(binary, characters, headTree.leftChild);
                    if (headTree.rightChild == null)
                        buildHeaderTree(binary, characters, headTree);
                    // if the left is not null put the character node in the right and call the method at the right node of the current node
                } else {
                    headTree.rightChild = temp;
                    buildHeaderTree(binary, characters, headTree.rightChild);
                }
            }
        }
    }

    public String traverse(HeapNode root) {
        if (root != null) {
            if (root.leftChild == null && root.rightChild == null) {
                content = content + "1" + (char) root.characterValue;
                return content;
            }
            content += "0";
            headerTree(root.leftChild);
            headerTree(root.rightChild);
        }
        return content;
    }
}