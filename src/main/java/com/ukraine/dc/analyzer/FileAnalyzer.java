package com.ukraine.dc.analyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class FileAnalyzer extends Analyzer {
    private static final Pattern REGEX_FOR_VALID_SENTENCES =
            Pattern.compile("(?:([A-Za-z\\u0401\\u0451\\u0410-\\u044f0-9\\s\\\\,]|))+(?:(\\.|\\?|!))");

    public static void main(String[] args) {
        FileAnalyzer analyzer = new FileAnalyzer();
        analyzer.analyze(args[0], args[1]);
    }

    @Override
    public FileInformation analyze(String pathToFile, String searchWord) {
        validateFilePath(pathToFile);
        validateSearchWord(searchWord);
        String fileContent = readFileContent(pathToFile);
        List<String> validSentencesWithWord = getSentencesWithWord(fileContent, searchWord);
        int wordCount = countWord(fileContent, searchWord);
        return new FileInformation(wordCount, validSentencesWithWord);
    }

    public List<String> getSentencesWithWord(String fileContent, String word) {
        validateSearchWord(word);
        return processFileContent(fileContent).stream()
                .filter(s -> s.toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    public int countWord(String fileContent, String word) {
        validateSearchWord(word);
        return countWordOccurrences(fileContent, word);
    }

    public String readFileContent(String pathToFile) {
        validateFilePath(pathToFile);
        try {
            return readFileContent(new FileInputStream(pathToFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(format("The file by this path: '%s' wasn't found.", pathToFile));
        }
    }

    private String readFileContent(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();

        try (InputStream stream = new BufferedInputStream(inputStream)) {
            int count;
            byte[] buffer = new byte[64];
            while ((count = stream.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, count));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return builder.toString();
    }

    private List<String> processFileContent(String fileContent) {
        if (!fileContent.isEmpty()) {
            Matcher matcher = REGEX_FOR_VALID_SENTENCES.matcher(fileContent);
            List<String> sentences = new ArrayList<>();
            while (matcher.find()) {
                sentences.add(matcher.group());
            }
            return sentences;
        }
        throw new RuntimeException("The file content is empty.");
    }

    private int countWordOccurrences(String fileContent, String word) {
        int counter = 0;
        word = word.toLowerCase();
        for (String sentence : fileContent.split("\r\n")) {
            sentence = sentence.toLowerCase();
            while (sentence.contains(word)) {
                sentence = sentence.substring(sentence.indexOf(word) + word.length());
                counter++;
            }
        }
        return counter;
    }

}
