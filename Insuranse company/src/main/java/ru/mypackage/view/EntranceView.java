package ru.mypackage.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Класс окна входа в систему
public class EntranceView {
    private Stage stage;
    private Scene scene;
    private TextField loginField;
    private PasswordField passwordField;
    private Button loginBtn;
    private Button regBtn;
    private Label message;

    // Конструктор: создаёт окно входа
    public EntranceView(Stage stage) {
        this.stage = stage;
        createView();
    }

    // Создаёт визуальные элементы окна входа
    private void createView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Вход в систему");
        loginField = new TextField();
        loginField.setPromptText("Логин");
        passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        loginBtn = new Button("Войти");
        regBtn = new Button("Регистрация");
        message = new Label();

        root.getChildren().addAll(title, loginField, passwordField, loginBtn, regBtn, message);
        scene = new Scene(root, 500, 400);
    }

    public Scene getScene() { return scene; }
    public Stage getStage() { return stage; }
    public TextField getLoginField() { return loginField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginBtn() { return loginBtn; }
    public Button getRegBtn() { return regBtn; }
    public Label getMessageLabel() { return message; }
    // Назначает обработчик для кнопки входа
    public void setLoginButtonHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        loginBtn.setOnAction(handler);
    }
    // Назначает обработчик для кнопки регистрации
    public void setRegisterButtonHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        regBtn.setOnAction(handler);
    }
} 