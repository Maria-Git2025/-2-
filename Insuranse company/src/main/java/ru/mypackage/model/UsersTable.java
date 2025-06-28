package ru.mypackage.model;

import java.util.ArrayList;
import java.sql.*;

// Модель таблицы пользователей
public class UsersTable implements TableModel<User> {
    private static UsersTable instance;
    private ArrayList<User> users;

    private UsersTable() {
        users = new ArrayList<>();
    }

    // Возвращает единственный экземпляр таблицы пользователей
    public static UsersTable getInstance() {
        if (instance == null) {
            instance = new UsersTable();
        }
        return instance;
    }

    // Добавляет пользователя в список
    public void addUser(User user) {
        try {
            Connection conn = new ru.mypackage.database.ConnectionDatabase().connection;
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Users(login, password, role) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            rs.close();
            stmt.close();
            conn.close();
            users.add(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ищет пользователя по логину
    public User findByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    // Добавляет пользователя в базу и список
    @Override
    public void add(User user, java.sql.Connection conn) throws java.sql.SQLException {
        addUser(user);
    }

    // Удаляет пользователя из базы и списка
    @Override
    public void remove(User user, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        }
        users.remove(user);
    }

    // Обновляет пользователя в базе
    @Override
    public void update(User user, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "UPDATE Users SET login = ?, password = ?, role = ? WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }
    }

    // Ищет пользователя по id
    @Override
    public User findById(int id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }

    // Возвращает всех пользователей
    @Override
    public java.util.List<User> getAll() {
        return users;
    }

    // Загружает пользователей из базы данных
    @Override
    public void loadFromDatabase(java.sql.Connection conn) throws java.sql.SQLException {
        users.clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Users");
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String login = rs.getString("login");
            String password = rs.getString("password");
            String role = rs.getString("role");
            
            User user = new User(id, login, password, role);
            users.add(user);
        }
        
        rs.close();
        stmt.close();
    }
} 