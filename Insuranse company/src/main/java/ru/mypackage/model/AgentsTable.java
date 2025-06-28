package ru.mypackage.model;

import java.util.ArrayList;
import java.sql.*;

// Модель таблицы агентов
public class AgentsTable implements TableModel<Agent> {
    private static AgentsTable instance;
    private ArrayList<Agent> agents;

    private AgentsTable() {
        agents = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы агентов
    public static AgentsTable getInstance() {
        if (instance == null) {
            instance = new AgentsTable();
        }
        return instance;
    }

    // Добавляет агента в базу и список
    @Override
    public void add(Agent agent, Connection conn) throws SQLException {
        String sql = "INSERT INTO Insurance_Agents(id_user, id_branch, surname, name, patronymic, address, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, agent.getIdUser());
            stmt.setInt(2, agent.getIdBranch());
            stmt.setString(3, agent.getSurname());
            stmt.setString(4, agent.getName());
            stmt.setString(5, agent.getPatronymic());
            stmt.setString(6, agent.getAddress());
            stmt.setString(7, agent.getPhoneNumber());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    agent.setId(generatedKeys.getInt(1));
                    this.agents.add(agent);
                }
            }
        }
    }

    // Возвращает всех агентов
    @Override
    public java.util.List<Agent> getAll() {
        return agents;
    }

    // Загружает агентов из базы данных
    @Override
    public void loadFromDatabase(Connection conn) throws SQLException {
        agents.clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Insurance_Agents");
        
        while (rs.next()) {
            int id = rs.getInt("id");
            int idUser = rs.getInt("id_user");
            int idBranch = rs.getInt("id_branch");
            String surname = rs.getString("surname");
            String name = rs.getString("name");
            String patronymic = rs.getString("patronymic");
            String address = rs.getString("address");
            String phoneNumber = rs.getString("phone_number");
            
            Agent agent = new Agent(id, idUser, idBranch, surname, name, patronymic, address, phoneNumber);
            agents.add(agent);
        }
        
        rs.close();
        stmt.close();
    }

    // Удаляет агента из базы и списка
    @Override
    public void remove(Agent agent, Connection conn) throws SQLException {
        String sql = "DELETE FROM Insurance_Agents WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agent.getId());
            stmt.executeUpdate();
        }
        agents.remove(agent);
    }

    // Обновляет агента в базе
    @Override
    public void update(Agent agent, Connection conn) throws SQLException {
        String sql = "UPDATE Insurance_Agents SET id_user = ?, id_branch = ?, surname = ?, name = ?, patronymic = ?, address = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agent.getIdUser());
            stmt.setInt(2, agent.getIdBranch());
            stmt.setString(3, agent.getSurname());
            stmt.setString(4, agent.getName());
            stmt.setString(5, agent.getPatronymic());
            stmt.setString(6, agent.getAddress());
            stmt.setString(7, agent.getPhoneNumber());
            stmt.setInt(8, agent.getId());
            stmt.executeUpdate();
        }
    }

    // Ищет агента по id
    @Override
    public Agent findById(int id) {
        for (Agent agent : agents) {
            if (agent.getId() == id) return agent;
        }
        return null;
    }
} 