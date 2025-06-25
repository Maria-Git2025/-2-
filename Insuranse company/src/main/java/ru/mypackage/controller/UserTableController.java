package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.User;
import ru.mypackage.model.UsersTable;
import ru.mypackage.view.UserTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы пользователей
public class UserTableController {
    private UsersTable model;
    private UserTableView view;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public UserTableController(UsersTable model, UserTableView view) {
        this.model = model;
        this.view = view;
        loadUsers();
        filteredData = new FilteredList<User>(userList, new Predicate<User>() {
            public boolean test(User user) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<User> sortedData = new SortedList<User>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.addBtn.setOnAction(new AddUserHandler());
        view.editBtn.setOnAction(new EditUserHandler());
        view.deleteBtn.setOnAction(new DeleteUserHandler());
    }

    // Загружает пользователей из базы данных
    public void loadUsers() {
        userList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users");
            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");
                String password = rs.getString("password");
                String role = rs.getString("role");
                userList.add(new User(id, login, password, role));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Слушатель изменений поля поиска
    private class SearchFieldListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Слушатель изменений выбора столбца фильтрации
    private class FilterColumnListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обновляет фильтр пользователей
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<User>() {
            public boolean test(User user) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(user.getId()).contains(filter);
                if ("Логин".equals(column)) return user.getLogin().toLowerCase().contains(filter);
                if ("Пароль".equals(column)) return user.getPassword().toLowerCase().contains(filter);
                if ("Роль".equals(column)) return user.getRole().toLowerCase().contains(filter);
                // "Все"
                return String.valueOf(user.getId()).contains(filter)
                    || user.getLogin().toLowerCase().contains(filter)
                    || user.getPassword().toLowerCase().contains(filter)
                    || user.getRole().toLowerCase().contains(filter);
            }
        });
    }

    // Обработчик кнопки "Добавить пользователя"
    private class AddUserHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить пользователя");
            VBox vbox = new VBox(10);
            TextField loginField = new TextField();
            TextField passwordField = new TextField();
            TextField roleField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddUserHandler(loginField, passwordField, roleField, dialog));
            vbox.getChildren().addAll(new Label("Логин:"), loginField, new Label("Пароль:"), passwordField, new Label("Роль:"), roleField, okBtn);
            dialog.setScene(new Scene(vbox, 250, 200));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить пользователя"
    private class EditUserHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            User selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить пользователя");
            VBox vbox = new VBox(10);
            TextField loginField = new TextField(selected.getLogin());
            TextField passwordField = new TextField(selected.getPassword());
            TextField roleField = new TextField(selected.getRole());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditUserHandler(loginField, passwordField, roleField, selected, dialog));
            vbox.getChildren().addAll(new Label("Логин:"), loginField, new Label("Пароль:"), passwordField, new Label("Роль:"), roleField, okBtn);
            dialog.setScene(new Scene(vbox, 250, 200));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить пользователя"
    private class DeleteUserHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            User selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE id=?");
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                showError("Невозможно удалить запись: есть связанные записи в других таблицах.");
            }
            loadUsers();
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

    // Обработчик OK в диалоге добавления пользователя
    private class OkAddUserHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField loginField, passwordField, roleField;
        private Stage dialog;
        public OkAddUserHandler(TextField loginField, TextField passwordField, TextField roleField, Stage dialog) {
            this.loginField = loginField;
            this.passwordField = passwordField;
            this.roleField = roleField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleField.getText().trim();
            if (!login.isEmpty() && !password.isEmpty() && !role.isEmpty()) {
                try {
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users(login, password, role) VALUES (?, ?, ?)");
                    stmt.setString(1, login);
                    stmt.setString(2, password);
                    stmt.setString(3, role);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadUsers();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось добавить запись из-за некорректных данных.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }

    // Обработчик OK в диалоге редактирования пользователя
    private class OkEditUserHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField loginField, passwordField, roleField;
        private User selected;
        private Stage dialog;
        public OkEditUserHandler(TextField loginField, TextField passwordField, TextField roleField, User selected, Stage dialog) {
            this.loginField = loginField;
            this.passwordField = passwordField;
            this.roleField = roleField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleField.getText().trim();
            if (!login.isEmpty() && !password.isEmpty() && !role.isEmpty()) {
                try {
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET login=?, password=?, role=? WHERE id=?");
                    stmt.setString(1, login);
                    stmt.setString(2, password);
                    stmt.setString(3, role);
                    stmt.setInt(4, selected.getId());
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadUsers();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось обновить запись.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }
} 