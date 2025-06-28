package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.InsuranceType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Таблица видов страхования с поиском и фильтрацией
public class InsuranceTypesTableView extends VBox {
    public TableView<InsuranceType> table;
    public Button addBtn;
    public Button editBtn;
    public Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    // Конструктор: создаёт таблицу видов страхования и элементы управления
    public InsuranceTypesTableView(boolean showButtons) {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "ID", "Название", "Процент агента");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<InsuranceType, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<InsuranceType, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<InsuranceType, String> percentCol = new TableColumn<>("Процент агента");
        percentCol.setCellValueFactory(new PropertyValueFactory<>("agentPercentage"));
        
        table.getColumns().addAll(idCol, nameCol, percentCol);
        
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

    public InsuranceTypesTableView() { this(true); }

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