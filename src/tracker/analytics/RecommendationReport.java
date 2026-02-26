package tracker.analytics;

import java.util.*;

public class RecommendationReport {

    private List<Recommendation> weakPerformanceTopics;
    private List<Recommendation> explorationTopics;

    public RecommendationReport() {
        this.weakPerformanceTopics = new ArrayList<>();
        this.explorationTopics = new ArrayList<>();
    }

    public List<Recommendation> getWeakPerformanceTopics() {
        return weakPerformanceTopics;
    }

    public List<Recommendation> getExplorationTopics() {
        return explorationTopics;
    }

    public void addWeakTopic(Recommendation recommendation) {
        weakPerformanceTopics.add(recommendation);
    }

    public void addExplorationTopic(Recommendation recommendation) {
        explorationTopics.add(recommendation);
    }
}