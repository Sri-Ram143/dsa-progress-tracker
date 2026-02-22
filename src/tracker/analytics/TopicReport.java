package tracker.analytics;

public class TopicReport {
    private String topic;
    private int attempts;
    private double accuracy;
    private double averageTime;
    private String confidenceLevel;
    public TopicReport(String topic,int attempts,double accuracy,double averageTime, String confidenceLevel){
        this.topic=topic;
        this.attempts=attempts;
        this.accuracy=accuracy;
        this.averageTime=averageTime;
        this.confidenceLevel=confidenceLevel;
    }
    public String getTopic(){
        return topic;
    }
    public int getAttempts(){
        return attempts;
    }
    public double getAccuracy(){
        return accuracy;
    }
    public double getAverageTime(){
        return averageTime;
    }
    public String getConfidenceLevel(){
        return confidenceLevel;
    }
}
