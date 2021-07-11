package com.heima.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
        root.var = ' ';
    }

    /**
     * 插入trie树
     *
     * @param word
     */
    public void insert(String word) {
        TrieNode ws = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!ws.children.keySet().contains(c)) {
                ws.children.put(c, new TrieNode(c));
            }
            ws = ws.children.get(c);
        }
        ws.isWord = true;
    }

    /**
     * 查询trie树
     *
     * @param prefix
     * @return
     */
    // public List<String> startWith(String prefix) {
    //     List<String> match = new ArrayList<>();
    //     TrieNode ws = root;
    //     for (int i = 0; i < prefix.length(); i++) {
    //         char c = prefix.charAt(i);
    //         if (!ws.children.keySet().contains(c)) return match;
    //         ws = ws.children.get(c);
    //         // 遍历子集合
    //         for (char cc : ws.children.keySet()) {
    //             match.add("" + c + cc);
    //         }
    //
    //     }
    //     return match;
    // }

    /**
     * 查询以关键字开头的数据
     *
     * @param prefix
     * @return
     */
    public List<String> startWith(String prefix) {
        List<String> match = new ArrayList<>();
        TrieNode ws = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!ws.children.keySet().contains(c)) {
                return match;
            }
            ws = ws.children.get(c);
        }
        List<String> data = getData("", ws);
        for (String s : data) {
            match.add(prefix + s);
        }
        return match;
    }

    public List<String> getData(String prefix, TrieNode trieNode) {
        List<String> list = new ArrayList<>();
        if (trieNode.children == null || trieNode.children.size() == 0) {
            list.add(prefix + trieNode.var);
            return list;
        }
        if (trieNode.isWord) {
            list.add(prefix + trieNode.var);
        }
        for (Map.Entry<Character, TrieNode> nodeEntry : trieNode.children.entrySet()) {
            TrieNode node = nodeEntry.getValue();
            List<String> data = getData(nodeEntry.getKey() + "", node);
            for (String s : data) {
                list.add(prefix + s);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        t.insert("黑马程序员");
        t.insert("黑马头条");
        t.insert("黑马旅游");
        t.insert("黑马项目");
        List<String> ret = t.startWith("黑马程");
        System.out.println(ret);
    }
}
