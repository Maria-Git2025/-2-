package ru.mypackage.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mypackage.model.Client;
import ru.mypackage.model.User;
import ru.mypackage.view.ClientView;
import ru.mypackage.database.ConnectionDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;

// Контроллер для личного кабинета клиента
public class ClientViewController {
    private final ClientView view;
    private final Client client;
    private final User user;

    public ClientViewController(ClientView view, Client client, User user) {
        this.view = view;
        this.client = client;
        this.user = user;
        fillProfile();
        loadClientContracts();
        this.view.setSaveProfileHandler(new SaveProfileHandler());
        this.view.setEditUserHandler(new EditUserHandler());
    }

    // Заполняет поля профиля клиента
    private void fillProfile() {
        view.surnameField.setText(client.getSurname());
        view.nameField.setText(client.getName());
        view.patronymicField.setText(client.getPatronymic());
        view.dobField.setText(client.getDateOfBirth());
        view.phoneField.setText(client.getPhoneNumber());
    }

    // Загружает только свои договоры в таблицу
    private void loadClientContracts() {
        try {
            javafx.collections.ObservableList<ru.mypackage.model.Contract> contracts = javafx.collections.FXCollections.observableArrayList();
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Contracts WHERE id_client = ?");
            stmt.setInt(1, client.getId());
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int idBranch = rs.getInt("id_branch");
                int idAgent = rs.getInt("id_agent");
                int idType = rs.getInt("id_insurance_type");
                String dateSigned = rs.getString("date_signed");
                String amount = rs.getString("insurance_amount");
                String rate = rs.getString("tariff_rate");
                // Получаем название филиала
                String branchName = "";
                java.sql.PreparedStatement bstmt = conn.prepareStatement("SELECT name FROM Branches WHERE id = ?");
                bstmt.setInt(1, idBranch);
                java.sql.ResultSet brs = bstmt.executeQuery();
                if (brs.next()) branchName = brs.getString("name");
                brs.close(); bstmt.close();
                // Получаем ФИО агента
                String agentFio = "";
                java.sql.PreparedStatement astmt = conn.prepareStatement("SELECT surname, name, patronymic FROM Insurance_Agents WHERE id = ?");
                astmt.setInt(1, idAgent);
                java.sql.ResultSet ars = astmt.executeQuery();
                if (ars.next()) agentFio = ars.getString("surname") + " " + ars.getString("name") + " " + ars.getString("patronymic");
                ars.close(); astmt.close();
                // Получаем название вида страхования
                String typeName = "";
                java.sql.PreparedStatement tstmt = conn.prepareStatement("SELECT name FROM Insurance_Types WHERE id = ?");
                tstmt.setInt(1, idType);
                java.sql.ResultSet trs = tstmt.executeQuery();
                if (trs.next()) typeName = trs.getString("name");
                trs.close(); tstmt.close();
                contracts.add(new ru.mypackage.model.Contract(id, idBranch, 0, idAgent, idType, dateSigned, amount, rate, branchName, agentFio, typeName));
            }
            rs.close(); stmt.close(); conn.close();
            view.contractsTableView.table.setItems(contracts);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Обработчик сохранения профиля клиента
    private class SaveProfileHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            // Получаем поля из view (предполагается, что они публичные или есть геттеры)
            TextField surnameField = view.surnameField;
            TextField nameField = view.nameField;
            TextField patronymicField = view.patronymicField;
            TextField dobField = view.dobField;
            TextField phoneField = view.phoneField;
            client.setSurname(surnameField.getText().trim());
            client.setName(nameField.getText().trim());
            client.setPatronymic(patronymicField.getText().trim());
            client.setDateOfBirth(dobField.getText().trim());
            client.setPhoneNumber(phoneField.getText().trim());
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Clients SET surname=?, name=?, patronymic=?, date_of_birth=?, phone_number=? WHERE id=?");
                stmt.setString(1, client.getSurname());
                stmt.setString(2, client.getName());
                stmt.setString(3, client.getPatronymic());
                stmt.setString(4, client.getDateOfBirth());
                stmt.setString(5, client.getPhoneNumber());
                stmt.setInt(6, client.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                showError("Ошибка при сохранении профиля: " + ex.getMessage());
            }
        }
    }

    // Обработчик открытия окна изменения пользователя
    private class EditUserHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Изменить регистрационные данные");
            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
            TextField loginField = new TextField(user.getLogin());
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Новый пароль");
            javafx.scene.control.Button saveUserBtn = new javafx.scene.control.Button("Сохранить");
            saveUserBtn.setOnAction(new SaveUserHandler(loginField, passwordField, dialog));
            vbox.getChildren().addAll(new javafx.scene.control.Label("Логин:"), loginField, new javafx.scene.control.Label("Пароль:"), passwordField, saveUserBtn);
            javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 300, 180);
            dialog.setScene(scene);
            dialog.showAndWait();
        }
    }

    // Обработчик сохранения пользователя
    private class SaveUserHandler implements EventHandler<ActionEvent> {
        private final TextField loginField;
        private final PasswordField passwordField;
        private final Stage dialog;
        public SaveUserHandler(TextField loginField, PasswordField passwordField, Stage dialog) {
            this.loginField = loginField;
            this.passwordField = passwordField;
            this.dialog = dialog;
        }
        @Override
        public void handle(ActionEvent e) {
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Users SET login=?, password=? WHERE id=?");
                stmt.setString(1, loginField.getText().trim());
                String newPassword = passwordField.getText().trim();
                stmt.setString(2, newPassword);
                stmt.setInt(3, user.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
                user.setLogin(loginField.getText().trim());
                user.setPassword(newPassword);
                dialog.close();
            } catch (Exception ex) {
                showError("Ошибка при сохранении пользователя: " + ex.getMessage());
            }
        }
    }

    // Показывает окно ошибки
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 