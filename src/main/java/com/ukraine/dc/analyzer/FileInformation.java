package com.ukraine.dc.analyzer;

import java.util.List;
import java.util.StringJoiner;

public class FileInformation {
    private int wordCount;
    private List<String> sentences;

    public FileInformation(int wordCount, List<String> sentences) {
        this.wordCount = wordCount;
        this.sentences = sentences;
    }

    public String printMessages() {
        StringJoiner joiner = new StringJoiner("\n", "[", "]");
        for (String sentence : sentences) {
            joiner.add(sentence);
        }
        return joiner.toString();
    }

    public int getWordCount() {
        return wordCount;
    }

    public List<String> getSentences() {
        return sentences;
    }
}
