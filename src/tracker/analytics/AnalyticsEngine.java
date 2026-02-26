package tracker.analytics;

import java.util.*;
import tracker.models.Attempt;

public class AnalyticsEngine {
    private List<Attempt> attempts;
    private static final int Under_Explored_Threshold=3;
    private static final int Mature_Threshold=3;
    public AnalyticsEngine(List<Attempt> attempts){
        this.attempts = attempts;
    }

    public int getTotalProblems(){
        return attempts.size();
    }

    public int getTotalSolved(){
        int count=0;
        for(Attempt a: attempts){
            if(a.isSolved()){
                count++;
            }
        }
        return count;
    }

    public double getAccuracy(){
        if(attempts.isEmpty()){
            return 0;
        }
        return (getTotalSolved()*100.0)/ attempts.size();
    }

    public double getAverageTime(){
        if(attempts.isEmpty()){
            return 0;
        }
        int totalTime=0;
        for(Attempt a: attempts){
            totalTime+=a.getTimeTaken();
        }
        return totalTime*1.0/ attempts.size();
    }

    public Map<String, Integer> getTopicDistribution() {

        Map<String, Integer> topicMap = new HashMap<>();

        for (Attempt p : attempts) {
            String topic = p.getTopic();
            topicMap.put(topic, topicMap.getOrDefault(topic, 0) + 1);
        }

        return topicMap;
    }

    public Map<String, Integer> getTopicAttemptCount() {

        Map<String, Integer> countMap = new HashMap<>();

        for (Attempt p : attempts) {
            String topic = p.getTopic();
            countMap.put(topic,countMap.getOrDefault(topic, 0) + 1);
        }

        return countMap;
    }

    public Map<String, Double> getAccuracyPerTopic() {

        Map<String, Integer> solvedMap = new HashMap<>();
        Map<String, Integer> totalMap = new HashMap<>();

        for (Attempt p : attempts) {
            String topic = p.getTopic();
            totalMap.put(topic,totalMap.getOrDefault(topic, 0) + 1);
            if (p.isSolved()) {
                solvedMap.put(topic,solvedMap.getOrDefault(topic, 0) + 1);
            }
        }

        Map<String, Double> accuracyMap = new HashMap<>();

        for (String topic : totalMap.keySet()) {

            int solved = solvedMap.getOrDefault(topic, 0);
            int total = totalMap.get(topic);

            double accuracy = (solved * 100.0) / total;

            accuracyMap.put(topic, accuracy);
        }

        return accuracyMap;
    }

    public String getTopicConfidenceLevel(String topic) {

        Map<String, Integer> attemptMap = getTopicAttemptCount();

        int attempts = attemptMap.getOrDefault(topic, 0);

        if (attempts < Under_Explored_Threshold) {
            return "Under-Explored";
        }
        else if (attempts <= Mature_Threshold   ) {
            return "Developing";
        }
        else {
            return "Mature";
        }
    }

    public Map<String, Double> getAverageTimePerTopic() {

        Map<String, Integer> totalTimeMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (Attempt p : attempts) {

            String topic = p.getTopic();

            totalTimeMap.put(topic,
                    totalTimeMap.getOrDefault(topic, 0) + p.getTimeTaken());

            countMap.put(topic,
                    countMap.getOrDefault(topic, 0) + 1);
        }

        Map<String, Double> averageMap = new HashMap<>();

        for (String topic : totalTimeMap.keySet()) {

            double avg =
                    totalTimeMap.get(topic) * 1.0 / countMap.get(topic);

            averageMap.put(topic, avg);
        }

        return averageMap;
    }

    public List<TopicReport> generateTopicReports() {

        Map<String, Integer> attempts = getTopicAttemptCount();
        Map<String, Double> accuracy = getAccuracyPerTopic();
        Map<String, Double> avgTime = getAverageTimePerTopic();

        List<TopicReport> reports = new ArrayList<>();

        for (String topic : attempts.keySet()) {

            TopicReport report = new TopicReport(
                    topic,
                    attempts.get(topic),
                    accuracy.get(topic),
                    avgTime.get(topic),
                    getTopicConfidenceLevel(topic)
            );

            reports.add(report);
        }

        return reports;
    }

    public RecommendationReport generateRecommendationReport() {

        RecommendationReport report = new RecommendationReport();

        Map<String, Integer> attempts = getTopicAttemptCount();
        Map<String, Double> accuracy = getAccuracyPerTopic();
        Map<String, Double> avgTime = getAverageTimePerTopic();

        double overallAvgTime = getAverageTime();

        for (String topic : attempts.keySet()) {

            int topicAttempts = attempts.get(topic);
            double topicAccuracy = accuracy.get(topic);
            double topicAvgTime = avgTime.get(topic);

            Recommendation recommendation = new Recommendation(topic);

            if (topicAttempts < 3) {
                recommendation.addReason("Low Exposure (<3 attempts)");
                report.addExplorationTopic(recommendation);
                continue;
            }

            if (topicAccuracy < 60) {
                recommendation.addReason("Low Accuracy (<60%)");
            }

            if (topicAvgTime > overallAvgTime) {
                recommendation.addReason("Slow Performance (Above overall avg)");
            }

            if (!recommendation.getReasons().isEmpty()) {
                report.addWeakTopic(recommendation);
            }
        }

        return report;
    }
}
