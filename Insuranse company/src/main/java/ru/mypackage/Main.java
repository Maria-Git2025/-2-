package ru.mypackage;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.mypackage.view.EntranceView;
import ru.mypackage.model.UsersTable;
import ru.mypackage.controller.EntranceController;
import java.sql.Connection;
import ru.mypackage.database.ConnectionDatabase;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Страховая компания - Авторизация");
        UsersTable usersTable = UsersTable.getInstance();
        try {
            Connection conn = new ConnectionDatabase().connection;
            usersTable.loadFromDatabase(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntranceView entranceView = new EntranceView(primaryStage);
        EntranceController entranceController = new EntranceController(usersTable, entranceView);
        primaryStage.setScene(entranceView.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}