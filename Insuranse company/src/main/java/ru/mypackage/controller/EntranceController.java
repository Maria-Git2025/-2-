package ru.mypackage.controller;

import ru.mypackage.model.UsersTable;
import ru.mypackage.model.User;
import ru.mypackage.view.EntranceView;
import ru.mypackage.view.AdminView;
import ru.mypackage.view.AgentView;
import ru.mypackage.view.ClientView;
import ru.mypackage.view.RegistrationView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ru.mypackage.model.Agent;
import ru.mypackage.database.ConnectionDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import ru.mypackage.model.Client;

// Контроллер для окна входа
public class EntranceController {
    private UsersTable model;
    private EntranceView view;

    // Конструктор: связывает view и назначает обработчики
    public EntranceController(UsersTable model, EntranceView view) {
        this.model = model;
        this.view = view;
        this.view.setLoginButtonHandler(new LoginButtonHandler());
        this.view.setRegisterButtonHandler(new RegisterButtonHandler());
    }

    // Обрабатывает нажатие кнопки входа
    private class LoginButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            String login = view.getLoginField().getText();
            String password = view.getPasswordField().getText();
            User user = model.findByLogin(login);
            if (user != null && password.equals(user.getPassword())) {
                Stage stage = view.getStage();
                switch (user.getRole()) {
                    case "admin": new AdminView(stage, user).show(); break;
                    case "agent":
                        Agent agent = null;
                        try {
                            Connection conn = new ConnectionDatabase().connection;
                            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Insurance_Agents WHERE id_user = ?");
                            stmt.setInt(1, user.getId());
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                agent = new Agent(
                                    rs.getInt("id"),
                                    rs.getInt("id_user"),
                                    rs.getInt("id_branch"),
                                    rs.getString("surname"),
                                    rs.getString("name"),
                                    rs.getString("patronymic"),
                                    rs.getString("address"),
                                    rs.getString("phone_number")
                                );
                            }
                            rs.close(); stmt.close(); conn.close();
                        } catch (Exception ex) { ex.printStackTrace(); }
                        if (agent != null) {
                            AgentView agentView = new AgentView(stage);
                            AgentViewController controller = new AgentViewController(agentView, agent, user);
                            agentView.show("ЛК Агента");
                        } else {
                            System.out.println("Агент не найден для пользователя id=" + user.getId());
                        }
                        break;
                    case "client":
                        Client client = null;
                        try {
                            Connection conn = new ConnectionDatabase().connection;
                            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Clients WHERE id_user = ?");
                            stmt.setInt(1, user.getId());
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                client = new Client(
                                    rs.getInt("id"),
                                    rs.getInt("id_user"),
                                    rs.getString("surname"),
                                    rs.getString("name"),
                                    rs.getString("patronymic"),
                                    rs.getString("date_of_birth"),
                                    rs.getString("phone_number")
                                );
                            }
                            rs.close(); stmt.close(); conn.close();
                        } catch (Exception ex) { ex.printStackTrace(); }
                        if (client != null) {
                            ClientView clientView = new ClientView(stage);
                            ClientViewController controller = new ClientViewController(clientView, client, user);
                            clientView.show("ЛК Клиента");
                        } else {
                            System.out.println("Клиент не найден для пользователя id=" + user.getId());
                        }
                        break;
                }
            } else {
                view.getMessageLabel().setText("Неверный логин или пароль");
            }
        }
    }

    // Обрабатывает нажатие кнопки регистрации
    private class RegisterButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            RegistrationView regView = new RegistrationView(view.getStage());
            new ru.mypackage.controller.RegistrationController(UsersTable.getInstance(), regView);
            regView.show();
        }
    }
} 