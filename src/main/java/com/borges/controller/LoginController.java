package com.borges.controller;

import com.borges.model.SheetsServices;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.net.URISyntaxException;
import java.security.GeneralSecurityException;


public class LoginController extends SheetsServices {
    private final SheetsServices sheetsServices;
    @FXML
    public TextField txtEmail;
    public TextField txtAuthentication;
    @FXML
    public Button loginButton;
    @FXML
    private Label loginMessageLabel;

    public LoginController() {
        sheetsServices = new SheetsServices();
    }

    Sheets service = null;

    public void autenticationAuth() throws GeneralSecurityException, IOException, URISyntaxException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        service = sheetsServices.getSheetsService(httpTransport);
    }


    public void loginButtonOnAction(ActionEvent event) throws GeneralSecurityException, IOException, URISyntaxException {


        String txtEmailLabel = txtEmail.getText();

        if (!txtEmailLabel.isEmpty()) {
            autenticationAuth();

            BorgesApplication.changeScreen("nfe");


        } else System.out.println("não funcionou");
        loginMessageLabel.setText("Digite o Email");


    }

    @FXML
    public Button cancelButton;


    public void cancelButtonOnAction(ActionEvent event) throws URISyntaxException, IOException, GeneralSecurityException {

    }

    @FXML
    protected void btnNovoAction(ActionEvent e) {
        System.out.println("funcionou");
        BorgesApplication.changeScreen("nfe");
    }


}
