package ru.mypackage.view;


import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Класс личного кабинета клиента
public class ClientView {
    public TextField surnameField;
    public TextField nameField;
    public TextField patronymicField;
    public TextField dobField;
    public TextField phoneField;
    private Button saveBtn;
    private Button editUserBtn;
    private VBox profileBox;
    private Stage stage;
    private Scene scene;
    public ContractsTableView contractsTableView;
    private VBox mainBox;

    public ClientView(Stage stage) {
        this.stage = stage;
        createProfileUI();
        contractsTableView = new ContractsTableView(false);
        mainBox = new VBox(10, contractsTableView);
    }

    // Создаёт UI для профиля клиента
    private void createProfileUI() {
        profileBox = new VBox(5);
        surnameField = new TextField();
        nameField = new TextField();
        patronymicField = new TextField();
        dobField = new TextField();
        phoneField = new TextField();
        saveBtn = new Button("Сохранить изменения");
        editUserBtn = new Button("Изменить логин/пароль");
        profileBox.getChildren().addAll(
            new Label("Фамилия:"), surnameField,
            new Label("Имя:"), nameField,
            new Label("Отчество:"), patronymicField,
            new Label("Дата рождения:"), dobField,
            new Label("Телефон:"), phoneField,
            saveBtn, editUserBtn
        );
    }

    // Показывает окно профиля клиента
    public void show(String title) {
        VBox root = new VBox(15, profileBox, mainBox);
        scene = new Scene(root, 900, 600);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    // Назначает обработчик для кнопки "Сохранить профиль"
    public void setSaveProfileHandler(EventHandler<ActionEvent> handler) {
        saveBtn.setOnAction(handler);
    }
    // Назначает обработчик для кнопки "Изменить пользователя"
    public void setEditUserHandler(EventHandler<ActionEvent> handler) {
        editUserBtn.setOnAction(handler);
    }
} 