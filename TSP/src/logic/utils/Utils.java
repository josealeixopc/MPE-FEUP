package logic.utils;

import logic.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {

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

}
