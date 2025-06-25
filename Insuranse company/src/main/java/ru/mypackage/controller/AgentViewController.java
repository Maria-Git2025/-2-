package ru.mypackage.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mypackage.model.Agent;
import ru.mypackage.model.User;
import ru.mypackage.view.AgentView;
import ru.mypackage.database.ConnectionDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;

// Контроллер для личного кабинета агента
public class AgentViewController {
    private final AgentView view;
    private final Agent agent;
    private final User user;

    public AgentViewController(AgentView view, Agent agent, User user) {
        this.view = view;
        this.agent = agent;
        this.user = user;
        fillProfile();
        loadAgentTables();
        this.view.setSaveProfileHandler(new SaveProfileHandler());
        this.view.setEditUserHandler(new EditUserHandler());

        ru.mypackage.controller.ClientTableController clientTableController = new ru.mypackage.controller.ClientTableController(null, this.view.clientTableView);
        ru.mypackage.controller.ContractsTableController contractsTableController = new ru.mypackage.controller.ContractsTableController(null, this.view.contractsTableView);
        this.view.setClientAddHandler(clientTableController.new AddClientHandler());
        this.view.setClientEditHandler(clientTableController.new EditClientHandler());
        this.view.setClientDeleteHandler(clientTableController.new DeleteClientHandler());
        this.view.setContractAddHandler(contractsTableController.new AddContractHandler());
        this.view.setContractEditHandler(contractsTableController.new EditContractHandler());
        this.view.setContractDeleteHandler(contractsTableController.new DeleteContractHandler());
    }

    // Заполняет поля профиля агента
    private void fillProfile() {
        view.surnameField.setText(agent.getSurname());
        view.nameField.setText(agent.getName());
        view.patronymicField.setText(agent.getPatronymic());
        view.addressField.setText(agent.getAddress());
        view.phoneField.setText(agent.getPhoneNumber());
    }

    // Загружает данные
    private void loadAgentTables() {
        // --- Клиенты агента ---
        try {
            javafx.collections.ObservableList<ru.mypackage.model.Client> clients = javafx.collections.FXCollections.observableArrayList();
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT c.* FROM Clients c JOIN Contracts ct ON c.id = ct.id_client WHERE ct.id_agent = ?"
            );
            stmt.setInt(1, agent.getId());
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int idUser = rs.getInt("id_user");
                String surname = rs.getString("surname");
                String name = rs.getString("name");
                String patronymic = rs.getString("patronymic");
                String dob = rs.getString("date_of_birth");
                String phone = rs.getString("phone_number");
                clients.add(new ru.mypackage.model.Client(id, idUser, surname, name, patronymic, dob, phone));
            }
            rs.close(); stmt.close(); conn.close();
            view.clientTableView.table.setItems(clients);
        } catch (Exception ex) { ex.printStackTrace(); }
        // --- Договоры агента ---
        try {
            javafx.collections.ObservableList<ru.mypackage.model.Contract> contracts = javafx.collections.FXCollections.observableArrayList();
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Contracts WHERE id_agent = ?");
            stmt.setInt(1, agent.getId());
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int idBranch = rs.getInt("id_branch");
                int idClient = rs.getInt("id_client");
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
                // Получаем ФИО клиента
                String clientFio = "";
                java.sql.PreparedStatement cstmt = conn.prepareStatement("SELECT surname, name, patronymic FROM Clients WHERE id = ?");
                cstmt.setInt(1, idClient);
                java.sql.ResultSet crs = cstmt.executeQuery();
                if (crs.next()) clientFio = crs.getString("surname") + " " + crs.getString("name") + " " + crs.getString("patronymic");
                crs.close(); cstmt.close();
                // Получаем название вида страхования
                String typeName = "";
                java.sql.PreparedStatement tstmt = conn.prepareStatement("SELECT name FROM Insurance_Types WHERE id = ?");
                tstmt.setInt(1, idType);
                java.sql.ResultSet trs = tstmt.executeQuery();
                if (trs.next()) typeName = trs.getString("name");
                trs.close(); tstmt.close();
                contracts.add(new ru.mypackage.model.Contract(id, idBranch, idClient, idAgent, idType, dateSigned, amount, rate, "", "", ""));
            }
            rs.close(); stmt.close(); conn.close();
            view.contractsTableView.table.setItems(contracts);
        } catch (Exception ex) { ex.printStackTrace(); }
        // --- Все филиалы ---
        try {
            javafx.collections.ObservableList<ru.mypackage.model.Branch> branches = javafx.collections.FXCollections.observableArrayList();
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM Branches");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String phone = rs.getString("phone_number");
                branches.add(new ru.mypackage.model.Branch(id, name, address, phone));
            }
            rs.close(); stmt.close(); conn.close();
            view.branchesTableView.table.setItems(branches);
        } catch (Exception ex) { ex.printStackTrace(); }
        // --- Все виды страхования ---
        try {
            javafx.collections.ObservableList<ru.mypackage.model.InsuranceType> types = javafx.collections.FXCollections.observableArrayList();
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM Insurance_Types");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String percent = rs.getString("agent_percentage");
                types.add(new ru.mypackage.model.InsuranceType(id, name, percent));
            }
            rs.close(); stmt.close(); conn.close();
            view.insuranceTypesTableView.table.setItems(types);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Обработчик сохранения профиля агента
    private class SaveProfileHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            TextField surnameField = view.surnameField;
            TextField nameField = view.nameField;
            TextField patronymicField = view.patronymicField;
            TextField addressField = view.addressField;
            TextField phoneField = view.phoneField;
            agent.setSurname(surnameField.getText().trim());
            agent.setName(nameField.getText().trim());
            agent.setPatronymic(patronymicField.getText().trim());
            agent.setAddress(addressField.getText().trim());
            agent.setPhoneNumber(phoneField.getText().trim());
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Insurance_Agents SET surname=?, name=?, patronymic=?, address=?, phone_number=? WHERE id=?");
                stmt.setString(1, agent.getSurname());
                stmt.setString(2, agent.getName());
                stmt.setString(3, agent.getPatronymic());
                stmt.setString(4, agent.getAddress());
                stmt.setString(5, agent.getPhoneNumber());
                stmt.setInt(6, agent.getId());
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