package tracker.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import tracker.models.Attempt;
import tracker.models.Difficulty;

class AnalyticsEngineTest {

    @Test
    void calculatesSummaryMetrics() {
        AnalyticsEngine analytics = new AnalyticsEngine(List.of(
                attempt(1, "Two Sum", "Arrays", 10, true),
                attempt(2, "Contains Duplicate", "Arrays", 20, false),
                attempt(3, "Valid Parentheses", "Stack", 30, true)
        ));

        assertEquals(3, analytics.getTotalProblems());
        assertEquals(2, analytics.getTotalSolved());
        assertEquals(66.67, analytics.getAccuracy(), 0.01);
        assertEquals(20.0, analytics.getAverageTime(), 0.01);
    }

    @Test
    void recommendsUnderExploredTopics() {
        AnalyticsEngine analytics = new AnalyticsEngine(List.of(
                attempt(1, "Two Sum", "Arrays", 10, true),
                attempt(2, "Contains Duplicate", "Arrays", 20, false)
        ));

        Recommendation recommendation = analytics.generateRecommendationReport()
                .getExplorationTopics().get(0);

        assertEquals("Arrays", recommendation.getTopic());
        assertTrue(recommendation.getReasons().contains("Low Exposure (<3 attempts)"));
    }

    @Test
    void recommendsWeakTopicsAfterEnoughAttempts() {
        AnalyticsEngine analytics = new AnalyticsEngine(List.of(
                attempt(1, "A", "Graphs", 10, false),
                attempt(2, "B", "Graphs", 20, false),
                attempt(3, "C", "Graphs", 30, true),
                attempt(4, "D", "Arrays", 5, true)
        ));

        Recommendation recommendation = analytics.generateRecommendationReport()
                .getWeakPerformanceTopics().get(0);

        assertEquals("Graphs", recommendation.getTopic());
        assertTrue(recommendation.getReasons().contains("Low Accuracy (<60%)"));
        assertTrue(recommendation.getReasons().contains("Slow Performance (Above overall avg)"));
    }

    @Test
    void reportsZeroCurrentStreakWhenTheLatestPracticeIsOld() {
        Attempt oldAttempt = new Attempt(1, "Two Sum", "LeetCode", "Arrays", Difficulty.EASY,
                10, true, "", LocalDateTime.now().minusDays(3));

        assertEquals(0, new AnalyticsEngine(List.of(oldAttempt)).getCurrentStreak());
    }

    private Attempt attempt(int id, String title, String topic, int minutes, boolean solved) {
        return new Attempt(id, title, "LeetCode", topic, Difficulty.MEDIUM, minutes, solved,
                "", LocalDateTime.now());
    }
}
