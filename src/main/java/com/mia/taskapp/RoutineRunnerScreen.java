package com.mia.taskapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Separator;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoutineRunnerScreen {

    private static Timeline timeline;

    private static int currentIndex;
    private static int remainingSeconds;
    private static boolean running;

    private static int currentTaskOriginalSeconds;
    private static Routine routine;

    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void show(Stage stage, Routine selectedRoutine) {
        // stop any previous run
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        routine = selectedRoutine;
        List<TaskItem> tasks = routine.getTasks();

        // guard
        if (tasks == null || tasks.isEmpty()) {
            Label empty = new Label("This routine has no tasks.");
            empty.setFont(Font.font(20));
            Button back = new Button("Back");
            back.setOnAction(e -> ViewTaskListScreen.show(stage));

            VBox root = new VBox(18, empty, back);
            root.setAlignment(Pos.CENTER);
            stage.setScene(new Scene(root, 640, 480));
            stage.setTitle("Routine Runner");
            stage.show();
            return;
        }

        // init state
        currentIndex = 0;
        currentTaskOriginalSeconds = tasks.get(0).getDurationSeconds();
        remainingSeconds = currentTaskOriginalSeconds;
        running = true;

        // UI pieces
        Label clockLabel = new Label(LocalTime.now().format(CLOCK_FMT));
        clockLabel.setFont(Font.font(22));

        // Label previousLabel = new Label("");
        // previousLabel.setStyle("-fx-text-fill: gray;");
        // previousLabel.setFont(Font.font(18));

        Label currentTaskLabel = new Label(tasks.get(0).getName());
        currentTaskLabel.setFont(Font.font(36));
        currentTaskLabel.setWrapText(true);
        currentTaskLabel.setMaxWidth(560);
        currentTaskLabel.setAlignment(Pos.CENTER);

        Label remainingLabel = new Label(formatMMSS(remainingSeconds));
        remainingLabel.setFont(Font.font(26));

        VBox upcomingBox = new VBox(10);
        upcomingBox.setAlignment(Pos.TOP_CENTER);

        //Label upcomingTitle = new Label("Next:");
        //upcomingTitle.setFont(Font.font(18));

        // Controls
        Button pauseResumeBtn = new Button("Pause");
        Button restartTaskBtn = new Button("Restart Task");
        Button restartRoutineBtn = new Button("Restart Routine");
        Button quitBtn = new Button("Quit");
        Button skipBtn = new Button("Next Task");
        Button prevBtn = new Button("Previous Task");

        HBox controls = new HBox(
            12,
            prevBtn,
            pauseResumeBtn, 
            restartTaskBtn, 
            skipBtn, 
            restartRoutineBtn, 
            quitBtn);
        controls.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox root = new VBox(14,
                clockLabel,
                // previousLabel,
                currentTaskLabel,
                remainingLabel,
                new Separator(),
                upcomingBox,
                spacer,
                controls
        );
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(18));

        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Routine Runner — " + safeTitle(routine.getTitle()));
        stage.show();

        // initial render
        renderForCurrent(tasks, currentTaskLabel, remainingLabel, upcomingBox, prevBtn);

        // Timeline tick: always update clock; only decrement when running
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            clockLabel.setText(LocalTime.now().format(CLOCK_FMT));

            if (!running) return;

            remainingSeconds--;
            remainingLabel.setText(formatMMSS(remainingSeconds));

            if (remainingSeconds <= 0) {
                advanceToNextTaskOrFinish(skipBtn, stage, tasks, currentTaskLabel, remainingLabel, upcomingBox,
                        pauseResumeBtn, restartTaskBtn, prevBtn);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Pause/Resume
        pauseResumeBtn.setOnAction(e -> {
            running = !running;
            pauseResumeBtn.setText(running ? "Pause" : "Resume");
        });

        prevBtn.setOnAction(e -> {
            // can't go back before first task
            if (currentIndex <= 0) return;

            currentIndex--;

            currentTaskOriginalSeconds = tasks.get(currentIndex).getDurationSeconds();
            remainingSeconds = currentTaskOriginalSeconds;

            renderForCurrent(tasks, currentTaskLabel, remainingLabel, upcomingBox, prevBtn);
        });


        // Restart Task (current)
        restartTaskBtn.setOnAction(e -> {
            if (isFinished(tasks)) return;
            remainingSeconds = currentTaskOriginalSeconds;
            remainingLabel.setText(formatMMSS(remainingSeconds));
        });

        skipBtn.setOnAction(e -> {
            if (isFinished(tasks)) return;

            advanceToNextTaskOrFinish(
                    skipBtn, stage, tasks, currentTaskLabel, remainingLabel, upcomingBox,
                    pauseResumeBtn, restartTaskBtn, prevBtn
            );
        });


        // Restart Routine (from task 1)
        restartRoutineBtn.setOnAction(e -> {
            currentIndex = 0;
            currentTaskOriginalSeconds = tasks.get(0).getDurationSeconds();
            remainingSeconds = currentTaskOriginalSeconds;
            running = true;
            pauseResumeBtn.setText("Pause");

            skipBtn.setDisable(false);
            pauseResumeBtn.setDisable(false);
            restartTaskBtn.setDisable(false);
            prevBtn.setDisable(false);

            renderForCurrent(tasks, currentTaskLabel, remainingLabel, upcomingBox, prevBtn);
        });

        // Quit back to routine list
        quitBtn.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            ViewTaskListScreen.show(stage);
        });
    }

    private static void advanceToNextTaskOrFinish(
            Button skipBtn,
            Stage stage,
            List<TaskItem> tasks,
            Label currentTaskLabel,
            Label remainingLabel,
            VBox upcomingBox,
            Button pauseResumeBtn,
            Button restartTaskBtn,
            Button prevBtn
    ) {
        currentIndex++;

        if (currentIndex >= tasks.size()) {
            // Finished
            running = false;
            //previousLabel.setText(tasks.get(tasks.size() - 1).getName());
            currentTaskLabel.setText("Routine Complete");
            remainingLabel.setText("00:00");
            upcomingBox.getChildren().clear();

            pauseResumeBtn.setDisable(true);
            restartTaskBtn.setDisable(true);
            skipBtn.setDisable(true);
            prevBtn.setDisable(true);
            return;
        }

        currentTaskOriginalSeconds = tasks.get(currentIndex).getDurationSeconds();
        remainingSeconds = currentTaskOriginalSeconds;

        renderForCurrent(tasks, currentTaskLabel, remainingLabel, upcomingBox, prevBtn);
    }
private static void renderForCurrent(
        List<TaskItem> tasks,
        Label currentTaskLabel,
        Label remainingLabel,
        VBox upcomingBox,
        Button prevBtn
) {
    prevBtn.setDisable(currentIndex==0);
    // Current (big label at top)
    currentTaskLabel.setText(tasks.get(currentIndex).getName());

    // Remaining (MM:SS under current)
    remainingLabel.setText(formatMMSS(remainingSeconds));

    // Full routine list (completed = gray, current = bold, future = normal)
    upcomingBox.getChildren().clear();

    for (int i = 0; i < tasks.size(); i++) {
        TaskItem t = tasks.get(i);

        Label name = new Label(t.getName());
        Label dur = new Label(formatMMSS(t.getDurationSeconds()));

        name.setMaxWidth(420);
        name.setWrapText(true);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox row = new HBox(10, name, gap, dur);
        row.setMaxWidth(560);
        row.setAlignment(Pos.CENTER_LEFT);

        if (i < currentIndex) {
            // completed
            name.setStyle("-fx-text-fill: gray;");
            dur.setStyle("-fx-text-fill: gray;");
        } else if (i == currentIndex) {
            // current
            name.setStyle("-fx-font-weight: bold;");
            dur.setStyle("-fx-font-weight: bold;");
        }
        // future tasks: default styling

        upcomingBox.getChildren().add(row);
    }
}


    private static boolean isFinished(List<TaskItem> tasks) {
        return currentIndex >= tasks.size();
    }

    private static String formatMMSS(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String mm = (minutes < 10) ? ("0" + minutes) : String.valueOf(minutes);
        String ss = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        return mm + ":" + ss;
    }

    private static String safeTitle(String s) {
        return (s == null || s.isBlank()) ? "Routine" : s.trim();
    }
}
