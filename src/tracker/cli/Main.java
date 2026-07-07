package tracker.cli;

import java.util.*;

import tracker.analytics.Recommendation;
import tracker.models.Attempt;
import tracker.models.Difficulty;
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
            System.out.println("1.Add Problem\n2.View Attempts\n3.Delete Attempt\n4.View Analytics\n5.Update Attempt\n6.Exit\n");
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
                    updateAttempt();
                    break;
                case 6:
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
        try {
            Difficulty.fromString(difficulty);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

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
        System.out.print("Enter title of the attempt: ");
        String topic=in.nextLine();
        List<Attempt> matched=repository.findByTitle(topic);

        if (matched.isEmpty()) {
            System.out.println("No attempts found for this title.");
            return;
        }

        Attempt selectedAttempt = null;

        if (matched.size() == 1) {

            selectedAttempt = matched.get(0);
            System.out.println("Only one attempt found.");
            System.out.println("Automatically selected Attempt ID: " + selectedAttempt.getAttemptId());

        } else {

            System.out.println("\nMatching Attempts:");

            for (Attempt attempt : matched) {
                System.out.println("ID: " + attempt.getAttemptId() +" | Time: " + attempt.getTimeTaken() + " mins" + " | Solved: " + attempt.isSolved() + " | Date: " + attempt.getTimestamp());
            }

            while (true) {

                System.out.print("Enter Attempt ID to delete: ");
                int id = in.nextInt();
                in.nextLine();

                boolean foundInList = false;

                for (Attempt attempt : matched) {
                    if (attempt.getAttemptId() == id) {
                        selectedAttempt = attempt;
                        foundInList = true;
                        break;
                    }
                }

                if (foundInList) {
                    break;
                } else {
                    System.out.println("Invalid selection. Choose from displayed IDs.");
                }
            }
        }

        int id = selectedAttempt.getAttemptId();

        System.out.println("\nCurrent Attempt Details:");
        System.out.println(selectedAttempt);

        boolean deleted=repository.deleteById(id);

        if(deleted){
            System.out.println("deleted successfully!");
        }
        else{
            System.out.println("No problems found in that topic!");
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

    private static void updateAttempt() {

        if (repository.isEmpty()) {
            System.out.println("No attempts available to update.");
            return;
        }

        System.out.print("Enter problem title: ");
        String title = in.nextLine();

        List<Attempt> matched = repository.findByTitle(title);

        if (matched.isEmpty()) {
            System.out.println("No attempts found for this title.");
            return;
        }

        Attempt selectedAttempt = null;

        if (matched.size() == 1) {

            selectedAttempt = matched.get(0);
            System.out.println("Only one attempt found.");
            System.out.println("Automatically selected Attempt ID: " + selectedAttempt.getAttemptId());

        } else {

            System.out.println("\nMatching Attempts:");

            for (Attempt attempt : matched) {
                System.out.println("ID: " + attempt.getAttemptId() +" | Time: " + attempt.getTimeTaken() + " mins" + " | Solved: " + attempt.isSolved() + " | Date: " + attempt.getTimestamp());
            }

            while (true) {

                System.out.print("Enter Attempt ID to update: ");
                int id = in.nextInt();
                in.nextLine();

                boolean foundInList = false;

                for (Attempt attempt : matched) {
                    if (attempt.getAttemptId() == id) {
                        selectedAttempt = attempt;
                        foundInList = true;
                        break;
                    }
                }

                if (foundInList) {
                    break;
                } else {
                    System.out.println("Invalid selection. Choose from displayed IDs.");
                }
            }
        }

        int id = selectedAttempt.getAttemptId();

        System.out.println("\nCurrent Attempt Details:");
        System.out.println(selectedAttempt);

        System.out.println("\nSelect field to edit:");
        System.out.println("1. Difficulty");
        System.out.println("2. Time Taken");
        System.out.println("3. Solved Status");
        System.out.println("4. Notes");
        System.out.println("5. Cancel");

        int choice = in.nextInt();
        in.nextLine();

        boolean updated = false;

        switch (choice) {

            case 1:
                System.out.print("Enter new Difficulty: ");
                String difficultyInput = in.nextLine();
                Difficulty difficulty;
                try {
                    difficulty = Difficulty.fromString(difficultyInput);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                updated = repository.updateDifficulty(id, difficulty);
                break;

            case 2:
                System.out.print("Enter new Time Taken: ");
                int time = in.nextInt();
                in.nextLine();
                updated = repository.updateTimeTaken(id, time);
                break;

            case 3:
                System.out.print("Enter new Solved Status (true/false): ");
                boolean solved = in.nextBoolean();
                in.nextLine();
                updated = repository.updateSolvedStatus(id, solved);
                break;

            case 4:
                System.out.print("Enter new Notes: ");
                String notes = in.nextLine();
                updated = repository.updateNotes(id, notes);
                break;

            case 5:
                System.out.println("Update cancelled.");
                return;

            default:
                System.out.println("Invalid choice.");
                return;
        }

        if (updated) {
            System.out.println("Attempt updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
    }
}
