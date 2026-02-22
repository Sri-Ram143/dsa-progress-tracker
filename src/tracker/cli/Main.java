package tracker.cli;

import java.util.*;
import tracker.models.Problem;
import tracker.storage.ProblemRepository;
import tracker.analytics.AnalyticsEngine;
import tracker.analytics.TopicReport;

public class Main {
    private static Scanner in=new Scanner(System.in);
    private static ProblemRepository repository;
    public static void main(String[] args) {
        repository=new ProblemRepository();
        while (true) {
            System.out.println("----MENU----");
            System.out.println("1.Add Problem\n2.View Problems\n3.Delete Problem\n4.View Analytics\n5.Exit\n");
            int choice;
            System.out.print("Enter your choice: ");
            choice = in.nextInt();
            in.nextLine();
            switch (choice) {
                case 1:
                    addProblem();
                    break;
                case 2:
                    viewProblems();
                    break;
                case 3:
                    deleteProblem();
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
    private static void viewProblems(){
        if(repository.isEmpty()){
            System.out.println("no problems logged yet!");
            return;
        }
        List<Problem> problems=repository.getAllProblems();
        for(Problem p:problems){
            System.out.println(p);
        }
    }
    private static void addProblem(){
        System.out.println("Enter ID: ");
        int id=in.nextInt();
        in.nextLine();

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

        Problem problem=new Problem(id,title,platform,topic,difficulty,timeTaken,solved,notes);
        boolean added=repository.addProblem(problem);
        if(added){
            System.out.println("Problem added successfully");
        }
        else{
            System.out.println("duplicate id found, problem already solved, not added");
        }
    }
    private static void deleteProblem(){
        if(repository.isEmpty()){
            System.out.println("no problems to delete!");
            return;
        }
        System.out.print("Enter ID: ");
        int id=in.nextInt();
        in.nextLine();
        boolean deleted=repository.deleteById(id);
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
        AnalyticsEngine analytics=new AnalyticsEngine(repository.getAllProblems());
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
    }

}
