package ru.mypackage.view;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import ru.mypackage.controller.*;
import ru.mypackage.model.User;
import ru.mypackage.model.UsersTable;
import ru.mypackage.model.ClientsTable;
import ru.mypackage.model.BranchesTable;
import ru.mypackage.model.AgentsTable;
import ru.mypackage.model.InsuranceTypesTable;
import ru.mypackage.model.ContractsTable;

// Отображает личный кабинет администратора
public class AdminView {
    private Stage stage;
    private User user;
    private VBox root;
    private ComboBox<String> tableSelector;
    private UserTableView userTableView;
    private ClientTableView clientTableView;
    private BranchesTableView branchesTableView;
    private AgentsTableView agentsTableView;
    private InsuranceTypesTableView insuranceTypesTableView;
    private ContractsTableView contractsTableView;

    // Конструктор: инициализация AdminView для окна и пользователя
    public AdminView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    // Показывает окно личного кабинета администратора
    public void show() {
        createTableSelector();
        createModels();
        createTableViews();
        createControllers();
        createMainInterface();
        setupTableSwitching();
        new ru.mypackage.controller.AdminViewController(this);
    }

    // Создаёт ComboBox для выбора раздела
    private void createTableSelector() {
        tableSelector = new ComboBox<>();
        tableSelector.getItems().addAll(
            "Пользователи",
            "Клиенты", 
            "Филиалы",
            "Агенты",
            "Виды страхования",
            "Договоры"
        );
        tableSelector.setValue("Пользователи");
    }

    // Инициализирует модели для всех таблиц
    private void createModels() {
        UsersTable.getInstance();
        ClientsTable.getInstance();
        BranchesTable.getInstance();
        AgentsTable.getInstance();
        InsuranceTypesTable.getInstance();
        ContractsTable.getInstance();
    }

    // Создаёт view-классы для таблиц
    private void createTableViews() {
        userTableView = new UserTableView();
        userTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        clientTableView = new ClientTableView();
        clientTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        branchesTableView = new BranchesTableView();
        branchesTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        agentsTableView = new AgentsTableView();
        agentsTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        insuranceTypesTableView = new InsuranceTypesTableView();
        insuranceTypesTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        contractsTableView = new ContractsTableView();
        contractsTableView.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Создаёт контроллеры для всех таблиц
    private void createControllers() {
        new UserTableController(UsersTable.getInstance(), userTableView);
        new ClientTableController(ClientsTable.getInstance(), clientTableView);
        new BranchesTableController(BranchesTable.getInstance(), branchesTableView);
        new AgentsTableController(AgentsTable.getInstance(), agentsTableView);
        new InsuranceTypesTableController(InsuranceTypesTable.getInstance(), insuranceTypesTableView);
        new ContractsTableController(ContractsTable.getInstance(), contractsTableView);
    }

    // Формирует основной интерфейс окна
    private void createMainInterface() {
        root = new VBox(10);
        root.getChildren().addAll(tableSelector, userTableView);
        
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("ЛК Администратора " + user.getLogin());
        stage.setScene(scene);
        stage.show();
    }

    // Настраивает переключение между таблицами
    private void setupTableSwitching() {
    }

    // Назначает обработчик для выбора таблицы
    public void setTableSelectorHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        tableSelector.setOnAction(handler);
    }

    public ComboBox<String> getTableSelector() { return tableSelector; }
    public VBox getRoot() { return root; }
    public UserTableView getUserTableView() { return userTableView; }
    public ClientTableView getClientTableView() { return clientTableView; }
    public BranchesTableView getBranchesTableView() { return branchesTableView; }
    public AgentsTableView getAgentsTableView() { return agentsTableView; }
    public InsuranceTypesTableView getInsuranceTypesTableView() { return insuranceTypesTableView; }
    public ContractsTableView getContractsTableView() { return contractsTableView; }
} 