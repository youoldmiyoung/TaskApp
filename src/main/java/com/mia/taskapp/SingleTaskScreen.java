package com.mia.taskapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SingleTaskScreen {

    private static Timeline timeline;
    private static int remainingSeconds;
    private static int originalSeconds;

    public static void show(Stage stage) {
        // Stop any existing timer if user re-enters the screen
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        // --- SETUP VIEW (enter task + time) ---
        Label title = new Label("Single Task");
        title.setFont(Font.font(22));

        TextField taskField = new TextField();
        taskField.setPromptText("Enter task name...");

        // minutes input (safer than free-text)
        Spinner<Integer> minutesSpinner = new Spinner<>(1, 600, 25); // 1 to 600 minutes, default 25
        minutesSpinner.setEditable(true);

        Label minutesLabel = new Label("Minutes:");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button doneBtn = new Button("Done");
        doneBtn.setDefaultButton(true); // Enter triggers Done

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> Home.show(stage));

        VBox root = new VBox(14,
                title,
                new Label("Task:"),
                taskField,
                minutesLabel,
                minutesSpinner,
                doneBtn,
                errorLabel,
                backBtn
        );
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(640, 480);

        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Single Task");
        stage.setScene(scene);
        stage.show();

        doneBtn.setOnAction(e -> {
            String taskName = taskField.getText() == null ? "" : taskField.getText().trim();
            if (taskName.isEmpty()) {
                errorLabel.setText("Please enter a task.");
                return;
            }

            Integer minutes = minutesSpinner.getValue();
            if (minutes == null || minutes <= 0) {
                errorLabel.setText("Please enter a valid number of minutes.");
                return;
            }

            // Convert minutes -> seconds
            originalSeconds = minutes * 60;
            remainingSeconds = originalSeconds;

            // Switch to timer view
            showTimerView(stage, taskName);
        });
    }

    private static void showTimerView(Stage stage, String taskName) {
        // --- TIMER VIEW ---
        Label taskLabel = new Label(taskName);
        taskLabel.setFont(Font.font(36));
        taskLabel.setWrapText(true);
        taskLabel.setMaxWidth(560);
        taskLabel.setAlignment(Pos.CENTER);

        Label timerLabel = new Label(formatTime(remainingSeconds));
        timerLabel.setFont(Font.font(24));

        Button pauseResumeBtn = new Button("Pause");
        Button restartBtn = new Button("Restart");
        Button quitBtn = new Button("Quit");

        // Build layout
        VBox root = new VBox(18, taskLabel, timerLabel, pauseResumeBtn, restartBtn, quitBtn);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(640, 480);

        stage.setScene(new Scene(root, 640, 480));
        stage.setTitle("Single Task — Timer");
        stage.show();

        // Create timeline ticking once per second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            remainingSeconds--;
            timerLabel.setText(formatTime(remainingSeconds));

            if (remainingSeconds <= 0) {
                timeline.stop();
                pauseResumeBtn.setDisable(true);
                timerLabel.setText("00:00");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Pause / Resume
        pauseResumeBtn.setOnAction(e -> {
            if (timeline == null) return;

            if (timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause();
                pauseResumeBtn.setText("Resume");
            } else {
                timeline.play();
                pauseResumeBtn.setText("Pause");
            }
        });

        // Restart (same task, reset timer)
        restartBtn.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            remainingSeconds = originalSeconds;
            timerLabel.setText(formatTime(remainingSeconds));

            pauseResumeBtn.setDisable(false);
            pauseResumeBtn.setText("Pause");

            timeline.playFromStart();
        });

        // Quit (reloads the Single Task setup page)
        quitBtn.setOnAction(e -> SingleTaskScreen.show(stage));
    }

    private static String formatTime(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Ensure 2 digits for seconds
        String sec = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        return minutes + ":" + sec;
    }
}
