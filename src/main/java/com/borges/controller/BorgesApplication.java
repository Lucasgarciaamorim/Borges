package com.borges.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.borges.controller.NfeController;

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

        primaryStage.setScene(loginScene);
        primaryStage.show();

        stage.setOnCloseRequest(e -> {
            if (NfeController.onCloseQuery()) {
                System.exit(0);
            } else {
                e.consume();
            }
        });
    }

    public static void main(String[] args) {
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
            // adicione outros casos conforme necessário
        }
    }
}


//    private Scene createSplashScene(Stage stage) {
//        Image logo = new Image(Objects.requireNonNull(getClass().getResource("/Images/logo.jpg")).toExternalForm());
//        ImageView logoView = new ImageView(logo);
//        StackPane root = new StackPane(logoView);
//        Scene splashScene = new Scene(root, 400, 200);
//
//        Task<Void> initializationTask = new Task<>() {
//            @Override
//            protected Void call() {
//                return null;
//            }
//        };
//
//        initializationTask.setOnSucceeded(event -> Platform.runLater(() -> loadMainScene(stage)));
//
//        new Thread(initializationTask).start();
//
//        return splashScene;
//    }
//
//    private void loadMainScene(Stage stage) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/borges/BorgesView.fxml"));
//            Scene scene = new Scene(fxmlLoader.load());
//
//            stage.setScene(scene);
//            stage.setTitle("Envio de Nota Fiscal");
//            stage.resizableProperty().setValue(Boolean.FALSE);
//
//            stage.setOnCloseRequest(e -> {
//                if (NfeController.onCloseQuery()) {
//                    System.exit(0);
//                } else {
//                    e.consume();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
