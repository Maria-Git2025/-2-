package ru.mypackage.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mypackage.model.Contract;
import ru.mypackage.model.ContractsTable;
import ru.mypackage.view.ContractsTableView;
import ru.mypackage.database.ConnectionDatabase;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import java.util.function.Predicate;

// Контроллер для таблицы договоров
public class ContractsTableController {
    private ContractsTable model;
    private ContractsTableView view;
    private ObservableList<Contract> contractList = FXCollections.observableArrayList();
    private FilteredList<Contract> filteredData;

    // Конструктор: связывает модель и view, назначает обработчики
    public ContractsTableController(ContractsTable model, ContractsTableView view) {
        this.model = model;
        this.view = view;
        loadContracts();
        filteredData = new FilteredList<Contract>(contractList, new Predicate<Contract>() {
            public boolean test(Contract contract) { return true; }
        });
        view.searchField.textProperty().addListener(new SearchFieldListener());
        view.filterColumnBox.valueProperty().addListener(new FilterColumnListener());
        SortedList<Contract> sortedData = new SortedList<Contract>(filteredData);
        sortedData.comparatorProperty().bind(view.table.comparatorProperty());
        view.table.setItems(sortedData);
        view.setAddButtonHandler(new AddContractHandler());
        view.setEditButtonHandler(new EditContractHandler());
        view.setDeleteButtonHandler(new DeleteContractHandler());
    }

    // Загружает договоры из базы данных
    public void loadContracts() {
        contractList.clear();
        try {
            Connection conn = new ConnectionDatabase().connection;
            ru.mypackage.model.InsuranceTypesTable.getInstance().loadFromDatabase(conn);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Contracts");
            while (rs.next()) {
                int id = rs.getInt("id");
                int idBranch = rs.getInt("id_branch");
                int idClient = rs.getInt("id_client");
                int idAgent = rs.getInt("id_agent");
                int idInsuranceType = rs.getInt("id_insurance_type");
                String dateSigned = rs.getString("date_signed");
                String insuranceAmount = rs.getString("insurance_amount");
                String tariffRate = rs.getString("tariff_rate");
                contractList.add(new Contract(id, idBranch, idClient, idAgent, idInsuranceType, dateSigned, insuranceAmount, tariffRate, "", "", ""));
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

    // Обновляет фильтр договоров
    private void updateFilter() {
        final String filter = view.searchField.getText().toLowerCase();
        final String column = view.filterColumnBox.getValue();
        filteredData.setPredicate(new Predicate<Contract>() {
            public boolean test(Contract contract) {
                if (filter == null || filter.isEmpty()) return true;
                if ("ID".equals(column)) return String.valueOf(contract.getId()).contains(filter);
                if ("ID филиала".equals(column)) return String.valueOf(contract.getIdBranch()).contains(filter);
                if ("ID клиента".equals(column)) return String.valueOf(contract.getIdClient()).contains(filter);
                if ("ID агента".equals(column)) return String.valueOf(contract.getIdAgent()).contains(filter);
                if ("ID вида страхования".equals(column)) return String.valueOf(contract.getIdInsuranceType()).contains(filter);
                if ("Дата заключения".equals(column)) return contract.getDateSigned().toLowerCase().contains(filter);
                if ("Страховая сумма".equals(column)) return contract.getInsuranceAmount().toLowerCase().contains(filter);
                if ("Тарифная ставка".equals(column)) return contract.getTariffRate().toLowerCase().contains(filter);
                return String.valueOf(contract.getId()).contains(filter)
                    || String.valueOf(contract.getIdBranch()).contains(filter)
                    || String.valueOf(contract.getIdClient()).contains(filter)
                    || String.valueOf(contract.getIdAgent()).contains(filter)
                    || String.valueOf(contract.getIdInsuranceType()).contains(filter)
                    || contract.getDateSigned().toLowerCase().contains(filter)
                    || contract.getInsuranceAmount().toLowerCase().contains(filter)
                    || contract.getTariffRate().toLowerCase().contains(filter);
            }
        });
    }

    // Показывает окно ошибки
    private void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("Не удалось добавить запись из-за некорректных данных.");
        alert.showAndWait();
    }

    // Обработчик кнопки "Добавить договор"
    public class AddContractHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Stage dialog = new Stage();
            dialog.setTitle("Добавить договор");
            VBox vbox = new VBox(10);
            TextField idBranchField = new TextField();
            TextField idClientField = new TextField();
            TextField idAgentField = new TextField();
            TextField idInsuranceTypeField = new TextField();
            TextField dateSignedField = new TextField();
            TextField insuranceAmountField = new TextField();
            TextField tariffRateField = new TextField();
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkAddContractHandler(idBranchField, idClientField, idAgentField, idInsuranceTypeField, dateSignedField, insuranceAmountField, tariffRateField, dialog));
            vbox.getChildren().addAll(new Label("ID филиала:"), idBranchField, new Label("ID клиента:"), idClientField, new Label("ID агента:"), idAgentField, new Label("ID типа страхования:"), idInsuranceTypeField, new Label("Дата подписания:"), dateSignedField, new Label("Страховая сумма:"), insuranceAmountField, new Label("Тарифная ставка:"), tariffRateField, okBtn);
            dialog.setScene(new Scene(vbox, 350, 500));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Изменить договор"
    public class EditContractHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Contract selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Stage dialog = new Stage();
            dialog.setTitle("Изменить договор");
            VBox vbox = new VBox(10);
            TextField idBranchField = new TextField(String.valueOf(selected.getIdBranch()));
            TextField idClientField = new TextField(String.valueOf(selected.getIdClient()));
            TextField idAgentField = new TextField(String.valueOf(selected.getIdAgent()));
            TextField idInsuranceTypeField = new TextField(String.valueOf(selected.getIdInsuranceType()));
            TextField dateSignedField = new TextField(selected.getDateSigned());
            TextField insuranceAmountField = new TextField(selected.getInsuranceAmount());
            TextField tariffRateField = new TextField(selected.getTariffRate());
            Button okBtn = new Button("OK");
            okBtn.setOnAction(new OkEditContractHandler(idBranchField, idClientField, idAgentField, idInsuranceTypeField, dateSignedField, insuranceAmountField, tariffRateField, selected, dialog));
            vbox.getChildren().addAll(new Label("ID филиала:"), idBranchField, new Label("ID клиента:"), idClientField, new Label("ID агента:"), idAgentField, new Label("ID типа страхования:"), idInsuranceTypeField, new Label("Дата подписания:"), dateSignedField, new Label("Страховая сумма:"), insuranceAmountField, new Label("Тарифная ставка:"), tariffRateField, okBtn);
            dialog.setScene(new Scene(vbox, 350, 500));
            dialog.showAndWait();
        }
    }

    // Обработчик кнопки "Удалить договор"
    public class DeleteContractHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        public void handle(javafx.event.ActionEvent e) {
            Contract selected = view.table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                Connection conn = new ConnectionDatabase().connection;
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Contracts WHERE id=?");
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            loadContracts();
        }
    }

    // Обработчик подтверждения добавления нового договора
    private class OkAddContractHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField idBranchField, idClientField, idAgentField, idInsuranceTypeField, dateSignedField, insuranceAmountField, tariffRateField;
        private Stage dialog;
        public OkAddContractHandler(TextField idBranchField, TextField idClientField, TextField idAgentField, TextField idInsuranceTypeField, TextField dateSignedField, TextField insuranceAmountField, TextField tariffRateField, Stage dialog) {
            this.idBranchField = idBranchField;
            this.idClientField = idClientField;
            this.idAgentField = idAgentField;
            this.idInsuranceTypeField = idInsuranceTypeField;
            this.dateSignedField = dateSignedField;
            this.insuranceAmountField = insuranceAmountField;
            this.tariffRateField = tariffRateField;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            try {
                int idBranch = Integer.parseInt(idBranchField.getText().trim());
                int idClient = Integer.parseInt(idClientField.getText().trim());
                int idAgent = Integer.parseInt(idAgentField.getText().trim());
                int idInsuranceType = Integer.parseInt(idInsuranceTypeField.getText().trim());
                String dateSigned = dateSignedField.getText().trim();
                String insuranceAmount = insuranceAmountField.getText().trim();
                String tariffRate = tariffRateField.getText().trim();
                if (!dateSigned.isEmpty() && !insuranceAmount.isEmpty() && !tariffRate.isEmpty()) {
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Contracts(id_branch, id_client, id_agent, id_insurance_type, date_signed, insurance_amount, tariff_rate) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    stmt.setInt(1, idBranch);
                    stmt.setInt(2, idClient);
                    stmt.setInt(3, idAgent);
                    stmt.setInt(4, idInsuranceType);
                    stmt.setString(5, dateSigned);
                    stmt.setString(6, insuranceAmount);
                    stmt.setString(7, tariffRate);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadContracts();
                    dialog.close();
                } else {
                    showError();
                }
            } catch (Exception ex) {
                showError();
            }
        }
    }

    // Обработчик подтверждения редактирования выбранного договора
    private class OkEditContractHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        private TextField idBranchField, idClientField, idAgentField, idInsuranceTypeField, dateSignedField, insuranceAmountField, tariffRateField;
        private Contract selected;
        private Stage dialog;
        public OkEditContractHandler(TextField idBranchField, TextField idClientField, TextField idAgentField, TextField idInsuranceTypeField, TextField dateSignedField, TextField insuranceAmountField, TextField tariffRateField, Contract selected, Stage dialog) {
            this.idBranchField = idBranchField;
            this.idClientField = idClientField;
            this.idAgentField = idAgentField;
            this.idInsuranceTypeField = idInsuranceTypeField;
            this.dateSignedField = dateSignedField;
            this.insuranceAmountField = insuranceAmountField;
            this.tariffRateField = tariffRateField;
            this.selected = selected;
            this.dialog = dialog;
        }
        public void handle(javafx.event.ActionEvent e) {
            try {
                int idBranch = Integer.parseInt(idBranchField.getText().trim());
                int idClient = Integer.parseInt(idClientField.getText().trim());
                int idAgent = Integer.parseInt(idAgentField.getText().trim());
                int idInsuranceType = Integer.parseInt(idInsuranceTypeField.getText().trim());
                String dateSigned = dateSignedField.getText().trim();
                String insuranceAmount = insuranceAmountField.getText().trim();
                String tariffRate = tariffRateField.getText().trim();
                if (!dateSigned.isEmpty() && !insuranceAmount.isEmpty() && !tariffRate.isEmpty()) {
                    if (!dateSigned.matches("\\d{4}-\\d{2}-\\d{2}")) throw new Exception();
                    Double.parseDouble(insuranceAmount.replace(",", "."));
                    Double.parseDouble(tariffRate.replace(",", "."));
                    Connection conn = new ConnectionDatabase().connection;
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Contracts SET id_branch=?, id_client=?, id_agent=?, id_insurance_type=?, date_signed=?, insurance_amount=?, tariff_rate=? WHERE id=?");
                    stmt.setInt(1, idBranch);
                    stmt.setInt(2, idClient);
                    stmt.setInt(3, idAgent);
                    stmt.setInt(4, idInsuranceType);
                    stmt.setString(5, dateSigned);
                    stmt.setString(6, insuranceAmount);
                    stmt.setString(7, tariffRate);
                    stmt.setInt(8, selected.getId());
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                    loadContracts();
                    dialog.close();
                } else {
                    showError();
                }
            } catch (Exception ex) {
                showError();
            }
        }
    }
} 