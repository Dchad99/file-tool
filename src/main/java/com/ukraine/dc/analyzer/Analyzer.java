package com.ukraine.dc.analyzer;

public abstract class Analyzer {

    protected abstract FileInformation analyze(String pathToFile, String searchWord);

    protected void validateSearchWord(String searchWord) {
        if (searchWord == null || searchWord.trim().isEmpty()) {
            throw new IllegalArgumentException("The 'word' parameter shouldn't be empty or null.");
        }
    }

    protected void validateFilePath(String pathToFile) {
        if (pathToFile == null || pathToFile.trim().isEmpty()) {
            throw new IllegalArgumentException("The 'path' parameter shouldn't be empty or null.");
        }
    }

}
