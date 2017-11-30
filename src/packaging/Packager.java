package packaging;

import main.Main;
import networking.ChatTestWindow;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

/**
 * Packages up a game for delivery as an executable JAR.
 *
 * TODO - Currently packages everything; need to make Player extend Application to be able to have game itself launch.
 * TODO - extract raw strings 
 *
 * @author Ben Schwennesen
 */
public class Packager {

    private final int MAX_ENTRY_LENGTH = 1024;
    private final String WINDOWS_PATH_DELIMITER_PATTERN = Pattern.quote("\\");
    private final String MANIFEST_VERSION = "1.0";
    private final String GAMES_ROOT = "data/games/";
    private final String JAR_EXTENSION = ".jar";
    private final File SOURCE_DIRECTORY = new File("src");
    private final Class LAUNCH_CLASS = Main.class;

    private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    /**
     * Generate an executable JAR file for an authored game.
     *
     * Based on https://stackoverflow.com/questions/1281229/how-to-use-jaroutputstream-to-create-a-jar-file.
     *
     * @param gameName - the chosen name of the game
     */
    public void generateJar(String gameName) {
        compile(new File("src"));
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, MANIFEST_VERSION);
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, LAUNCH_CLASS.getName());
        try {
            FileOutputStream outputStream = new FileOutputStream(GAMES_ROOT + gameName + JAR_EXTENSION);
            JarOutputStream target = new JarOutputStream(outputStream, manifest);
            addToJar(SOURCE_DIRECTORY, target);
            writeResources(target);
            addLibraries(target);
            target.close();
        } catch (IOException e) {
            // TODO - report failure to build the game
            e.printStackTrace();
        }
    }

    private void compile(File source) {
        if (source.isDirectory() && !source.getName().isEmpty()) {
            for (File subdirectoryOrFile : source.listFiles()) {
                compile(subdirectoryOrFile);
            }
        } else if (source.getName().endsWith(".java")){
            javaCompiler.run(null, null, null, source.getAbsolutePath());
        }
    }

    // This and its called methods are based on
    // https://stackoverflow.com/questions/1281229/how-to-use-jaroutputstream-to-create-a-jar-file
    private void addToJar(File source, JarOutputStream target) throws IOException {
        if (source.isDirectory() && !source.getName().isEmpty()) {
            addDirectoryToJar(source, target);
        } else if (source.getName().endsWith(".class")){
            writeFileToJar(target, source, "src/");
        }
    }

    private void addDirectoryToJar(File source, JarOutputStream target) throws IOException {
        String name = convertPathToJarFormat(source.getPath());
        if (!name.endsWith(File.separator)) {
            name += File.separator;
        }
        JarEntry entry = new JarEntry(name.replace("src/", ""));
        entry.setTime(source.lastModified());
        target.putNextEntry(entry);
        target.closeEntry();
        File[] nestedFilesAndDirectories = source.listFiles();
        if (nestedFilesAndDirectories != null) {
            for (File nestedElement : nestedFilesAndDirectories) {
                addToJar(nestedElement, target);
            }
        }
    }

    private void writeToJar(InputStream sourceStream, JarOutputStream target) throws IOException {
        InputStream in = new BufferedInputStream(sourceStream);
        byte[] buffer = new byte[MAX_ENTRY_LENGTH];
        int count;
        while ((count = in.read(buffer)) != -1) {
            target.write(buffer, 0, count);
        }
        in.close();
    }

    private String convertPathToJarFormat(String path) {
        return path.replaceAll(WINDOWS_PATH_DELIMITER_PATTERN, File.separator);
    }

    private void writeResources(JarOutputStream target) throws IOException {
        Queue<File> fileQueue = new LinkedList<>(Arrays.asList(new File
                ("out/production/voogasalad_duvallinthistogether").listFiles()));
        while(!fileQueue.isEmpty()){
            File resourceOrResourceDirectory = fileQueue.poll();
            if (resourceOrResourceDirectory.isDirectory()) {
                fileQueue.addAll(Arrays.asList(resourceOrResourceDirectory.listFiles()));
            } else if (!resourceOrResourceDirectory.getPath().endsWith("md") &&
                    !resourceOrResourceDirectory.getPath().endsWith(".class")) {
                writeFileToJar(target, resourceOrResourceDirectory, "out/production/voogasalad_duvallinthistogether/");
            }
        }
    }

    private void writeFileToJar(JarOutputStream target, File file, String replace) throws IOException {
        JarEntry entry = new JarEntry(convertPathToJarFormat(file.getPath().replace(replace, "")));
        addEntryToJar(target, file, entry);
    }

    private void addEntryToJar(JarOutputStream target, File entrySource, JarEntry entry) throws IOException {
        entry.setTime(entrySource.lastModified());
        target.putNextEntry(entry);
        writeToJar(new FileInputStream(entrySource), target);
        target.closeEntry();
    }

    private void addLibraries(JarOutputStream target) throws IOException {
        for (File libraryFile : new File("lib").listFiles()) {
            if (libraryFile.getPath().endsWith(".jar")) {
                JarFile jarFile = new JarFile(libraryFile);
                Stream<JarEntry> jarEntryStream = jarFile.stream();
                jarEntryStream.forEach(jarEntry -> addLibraryJarEntry(target, jarFile, jarEntry));

            }
        }
    }

    private void addLibraryJarEntry(JarOutputStream target, JarFile jarFile, JarEntry jarEntry) {
        try {
            if (!jarEntry.getName().endsWith(".MF")) {
                target.putNextEntry(jarEntry);
                writeToJar(jarFile.getInputStream(jarEntry), target);
                target.closeEntry();
            }
        } catch (IOException ioException) {
            // ignore
            ioException.printStackTrace();
        }
    }
}