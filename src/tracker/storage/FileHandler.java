package tracker.storage;

import tracker.models.Attempt;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;

public class FileHandler {
    private static final String FILE_NAME="problems.txt";

    public static void saveAttemptsToFile(List<Attempt> attempts){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(FILE_NAME))){
            for(Attempt a: attempts){
                writer.write(a.toFileFormat());
                writer.newLine();
            }
        }
        catch(Exception e){
            System.out.println("Error saving to File: "+e.getMessage());
        }
    }

    public static List<Attempt> loadAttemptsFromFile(){
        List<Attempt> attempts =new ArrayList<>();
        File file=new File(FILE_NAME);
        if(!file.exists()){
            return attempts;
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
                String timestampStr=parts[8];
                LocalDateTime timestamp=LocalDateTime.parse(timestampStr);
                Attempt attempt = new Attempt(id,title,platform,topic,difficulty,timeTaken,solved,notes,timestamp);
                attempts.add(attempt);
            }
        }
        catch(Exception e){
            System.out.println("Error loading file: "+e.getMessage());
        }
        return attempts;
    }
}
