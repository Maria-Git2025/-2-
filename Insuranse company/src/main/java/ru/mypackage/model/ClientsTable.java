package ru.mypackage.model;

import java.util.ArrayList;
import java.sql.*;

// Модель таблицы клиентов
public class ClientsTable implements TableModel<Client> {
    private static ClientsTable instance;
    private ArrayList<Client> clients;

    private ClientsTable() {
        clients = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы клиентов
    public static ClientsTable getInstance() {
        if (instance == null) {
            instance = new ClientsTable();
        }
        return instance;
    }

    // Добавляет клиента в список
    public void addClient(Client client) {
        clients.add(client);
    }

    // Добавляет клиента в базу и список
    @Override
    public void add(Client client, Connection conn) throws SQLException {
        String sql = "INSERT INTO Clients(id_user, surname, name, patronymic, date_of_birth, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, client.getIdUser());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, client.getName());
            stmt.setString(4, client.getPatronymic());
            stmt.setString(5, client.getDateOfBirth());
            stmt.setString(6, client.getPhoneNumber());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                    this.clients.add(client);
                }
            }
        }
    }

    // Удаляет клиента из базы и списка
    @Override
    public void remove(Client client, Connection conn) throws SQLException {
        String sql = "DELETE FROM Clients WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, client.getId());
            stmt.executeUpdate();
        }
        clients.remove(client);
    }

    // Обновляет клиента в базе
    @Override
    public void update(Client client, Connection conn) throws SQLException {
        String sql = "UPDATE Clients SET surname = ?, name = ?, patronymic = ?, date_of_birth = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getSurname());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getPatronymic());
            stmt.setString(4, client.getDateOfBirth());
            stmt.setString(5, client.getPhoneNumber());
            stmt.setInt(6, client.getId());
            stmt.executeUpdate();
        }
    }

    // Ищет клиента по id
    @Override
    public Client findById(int id) {
        for (Client client : clients) {
            if (client.getId() == id) return client;
        }
        return null;
    }

    // Возвращает всех клиентов
    @Override
    public java.util.List<Client> getAll() {
        return clients;
    }

    // Загружает клиентов из базы данных
    @Override
    public void loadFromDatabase(java.sql.Connection conn) throws java.sql.SQLException {
        clients.clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Clients");
        
        while (rs.next()) {
            int id = rs.getInt("id");
            int idUser = rs.getInt("id_user");
            String surname = rs.getString("surname");
            String name = rs.getString("name");
            String patronymic = rs.getString("patronymic");
            String dateOfBirth = rs.getString("date_of_birth");
            String phoneNumber = rs.getString("phone_number");
            
            Client client = new Client(id, idUser, surname, name, patronymic, dateOfBirth, phoneNumber);
            clients.add(client);
        }
        
        rs.close();
        stmt.close();
    }
} 