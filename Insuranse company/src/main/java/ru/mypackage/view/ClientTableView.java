package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Класс отображения таблицы клиентов
public class ClientTableView extends VBox {
    public TableView<Client> table;
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    // Конструктор: создаёт таблицу клиентов и элементы управления.
    public ClientTableView(boolean showAddButton) {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "ID", "ID пользователя", "Фамилия", "Имя", "Отчество", "Дата рождения", "Телефон");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<Client, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Client, Integer> idUserCol = new TableColumn<>("ID пользователя");
        idUserCol.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        TableColumn<Client, String> surnameCol = new TableColumn<>("Фамилия");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        
        TableColumn<Client, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Client, String> patronymicCol = new TableColumn<>("Отчество");
        patronymicCol.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        
        TableColumn<Client, String> dobCol = new TableColumn<>("Дата рождения");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        
        TableColumn<Client, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        table.getColumns().addAll(idCol, idUserCol, surnameCol, nameCol, patronymicCol, dobCol, phoneCol);
        
        editBtn = new Button("Изменить");
        deleteBtn = new Button("Удалить");
        if (showAddButton) {
            addBtn = new Button("Добавить");
        }
        
        HBox searchBox = new HBox(10);
        searchBox.getChildren().addAll(filterColumnBox, searchField);
        
        HBox buttonsBox = new HBox(10);
        if (showAddButton) buttonsBox.getChildren().add(addBtn);
        buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        
        this.getChildren().addAll(searchBox, table, buttonsBox);
        
        this.setSpacing(10);
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public ClientTableView() { this(true); }

     // Назначает обработчик для кнопки "Добавить"
    public void setAddButtonHandler(EventHandler<ActionEvent> handler) {
        if (addBtn != null) {
            addBtn.setOnAction(handler);
        }
    }
    // Назначает обработчик для кнопки "Изменить".
    public void setEditButtonHandler(EventHandler<ActionEvent> handler) {
        editBtn.setOnAction(handler);
    }

    // Назначает обработчик для кнопки "Удалить".
    public void setDeleteButtonHandler(EventHandler<ActionEvent> handler) {
        deleteBtn.setOnAction(handler);
    }

    public Button getAddBtn() { return addBtn; }
    public Button getEditBtn() { return editBtn; }
    public Button getDeleteBtn() { return deleteBtn; }
} 