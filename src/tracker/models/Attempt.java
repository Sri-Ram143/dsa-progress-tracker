package tracker.models;

import java.time.LocalDateTime;

public class Attempt {
    private int attemptId;
    private String problemTitle;
    private String platform;
    private String topic;
    private Difficulty difficulty;
    private int timeTaken;
    private boolean solved;
    private String notes;
    private LocalDateTime timestamp;

    public Attempt(int attemptId, String problemTitle, String platform, String topic, Difficulty difficulty, int timeTaken, boolean solved, String notes, LocalDateTime timestamp){
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
    public String getDisplayTopic(){
        String normalized = topic.trim().replaceAll("\\s+", " ").toLowerCase();
        String[] words = normalized.split(" ");
        StringBuilder displayTopic = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (displayTopic.length() > 0) {
                    displayTopic.append(" ");
                }

                displayTopic.append(Character.toUpperCase(word.charAt(0)));
                displayTopic.append(word.substring(1));
            }
        }

        return displayTopic.toString();
    }
    public Difficulty getDifficulty(){
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
    public LocalDateTime getTimestamp(){return timestamp;}

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ===== Convert to File Format =====

    public String toFileFormat() {
        return attemptId + "|" + problemTitle + "|" + platform + "|" + topic + "|" + difficulty.name() + "|" + timeTaken + "|" + solved + "|" + notes + "|" +timestamp.toString();
    }

    //convert to file
    @Override
    public String toString() {
        return "ID: " + attemptId + ", Title: " + problemTitle + ", Platform: " + platform + ", Topic: " + getDisplayTopic() + ", Difficulty: " + difficulty.getlabel() + ", Time: " + timeTaken + " mins" + ", Solved: " + solved + ", Notes: " + notes + ", Time: " +timestamp.toString();
    }
}
