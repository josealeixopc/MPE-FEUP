package logic.utils;

import logic.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String RESULTS_FOLDER = "results";

    public static void createDirectoryIfNotExists(String directoryName){

        File dir = new File(directoryName);

        boolean isDirCreated = dir.exists() || dir.mkdirs();

        if(!isDirCreated){
            System.err.println("The folder '" + directoryName + "' could not be created.");
        }
    }

    public static void createFileIfNotExists(String fileName){
        try {
            File file = new File(fileName);
            boolean isFileCreated = file.exists() || file.createNewFile();

            if(!isFileCreated){
                System.err.println("The file '" + fileName + "' could not be created.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String fileName, String content){
        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(String fileName, String content){
        try {
            Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createDirectoryForResults(String executionFolderName, long timeLimitMillis, int numberOfCities){
        String newDirName = executionFolderName + File.separator + timeLimitMillis + "ms_" + numberOfCities;
        createDirectoryIfNotExists(newDirName);

        return newDirName;
    }

    public static String createDirectoryForExecution(){
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");

        String newDirName = RESULTS_FOLDER + File.separator + timeStampPattern.format(LocalDateTime.now());
        createDirectoryIfNotExists(newDirName);

        return newDirName;
    }

}
