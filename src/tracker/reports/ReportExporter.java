package tracker.reports;

import tracker.analytics.AnalyticsEngine;
import tracker.analytics.TopicReport;
import tracker.analytics.RecommendationReport;
import tracker.analytics.Recommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ReportExporter {
    public static boolean exportToTxt(AnalyticsEngine analytics,String fileName){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("DSA PROGRESS REPORT");
            writer.newLine();
            writer.write("==========================");
            writer.newLine();
            writer.newLine();

            writer.write("Generated On: " + java.time.LocalDateTime.now());
            writer.newLine();
            writer.newLine();

            writer.write("SUMMARY");
            writer.newLine();
            writer.write("-------");
            writer.newLine();

            writer.write("Total Attempts : " + analytics.getTotalProblems());
            writer.newLine();

            writer.write("Total Solved   : " + analytics.getTotalSolved());
            writer.newLine();

            writer.write(String.format("Accuracy       : %.2f%%",
                    analytics.getAccuracy()));
            writer.newLine();

            writer.write(String.format("Average Time   : %.2f mins",
                    analytics.getAverageTime()));
            writer.newLine();

            writer.write("Current Streak : " + analytics.getCurrentStreak() + " day(s)");
            writer.newLine();

            writer.write("Longest Streak : " + analytics.getLongestStreak() + " day(s)");
            writer.newLine();

            writer.write("Solved This Week : " + analytics.getSolvedThisWeek());
            writer.newLine();
            writer.newLine();

            writer.write("TOPIC DIAGNOSTICS");
            writer.newLine();
            writer.write("-----------------");
            writer.newLine();
            writer.newLine();

            for (TopicReport report : analytics.generateTopicReports()) {

                writer.write("Topic: " + report.getTopic());
                writer.newLine();

                writer.write("Attempts: " + report.getAttempts());
                writer.newLine();

                writer.write(String.format("Accuracy: %.2f%%",
                        report.getAccuracy()));
                writer.newLine();

                writer.write(String.format("Average Time: %.2f mins",
                        report.getAverageTime()));
                writer.newLine();

                writer.write("Confidence: " + report.getConfidenceLevel());
                writer.newLine();
                writer.newLine();
            }

            RecommendationReport recommendationReport = analytics.generateRecommendationReport();

            writer.write("RECOMMENDATIONS");
            writer.newLine();
            writer.write("---------------");
            writer.newLine();
            writer.newLine();

            writer.write("Weak Performance");
            writer.newLine();

            if (recommendationReport.getWeakPerformanceTopics().isEmpty()) {

                writer.write("None");
                writer.newLine();

            } else {

                for (Recommendation recommendation : recommendationReport.getWeakPerformanceTopics()) {

                    writer.write("Topic: " + recommendation.getTopic());
                    writer.newLine();

                    for (String reason : recommendation.getReasons()) {
                        writer.write("- " + reason);
                        writer.newLine();
                    }

                    writer.newLine();
                }
            }

            writer.newLine();

            writer.write("Low Exposure Topics");
            writer.newLine();

            if (recommendationReport.getExplorationTopics().isEmpty()) {

                writer.write("None");
                writer.newLine();

            } else {

                for (Recommendation recommendation : recommendationReport.getExplorationTopics()) {

                    writer.write("Topic: " + recommendation.getTopic());
                    writer.newLine();

                    for (String reason : recommendation.getReasons()) {
                        writer.write("- " + reason);
                        writer.newLine();
                    }

                    writer.newLine();
                }
            }
            return true;
        }
        catch(IOException e) {
            System.out.println("Error exporting report: "+e.getMessage());
            return false;
        }
    }

    public static boolean exportToCsv(AnalyticsEngine analytics, String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Metric,Value");
            writer.newLine();

            writer.write("Total Attempts," + analytics.getTotalProblems());
            writer.newLine();

            writer.write("Total Solved," + analytics.getTotalSolved());
            writer.newLine();

            writer.write("Accuracy," + formatDecimal(analytics.getAccuracy()));
            writer.newLine();

            writer.write("Average Time," + formatDecimal(analytics.getAverageTime()));
            writer.newLine();

            writer.write("Current Streak," + analytics.getCurrentStreak());
            writer.newLine();

            writer.write("Longest Streak," + analytics.getLongestStreak());
            writer.newLine();

            writer.write("Solved This Week," + analytics.getSolvedThisWeek());
            writer.newLine();
            writer.newLine();

            writer.write("Topic,Attempts,Accuracy,Average Time,Confidence");
            writer.newLine();

            for (TopicReport report : analytics.generateTopicReports()) {

                writer.write(csvField(report.getTopic()) + "," + report.getAttempts() + "," + formatDecimal(report.getAccuracy()) + "," + formatDecimal(report.getAverageTime()) + "," + csvField(report.getConfidenceLevel()));
                writer.newLine();
            }
            return true;
        }
        catch (IOException e) {
            System.out.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    private static String formatDecimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static String csvField(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
