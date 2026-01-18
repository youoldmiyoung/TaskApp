package com.mia.taskapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ViewTaskListScreen {

    public static void show(Stage stage) {
        RoutineStore.initIfNeeded();

        Label title = new Label("Saved Routines");

        ListView<Routine> listView = new ListView<>();
        listView.getItems().setAll(RoutineStore.getRoutines());

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Routine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " (" + item.getTasks().size() + " tasks)");
                }
            }
        });

        Button startBtn = new Button("Start");
        Button deleteBtn = new Button("Delete");
        Button createBtn = new Button("Create New");
        Button backBtn = new Button("Back");

        startBtn.setOnAction(e -> {
            Routine selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                RoutineRunnerScreen.show(stage, selected);
            }
        });

        deleteBtn.setOnAction(e -> {
            Routine selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                listView.getItems().remove(selected); // view-only
            }
        });

        createBtn.setOnAction(e -> CreateTaskListScreen.show(stage));
        backBtn.setOnAction(e -> Home.show(stage));

        startBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());

        HBox buttons = new HBox(10, startBtn, deleteBtn, createBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, listView, buttons);
        root.setPadding(new Insets(16));
        root.setPrefSize(640, 480);

        stage.setTitle("View Task Lists");
        stage.setScene(new Scene(root, 640, 480));
        stage.show();
    }
}
