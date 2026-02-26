package tracker.storage;

import java.util.*;
import tracker.models.Attempt;
import java.time.LocalDateTime;

public class AttemptRepository {
    private List<Attempt> attempts;
    public AttemptRepository(){
        attempts=FileHandler.loadAttemptsFromFile();
    }
    public int recordAttempt(String title,String platform,String topic,String difficulty,int timeTaken,boolean solved,String notes){
        int nextId = generateNextAttemptId();
        LocalDateTime timestamp=LocalDateTime.now();
        Attempt attempt = new Attempt(nextId,title,platform,topic,difficulty,timeTaken,solved,notes,timestamp);
        attempts.add(attempt);
        FileHandler.saveAttemptsToFile(attempts);

        return nextId;
    }

    public boolean deleteByTopic(String topic){
        boolean removed = attempts.removeIf(a -> a.getTopic().equalsIgnoreCase(topic));
        if(removed){
            FileHandler.saveAttemptsToFile(attempts);
        }
        return removed;
    }

    private int generateNextAttemptId() {
        int maxId = 0;

        for (Attempt attempt : attempts) {
            if (attempt.getAttemptId() > maxId) {
                maxId = attempt.getAttemptId();
            }
        }

        return maxId + 1;
    }

    public List<Attempt> getAllAttempts(){
        return attempts;
    }
    public boolean isEmpty(){
        return attempts.isEmpty();
    }
    public Attempt findById(int id){
        for(Attempt i:attempts){
            if(i.getAttemptId()==id){
                return i;
            }
        }
        return null;
    }

}
