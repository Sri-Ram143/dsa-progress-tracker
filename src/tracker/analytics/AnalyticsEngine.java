package tracker.analytics;

import java.util.*;
import tracker.models.Problem;

public class AnalyticsEngine {
    private List<Problem> problems;
    public AnalyticsEngine(List<Problem> problems){
        this.problems=problems;
    }

    public int getTotalProblems(){
        return problems.size();
    }

    public int getTotalSolved(){
        int count=0;
        for(Problem p:problems){
            if(p.isSolved()){
                count++;
            }
        }
        return count;
    }

    public double Accuracy(){
        if(problems.isEmpty()){
            return 0;
        }
        return (getTotalSolved()*100.0)/problems.size();
    }

    public double getAverageTime(){
        if(problems.isEmpty()){
            return 0;
        }
        int totalTime=0;
        for(Problem p:problems){
            totalTime+=p.getTimeTaken();
        }
        return totalTime*1.0/problems.size();
    }

    public Map<String, Integer> getTopicDistribution() {

        Map<String, Integer> topicMap = new HashMap<>();

        for (Problem p : problems) {
            String topic = p.getTopic();
            topicMap.put(topic, topicMap.getOrDefault(topic, 0) + 1);
        }

        return topicMap;
    }
}
