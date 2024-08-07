package com.borges.model;

import java.awt.Desktop;
import java.net.URI;


import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;


import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.scene.control.TextInputDialog;


import java.security.GeneralSecurityException;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import static com.borges.controller.NfeController.incrementAndReturnID;


@SuppressWarnings("ALL")
public class SheetsServices {


    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials2.json";


    public Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        InputStream in = SheetsServices.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential("user");
        if (credential != null && (credential.getAccessToken() != null || credential.getRefreshToken() != null)) {
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() < 30) {
                credential.refreshToken();
            }

            return credential;
        }

        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
        String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
        String url = authorizationUrl.setRedirectUri(redirectUri).build();
        try {
            URI link = new URI(url);
            Desktop.getDesktop().browse(link);
        } catch (Exception erro) {
            System.out.println("ERRO");
        }
        openAuthorizationDialog(flow, redirectUri);
        return credential;
    }

    public Credential openAuthorizationDialog(GoogleAuthorizationCodeFlow flow, String redirectUri) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Authorization");
        dialog.setHeaderText("Digite o email e código de autorização");
        dialog.setContentText("Email:");




        TextInputDialog dialogCode = new TextInputDialog();
        dialogCode.setTitle("Authorization");
        dialogCode.setHeaderText("Digite o código de autorização");
        dialogCode.setContentText("Código de Autorização:");


        Optional<String> resultCode = dialogCode.showAndWait();
        String code = resultCode.orElse("");

        try {

            GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            return flow.createAndStoreCredential(response, "user");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String... args) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        SheetsServices sheetsServices = new SheetsServices();
        Sheets service = sheetsServices.getSheetsService(HTTP_TRANSPORT);

        final String spreadsheetId = "1_5V7naaPADDvRJuY5SAF5BrelNhuZ558jKXzyrudPJg";
        final String range = "NOTAS APP";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List<Object> row : values) {

                System.out.printf("%s, %s\n", row.get(0), row.get(4));
            }
        }
        adicionarDadosFixos(service,
                spreadsheetId,
                "ChaveNFE",
                "NOTA",
                "MARCA",
                "Name",
                "formattedDate",
                "lojaSelecionada",
                "formattedDateEmissao",
                Long.parseLong("diffEmDays"));

    }

    public String getSpreadsheetId() {
        return "1_5V7naaPADDvRJuY5SAF5BrelNhuZ558jKXzyrudPJg";
    }


    public Sheets getSheetsService(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException, GeneralSecurityException {
        Credential credential = authorize(HTTP_TRANSPORT);


        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void adicionarDadosFixos(Sheets service,
                                           String spreadsheetId,
                                           String chaveNFe,
                                           String numeroNota,
                                           String name,
                                           String formattedDate,
                                           String marcaSelecionada,
                                           String lojaSelecionada,
                                           String formattedDateEmissao,
                                           long diffEmDias) throws IOException {

        int currentID = incrementAndReturnID(service, spreadsheetId, "A2827");
        ValueRange body = new ValueRange()
                .setValues(List.of(
                        Arrays.asList(
                                currentID,
                                formattedDateEmissao,
                                formattedDate,
                                diffEmDias,
                                chaveNFe,
                                numeroNota,
                                "x",
                                marcaSelecionada,
                                lojaSelecionada,
                                "NÃO",
                                name)
                ));

        try {
            AppendValuesResponse result = service.spreadsheets().values()
                    .append(spreadsheetId, "NOTAS APP", body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            System.out.println("Dados adicionados: " + result.getUpdates().getUpdatedCells() + " células adicionadas.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
