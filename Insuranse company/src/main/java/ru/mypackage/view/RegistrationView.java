package ru.mypackage.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Класс окна регистрации пользователя
public class RegistrationView {
    private Stage stage;
    private Scene scene;
    private TextField loginField;
    private PasswordField passwordField;
    private ComboBox<String> roleCombo;
    private Button submitBtn;
    private Label message;

    // Конструктор: создаёт окно регистрации
    public RegistrationView(Stage stage) {
        this.stage = stage;
        createView();
    }

    // Создаёт визуальные элементы окна регистрации
    private void createView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Регистрация нового пользователя");
        loginField = new TextField();
        loginField.setPromptText("Логин");
        passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Администратор", "Агент", "Клиент");
        roleCombo.setPromptText("Выберите роль");
        submitBtn = new Button("Зарегистрироваться");
        message = new Label();

        root.getChildren().addAll(title, loginField, passwordField, roleCombo, submitBtn, message);
        scene = new Scene(root, 500, 400);
    }

    public Scene getScene() { return scene; }
    public Stage getStage() { return stage; }
    public TextField getLoginField() { return loginField; }
    public PasswordField getPasswordField() { return passwordField; }
    public ComboBox<String> getRoleCombo() { return roleCombo; }
    public Button getSubmitBtn() { return submitBtn; }
    public Label getMessageLabel() { return message; }

    // Назначает обработчик для кнопки регистрации
    public void setSubmitButtonHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        submitBtn.setOnAction(handler);
    }
    // Показывает окно регистрации
    public void show() {
        stage.setTitle("Регистрация");
        stage.setScene(scene);
        stage.show();
    }
} 