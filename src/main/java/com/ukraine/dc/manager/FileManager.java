package com.ukraine.dc.manager;

import java.io.*;
import java.util.Objects;

import static java.lang.String.format;

/**
 * The type FileManager.
 */
public final class FileManager {

    private FileManager() {
    }

    public static int countDirs(String path) {
        int counter = 0;
        File file = new File(path);
        if (file.isDirectory()) {
            counter++;
            counter += countDirsRecursively(file.listFiles());
        }
        return counter;
    }

    public static int countFiles(String path) {
        int counter = 0;
        File file = new File(path);
        file.setReadable(true);
        if (file.isDirectory()) {
            counter += countFilesRecursively(file.listFiles());
        } else {
            counter++;
        }
        return counter;
    }

    public static void move(String from, String to) {
        File source = new File(from);
        File destination = new File(to);
        if (!destination.isDirectory()) {
            throw new RuntimeException("Invalid parameter 'to' - it's not a directory.");
        }
        copy(source.getAbsolutePath(), destination.getAbsolutePath());
        deleteFiles(source);
    }

    public static void copy(String from, String to) {
        File source = new File(from);
        File destination = new File(to);

        validatePath(source);
        validatePath(destination);

        if (source.isDirectory()) {
            destination = new File(destination, source.getName());
            if (!destination.exists()) {
                destination.mkdir();
            }
        }

        checkReadPermission(source);
        copyRecursively(source, destination);
    }

    private static void copyRecursively(File source, File destination) {
        String[] files = source.list();
        if (files != null && files.length != 0) {
            for (String nestedFile : files) {
                File file = new File(source.getAbsolutePath(), nestedFile);
                if (file.isDirectory()) {
                    File dest = new File(destination.getAbsolutePath(), file.getName());
                    if (!dest.exists()) {
                        dest.mkdir();
                    }
                    copyRecursively(file, dest);
                } else {
                    handleFile(file, destination);
                }
            }
        } else if (source.isFile()) {
            source.setReadable(true);
            handleFile(source, destination);
        }
    }

    private static void handleFile(File from, File to) {
        File destination = new File(to.getAbsolutePath(), from.getName());
        try {
            if (!destination.exists()) {
                destination.createNewFile();
            }
            destination.setWritable(true);
            copyFileContent(from, destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFileContent(File sourcePath, File destination) {
        try (InputStream inputStream = new FileInputStream(sourcePath);
             OutputStream outputStream = new FileOutputStream(destination)) {
            int count;
            byte[] buffer = new byte[512];
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validatePath(File file) {
        if (!file.exists()) {
            throw new RuntimeException(format("The path '%s' is not present. Please specify a valid one.", file.getAbsolutePath()));
        }
    }

    private static void checkReadPermission(File file) {
        if (!file.canRead()) {
            throw new RuntimeException(format("No permission to read file by this path: %s", file.getAbsolutePath()));
        }
    }

    private static int countFilesRecursively(File[] files) {
        int counter = 0;

        if (files != null) {
            for (File file : files) {
                file.setReadable(true);
                if (file.isDirectory()) {
                    counter += countFilesRecursively(file.listFiles());
                } else {
                    counter++;
                }
            }
        }

        return counter;
    }

    private static int countDirsRecursively(File[] files) {
        int counter = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    counter++;
                    counter += countDirsRecursively(file.listFiles());
                }
            }
        }
        return counter;
    }

    public static void deleteFiles(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File nestedFile : Objects.requireNonNull(files)) {
                if (nestedFile.isDirectory()) {
                    deleteFiles(nestedFile);
                }
                nestedFile.delete();
            }
            file.delete();
        }
    }

}
