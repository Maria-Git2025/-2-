package ru.mypackage.view;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mypackage.model.Contract;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Таблица договоров с поиском и фильтрацией
public class ContractsTableView extends VBox {
    public TableView<Contract> table;
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    public TextField searchField;
    public ComboBox<String> filterColumnBox;

    // Конструктор: создаёт таблицу договоров и элементы управления
    public ContractsTableView(boolean showButtons) {
        table = new TableView<>();
        
        filterColumnBox = new ComboBox<>();
        filterColumnBox.getItems().addAll("Все", "Филиал", "Агент", "Вид страхования", "Дата заключения", "Страховая сумма", "Тарифная ставка");
        filterColumnBox.setValue("Все");
        
        searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");
        
        TableColumn<Contract, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Contract, Integer> branchIdCol = new TableColumn<>("ID филиала");
        branchIdCol.setCellValueFactory(new PropertyValueFactory<>("idBranch"));
        TableColumn<Contract, Integer> clientIdCol = new TableColumn<>("ID клиента");
        clientIdCol.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        TableColumn<Contract, Integer> typeIdCol = new TableColumn<>("ID вида страхования");
        typeIdCol.setCellValueFactory(new PropertyValueFactory<>("idInsuranceType"));
        
        TableColumn<Contract, String> dateCol = new TableColumn<>("Дата заключения");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateSigned"));
        
        TableColumn<Contract, String> amountCol = new TableColumn<>("Страховая сумма");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("insuranceAmount"));
        
        TableColumn<Contract, String> rateCol = new TableColumn<>("Тарифная ставка");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("tariffRate"));
        
        table.getColumns().clear();
        table.getColumns().addAll(idCol, branchIdCol, clientIdCol, typeIdCol, dateCol, amountCol, rateCol);
        
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

    public ContractsTableView() { this(true); }

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