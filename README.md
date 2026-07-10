# DSA Progress Tracker

A Java command-line application for tracking Data Structures and Algorithms practice attempts, analyzing topic-wise performance, and identifying areas that need more focus.

This project is built as a learning-focused portfolio project. It starts with a simple CLI tracker and gradually grows into a more useful personal analytics tool for coding practice.

## Features

- Add coding problem attempts with title, platform, topic, difficulty, time taken, solved status, and notes
- View all recorded attempts
- Search and filter attempts by title, topic, platform, difficulty, or solved status
- Normalize topic names so casing and extra spaces do not split analytics results
- Update attempt details such as difficulty, time taken, solved status, and notes
- Delete attempts by selecting the correct attempt ID
- Store attempts in a local text file
- Calculate overall analytics:
  - total problems attempted
  - total solved
  - accuracy percentage
  - average solving time
  - current practice streak
  - longest practice streak
  - problems solved this week
- Generate topic-wise diagnostics:
  - attempts per topic
  - topic accuracy
  - average time per topic
  - confidence level
- Recommend focus areas based on weak performance or low exposure

## Why I Built This

While practicing DSA, it is easy to count solved problems but harder to understand actual progress. This project helps track not just quantity, but also topic strength, solving speed, consistency, and weak areas.

The goal is to turn raw practice history into useful feedback for better preparation.

## Tech Stack

- Java
- Object-Oriented Programming
- File handling
- Collections
- CLI-based user interaction

## Project Structure

```text
src/
  tracker/
    analytics/
      AnalyticsEngine.java
      Recommendation.java
      RecommendationReport.java
      TopicReport.java
    cli/
      Main.java
    models/
      Attempt.java
      Difficulty.java
    storage/
      AttemptRepository.java
      FileHandler.java
```

## How To Run

Compile the project:

```bash
javac -d out src/tracker/models/*.java src/tracker/storage/*.java src/tracker/analytics/*.java src/tracker/cli/*.java
```

Run the application:

```bash
java -cp out tracker.cli.Main
```

## Sample Menu

```text
----MENU----
1. Add Problem
2. View Attempts
3. Delete Attempt
4. View Analytics
5. Update Attempt
6. Search/Filter Attempts
7. Exit
```

## Current Analytics Logic

The app evaluates progress using simple rules:

- Topics with fewer than 3 attempts are marked as low exposure areas
- Topics with accuracy below 60% are marked as weak performance areas
- Topics taking more than the overall average time are marked as slow performance areas
- Each topic receives a confidence level based on attempt count
- Streaks are calculated from unique practice dates, so multiple attempts on the same day count as one streak day

## Learning Goals

This project is being developed step by step to practice:

- clean Java class design
- separation of concerns
- file-based persistence
- analytics logic
- input validation
- Git and GitHub workflow
- writing professional project documentation

## Planned Improvements

- Improve input validation and error handling
- Export analytics reports
- Move from text-file storage to CSV, JSON, or SQLite
- Add unit tests for analytics logic
- Convert the project to Maven or Gradle

## Status

In active development. The current version is a functional CLI tracker with basic analytics and recommendation support.
