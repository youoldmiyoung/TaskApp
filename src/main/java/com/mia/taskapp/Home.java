package com.mia.taskapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Home {

    public static void show(Stage stage) {

        // Main buttons
        Button newTaskListBtn = new Button("New Tasks List");
        Button viewTaskListBtn = new Button("View Task List");
        Button singleTaskBtn = new Button("Single Task");

        newTaskListBtn.setOnAction(e -> NewTaskListScreen.show(stage));
        viewTaskListBtn.setOnAction(e -> ViewTaskListScreen.show(stage));
        singleTaskBtn.setOnAction(e -> SingleTaskScreen.show(stage));

        // Slightly smaller square buttons
        int mainSize = 130;
        newTaskListBtn.setPrefSize(mainSize, mainSize);
        viewTaskListBtn.setPrefSize(mainSize, mainSize);
        singleTaskBtn.setPrefSize(mainSize, mainSize);

        // Logout button (small & rectangular)
        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefSize(100, 30);
        logoutBtn.setOnAction(e -> Login.show(stage));

        // Spacer to push logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Layout
        VBox root = new VBox(18);
        root.setAlignment(Pos.TOP_CENTER);
        root.setTranslateY(30); // move content up slightly

        root.getChildren().addAll(
                newTaskListBtn,
                viewTaskListBtn,
                singleTaskBtn,
                spacer,
                logoutBtn
        );

        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Home");
        stage.setScene(scene);
        stage.show();
    }
}
