package tracker.cli;

import java.util.*;

import tracker.analytics.Recommendation;
import tracker.models.Attempt;
import tracker.storage.AttemptRepository;
import tracker.analytics.AnalyticsEngine;
import tracker.analytics.TopicReport;
import tracker.analytics.RecommendationReport;

public class Main {
    private static Scanner in=new Scanner(System.in);
    private static AttemptRepository repository;
    public static void main(String[] args) {
        repository=new AttemptRepository();
        while (true) {
            System.out.println("----MENU----");
            System.out.println("1.Add Problem\n2.View Attempts\n3.Delete Attempt\n4.View Analytics\n5.Exit\n");
            int choice;
            System.out.print("Enter your choice: ");
            choice = in.nextInt();
            in.nextLine();
            switch (choice) {
                case 1:
                    recordAttempt();
                    break;
                case 2:
                    viewAttempts();
                    break;
                case 3:
                    deleteAttempt();
                    break;
                case 4:
                    showAnalytics();
                    break;
                case 5:
                    System.out.println("exiting program!");
                    return;
                default:
                    System.out.println("invalid choice. Try again");
                    break;
            }
        }
    }
    private static void viewAttempts(){
        if(repository.isEmpty()){
            System.out.println("no problems logged yet!");
            return;
        }
        for(Attempt attempt : repository.getAllAttempts()){
            System.out.println(attempt);
        }
    }
    private static void recordAttempt(){
        System.out.println("Enter Title: ");
        String title=in.nextLine();

        System.out.println("Enter Platform: ");
        String platform=in.nextLine();

        System.out.println("Enter Topic: ");
        String topic=in.nextLine();

        System.out.println("Enter Difficulty: ");
        String difficulty=in.nextLine();

        System.out.println("Enter Time Taken(in minutes): ");
        int timeTaken=in.nextInt();

        System.out.println("Solved(True/false): ");
        boolean solved=in.nextBoolean();
        in.nextLine();

        System.out.println("Enter Notes: ");
        String notes=in.nextLine();

        int attemptId=repository.recordAttempt(title,platform,topic,difficulty,timeTaken,solved,notes);
        System.out.println("Attempt recorded with ID: " + attemptId);
    }
    private static void deleteAttempt(){
        if(repository.isEmpty()){
            System.out.println("no problems to delete!");
            return;
        }
        System.out.print("Enter topic: ");
        String topic=in.nextLine();
        in.nextLine();
        boolean deleted=repository.deleteByTopic(topic);
        if(deleted){
            System.out.println("deleted successfully!");
        }
        else{
            System.out.println("Problem not found!");
        }
    }
    private static void showAnalytics(){
        if(repository.isEmpty()){
            System.out.println("No data available for analytics");
            return;
        }
        AnalyticsEngine analytics=new AnalyticsEngine(repository.getAllAttempts());
        System.out.println("\n---- ANALYTICS ----");

        System.out.println("Total Problems: "+analytics.getTotalProblems());

        System.out.println("Total Solved: " + analytics.getTotalSolved());

        System.out.println("Accuracy: " + String.format("%.2f", analytics.getAccuracy()) + "%");

        System.out.println("Average Time: " + String.format("%.2f",analytics.getAverageTime()) + " mins");

        System.out.println("\nTopic Distribution:");

        analytics.getTopicDistribution().forEach((topic, count) -> System.out.println(topic + " : " + count));

        List<TopicReport> reports = analytics.generateTopicReports();

        System.out.println("\n---- Topic Diagnostics ----");

        for (TopicReport report : reports) {

            System.out.println("\nTopic: " + report.getTopic());
            System.out.println("Attempts: " + report.getAttempts());
            System.out.println("Accuracy: " + String.format("%.2f", report.getAccuracy()) + "%");
            System.out.println("Avg Time: " + String.format("%.2f", report.getAverageTime()) + " mins");
            System.out.println("Confidence: " + report.getConfidenceLevel());
        }

        RecommendationReport report = analytics.generateRecommendationReport();

        System.out.println("\n---- Focus Areas (Weak Performance) ----");

        if (report.getWeakPerformanceTopics().isEmpty()) {
            System.out.println("No weak performance areas detected.");
        } else {
            for (Recommendation rec : report.getWeakPerformanceTopics()) {
                System.out.println("\nTopic: " + rec.getTopic());
                for (String reason : rec.getReasons()) {
                    System.out.println("- " + reason);
                }
            }
        }

        System.out.print("\n---- Exploration Areas (Low Exposure) ----");

        if (report.getExplorationTopics().isEmpty()) {
            System.out.println("All topics sufficiently explored.");
        } else {
            for (Recommendation rec : report.getExplorationTopics()) {
                System.out.println("\nTopic: " + rec.getTopic());
                for (String reason : rec.getReasons()) {
                    System.out.println("- " + reason);
                }
            }
            System.out.println("\n");
        }
    }
}
