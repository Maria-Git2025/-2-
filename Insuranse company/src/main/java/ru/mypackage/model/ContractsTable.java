package ru.mypackage.model;

import java.util.ArrayList;
import java.sql.*;
import java.util.List;

// Модель таблицы договоров
public class ContractsTable implements TableModel<Contract> {
    private static ContractsTable instance;
    private ArrayList<Contract> contracts;

    private ContractsTable() {
        contracts = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы договоров
    public static ContractsTable getInstance() {
        if (instance == null) {
            instance = new ContractsTable();
        }
        return instance;
    }

    // Добавляет договор в базу и список
    @Override
    public void add(Contract contract, Connection conn) throws SQLException {
        String sql = "INSERT INTO Contracts(id_branch, id_client, id_agent, id_insurance_type, date_signed, insurance_amount, tariff_rate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contract.getIdBranch());
            stmt.setInt(2, contract.getIdClient());
            stmt.setInt(3, contract.getIdAgent());
            stmt.setInt(4, contract.getIdInsuranceType());
            stmt.setString(5, contract.getDateSigned());
            stmt.setString(6, contract.getInsuranceAmount());
            stmt.setString(7, contract.getTariffRate());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contract.setId(generatedKeys.getInt(1));
                    this.contracts.add(contract);
                }
            }
        }
    }

    // Обновляет договор в базе
    @Override
    public void update(Contract contract, Connection conn) throws SQLException {
        String sql = "UPDATE Contracts SET id_branch = ?, id_client = ?, id_agent = ?, id_insurance_type = ?, date_signed = ?, insurance_amount = ?, tariff_rate = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contract.getIdBranch());
            stmt.setInt(2, contract.getIdClient());
            stmt.setInt(3, contract.getIdAgent());
            stmt.setInt(4, contract.getIdInsuranceType());
            stmt.setString(5, contract.getDateSigned());
            stmt.setString(6, contract.getInsuranceAmount());
            stmt.setString(7, contract.getTariffRate());
            stmt.setInt(8, contract.getId());
            stmt.executeUpdate();
        }
    }

    // Удаляет договор из базы и списка
    @Override
    public void remove(Contract contract, Connection conn) throws SQLException {
        String sql = "DELETE FROM Contracts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contract.getId());
            stmt.executeUpdate();
        }
        contracts.remove(contract);
    }

    // Ищет договор по id
    @Override
    public Contract findById(int id) {
        for (Contract contract : contracts) {
            if (contract.getId() == id) return contract;
        }
        return null;
    }

    // Возвращает все договоры
    @Override
    public List<Contract> getAll() {
        return contracts;
    }

    // Загружает договоры из базы данных
    @Override
    public void loadFromDatabase(Connection conn) throws SQLException {
        contracts.clear();
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
            
            Contract contract = new Contract(id, idBranch, idClient, idAgent, idInsuranceType, dateSigned, insuranceAmount, tariffRate, "", "", "");
            contracts.add(contract);
        }
        
        rs.close();
        stmt.close();
    }
} 