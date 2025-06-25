package ru.mypackage.controller;

import ru.mypackage.model.UsersTable;
import ru.mypackage.model.User;
import ru.mypackage.view.RegistrationView;
import ru.mypackage.view.EntranceView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

// Контроллер для окна регистрации
public class RegistrationController {
    private UsersTable model;
    private RegistrationView view;

    // Конструктор: связывает view и назначает обработчики
    public RegistrationController(UsersTable model, RegistrationView view) {
        this.model = model;
        this.view = view;
        this.view.setSubmitButtonHandler(new SubmitButtonHandler());
    }

    // Обрабатывает нажатие кнопки регистрации
    private class SubmitButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            String login = view.getLoginField().getText();
            String pwd = view.getPasswordField().getText();
            String role = view.getRoleCombo().getValue();
            if ("Администратор".equals(role)) role = "admin";
            else if ("Агент".equals(role)) role = "agent";
            else if ("Клиент".equals(role)) role = "client";
            boolean success = register(login, pwd, role);
            if (success) {
                view.getMessageLabel().setText("Регистрация успешна. Войдите.");
                Stage stage = view.getStage();
                ru.mypackage.view.EntranceView entranceView = new ru.mypackage.view.EntranceView(stage);
                new ru.mypackage.controller.EntranceController(ru.mypackage.model.UsersTable.getInstance(), entranceView);
                stage.setScene(entranceView.getScene());
            } else {
                view.getMessageLabel().setText("Пользователь с таким логином уже существует.");
            }
        }
    }

    // Регистрирует нового пользователя, возвращает true если успешно
    private boolean register(String login, String password, String role) {
        if (model.findByLogin(login) != null) {
            return false;
        }
        User user = new User(0, login, password, role);
        model.addUser(user);
        if ("agent".equals(role)) {
            try {
                java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
                java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Insurance_Agents(id_user, id_branch, surname, name, patronymic, address, phone_number) VALUES (?, 1, '', '', '', '', '')");
                stmt.setInt(1, user.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            java.sql.Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            model.loadFromDatabase(conn);
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
} 