package ru.mypackage.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Интерфейс для моделей таблиц (CRUD + загрузка из базы)
public interface TableModel<T> {
    // Добавляет объект в таблицу и базу
    void add(T obj, Connection conn) throws SQLException;
    // Удаляет объект из таблицы и базы
    void remove(T obj, Connection conn) throws SQLException;
    // Обновляет объект в базе
    void update(T obj, Connection conn) throws SQLException;
    // Ищет объект по id
    T findById(int id);
    // Возвращает все объекты
    List<T> getAll();
    // Загружает объекты из базы данных
    void loadFromDatabase(Connection conn) throws SQLException;
} 