package com.borges.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.borges.controller.NfeController;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class BorgesApplication extends Application {

    private static Stage stage;

    private static Scene loginScene;
    private static Scene nfeScene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        primaryStage.setTitle("Login");

        Parent fxmlLogin = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/borges/login_screen.fxml")));
        loginScene = new Scene(fxmlLogin, 600, 400);

        Parent fxmlNfe = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/borges/BorgesView.fxml")));
        nfeScene = new Scene(fxmlNfe, 700, 400);

        primaryStage.setScene(nfeScene);
        primaryStage.show();

        stage.setOnCloseRequest(e -> {
            if (NfeController.onCloseQuery()) {
                System.exit(0);
            } else {
                e.consume();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public static void changeScreen(String scr) {
        switch (scr) {
            case "Login":
                stage.setScene(loginScene);
                break;
            case "nfe":
                stage.setScene(nfeScene);
                break;
        }
    }
}

