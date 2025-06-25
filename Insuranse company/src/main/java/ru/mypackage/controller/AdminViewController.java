package ru.mypackage.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ru.mypackage.view.AdminView;

// Контроллер для личного кабинета администратора
public class AdminViewController {
    private final AdminView view;

    public AdminViewController(AdminView view) {
        this.view = view;
        this.view.setTableSelectorHandler(new TableSelectorHandler());
    }

    // Обработчик выбора таблицы
    private class TableSelectorHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            String selectedTable = view.getTableSelector().getValue();
            if ("Пользователи".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getUserTableView());
            } else if ("Клиенты".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getClientTableView());
            } else if ("Филиалы".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getBranchesTableView());
            } else if ("Агенты".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getAgentsTableView());
            } else if ("Виды страхования".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getInsuranceTypesTableView());
            } else if ("Договоры".equals(selectedTable)) {
                view.getRoot().getChildren().setAll(view.getTableSelector(), view.getContractsTableView());
            }
        }
    }
} 