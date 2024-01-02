
package com.borges.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthorizationDialogController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField codeTextField;

    private Stage dialogStage;
    private boolean confirmed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void confirmAction() {
        confirmed = true;
        dialogStage.close();
    }

    public String getEmail() {
        return emailTextField.getText();
    }

    public String getCode() {
        return codeTextField.getText();
    }
}
