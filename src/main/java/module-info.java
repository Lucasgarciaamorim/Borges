module com.borges.borges {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.api.client.auth;

    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.sheets;
    requires com.google.api.client.extensions.java6.auth;
     requires java.desktop;

    opens com.borges.model to javafx.fxml;
    opens com.borges to javafx.fxml;
    exports com.borges.controller;
    opens com.borges.controller to javafx.fxml;
}