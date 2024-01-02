package com.borges.controller;

import com.borges.model.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.borges.model.SheetsServices.adicionarDadosFixos;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class NfeController extends BorgesApplication {
    private final SheetsServices sheetsServices;
    @FXML
    private ComboBox<String> cbLoja;
    @FXML
    private DatePicker dtDatePicker;
    @FXML
    private DatePicker dtDatePickerEmissao;
    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<String> cbMarca;
    @FXML
    private TextField txtChaveDanfe;
    @FXML
    private Button btnClear;
    @FXML
    private TextField txtAuthentic;
    @FXML
    private Button enviar;

    public NfeController() {
        sheetsServices = new SheetsServices();
    }

    public boolean validateExtractNFeNumberAndCNPJ(String chave, StringBuilder numeroNotaFiscal, StringBuilder cnpj) {
        if (chave.length() != 44) {
            return false;
        }
        if (!chave.matches("\\d+")) {
            return false;
        }
        int digitoVerificador = Integer.parseInt(chave.substring(43));
        String chaveSemDigito = chave.substring(0, 43);

        int peso = 2;
        int soma = 0;

        for (int i = chaveSemDigito.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(chaveSemDigito.substring(i, i + 1));
            soma += digito * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }

        int resto = soma % 11;
        int digitoCalculado = 11 - resto;

        if (digitoCalculado >= 10) {
            digitoCalculado = 0;
        }

        if (digitoCalculado == digitoVerificador) {
            numeroNotaFiscal.append(chave, 25, 34);
            cnpj.append(chave, 6, 20);
            return true;
        }
        return false;
    }

    Sheets service = null;

    public void autenticationAuth() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        service = sheetsServices.getSheetsService(httpTransport);
    }


    //    @FXML
//    public void txtAuthenticOnAction(ActionEvent event) {
//
//    }
    @FXML
    public void enviarOnAction(ActionEvent event) {
        String txtAutentic = txtAuthentic.getText();

    }

    @FXML
    void loginOnAction(ActionEvent event) {
        try {
            autenticationAuth();
        } catch (IOException e) {
            System.out.println("Alerta");

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthorizationDialogController openAuthorizationDialog(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/borges/AuthorizationDialog.fxml"));
        Stage dialogStage = new Stage();
        Parent root = loader.load();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.setTitle("Authorization Dialog");

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        AuthorizationDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        dialogStage.showAndWait();

        return controller;
    }

    @FXML
    void confirmEnvio(ActionEvent event) {

        try {
            autenticationAuth();

            String spreadsheetId = sheetsServices.getSpreadsheetId();

            String chaveNFe = txtChaveDanfe.getText();
            StringBuilder numeroNotaFiscal = new StringBuilder();
            StringBuilder cnpjEmpresa = new StringBuilder();
            if (!validateExtractNFeNumberAndCNPJ(chaveNFe, numeroNotaFiscal, cnpjEmpresa)) {

                exibirAlerta("Chave da NFe inválida", "A chave da NFe é inválida. Verifique e tente novamente.", Alert.AlertType.ERROR);
                return;
            }

            String marcaSelecionada = cbMarca.getValue();
            if (marcaSelecionada == null) {
                exibirAlerta("Verificação de Dados", "Por favor Selecione uma Marca", Alert.AlertType.ERROR);
                return;
            }
            String lojaSelecionada = cbLoja.getValue();
            if (lojaSelecionada == null) {
                exibirAlerta("Verificação de Dados", "Por favor, selecione uma Loja.", Alert.AlertType.ERROR);
                return;
            }

            String Name = txtName.getText().trim().toUpperCase();
            if (Name.isEmpty()) {
                exibirAlerta("Erro", "Nome em branco. Por favor, insira um nome.", Alert.AlertType.ERROR);
                return;
            }
            String numeroNota = extractNFeNumber(chaveNFe);

            LocalDate selectedDate = dtDatePicker.getValue();
            if (selectedDate == null) {
                exibirAlerta("Verificação de Dados ", "Por favor, Selecione a data de chegada corretamente", Alert.AlertType.ERROR);
                return;
            }

            LocalDate selectDateEmissao = dtDatePickerEmissao.getValue();
            if (selectDateEmissao == null) {
                exibirAlerta("Verificação de Dados ", "Por favor, Selecione a data de emissão corretamente", Alert.AlertType.ERROR);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = selectedDate.format(formatter);

            DateTimeFormatter formatterEmissao = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDateEmissao = selectDateEmissao.format(formatterEmissao);

            LocalDate data1 = LocalDate.parse(formattedDate, formatter);
            LocalDate data2 = LocalDate.parse(formattedDateEmissao, formatterEmissao);

            long diffEmDias = ChronoUnit.DAYS.between(data2, data1);


            if (numeroNota != null) {
                adicionarDadosFixos(service, spreadsheetId, chaveNFe, numeroNota, Name,
                        formattedDate, marcaSelecionada, lojaSelecionada, formattedDateEmissao, diffEmDias
                );

                txtChaveDanfe.clear();
                txtChaveDanfe.requestFocus();


                exibirAlerta("Nota enviada", "Os dados foram enviados com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                exibirAlerta("Nota não enviada", "Não foi possível extrair os dados da NFe. Verifique a chave da NFe e tente novamente.", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            exibirAlerta("Erro", "Ocorreu um erro ao enviar a nota: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void clearOnAction(ActionEvent event) {
        txtName.clear();
        txtChaveDanfe.clear();
        cbLoja.setValue(null);
        cbMarca.setValue(null);
        dtDatePickerEmissao.setValue(null);
        dtDatePicker.setValue(null);

    }

    private void exibirAlerta(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String extractNFeNumber(String chave) {

        if (chave.length() == 44 && chave.matches("\\d+")) {
            return chave.substring(25, 34);
        }
        return null;
    }


    public void initialize() {


        ObservableList<String> marcaOptions = FXCollections.observableArrayList(
                "ACTIVITA", "ADDAN", "AKAZZO", "AKAZZO(NEFESH)", "BEIRA RIO", "BLITZZ",
                "BOTINHO", "BOX", "CARTAGO", "FILA", "GRENDENE", "HOST", "IZALU", "KANXA", "LICITTO",
                "LOOK FASHION", "MENDII", "MISSISIPI", "MIZUNO", "MODA LEVE", "MODARE", "MOLECA",
                "MOLEKINHA", "MRS ATACADO", "NEFESH", "OLYMPIKUS - VULCABRAS", "ONITY",
                "OPEN", "ORTOBESSA", "PARTHENON", "PEGADA", "PLUMAX", "POLO ENERGY",
                "RAYON", "REED", "SELENE", "SERENITY", "STAR CHIC", "TOOPER", "VIA VIP",
                "VIVANZ", "VIZZANO", "VULCABRAS", "NÃO LISTADO");

        cbMarca.setItems(marcaOptions);

        ObservableList<String> lojaOptions = FXCollections.observableArrayList(
                "001 - LOJA 152", "002 - LOJA 216", "003 - LOJA 27",
                "004 - LOJA 93", "005 - LOJA 238", "006 - LOJA 851",
                "007 - LOJA 29", "008 - LOJA 92", "009 - LOJA 356",
                "010 - LOJA GAB", "013 - LOJA 22", "014 - LOJA 256",
                "016 - LOJA 16", "017 - LOJA 57", "018 - LOJA 37",
                "019 - LOJA 322", "020 - LOJA 391"
        );
        cbLoja.setItems((lojaOptions));
    }
    public static boolean onCloseQuery() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Exit");
        alerta.setHeaderText("Deseja Sair?");
        ButtonType botaoNao = ButtonType.NO;
        ButtonType botaoSim = ButtonType.YES;
        alerta.getButtonTypes().setAll(botaoSim, botaoNao);
        Optional<ButtonType> opcaoClicada = alerta.showAndWait();

        return opcaoClicada.get() == botaoSim;

    }

    public static int incrementAndReturnID(Sheets service, String spreadsheetId, String range) throws IOException {
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        int currentID = 0;
        if (values != null && !values.isEmpty() && values.get(0) != null && !values.get(0).isEmpty()) {
            currentID = Integer.parseInt(values.get(0).get(0).toString());
        }

        int newID = currentID + 1;

        ValueRange idUpdate = new ValueRange().setValues(List.of(List.of(newID)));
        service.spreadsheets().values().update(spreadsheetId, range, idUpdate)
                .setValueInputOption("RAW")
                .execute();

        return newID;
    }

}