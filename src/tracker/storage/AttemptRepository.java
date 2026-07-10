package tracker.storage;

import java.util.*;
import tracker.models.Attempt;
import tracker.models.Difficulty;

import java.time.LocalDateTime;

public class AttemptRepository {
    private List<Attempt> attempts;
    public AttemptRepository(){
        attempts=FileHandler.loadAttemptsFromFile();
    }
    public int recordAttempt(String title,String platform,String topic,String difficultyInput,int timeTaken,boolean solved,String notes){
        int nextId = generateNextAttemptId();
        LocalDateTime timestamp=LocalDateTime.now();
        topic = normalizeStorage(topic);
        platform = normalizeStorage(platform);
        Difficulty difficulty= Difficulty.fromString(difficultyInput);
        Attempt attempt = new Attempt(nextId,title,platform,topic,difficulty,timeTaken,solved,notes,timestamp);
        attempts.add(attempt);
        FileHandler.saveAttemptsToFile(attempts);

        return nextId;
    }

    private String normalizeStorage(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    public boolean deleteById(int id){
        boolean removed=false;
        Iterator<Attempt> iterator = attempts.iterator();
        while (iterator.hasNext()) {
            Attempt attempt = iterator.next();
            if(attempt.getAttemptId()==id){
                iterator.remove();
                removed=true;
                break;
            }
        }
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

    public boolean updateDifficulty(int id,Difficulty newDifficulty){
        Attempt attempt=findById(id);

        if(attempt==null){
            return false;
        }

        attempt.setDifficulty(newDifficulty);
        FileHandler.saveAttemptsToFile(attempts);

        return true;
    }

    public boolean updateTimeTaken(int id,int newTime){
        Attempt attempt=findById(id);

        if(attempt==null){
            return false;
        }

        attempt.setTimeTaken(newTime);
        FileHandler.saveAttemptsToFile(attempts);

        return true;
    }

    public boolean updateSolvedStatus(int id,boolean solved){
        Attempt attempt=findById(id);

        if(attempt==null){
            return false;
        }

        attempt.setSolved(solved);
        FileHandler.saveAttemptsToFile(attempts);

        return true;
    }

    public boolean updateNotes(int id,String notes){
        Attempt attempt=findById(id);

        if(attempt==null){
            return false;
        }

        attempt.setNotes(notes);
        FileHandler.saveAttemptsToFile(attempts);

        return true;
    }

    private String normalize(String input) {
        return input.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    public List<Attempt> findByTitle(String title){
        List<Attempt> matched=new ArrayList<>();

        if(title==null){
            return matched;
        }

        String normalOutput=normalize(title);

        for(Attempt attempt:attempts){
            if(normalize(attempt.getProblemTitle()).trim().equalsIgnoreCase(normalOutput)){
                matched.add(attempt);
            }
        }

        return matched;
    }

    public List<Attempt> findByTopic(String topic) {
        List<Attempt> matched = new ArrayList<>();

        if (topic == null) {
            return matched;
        }

        String normalizedTopic = normalize(topic);

        for (Attempt attempt : attempts) {
            if (normalize(attempt.getTopic()).equals(normalizedTopic)) {
                matched.add(attempt);
            }
        }

        return matched;
    }

    public List<Attempt> findByPlatform(String platform) {
        List<Attempt> matched = new ArrayList<>();

        if (platform == null) {
            return matched;
        }

        String normalizedPlatform = normalize(platform);

        for (Attempt attempt : attempts) {
            if (normalize(attempt.getPlatform()).equals(normalizedPlatform)) {
                matched.add(attempt);
            }
        }

        return matched;
    }

    public List<Attempt> findByDifficulty(Difficulty difficulty) {
        List<Attempt> matched = new ArrayList<>();

        for (Attempt attempt : attempts) {
            if (attempt.getDifficulty() == difficulty) {
                matched.add(attempt);
            }
        }

        return matched;
    }

    public List<Attempt> findBySolvedStatus(boolean solved) {
        List<Attempt> matched = new ArrayList<>();

        for (Attempt attempt : attempts) {
            if (attempt.isSolved() == solved) {
                matched.add(attempt);
            }
        }

        return matched;
    }
}
