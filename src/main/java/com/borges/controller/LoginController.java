package com.borges.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    protected void btnNovoAction(ActionEvent e) {
        System.out.println("funcionou");


        BorgesApplication.changeScreen("nfe");
    }
}
