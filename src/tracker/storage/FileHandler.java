package tracker.storage;

import tracker.models.Problem;
import java.util.*;
import java.io.*;

public class FileHandler {
    private static final String FILE_NAME="problems.txt";

    public static void saveToFile(List<Problem> problems){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(FILE_NAME))){
            for(Problem p:problems){
                writer.write(p.toFileFormat());
                writer.newLine();
            }
        }
        catch(Exception e){
            System.out.println("Error saving to File: "+e.getMessage());
        }
    }

    public static List<Problem> loadFromFile(){
        List<Problem> problems=new ArrayList<>();
        File file=new File(FILE_NAME);
        if(!file.exists()){
            return problems;
        }
        try(BufferedReader reader=new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line=reader.readLine())!=null){
                String[] parts=line.split("\\|");
                int id=Integer.parseInt(parts[0]);
                String title = parts[1];
                String platform = parts[2];
                String topic = parts[3];
                String difficulty = parts[4];
                int timeTaken = Integer.parseInt(parts[5]);
                boolean solved = Boolean.parseBoolean(parts[6]);
                String notes = parts[7];

                Problem problem = new Problem(id,title,platform,topic,difficulty,timeTaken,solved,notes);
                problems.add(problem);
            }
        }
        catch(Exception e){
            System.out.println("Error leading file: "+e.getMessage());
        }
        return problems;
    }
}
