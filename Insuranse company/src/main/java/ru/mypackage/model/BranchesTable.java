package ru.mypackage.model;

import java.util.ArrayList;
import java.sql.*;

// Модель таблицы филиалов
public class BranchesTable implements TableModel<Branch> {
    private static BranchesTable instance;
    private ArrayList<Branch> branches;

    private BranchesTable() {
        branches = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы филиалов
    public static BranchesTable getInstance() {
        if (instance == null) {
            instance = new BranchesTable();
        }
        return instance;
    }
    
    // Добавляет филиал в базу и список
    @Override
    public void add(Branch branch, Connection conn) throws SQLException {
        String sql = "INSERT INTO Branches(name, address, phone_number) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getPhoneNumber());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    branch.setId(generatedKeys.getInt(1));
                    this.branches.add(branch);
                }
            }
        }
    }

    // Возвращает все филиалы
    @Override
    public java.util.List<Branch> getAll() {
        return branches;
    }

    // Загружает филиалы из базы данных
    @Override
    public void loadFromDatabase(java.sql.Connection conn) throws java.sql.SQLException {
        branches.clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Branches");
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String address = rs.getString("address");
            String phoneNumber = rs.getString("phone_number");
            
            Branch branch = new Branch(id, name, address, phoneNumber);
            branches.add(branch);
        }
        
        rs.close();
        stmt.close();
    }

    // Удаляет филиал из базы и списка
    @Override
    public void remove(Branch branch, Connection conn) throws SQLException {
        String sql = "DELETE FROM Branches WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branch.getId());
            stmt.executeUpdate();
        }
        branches.remove(branch);
    }

    // Обновляет филиал в базе
    @Override
    public void update(Branch branch, Connection conn) throws SQLException {
        String sql = "UPDATE Branches SET name = ?, address = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getPhoneNumber());
            stmt.setInt(4, branch.getId());
            stmt.executeUpdate();
        }
    }

    // Ищет филиал по id
    @Override
    public Branch findById(int id) {
        for (Branch branch : branches) {
            if (branch.getId() == id) return branch;
        }
        return null;
    }
} 