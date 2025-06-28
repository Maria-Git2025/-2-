package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.Branch;
import ru.mypackage.model.BranchesTable;
import ru.mypackage.view.BranchesTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы филиалов
public class BranchesTableController {
    private BranchesTable model;
    private BranchesTableView view;
    private ObservableList<Branch> branchList = FXCollections.observableArrayList();
    private FilteredList<Branch> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public BranchesTableController(BranchesTable model, BranchesTableView view) {
        this.model = model;
        this.view = view;
        loadBranchesFromModel();
        filteredData = new FilteredList<Branch>(branchList, new Predicate<Branch>() {
            public boolean test(Branch branch) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<Branch> sortedData = new SortedList<Branch>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.setAddButtonHandler(new AddBranchHandler());
        view.setEditButtonHandler(new EditBranchHandler());
        view.setDeleteButtonHandler(new DeleteBranchHandler());
    }

    // Загружает филиалы из модели (которая работает с базой данных)
    public void loadBranchesFromModel() {
        branchList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            model.loadFromDatabase(conn);
            branchList.addAll(model.getAll());
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Слушатель для поля поиска: обновляет фильтр при изменении текста
    private class SearchFieldListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Слушатель для выбора столбца фильтрации: обновляет фильтр при изменении столбца
    private class FilterColumnListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обновляет фильтр филиалов
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<Branch>() {
            public boolean test(Branch branch) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(branch.getId()).contains(filter);
                if ("Название".equals(column)) return branch.getName().toLowerCase().contains(filter);
                if ("Адрес".equals(column)) return branch.getAddress().toLowerCase().contains(filter);
                if ("Телефон".equals(column)) return branch.getPhoneNumber().toLowerCase().contains(filter);
                return String.valueOf(branch.getId()).contains(filter)
                    || branch.getName().toLowerCase().contains(filter)
                    || branch.getAddress().toLowerCase().contains(filter)
                    || branch.getPhoneNumber().toLowerCase().contains(filter);
            }
        });
    }

    // Обработчик кнопки "Добавить филиал"
    private class AddBranchHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить филиал");
            VBox vbox = new VBox(10);
            TextField nameField = new TextField();
            TextField addressField = new TextField();
            TextField phoneField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddBranchHandler(nameField, addressField, phoneField, dialog));
            vbox.getChildren().addAll(new Label("Название:"), nameField, new Label("Адрес:"), addressField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 220));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить филиал"
    private class EditBranchHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Branch selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить филиал");
            VBox vbox = new VBox(10);
            TextField nameField = new TextField(selected.getName());
            TextField addressField = new TextField(selected.getAddress());
            TextField phoneField = new TextField(selected.getPhoneNumber());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditBranchHandler(nameField, addressField, phoneField, selected, dialog));
            vbox.getChildren().addAll(new Label("Название:"), nameField, new Label("Адрес:"), addressField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 220));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить филиал"
    private class DeleteBranchHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Branch selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                model.remove(selected, conn);
                conn.close();
            } catch (SQLException ex) {
                showError("Невозможно удалить запись: есть связанные записи в других таблицах.");
            }
            loadBranchesFromModel();
        }
    }

    // Показывает окно ошибки
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Обработчик подтверждения добавления нового филиала
    private class OkAddBranchHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField nameField, addressField, phoneField;
        private Stage dialog;
        public OkAddBranchHandler(TextField nameField, TextField addressField, TextField phoneField, Stage dialog) {
            this.nameField = nameField;
            this.addressField = addressField;
            this.phoneField = phoneField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                try {
                    Branch newBranch = new Branch(0, name, address, phone);
                    Connection conn = new ConnectionDatabase().connection;
                    model.add(newBranch, conn);
                    conn.close();
                    loadBranchesFromModel();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось добавить запись из-за ошибки.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }

    // Обработчик подтверждения редактирования филиала
    private class OkEditBranchHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField nameField, addressField, phoneField;
        private Branch selected;
        private Stage dialog;
        public OkEditBranchHandler(TextField nameField, TextField addressField, TextField phoneField, Branch selected, Stage dialog) {
            this.nameField = nameField;
            this.addressField = addressField;
            this.phoneField = phoneField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                selected.setName(name);
                selected.setAddress(address);
                selected.setPhoneNumber(phone);
                try {
                    Connection conn = new ConnectionDatabase().connection;
                    model.update(selected, conn);
                    conn.close();
                    loadBranchesFromModel();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось обновить запись из-за ошибки.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }
} 