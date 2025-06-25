package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.Client;
import ru.mypackage.model.ClientsTable;
import ru.mypackage.view.ClientTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы клиентов
public class ClientTableController {
    private ClientsTable model;
    private ClientTableView view;
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public ClientTableController(ClientsTable model, ClientTableView view) {
        this.model = model;
        this.view = view;
        loadClients();
        filteredData = new FilteredList<Client>(clientList, new Predicate<Client>() {
            public boolean test(Client client) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<Client> sortedData = new SortedList<Client>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.setAddButtonHandler(new AddClientHandler());
        view.setEditButtonHandler(new EditClientHandler());
        view.setDeleteButtonHandler(new DeleteClientHandler());
    }

    // Загружает клиентов из базы данных
    public void loadClients() {
        clientList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Clients");
            while (rs.next()) {
                int id = rs.getInt("id");
                int idUser = rs.getInt("id_user");
                String surname = rs.getString("surname");
                String name = rs.getString("name");
                String patronymic = rs.getString("patronymic");
                String dateOfBirth = rs.getString("date_of_birth");
                String phoneNumber = rs.getString("phone_number");
                clientList.add(new Client(id, idUser, surname, name, patronymic, dateOfBirth, phoneNumber));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Слушатель для поля поиска: обновляет фильтр при изменении текста
    private class SearchFieldListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Слушатель для выбора столбца фильтрации: обновляет фильтр при изменении столбца
    private class FilterColumnListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обновляет фильтр клиентов
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<Client>() {
            public boolean test(Client client) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(client.getId()).contains(filter);
                if ("ID пользователя".equals(column)) return String.valueOf(client.getIdUser()).contains(filter);
                if ("Фамилия".equals(column)) return client.getSurname().toLowerCase().contains(filter);
                if ("Имя".equals(column)) return client.getName().toLowerCase().contains(filter);
                if ("Отчество".equals(column)) return client.getPatronymic() != null && client.getPatronymic().toLowerCase().contains(filter);
                if ("Дата рождения".equals(column)) return client.getDateOfBirth().toLowerCase().contains(filter);
                if ("Телефон".equals(column)) return client.getPhoneNumber().toLowerCase().contains(filter);
                return String.valueOf(client.getId()).contains(filter)
                    || String.valueOf(client.getIdUser()).contains(filter)
                    || client.getSurname().toLowerCase().contains(filter)
                    || client.getName().toLowerCase().contains(filter)
                    || (client.getPatronymic() != null && client.getPatronymic().toLowerCase().contains(filter))
                    || client.getDateOfBirth().toLowerCase().contains(filter)
                    || client.getPhoneNumber().toLowerCase().contains(filter);
            }
        });
    }

    // Показывает окно ошибки
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Обработчик кнопки "Добавить клиента"
    public class AddClientHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить клиента");
            VBox vbox = new VBox(10);
            TextField idUserField = new TextField();
            TextField surnameField = new TextField();
            TextField nameField = new TextField();
            TextField patronymicField = new TextField();
            TextField dobField = new TextField();
            TextField phoneField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddClientHandler(idUserField, surnameField, nameField, patronymicField, dobField, phoneField, dialog));
            vbox.getChildren().addAll(new Label("ID пользователя:"), idUserField, new Label("Фамилия:"), surnameField, new Label("Имя:"), nameField, new Label("Отчество:"), patronymicField, new Label("Дата рождения:"), dobField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 350));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить клиента"
    public class EditClientHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Client selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить клиента");
            VBox vbox = new VBox(10);
            TextField surnameField = new TextField(selected.getSurname());
            TextField nameField = new TextField(selected.getName());
            TextField patronymicField = new TextField(selected.getPatronymic());
            TextField dobField = new TextField(selected.getDateOfBirth());
            TextField phoneField = new TextField(selected.getPhoneNumber());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditClientHandler(surnameField, nameField, patronymicField, dobField, phoneField, selected, dialog));
            vbox.getChildren().addAll(new Label("Фамилия:"), surnameField, new Label("Имя:"), nameField, new Label("Отчество:"), patronymicField, new Label("Дата рождения:"), dobField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 320));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить клиента"
    public class DeleteClientHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Client selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Clients WHERE id=?");
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                showError("Невозможно удалить запись: есть связанные записи в других таблицах.");
            }
            loadClients();
        }
    }

    // Обработчик подтверждения добавления нового клиента
    private class OkAddClientHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField idUserField, surnameField, nameField, patronymicField, dobField, phoneField;
        private Stage dialog;
        public OkAddClientHandler(TextField idUserField, TextField surnameField, TextField nameField, TextField patronymicField, TextField dobField, TextField phoneField, Stage dialog) {
            this.idUserField = idUserField;
            this.surnameField = surnameField;
            this.nameField = nameField;
            this.patronymicField = patronymicField;
            this.dobField = dobField;
            this.phoneField = phoneField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            try {
                int idUser = Integer.parseInt(idUserField.getText().trim());
                String surname = surnameField.getText().trim();
                String name = nameField.getText().trim();
                String patronymic = patronymicField.getText().trim();
                String dob = dobField.getText().trim();
                String phone = phoneField.getText().trim();
                if (!surname.isEmpty() && !name.isEmpty() && !dob.isEmpty() && !phone.isEmpty()) {
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clients(id_user, surname, name, patronymic, date_of_birth, phone_number) VALUES (?, ?, ?, ?, ?, ?)");
                    stmt.setInt(1, idUser);
                    stmt.setString(2, surname);
                    stmt.setString(3, name);
                    stmt.setString(4, patronymic);
                    stmt.setString(5, dob);
                    stmt.setString(6, phone);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadClients();
                    dialog.close();
                } else {
                    showError("Не все поля заполнены.");
                }
            } catch (Exception ex) {
                showError("Не удалось добавить запись из-за некорректных данных.");
            }
        }
    }

    // Обработчик подтверждения редактирования клиента
    private class OkEditClientHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField surnameField, nameField, patronymicField, dobField, phoneField;
        private Client selected;
        private Stage dialog;
        public OkEditClientHandler(TextField surnameField, TextField nameField, TextField patronymicField, TextField dobField, TextField phoneField, Client selected, Stage dialog) {
            this.surnameField = surnameField;
            this.nameField = nameField;
            this.patronymicField = patronymicField;
            this.dobField = dobField;
            this.phoneField = phoneField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String surname = surnameField.getText().trim();
            String name = nameField.getText().trim();
            String patronymic = patronymicField.getText().trim();
            String dob = dobField.getText().trim();
            String phone = phoneField.getText().trim();
            if (!surname.isEmpty() && !name.isEmpty() && !dob.isEmpty() && !phone.isEmpty()) {
                try {
                    if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) throw new Exception();
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Clients SET surname=?, name=?, patronymic=?, date_of_birth=?, phone_number=? WHERE id=?");
                    stmt.setString(1, surname);
                    stmt.setString(2, name);
                    stmt.setString(3, patronymic);
                    stmt.setString(4, dob);
                    stmt.setString(5, phone);
                    stmt.setInt(6, selected.getId());
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadClients();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось обновить запись из-за некорректных данных.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }
} 