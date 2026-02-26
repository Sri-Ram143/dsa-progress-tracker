package tracker.models;

import java.time.LocalDateTime;

public class Attempt {
    private int attemptId;
    private String problemTitle;
    private String platform;
    private String topic;
    private String difficulty;
    private int timeTaken;
    private boolean solved;
    private String notes;
    private LocalDateTime timestamp;

    public Attempt(int attemptId, String problemTitle, String platform, String topic, String difficulty, int timeTaken, boolean solved, String notes, LocalDateTime timestamp){
        this.attemptId = attemptId;
        this.problemTitle = problemTitle;
        this.platform = platform;
        this.topic = topic;
        this.difficulty = difficulty;
        this.timeTaken = timeTaken;    //time taken in minutes
        this.solved = solved;
        this.notes = notes;
        this.timestamp=timestamp;
    }

    //-----GETTERS-----

    public int getAttemptId(){
        return attemptId;
    }
    public String getProblemTitle(){
        return problemTitle;
    }
    public String getPlatform(){
        return platform;
    }
    public String getTopic(){
        return topic;
    }
    public String getDifficulty(){
        return difficulty;
    }
    public int getTimeTaken(){
        return timeTaken;
    }
    public boolean isSolved(){
        return solved;
    }
    public String getNotes(){
        return notes;
    }

    //-----controlled setters-----

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ===== Convert to File Format =====

    public String toFileFormat() {
        return attemptId + "|" + problemTitle + "|" + platform + "|" + topic + "|" + difficulty + "|" + timeTaken + "|" + solved + "|" + notes + "|" +timestamp.toString();
    }

    //convert to file
    @Override
    public String toString() {
        return "ID: " + attemptId + ", Title: " + problemTitle + ", Platform: " + platform + ", Topic: " + topic + ", Difficulty: " + difficulty + ", Time: " + timeTaken + " mins" + ", Solved: " + solved + ", Notes: " + notes + ", Time: " +timestamp.toString();
    }
}
