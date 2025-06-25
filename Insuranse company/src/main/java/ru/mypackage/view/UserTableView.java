package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Таблица пользователей с поиском и фильтрацией
public class UserTableView extends VBox {
    public TableView<User> table;
    public Button addBtn;
    public Button editBtn;
    public Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    // Конструктор: создаёт таблицу пользователей и элементы управления
    public UserTableView() {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "ID", "Логин", "Пароль", "Роль");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<User, String> loginCol = new TableColumn<>("Логин");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        
        TableColumn<User, String> passwordCol = new TableColumn<>("Пароль");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        
        TableColumn<User, String> roleCol = new TableColumn<>("Роль");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        table.getColumns().addAll(idCol, loginCol, passwordCol, roleCol);
        
        addBtn = new Button("Добавить");
        editBtn = new Button("Изменить");
        deleteBtn = new Button("Удалить");
        
        HBox searchBox = new HBox(10);
        searchBox.getChildren().addAll(filterColumnBox, searchField);
        
        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        this.getChildren().addAll(searchBox, table, buttonsBox);
        
        this.setSpacing(10);
    }

    // Назначает обработчик для кнопки "Добавить"
    public void setAddButtonHandler(EventHandler<ActionEvent> handler) {
        addBtn.setOnAction(handler);
    }

    // Назначает обработчик для кнопки "Изменить"
    public void setEditButtonHandler(EventHandler<ActionEvent> handler) {
        editBtn.setOnAction(handler);
    }

    // Назначает обработчик для кнопки "Удалить"
    public void setDeleteButtonHandler(EventHandler<ActionEvent> handler) {
        deleteBtn.setOnAction(handler);
    }

    public Button getAddBtn() { return addBtn; }
    public Button getEditBtn() { return editBtn; }
    public Button getDeleteBtn() { return deleteBtn; }
} 