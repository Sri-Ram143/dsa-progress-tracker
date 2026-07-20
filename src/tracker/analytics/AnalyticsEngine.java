package tracker.analytics;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import tracker.models.Attempt;

public class AnalyticsEngine {
    private final List<Attempt> attempts;
    private static final int UNDER_EXPLORED_THRESHOLD = 3;
    private static final int MATURE_THRESHOLD = 6;
    public AnalyticsEngine(List<Attempt> attempts){
        this.attempts = List.copyOf(attempts);
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

    public int getCurrentStreak() {
        TreeSet<LocalDate> practiceDates = getPracticeDates();

        if (practiceDates.isEmpty()) {
            return 0;
        }

        LocalDate currentDate = practiceDates.last();
        if (currentDate.isBefore(LocalDate.now().minusDays(1))) {
            return 0;
        }

        int streak = 1;

        while (practiceDates.contains(currentDate.minusDays(1))) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }

        return streak;
    }

    public int getLongestStreak() {
        TreeSet<LocalDate> practiceDates = getPracticeDates();

        if (practiceDates.isEmpty()) {
            return 0;
        }

        int longestStreak = 1;
        int currentStreak = 1;
        LocalDate previousDate = null;

        for (LocalDate date : practiceDates) {
            if (previousDate != null && date.equals(previousDate.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }

            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }

            previousDate = date;
        }

        return longestStreak;
    }

    public int getSolvedThisWeek() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        int count = 0;

        for (Attempt attempt : attempts) {
            LocalDate attemptDate = attempt.getTimestamp().toLocalDate();

            if (attempt.isSolved() && !attemptDate.isBefore(startOfWeek) && !attemptDate.isAfter(endOfWeek)) {
                count++;
            }
        }

        return count;
    }

    private TreeSet<LocalDate> getPracticeDates() {
        TreeSet<LocalDate> practiceDates = new TreeSet<>();

        for (Attempt attempt : attempts) {
            practiceDates.add(attempt.getTimestamp().toLocalDate());
        }
        return practiceDates;
    }

    public Map<String, Integer> getTopicDistribution() {

        Map<String, Integer> topicMap = new TreeMap<>();

        for (Attempt p : attempts) {
            String topic = normalizeTopicName(p.getTopic());
            topicMap.put(topic, topicMap.getOrDefault(topic, 0) + 1);
        }

        return topicMap;
    }

    public Map<String, Integer> getTopicAttemptCount() {

        Map<String, Integer> countMap = new TreeMap<>();

        for (Attempt p : attempts) {
            String topic = normalizeTopicName(p.getTopic());
            countMap.put(topic,countMap.getOrDefault(topic, 0) + 1);
        }

        return countMap;
    }

    public Map<String, Double> getAccuracyPerTopic() {

        Map<String, Integer> solvedMap = new TreeMap<>();
        Map<String, Integer> totalMap = new TreeMap<>();

        for (Attempt p : attempts) {
            String topic = normalizeTopicName(p.getTopic());
            totalMap.put(topic,totalMap.getOrDefault(topic, 0) + 1);
            if (p.isSolved()) {
                solvedMap.put(topic,solvedMap.getOrDefault(topic, 0) + 1);
            }
        }

        Map<String, Double> accuracyMap = new TreeMap<>();

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

        if (attempts < UNDER_EXPLORED_THRESHOLD) {
            return "Under-Explored";
        }
        else if (attempts < MATURE_THRESHOLD) {
            return "Developing";
        }
        else {
            return "Mature";
        }
    }

    public Map<String, Double> getAverageTimePerTopic() {

        Map<String, Integer> totalTimeMap = new TreeMap<>();
        Map<String, Integer> countMap = new TreeMap<>();

        for (Attempt p : attempts) {

            String topic = normalizeTopicName(p.getTopic());

            totalTimeMap.put(topic,
                    totalTimeMap.getOrDefault(topic, 0) + p.getTimeTaken());

            countMap.put(topic,
                    countMap.getOrDefault(topic, 0) + 1);
        }

        Map<String, Double> averageMap = new TreeMap<>();

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

    private String normalizeTopicName(String input) {
        String normalized = input.trim().replaceAll("\\s+", " ").toLowerCase();
        String[] words = normalized.split(" ");
        StringBuilder topicName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (topicName.length() > 0) {
                    topicName.append(" ");
                }

                topicName.append(Character.toUpperCase(word.charAt(0)));
                topicName.append(word.substring(1));
            }
        }

        return topicName.toString();
    }
}
