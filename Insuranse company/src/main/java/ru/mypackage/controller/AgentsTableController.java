package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.Agent;
import ru.mypackage.model.AgentsTable;
import ru.mypackage.view.AgentsTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы агентов
public class AgentsTableController {
    private AgentsTable model;
    private AgentsTableView view;
    private ObservableList<Agent> agentList = FXCollections.observableArrayList();
    private FilteredList<Agent> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public AgentsTableController(AgentsTable model, AgentsTableView view) {
        this.model = model;
        this.view = view;
        loadAgentsFromModel();
        filteredData = new FilteredList<Agent>(agentList, new Predicate<Agent>() {
            public boolean test(Agent agent) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<Agent> sortedData = new SortedList<Agent>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.setAddButtonHandler(new AddAgentHandler());
        view.setEditButtonHandler(new EditAgentHandler());
        view.setDeleteButtonHandler(new DeleteAgentHandler());
    }

    // Загружает агентов из модели (которая работает с базой данных)
    public void loadAgentsFromModel() {
        agentList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            model.loadFromDatabase(conn);
            agentList.addAll(model.getAll());
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Слушатель изменений поля поиска
    private class SearchFieldListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Слушатель изменений выбора столбца фильтрации
    private class FilterColumnListener implements javafx.beans.value.ChangeListener<String> {
        public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldVal, String newVal) {
            updateFilter();
        }
    }

    // Обновляет фильтр агентов
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<Agent>() {
            public boolean test(Agent agent) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(agent.getId()).contains(filter);
                if ("ID пользователя".equals(column)) return String.valueOf(agent.getIdUser()).contains(filter);
                if ("ID филиала".equals(column)) return String.valueOf(agent.getIdBranch()).contains(filter);
                if ("Фамилия".equals(column)) return agent.getSurname().toLowerCase().contains(filter);
                if ("Имя".equals(column)) return agent.getName().toLowerCase().contains(filter);
                if ("Отчество".equals(column)) return agent.getPatronymic() != null && agent.getPatronymic().toLowerCase().contains(filter);
                if ("Адрес".equals(column)) return agent.getAddress().toLowerCase().contains(filter);
                if ("Телефон".equals(column)) return agent.getPhoneNumber().toLowerCase().contains(filter);
                return String.valueOf(agent.getId()).contains(filter)
                    || String.valueOf(agent.getIdUser()).contains(filter)
                    || String.valueOf(agent.getIdBranch()).contains(filter)
                    || agent.getSurname().toLowerCase().contains(filter)
                    || agent.getName().toLowerCase().contains(filter)
                    || (agent.getPatronymic() != null && agent.getPatronymic().toLowerCase().contains(filter))
                    || agent.getAddress().toLowerCase().contains(filter)
                    || agent.getPhoneNumber().toLowerCase().contains(filter);
            }
        });
    }

    // Показывает окно ошибки
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Обработчик кнопки "Добавить агента"
    private class AddAgentHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить агента");
            VBox vbox = new VBox(10);
            TextField idUserField = new TextField();
            TextField idBranchField = new TextField();
            TextField surnameField = new TextField();
            TextField nameField = new TextField();
            TextField patronymicField = new TextField();
            TextField addressField = new TextField();
            TextField phoneField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddAgentHandler(idUserField, idBranchField, surnameField, nameField, patronymicField, addressField, phoneField, dialog));
            vbox.getChildren().addAll(new Label("ID пользователя:"), idUserField, new Label("ID филиала:"), idBranchField, new Label("Фамилия:"), surnameField, new Label("Имя:"), nameField, new Label("Отчество:"), patronymicField, new Label("Адрес:"), addressField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 350, 400));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить агента"
    private class EditAgentHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Agent selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить агента");
            VBox vbox = new VBox(10);
            TextField idUserField = new TextField(String.valueOf(selected.getIdUser()));
            TextField idBranchField = new TextField(String.valueOf(selected.getIdBranch()));
            TextField surnameField = new TextField(selected.getSurname());
            TextField nameField = new TextField(selected.getName());
            TextField patronymicField = new TextField(selected.getPatronymic());
            TextField addressField = new TextField(selected.getAddress());
            TextField phoneField = new TextField(selected.getPhoneNumber());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditAgentHandler(idUserField, idBranchField, surnameField, nameField, patronymicField, addressField, phoneField, selected, dialog));
            vbox.getChildren().addAll(new Label("ID пользователя:"), idUserField, new Label("ID филиала:"), idBranchField, new Label("Фамилия:"), surnameField, new Label("Имя:"), nameField, new Label("Отчество:"), patronymicField, new Label("Адрес:"), addressField, new Label("Телефон:"), phoneField, okBtn);
            dialog.setScene(new Scene(vbox, 350, 400));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить агента"
    private class DeleteAgentHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Agent selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                model.remove(selected, conn);
                conn.close();
            } catch (SQLException ex) {
                showError("Невозможно удалить запись: есть связанные записи в других таблицах.");
            }
            loadAgentsFromModel();
        }
    }

    // Обработчик OK в диалоге добавления агента
    private class OkAddAgentHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField idUserField, idBranchField, surnameField, nameField, patronymicField, addressField, phoneField;
        private Stage dialog;
        public OkAddAgentHandler(TextField idUserField, TextField idBranchField, TextField surnameField, TextField nameField, TextField patronymicField, TextField addressField, TextField phoneField, Stage dialog) {
            this.idUserField = idUserField;
            this.idBranchField = idBranchField;
            this.surnameField = surnameField;
            this.nameField = nameField;
            this.patronymicField = patronymicField;
            this.addressField = addressField;
            this.phoneField = phoneField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            try {
                int idUser = Integer.parseInt(idUserField.getText().trim());
                int idBranch = Integer.parseInt(idBranchField.getText().trim());
                String surname = surnameField.getText().trim();
                String name = nameField.getText().trim();
                String patronymic = patronymicField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                if (!surname.isEmpty() && !name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                    Agent newAgent = new Agent(0, idUser, idBranch, surname, name, patronymic, address, phone);
                    Connection conn = new ConnectionDatabase().connection;
                    model.add(newAgent, conn);
                    conn.close();
                    loadAgentsFromModel();
                    dialog.close();
                } else {
                    showError("Не все поля заполнены корректно.");
                }
            } catch (Exception ex) {
                showError("Не удалось добавить запись из-за ошибки.");
            }
        }
    }

    // Обработчик OK в диалоге редактирования агента
    private class OkEditAgentHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField idUserField, idBranchField, surnameField, nameField, patronymicField, addressField, phoneField;
        private Agent selected;
        private Stage dialog;
        public OkEditAgentHandler(TextField idUserField, TextField idBranchField, TextField surnameField, TextField nameField, TextField patronymicField, TextField addressField, TextField phoneField, Agent selected, Stage dialog) {
            this.idUserField = idUserField;
            this.idBranchField = idBranchField;
            this.surnameField = surnameField;
            this.nameField = nameField;
            this.patronymicField = patronymicField;
            this.addressField = addressField;
            this.phoneField = phoneField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            try {
                int idUser = Integer.parseInt(idUserField.getText().trim());
                int idBranch = Integer.parseInt(idBranchField.getText().trim());
                String surname = surnameField.getText().trim();
                String name = nameField.getText().trim();
                String patronymic = patronymicField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                if (!surname.isEmpty() && !name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                    selected.setIdUser(idUser);
                    selected.setIdBranch(idBranch);
                    selected.setSurname(surname);
                    selected.setName(name);
                    selected.setPatronymic(patronymic);
                    selected.setAddress(address);
                    selected.setPhoneNumber(phone);
                    Connection conn = new ConnectionDatabase().connection;
                    model.update(selected, conn);
                    conn.close();
                    loadAgentsFromModel();
                    dialog.close();
                } else {
                    showError("Не все поля заполнены корректно.");
                }
            } catch (Exception ex) {
                showError("Не удалось обновить запись из-за ошибки.");
            }
        }
    }
} 