package tracker.storage;

import tracker.models.Attempt;
import tracker.models.Difficulty;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                try {
                    List<String> parts = splitFields(line);
                    if (parts.size() != 9) {
                        System.out.println("Skipping invalid saved attempt: expected 9 fields.");
                        continue;
                    }
                    int id=Integer.parseInt(parts.get(0));
                    String title = parts.get(1);
                    String platform = parts.get(2);
                    String topic = parts.get(3);
                    Difficulty difficulty = Difficulty.valueOf(parts.get(4));
                    int timeTaken = Integer.parseInt(parts.get(5));
                    boolean solved = parseBoolean(parts.get(6));
                    String notes = parts.get(7);
                    String timestampStr=parts.get(8);
                    validateAttempt(id, title, platform, topic, timeTaken);
                    LocalDateTime timestamp=LocalDateTime.parse(timestampStr);
                    Attempt attempt = new Attempt(id,title,platform,topic,difficulty,timeTaken,solved,notes,timestamp);
                    attempts.add(attempt);
                }
                catch(Exception e){
                    System.out.println("Skipping invalid saved attempt: " + e.getMessage());
                }
            }
        }
        catch(Exception e){
            System.out.println("Error loading file: "+e.getMessage());
        }
        return attempts;
    }

    private static void validateAttempt(int id, String title, String platform, String topic, int timeTaken) {
        if (id <= 0 || title.trim().isEmpty() || platform.trim().isEmpty() || topic.trim().isEmpty() || timeTaken <= 0) {
            throw new IllegalArgumentException("attempt contains missing or invalid values");
        }
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("solved status must be true or false");
    }

    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;

        for (char character : line.toCharArray()) {
            if (escaped) {
                switch (character) {
                    case 'n': field.append('\n'); break;
                    case 'r': field.append('\r'); break;
                    case '|': field.append('|'); break;
                    case '\\': field.append('\\'); break;
                    default:
                        field.append('\\').append(character);
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (escaped) {
            field.append('\\');
        }
        fields.add(field.toString());
        return fields;
    }
}
