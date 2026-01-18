package com.mia.taskapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NewTaskListScreen {

    private static final ObservableList<TaskItem> workingTasks =
            FXCollections.observableArrayList();

    public static void show(Stage stage) {
        workingTasks.clear();

        Label title = new Label("Create New Task List");
        title.setStyle("-fx-font-size: 22px;");

        TextField routineNameField = new TextField();
        routineNameField.setPromptText("Routine name (e.g. Morning Routine)");

        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Task name");

        Spinner<Integer> minutesSpinner = new Spinner<>(1, 600, 5);
        minutesSpinner.setEditable(true);

        Button addTaskBtn = new Button("Add Task");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // List view for tasks (supports reorder/remove)
        ListView<TaskItem> listView = new ListView<>(workingTasks);
        listView.setPrefHeight(260);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TaskItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label name = new Label(item.getName());
                Label dur = new Label(formatMMSS(item.getDurationSeconds()));

                Region gap = new Region();
                HBox.setHgrow(gap, Priority.ALWAYS);

                Button up = new Button("↑");
                Button down = new Button("↓");
                Button remove = new Button("✕");

                int index = getIndex();

                up.setDisable(index <= 0);
                down.setDisable(index >= workingTasks.size() - 1);

                up.setOnAction(e -> swap(index, index - 1));
                down.setOnAction(e -> swap(index, index + 1));
                remove.setOnAction(e -> workingTasks.remove(item));

                HBox row = new HBox(10, name, gap, dur, up, down, remove);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(4));

                setGraphic(row);
            }
        });

        addTaskBtn.setOnAction(e -> {
            String taskName = taskNameField.getText().trim();
            Integer minutes = minutesSpinner.getValue();

            if (taskName.isEmpty()) {
                errorLabel.setText("Task name required.");
                return;
            }

            workingTasks.add(new TaskItem(taskName, minutes * 60));
            taskNameField.clear();
            errorLabel.setText("");
        });

        Button saveBtn = new Button("Save Routine");
        Button backBtn = new Button("Back");

        saveBtn.setOnAction(e -> {
            String routineName = routineNameField.getText().trim();

            if (routineName.isEmpty() || workingTasks.isEmpty()) {
                errorLabel.setText("Routine name and at least one task required.");
                return;
            }

            Routine routine = new Routine(routineName, java.util.List.copyOf(workingTasks));
            RoutineStore.addRoutine(routine);

            ViewTaskListScreen.show(stage);
        });

        backBtn.setOnAction(e -> Home.show(stage));

        HBox taskInputRow = new HBox(10, taskNameField, minutesSpinner, addTaskBtn);
        taskInputRow.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10, saveBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(14,
                title,
                routineNameField,
                new Separator(),
                taskInputRow,
                listView,
                errorLabel,
                buttons
        );
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        stage.setTitle("New Task List");
        stage.setScene(new Scene(root, 640, 480));
        stage.show();
    }

    private static void swap(int i, int j) {
        if (i < 0 || j < 0 || i >= workingTasks.size() || j >= workingTasks.size()) return;
        TaskItem tmp = workingTasks.get(i);
        workingTasks.set(i, workingTasks.get(j));
        workingTasks.set(j, tmp);
    }

    private static String formatMMSS(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String mm = (minutes < 10) ? ("0" + minutes) : String.valueOf(minutes);
        String ss = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        return mm + ":" + ss;
    }
}
