package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.InsuranceType;
import ru.mypackage.model.InsuranceTypesTable;
import ru.mypackage.view.InsuranceTypesTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы видов страхования
public class InsuranceTypesTableController {
    private InsuranceTypesTable model;
    private InsuranceTypesTableView view;
    private ObservableList<InsuranceType> typeList = FXCollections.observableArrayList();
    private FilteredList<InsuranceType> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public InsuranceTypesTableController(InsuranceTypesTable model, InsuranceTypesTableView view) {
        this.model = model;
        this.view = view;
        loadTypes();
        filteredData = new FilteredList<InsuranceType>(typeList, new Predicate<InsuranceType>() {
            public boolean test(InsuranceType type) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<InsuranceType> sortedData = new SortedList<InsuranceType>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.setAddButtonHandler(new AddTypeHandler());
        view.setEditButtonHandler(new EditTypeHandler());
        view.setDeleteButtonHandler(new DeleteTypeHandler());
    }

    // Загружает виды страхования из базы данных
    public void loadTypes() {
        typeList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Insurance_Types");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String agentPercentage = rs.getString("agent_percentage");
                typeList.add(new InsuranceType(id, name, agentPercentage));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Обработчик изменения текста в поле поиска для обновления фильтра
    private class SearchFieldListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обработчик изменения выбранного столбца фильтрации для обновления фильтра
    private class FilterColumnListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обновляет фильтр видов страхования
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<InsuranceType>() {
            public boolean test(InsuranceType type) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(type.getId()).contains(filter);
                if ("Название".equals(column)) return type.getName().toLowerCase().contains(filter);
                if ("Процент агента".equals(column)) return type.getAgentPercentage().toLowerCase().contains(filter);
                return String.valueOf(type.getId()).contains(filter)
                    || type.getName().toLowerCase().contains(filter)
                    || type.getAgentPercentage().toLowerCase().contains(filter);
            }
        });
    }

    // Обработчик кнопки "Добавить вид страхования"
    private class AddTypeHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить вид страхования");
            VBox vbox = new VBox(10);
            TextField nameField = new TextField();
            TextField percentField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddTypeHandler(nameField, percentField, dialog));
            vbox.getChildren().addAll(new Label("Название вида страхования:"), nameField, new Label("Процент агента:"), percentField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 220));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить вид страхования"
    private class EditTypeHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            InsuranceType selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить вид страхования");
            VBox vbox = new VBox(10);
            TextField nameField = new TextField(selected.getName());
            TextField percentField = new TextField(selected.getAgentPercentage());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditTypeHandler(nameField, percentField, selected, dialog));
            vbox.getChildren().addAll(new Label("Название вида страхования:"), nameField, new Label("Процент агента:"), percentField, okBtn);
            dialog.setScene(new Scene(vbox, 300, 220));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить вид страхования"
    private class DeleteTypeHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            InsuranceType selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Insurance_Types WHERE id=?");
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                showError("Невозможно удалить запись: есть связанные записи в других таблицах.");
            }
            loadTypes();
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

    // Обработчик подтверждения добавления нового типа страхования
    private class OkAddTypeHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField nameField, percentField;
        private Stage dialog;
        public OkAddTypeHandler(TextField nameField, TextField percentField, Stage dialog) {
            this.nameField = nameField;
            this.percentField = percentField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String name = nameField.getText().trim();
            String percent = percentField.getText().trim();
            if (!name.isEmpty() && !percent.isEmpty()) {
                try {
                    Double.parseDouble(percent.replace(",", "."));
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Insurance_Types(name, agent_percentage) VALUES (?, ?)");
                    stmt.setString(1, name);
                    stmt.setString(2, percent);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadTypes();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось добавить запись из-за некорректных данных.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }

    // Обработчик подтверждения редактирования выбранного типа страхования
    private class OkEditTypeHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField nameField, percentField;
        private InsuranceType selected;
        private Stage dialog;
        public OkEditTypeHandler(TextField nameField, TextField percentField, InsuranceType selected, Stage dialog) {
            this.nameField = nameField;
            this.percentField = percentField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            String name = nameField.getText().trim();
            String percent = percentField.getText().trim();
            if (!name.isEmpty() && !percent.isEmpty()) {
                try {
                    Double.parseDouble(percent.replace(",", "."));
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Insurance_Types SET name=?, agent_percentage=? WHERE id=?");
                    stmt.setString(1, name);
                    stmt.setString(2, percent);
                    stmt.setInt(3, selected.getId());
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadTypes();
                    dialog.close();
                } catch (Exception ex) {
                    showError("Не удалось обновить запись из-за некорректных данных.");
                }
            } else {
                showError("Не все поля заполнены.");
            }
        }
    }
} 