package model;

import java.util.*;


/**
 * Class that handles the encoding and decoding of a string using a Huffman tree.
 */
public class HuffmanEncoder
{
    private Map<Character, String> encodeTable = new HashMap<>();
    private Map<String, Character> decodeTable = new HashMap<>();

    public HuffmanEncoder(HuffmanTree tree) { buildCodeTable(tree.root, ""); }

    public HuffmanEncoder(Map<String, Character> decodeTable) { this.decodeTable = decodeTable; }

    public Map<Character, String> getEncodeTable()
    {
        if (encodeTable.isEmpty()) return null;
        return encodeTable;
    }

    private void buildCodeTable(HuffmanTree.Node node, String code)
    {
        if (node.isLeaf())
        {
            encodeTable.put(node.ch, code);
            decodeTable.put(code, node.ch);
        }
        else
        {
            buildCodeTable(node.left, code + "0");
            buildCodeTable(node.right, code + "1");
        }
    }

    public String encode(String s)
    {
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray())
            sb.append(encodeTable.get(ch));

        return sb.toString();
    }


    public String[] decode(String s)
    {
        String res = "", aux = "";

        for (int i=0; i<s.length(); i++)
        {
            aux += s.charAt(i);
            if (decodeTable.get(aux) != null)
            {
                res += decodeTable.get(aux);
                aux = "";
                s = s.substring(i);
                i = 0;
            }
        }
        return new String[]{res, aux};
    }
}
