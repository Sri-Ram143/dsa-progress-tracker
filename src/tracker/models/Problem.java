package tracker.models;

public class Problem {
    private int id;
    private String title;
    private String platform;
    private String topic;
    private String difficulty;
    private int timeTaken;
    private boolean solved;
    private String notes;

    public Problem(int id,String title,String platform,String topic,String difficulty,int timeTaken,boolean solved,String notes){
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.topic = topic;
        this.difficulty = difficulty;
        this.timeTaken = timeTaken;    //time taken in minutes
        this.solved = solved;
        this.notes = notes;
    }

    //-----GETTERS-----

    public int getId(){
        return id;
    }
    public String getTitle(){
        return title;
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
        return id + "|" +
                title + "|" +
                platform + "|" +
                topic + "|" +
                difficulty + "|" +
                timeTaken + "|" +
                solved + "|" +
                notes;
    }

    //convert to file
    @Override
    public String toString() {
        return "ID: " + id +
                ", Title: " + title +
                ", Platform: " + platform +
                ", Topic: " + topic +
                ", Difficulty: " + difficulty +
                ", Time: " + timeTaken + " mins" +
                ", Solved: " + solved +
                ", Notes: " + notes;
    }
}
