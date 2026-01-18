package com.mia.taskapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskListScreen {

    public static void show(Stage stage) {
        RoutineStore.initIfNeeded();

        Label title = new Label("Create Task List");

        TextField routineNameField = new TextField();
        routineNameField.setPromptText("Routine name (e.g., Morning Routine)");

        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Task name");

        Spinner<Integer> minutesSpinner = new Spinner<>(0, 999, 5);
        minutesSpinner.setEditable(true);

        Spinner<Integer> secondsSpinner = new Spinner<>(0, 59, 0);
        secondsSpinner.setEditable(true);

        Button addTaskBtn = new Button("Add Task");
        addTaskBtn.setDefaultButton(true);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        ListView<TaskItem> taskListView = new ListView<>();
        taskListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TaskItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getName() + " — " + formatMMSS(item.getDurationSeconds()));
            }
        });

        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            TaskItem sel = taskListView.getSelectionModel().getSelectedItem();
            if (sel != null) taskListView.getItems().remove(sel);
        });
        removeBtn.disableProperty().bind(taskListView.getSelectionModel().selectedItemProperty().isNull());

        addTaskBtn.setOnAction(e -> {
            errorLabel.setText("");

            String taskName = taskNameField.getText() == null ? "" : taskNameField.getText().trim();
            int mins = minutesSpinner.getValue() == null ? 0 : minutesSpinner.getValue();
            int secs = secondsSpinner.getValue() == null ? 0 : secondsSpinner.getValue();
            int total = mins * 60 + secs;

            if (taskName.isEmpty()) {
                errorLabel.setText("Task name is required.");
                return;
            }
            if (total <= 0) {
                errorLabel.setText("Duration must be > 0.");
                return;
            }

            taskListView.getItems().add(new TaskItem(taskName, total));
            taskNameField.clear();
            minutesSpinner.getValueFactory().setValue(0);
            secondsSpinner.getValueFactory().setValue(0);
        });

        Button saveBtn = new Button("Save Routine");
        Button startBtn = new Button("Start");
        Button backBtn = new Button("Back");

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            String routineName = routineNameField.getText() == null ? "" : routineNameField.getText().trim();

            if (routineName.isEmpty()) {
                errorLabel.setText("Routine name is required.");
                return;
            }
            if (taskListView.getItems().isEmpty()) {
                errorLabel.setText("Add at least 1 task.");
                return;
            }

            List<TaskItem> tasks = new ArrayList<>(taskListView.getItems());
            RoutineStore.addRoutine(new Routine(routineName, tasks));

            // go back to view
            ViewTaskListScreen.show(stage);
        });

        startBtn.setOnAction(e -> {
            errorLabel.setText("");
            String routineName = routineNameField.getText() == null ? "" : routineNameField.getText().trim();
            if (routineName.isEmpty()) routineName = "Untitled Routine";

            if (taskListView.getItems().isEmpty()) {
                errorLabel.setText("Add at least 1 task.");
                return;
            }

            Routine temp = new Routine(routineName, new ArrayList<>(taskListView.getItems()));
            RoutineRunnerScreen.show(stage, temp);
        });

        backBtn.setOnAction(e -> ViewTaskListScreen.show(stage));

        HBox durationRow = new HBox(8,
                new Label("Minutes:"), minutesSpinner,
                new Label("Seconds:"), secondsSpinner
        );
        durationRow.setAlignment(Pos.CENTER_LEFT);

        HBox taskControls = new HBox(10, addTaskBtn, removeBtn);
        taskControls.setAlignment(Pos.CENTER_LEFT);

        HBox bottomControls = new HBox(10, saveBtn, startBtn, backBtn);
        bottomControls.setAlignment(Pos.CENTER);

        VBox root = new VBox(12,
                title,
                new Label("Routine Name:"), routineNameField,
                new Label("Add Task:"), taskNameField,
                durationRow,
                taskControls,
                new Label("Tasks:"),
                taskListView,
                errorLabel,
                bottomControls
        );

        root.setPadding(new Insets(16));
        root.setPrefSize(640, 480);

        stage.setTitle("Create Task List");
        stage.setScene(new Scene(root, 640, 480));
        stage.show();
    }

    private static String formatMMSS(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
