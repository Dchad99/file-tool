package com.ukraine.dc.analyzer;

import com.ukraine.dc.analyzer.FileAnalyzer;
import com.ukraine.dc.analyzer.FileInformation;
import org.junit.jupiter.api.*;

import java.util.List;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileAnalyzerTest {
    private static String searchWord = "hello";
    private static final String testContent = "       !   . ? Hello, world!\n" +
            "World hello.\n" +
            "He said hello.\n" +
            "How are you? Hello, fine.\n" +
            "\n" +
            "Привет, как дела? hello, i'm fine.\n" +
            "test.\n" +
            "?!\n" +
            "hello.     HELLOhello!\n" +
            "g.s.\n" +
            "worldHellohello_helloWorld   hello!";

    private final FileAnalyzer analyzer = new FileAnalyzer();
    private File file;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test", ".txt");
        fillFileWithContent(file);
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }

    @Test
    @DisplayName("Test readContent() method, then compare testContent with result of readContent() call.")
    void whenCallReadFileContent_thenShouldReturnTheSameContentAsTest() {
        String content = analyzer.readFileContent(file.getAbsolutePath());
        assertEquals(testContent, content);
    }

    @Test
    @DisplayName("Test readContent() when path is empty, should throw an exception with a correspondent message.")
    void testReadContentWhenFilePathIsEmpty() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyzer.readFileContent(""));
        assertEquals("The 'path' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test readContent() when path is null, should throw an exception with a correspondent message.")
    void testReadContentWhenFilePathIsNUll() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyzer.readFileContent(""));
        assertEquals("The 'path' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test readContent() when file is not present.")
    void testReadFileContent_whenFileIsNotFound() {
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> analyzer.readFileContent("test"));
        assertEquals("The file by this path: 'test' wasn't found.", exception.getMessage());
    }

    @Test
    @DisplayName("Test getSentencesWithWord() and check each sentences that it contains search word and ends with ['!' or '.' or '?'].")
    void testProcessFileContentBySearchingSentencesWithSearchWord() {
        List<String> sentences = analyzer.getSentencesWithWord(testContent, searchWord);
        assertEquals(7, sentences.size());
        for (String sentence : sentences) {
            assertTrue(sentence.endsWith(".") || sentence.endsWith("?") || sentence.endsWith("!"));
            searchWord = searchWord.toLowerCase();
            assertTrue(sentence.toLowerCase().contains(searchWord));
        }
    }

    @Test
    @DisplayName("Test getSentencesWithWord() when searchWord parameter is empty, should throw an exception with a correspondent message.")
    void testProcessFileContentWhenSearchWordIsEmpty() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyzer.getSentencesWithWord(testContent, ""));
        assertEquals("The 'word' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test getSentencesWithWord() when searchWord is null, should throw an exception with a correspondent message.")
    void testProcessFileContentWhenSearchWordIsNull() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyzer.getSentencesWithWord(testContent, null));
        assertEquals("The 'word' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test getSentencesWithWord when content is empty, should throw an exception with a correspondent message.")
    void testProcessFileContent_whenFileContentIsEmpty() {
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> analyzer.getSentencesWithWord("", searchWord));
        assertEquals("The file content is empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Test countWord() when word is empty, should throw an exception with a correspondent message.")
    void testCountWordsMethod_whenSearchWordIsEmpty() {
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> analyzer.countWord(testContent, ""));
        assertEquals("The 'word' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test countWord() when word is null, should throw an exception with a correspondent message.")
    void testCountWordsMethod_whenSearchWordIsNull() {
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> analyzer.countWord(testContent, null));
        assertEquals("The 'word' parameter shouldn't be empty or null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test wordCount() method, should return quantity occurrences of 'searchWord'.")
    void testCountWord() {
        int counter = analyzer.countWord(testContent, searchWord);
        assertEquals(12, counter);
    }

    @Test
    @DisplayName("Test analyze() method")
    void testMethodAnalyze() {
        FileInformation fileInformation = analyzer.analyze(file.getAbsolutePath(), searchWord);
        assertEquals(12, fileInformation.getWordCount());
        assertEquals(7, fileInformation.getSentences().size());
        searchWord = searchWord.toLowerCase();
        for (String sentence : fileInformation.getSentences()) {
            assertTrue(sentence.toLowerCase().contains(searchWord));
            assertTrue(sentence.endsWith(".") || sentence.endsWith("?") || sentence.endsWith("!"));
        }
    }

    private void fillFileWithContent(File file) {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytes = testContent.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error during writing to the file", e);
        }
    }

}