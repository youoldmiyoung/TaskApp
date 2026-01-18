package com.mia.taskapp;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Login {

    private static final String CORRECT_USERNAME = "admin";
    private static final String CORRECT_PASSWORD = "pass123";

    public static void show(Stage stage) {
        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        Label resultLabel = new Label();

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);

        loginButton.setOnAction(event -> {
            if (
                usernameField.getText().equals(CORRECT_USERNAME) &&
                passwordField.getText().equals(CORRECT_PASSWORD)
            ) {
                Home.show(stage);
            } else {
                resultLabel.setText("Login failed. Please check your credentials.");
            }
        });

        VBox root = new VBox(10,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                loginButton, resultLabel
        );

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Login");
        stage.show();
    }
}
