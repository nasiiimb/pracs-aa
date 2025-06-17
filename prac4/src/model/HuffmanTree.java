package model;

import java.util.*;


/**
 * Huffman tree data structure.
 */
public class HuffmanTree
{
    public Node root;

    public static class Node implements Comparable<Node>
    {
        char ch;
        int freq;
        Node parent, left, right;

        Node(char ch, int freq, Node parent, Node left, Node right)
        {
            this.ch = ch;
            this.freq = freq;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() { return left == null && right == null; }

        @Override
        public int compareTo(Node that) { return this.freq - that.freq; }
    }
    
    
    public HuffmanTree(Map<Character, Integer> freq)
    {
        root = null;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char ch : freq.keySet())
        {
            pq.add(new Node(ch, freq.get(ch), null,null, null));
        }
        while (pq.size() > 1)
        {
            Node left = pq.remove();
            Node right = pq.remove();
            Node parent = new Node('\0', left.freq + right.freq, null, left, right);
            pq.add(parent);
        }
        root = pq.remove();
    }
}
