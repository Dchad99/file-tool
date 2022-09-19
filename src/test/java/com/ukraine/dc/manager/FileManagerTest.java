package com.ukraine.dc.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {
    private static final String PATH_IS_NOT_VALID = "The path '%s' is not present. Please specify a valid one.";

    @Test
    void testCountFiles() {
        int count = FileManager.countFiles("src/test/resources/dir1");
        assertEquals(4, count);
    }

    @Test
    void testCountFilesOnEmptyDir() {
        assertEquals(0, FileManager.countFiles("src/test/resources/emptyDir"));
    }

    @Test
    void testCountDirs() {
        int count = FileManager.countDirs("src/test/resources/dir1");
        assertEquals(3, count);
    }

    @Test
    void testCountDirsOnEmptyDir() {
        assertEquals(1, FileManager.countDirs("src/test/resources/emptyDir"));
    }

    @Test
    void testCopyFile() {
        String from = "src/test/resources/text1.txt";
        String to = "src/test/resources/emptyDir";
        FileManager.copy(from, to);

        File source = new File(from);
        File destination = new File(to, source.getName());

        String content = readContentByPath(destination.getAbsolutePath());
        assertEquals("text1.txt", content);
        destination.delete();
    }

    @Test
    void testCopyEmptyDirectoryToAnotherDirectory() {
        String from = "src/test/resources/emptyDir";
        String to = "src/test/resources/dir1";
        FileManager.copy(from, to);

        File source = new File(from);
        File destination = new File(to, source.getName());

        assertTrue(destination.exists());
        assertTrue(destination.isDirectory());
        destination.delete();
    }

    @Test
    void testCopy_whenSourceIsNotValid() {
        String from = "src/test/1";
        String to = "src/test/resources/dir1";

        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileManager.copy(from, to));
        assertEquals(format(PATH_IS_NOT_VALID, new File(from).getAbsolutePath()), exception.getMessage());
    }

    @Test
    void testCopyFolderWithFilesToAnotherFolder() {
        String from = "src/test/resources/dir1/dir2";
        String to = "src/test/resources/emptyDir";

        FileManager.copy(from, to);
        File source = new File(from);
        File destination = new File(to, source.getName());

        assertTrue(destination.isDirectory());
        assertNotNull(destination.listFiles());

        String dir2Files = "[dir3, test.txt]";
        assertEquals(dir2Files, getFolderList(destination));

        File dir2File = new File(destination.getAbsolutePath(), "test.txt");
        String dir2FileContent = readContentByPath(dir2File.getAbsolutePath());
        assertEquals("hello, world!", dir2FileContent);


        destination = new File(destination.getAbsolutePath(), "dir3");
        String dir3Files = "[test3.txt]";
        assertEquals(dir3Files, getFolderList(destination));

        File dir3File = new File(destination.getAbsolutePath(), "test3.txt");
        String dir3FileContent = readContentByPath(dir3File.getAbsolutePath());
        assertEquals("Hello, test3", dir3FileContent);

        destination = new File(to, source.getName());
        clearDirectory(destination);
    }

    @Test
    void testMoveEmptyFolderWithFileToAnotherDirectory() {
        String from = "src/test/resources/t1";
        String to = "src/test/resources/moveDir";

        File source = new File(from);
        File dest = new File(to);

        new File(from).mkdir();
        new File(to).mkdir();

        File sourceFile = new File(from, "test.txt");
        fillFileWithContent(sourceFile.getAbsolutePath());
        FileManager.move(from, to);

        String dirFiles = "[test.txt]";
        File destination = new File(to, source.getName());
        assertEquals(dirFiles, getFolderList(destination));

        File fileDest = new File(destination.getAbsolutePath(), sourceFile.getName());
        assertEquals("hello", readContentByPath(fileDest.getAbsolutePath()));

        assertEquals("[]", getFolderList(sourceFile));
        removeTempFiles(source);
        removeTempFiles(dest);
    }

    @Test
    void testMoveWhenParameterToIsNotDirectory() {
        String from = "src/test/resources/emptyDir";
        String to = "src/test/resources/text1.txt";
        new File(from).mkdir();

        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileManager.move(from, to));
        assertEquals("Invalid parameter 'to' - it's not a directory.", exception.getMessage());
    }

    private String readContentByPath(String path) {
        StringBuilder builder = new StringBuilder();
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(path))) {
            int count;
            byte[] buffer = new byte[64];
            while ((count = inputStream.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, count));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private void fillFileWithContent(String path) {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFolderList(File file) {
        return (file.list() == null || file.list().length == 0) ? "[]" :
                Arrays.asList(Objects.requireNonNull(file.list())).toString();
    }

    private void clearDirectory(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File nestedFile : files) {
                    if (nestedFile.isDirectory()) {
                        clearDirectory(nestedFile);
                    }
                    nestedFile.delete();
                }
                file.delete();
            }
        }
    }

    private void removeTempFiles(File file) {
        clearDirectory(file);
        if (file.exists()) {
            file.delete();
        }
    }
}