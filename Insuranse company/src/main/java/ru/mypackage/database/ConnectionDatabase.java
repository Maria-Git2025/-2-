package ru.mypackage.database;

import java.sql.*;

// Класс для управления соединением с базой данных
public class ConnectionDatabase {
    public Connection connection;

    // Конструктор: устанавливает соединение с базой данных
    public ConnectionDatabase() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Insurance%20company", "root", "");
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e);
        }
    }
} 