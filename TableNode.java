package com.example.project2;

public class TableNode {
    int character; // character represented by this node
    int frequency; // frequency of occurrence of the character
    int bitLength; // length of the Huffman code in bits
    String huffmanCode; // huffman code for the character

    public TableNode() {
        this.character = 0;
        this.frequency = 0;
        this.bitLength = 0;
        this.huffmanCode = "";
    }

    public int getCharacter() {
        return character;
    }

    public void setCharacter(int character) {
        this.character = character;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getBitLength() {
        return bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public String getHuffmanCode() {
        return huffmanCode;
    }

    public void setHuffmanCode(String huffmanCode) {
        this.huffmanCode = huffmanCode;
    }
}
