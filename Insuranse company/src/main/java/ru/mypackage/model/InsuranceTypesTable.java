package ru.mypackage.model;

import java.util.ArrayList;

// Модель таблицы видов страхования
public class InsuranceTypesTable implements TableModel<InsuranceType> {
    private static InsuranceTypesTable instance;
    private ArrayList<InsuranceType> insuranceTypes;

    private InsuranceTypesTable() {
        insuranceTypes = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы видов страхования
    public static InsuranceTypesTable getInstance() {
        if (instance == null) {
            instance = new InsuranceTypesTable();
        }
        return instance;
    }

    // Добавляет вид страхования в базу и список
    @Override
    public void add(InsuranceType type, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "INSERT INTO Insurance_Types(name, agent_percentage) VALUES (?, ?)";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getAgentPercentage());
            stmt.executeUpdate();
            try (java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    type.setId(generatedKeys.getInt(1));
                    this.insuranceTypes.add(type);
                }
            }
        }
    }

    // Возвращает все виды страхования
    @Override
    public java.util.List<InsuranceType> getAll() {
        return insuranceTypes;
    }

    // Удаляет вид страхования из списка и базы
    public void removeInsuranceType(InsuranceType type, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "DELETE FROM Insurance_Types WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
        }
        insuranceTypes.remove(type);
    }

    // Удаляет вид страхования из базы и списка
    @Override
    public void remove(InsuranceType type, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "DELETE FROM Insurance_Types WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
        }
        insuranceTypes.remove(type);
    }

    // Обновляет вид страхования в базе
    public void updateInsuranceTypeInDb(InsuranceType type, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "UPDATE Insurance_Types SET name = ?, agent_percentage = ? WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getAgentPercentage());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        }
    }

    // Обновляет вид страхования в базе
    @Override
    public void update(InsuranceType type, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "UPDATE Insurance_Types SET name = ?, agent_percentage = ? WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getAgentPercentage());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        }
    }

    // Ищет вид страхования по id
    @Override
    public InsuranceType findById(int id) {
        for (InsuranceType type : insuranceTypes) {
            if (type.getId() == id) return type;
        }
        return null;
    }

    // Загружает виды страхования из базы данных
    @Override
    public void loadFromDatabase(java.sql.Connection conn) throws java.sql.SQLException {
        insuranceTypes.clear();
        java.sql.Statement stmt = conn.createStatement();
        java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM Insurance_Types");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String agentPercentage = rs.getString("agent_percentage");
            InsuranceType type = new InsuranceType(id, name, agentPercentage);
            insuranceTypes.add(type);
        }
        rs.close();
        stmt.close();
    }
} 