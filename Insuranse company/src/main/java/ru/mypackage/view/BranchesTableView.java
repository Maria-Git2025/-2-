package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.Branch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Таблица филиалов с поиском и фильтрацией
public class BranchesTableView extends VBox {
    public TableView<Branch> table;
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    // Конструктор: создаёт таблицу филиалов и элементы управления
    public BranchesTableView(boolean showButtons) {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "ID", "Название", "Адрес", "Телефон");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<Branch, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Branch, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Branch, String> addressCol = new TableColumn<>("Адрес");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        TableColumn<Branch, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        table.getColumns().addAll(idCol, nameCol, addressCol, phoneCol);
        
        if (showButtons) {
            addBtn = new Button("Добавить");
            editBtn = new Button("Изменить");
            deleteBtn = new Button("Удалить");
        }
        
        HBox searchBox = new HBox(10);
        searchBox.getChildren().addAll(filterColumnBox, searchField);
        
        HBox buttonsBox = new HBox(10);
        if (showButtons) buttonsBox.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        this.getChildren().addAll(searchBox, table, buttonsBox);
        
        this.setSpacing(10);
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public BranchesTableView() { this(true); }

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
    // Возвращает кнопку "Добавить"
    public Button getAddBtn() { return addBtn; }
    // Возвращает кнопку "Изменить"
    public Button getEditBtn() { return editBtn; }
    // Возвращает кнопку "Удалить"
    public Button getDeleteBtn() { return deleteBtn; }
} 