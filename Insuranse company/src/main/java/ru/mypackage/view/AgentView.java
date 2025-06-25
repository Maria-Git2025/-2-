package ru.mypackage.view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Класс личного кабинета агента
public class AgentView {
    public TextField surnameField;
    public TextField nameField;
    public TextField patronymicField;
    public TextField addressField;
    public TextField phoneField;
    private Button saveBtn;
    private Button editUserBtn;
    private VBox profileBox;
    private Stage stage;
    private Scene scene;
    private ComboBox<String> sectionSelector;
    public ClientTableView clientTableView;
    public ContractsTableView contractsTableView;
    public BranchesTableView branchesTableView;
    public InsuranceTypesTableView insuranceTypesTableView;
    private VBox mainBox;

    public AgentView(Stage stage) {
        this.stage = stage;
        createProfileUI();
        createSectionSelectorAndTables();
    }

    // Создаёт профиль агента
    private void createProfileUI() {
        profileBox = new VBox(5);
        surnameField = new TextField();
        nameField = new TextField();
        patronymicField = new TextField();
        addressField = new TextField();
        phoneField = new TextField();
        saveBtn = new Button("Сохранить изменения");
        editUserBtn = new Button("Изменить логин/пароль");
        profileBox.getChildren().addAll(
            new Label("Фамилия:"), surnameField,
            new Label("Имя:"), nameField,
            new Label("Отчество:"), patronymicField,
            new Label("Адрес:"), addressField,
            new Label("Телефон:"), phoneField,
            saveBtn, editUserBtn
        );
    }

    private void createSectionSelectorAndTables() {
        sectionSelector = new ComboBox<>();
        sectionSelector.getItems().addAll("Мои клиенты", "Мои договоры", "Филиалы", "Виды страхования");
        sectionSelector.setValue("Мои клиенты");
        clientTableView = new ClientTableView(false);
        contractsTableView = new ContractsTableView();
        branchesTableView = new BranchesTableView(false);
        insuranceTypesTableView = new InsuranceTypesTableView(false);
        mainBox = new VBox(10, sectionSelector, clientTableView);
        sectionSelector.setOnAction(e -> updateTableView());
    }

    private void updateTableView() {
        mainBox.getChildren().remove(1);
        switch (sectionSelector.getValue()) {
            case "Мои клиенты":
                mainBox.getChildren().add(clientTableView);
                break;
            case "Мои договоры":
                mainBox.getChildren().add(contractsTableView);
                break;
            case "Филиалы":
                mainBox.getChildren().add(branchesTableView);
                break;
            case "Виды страхования":
                mainBox.getChildren().add(insuranceTypesTableView);
                break;
        }
    }

    // Показывает окно профиля агента
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

    // Методы для назначения обработчиков таблиц
    public void setClientAddHandler(EventHandler<ActionEvent> handler) { clientTableView.setAddButtonHandler(handler); }
    public void setClientEditHandler(EventHandler<ActionEvent> handler) { clientTableView.setEditButtonHandler(handler); }
    public void setClientDeleteHandler(EventHandler<ActionEvent> handler) { clientTableView.setDeleteButtonHandler(handler); }
    public void setContractAddHandler(EventHandler<ActionEvent> handler) { contractsTableView.setAddButtonHandler(handler); }
    public void setContractEditHandler(EventHandler<ActionEvent> handler) { contractsTableView.setEditButtonHandler(handler); }
    public void setContractDeleteHandler(EventHandler<ActionEvent> handler) { contractsTableView.setDeleteButtonHandler(handler); }
} 