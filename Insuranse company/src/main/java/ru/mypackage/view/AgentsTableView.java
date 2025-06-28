package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.Agent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Класс отображения таблицы агентов
public class AgentsTableView extends VBox {
    public TableView<Agent> table;
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    public AgentsTableView() {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "ID", "ID пользователя", "ID филиала", "Фамилия", "Имя", "Отчество", "Адрес", "Телефон");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<Agent, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Agent, Integer> idUserCol = new TableColumn<>("ID пользователя");
        idUserCol.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        TableColumn<Agent, Integer> idBranchCol = new TableColumn<>("ID филиала");
        idBranchCol.setCellValueFactory(new PropertyValueFactory<>("idBranch"));
        
        TableColumn<Agent, String> surnameCol = new TableColumn<>("Фамилия");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        
        TableColumn<Agent, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Agent, String> patronymicCol = new TableColumn<>("Отчество");
        patronymicCol.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        
        TableColumn<Agent, String> addressCol = new TableColumn<>("Адрес");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        TableColumn<Agent, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        table.getColumns().addAll(idCol, idUserCol, idBranchCol, surnameCol, nameCol, patronymicCol, addressCol, phoneCol);
        
        addBtn = new Button("Добавить");
        editBtn = new Button("Изменить");
        deleteBtn = new Button("Удалить");
        
        HBox searchBox = new HBox(10);
        searchBox.getChildren().addAll(filterColumnBox, searchField);
        
        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        this.getChildren().addAll(searchBox, table, buttonsBox);
        
        this.setSpacing(10);
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
} 