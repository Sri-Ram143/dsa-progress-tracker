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
    private static final Scanner in = new Scanner(System.in);
    private static AttemptRepository repository;

    public static void main(String[] args) {
        repository=new AttemptRepository();
        while (true) {
            System.out.println("----MENU----");
            System.out.println("1.Add Problem\n2.View Attempts\n3.Delete Attempt\n4.View Analytics\n5.Update Attempt\n6.Search/Filter Attempts\n7.Exit\n");
            int choice = readIntInRange("Enter your choice: ", 1, 7);
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
                    searchAttempts();
                    break;
                case 7:
                    System.out.println("exiting program!");
                    return;
            }
        }
    }
    private static void viewAttempts(){
        if(repository.isEmpty()){
            System.out.println("no problems logged yet!");
            return;
        }
        displayAttempts(repository.getAllAttempts());
    }

    private static void recordAttempt(){
        String title = readRequiredText("Enter Title: ");

        String platform = readRequiredText("Enter Platform: ");

        String topic = readRequiredText("Enter Topic: ");

        Difficulty difficulty = readDifficulty("Enter Difficulty (EASY/MEDIUM/HARD): ");

        int timeTaken = readPositiveInt("Enter Time Taken (in minutes): ");

        boolean solved = readBoolean("Solved (true/false): ");

        System.out.print("Enter Notes: ");
        String notes = in.nextLine().trim();

        int attemptId=repository.recordAttempt(title,platform,topic,difficulty.name(),timeTaken,solved,notes);
        System.out.println("Attempt recorded with ID: " + attemptId);
    }

    private static void deleteAttempt(){
        if(repository.isEmpty()){
            System.out.println("no problems to delete!");
            return;
        }
        String title = readRequiredText("Enter title of the attempt: ");
        List<Attempt> matched=repository.findByTitle(title);

        if (matched.isEmpty()) {
            System.out.println("No attempts found for this title.");
            return;
        }

        Attempt selectedAttempt = selectAttempt(matched, "delete");

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

        System.out.println("\n---- Progress Summary ----");

        System.out.println("Current Streak: " + analytics.getCurrentStreak() + " day(s)");

        System.out.println("Longest Streak: " + analytics.getLongestStreak() + " day(s)");

        System.out.println("Solved This Week: " + analytics.getSolvedThisWeek());

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

        String title = readRequiredText("Enter problem title: ");

        List<Attempt> matched = repository.findByTitle(title);

        if (matched.isEmpty()) {
            System.out.println("No attempts found for this title.");
            return;
        }

        Attempt selectedAttempt = selectAttempt(matched, "update");

        int id = selectedAttempt.getAttemptId();

        System.out.println("\nCurrent Attempt Details:");
        System.out.println(selectedAttempt);

        System.out.println("\nSelect field to edit:");
        System.out.println("1. Difficulty");
        System.out.println("2. Time Taken");
        System.out.println("3. Solved Status");
        System.out.println("4. Notes");
        System.out.println("5. Cancel");

        int choice = readIntInRange("Enter your choice: ", 1, 5);

        boolean updated = false;

        switch (choice) {

            case 1:
                Difficulty difficulty = readDifficulty("Enter new Difficulty (EASY/MEDIUM/HARD): ");
                updated = repository.updateDifficulty(id, difficulty);
                break;

            case 2:
                int time = readPositiveInt("Enter new Time Taken: ");
                updated = repository.updateTimeTaken(id, time);
                break;

            case 3:
                boolean solved = readBoolean("Enter new Solved Status (true/false): ");
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
        }

        if (updated) {
            System.out.println("Attempt updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private static void searchAttempts() {
        if (repository.isEmpty()) {
            System.out.println("No attempts available to search.");
            return;
        }

        System.out.println("\n---- SEARCH / FILTER ----");
        System.out.println("1. Search by Title");
        System.out.println("2. Filter by Topic");
        System.out.println("3. Filter by Platform");
        System.out.println("4. Filter by Difficulty");
        System.out.println("5. Filter by Solved Status");
        System.out.println("6. Back to Main Menu");

        int choice = readIntInRange("Enter your choice: ", 1, 6);
        List<Attempt> results;

        switch (choice) {
            case 1:
                String title = readRequiredText("Enter title: ");
                results = repository.findByTitle(title);
                break;

            case 2:
                String topic = readRequiredText("Enter topic: ");
                results = repository.findByTopic(topic);
                break;

            case 3:
                String platform = readRequiredText("Enter platform: ");
                results = repository.findByPlatform(platform);
                break;

            case 4:
                Difficulty difficulty = readDifficulty("Enter difficulty (EASY/MEDIUM/HARD): ");
                results = repository.findByDifficulty(difficulty);
                break;

            case 5:
                boolean solved = readBoolean("Solved status (true/false): ");
                results = repository.findBySolvedStatus(solved);
                break;

            case 6:
                System.out.println("Returning to main menu.");
                return;

            default:
                results = new ArrayList<>();
        }

        if (results.isEmpty()) {
            System.out.println("No matching attempts found.");
            return;
        }

        System.out.println("\nMatching Attempts:");
        displayAttempts(results);
    }

    private static void displayAttempts(List<Attempt> attempts) {
        for (Attempt attempt : attempts) {
            System.out.println(attempt);
        }
    }

    private static String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

                System.out.println("Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim().toLowerCase();

            if (input.equals("true") || input.equals("yes") || input.equals("y")) {
                return true;
            }

            if (input.equals("false") || input.equals("no") || input.equals("n")) {
                return false;
            }

            System.out.println("Please enter true/false or yes/no.");
        }
    }

    private static Difficulty readDifficulty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine();

            try {
                return Difficulty.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Attempt selectAttempt(List<Attempt> matched, String action) {
        if (matched.size() == 1) {
            Attempt selectedAttempt = matched.get(0);
            System.out.println("Only one attempt found.");
            System.out.println("Automatically selected Attempt ID: " + selectedAttempt.getAttemptId());
            return selectedAttempt;
        }

        System.out.println("\nMatching Attempts:");

        for (Attempt attempt : matched) {
            System.out.println("ID: " + attempt.getAttemptId() +" | Time: " + attempt.getTimeTaken() + " mins" + " | Solved: " + attempt.isSolved() + " | Date: " + attempt.getTimestamp());
        }

        while (true) {
            int id = readPositiveInt("Enter Attempt ID to " + action + ": ");

            for (Attempt attempt : matched) {
                if (attempt.getAttemptId() == id) {
                    return attempt;
                }
            }

            System.out.println("Invalid selection. Choose from displayed IDs.");
        }
    }
}
