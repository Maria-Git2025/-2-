package ru.mypackage;
import java.sql.*;  // Импортируем все основные JDBC-классы

public class Main {
    public static void main(String[] args) {
        try {
            // 1) Ручная загрузка класса драйвера MySQL
            //    - "com.mysql.cj.jdbc.Driver" — имя класса официального драйвера MySQL Connector/J 8+.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2) Установка соединения с базой данных:
            //    - URL: "jdbc:mysql://localhost:3306/company"
            //        jdbc:mysql://      — протокол JDBC + подпротокол MySQL
            //        localhost:3306     — адрес и порт сервера MySQL
            //        /company           — имя базы данных на сервере
            //    - "root", ""         — логин и пароль для доступа к БД
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/company",
                    "root",
                    ""
            );

            // 3) Создаём объект Statement для отправки SQL-запросов.
            Statement statement = connection.createStatement();

            // 4) Текст SQL-запроса:
            String query = "SELECT * FROM posts";

            // 5) Выполняем запрос и получаем результат в виде объекта ResultSet.
            //    result — «курсор» по строкам выборки (каждая строка = одна запись таблицы).
            ResultSet result = statement.executeQuery(query);

            // 6) Обходим все строки результата:
            //    result.next() переводит курсор на следующую строку;
            //    возвращает false, когда строки кончились.
            while (result.next()) {
                // 7) Из текущей строки извлекаем данные:
                //    getInt("id")         — получаем целочисленное значение из колонки id
                int id = result.getInt("id");
                //    getString("name")    — получаем строку из колонки name
                String name = result.getString("name");
                //    getString("short_name") — строка из колонки short_name
                String short_name = result.getString("short_name");

                // 8) Формируем вывод в консоль:
                System.out.print("Vacant post: ");
                System.out.print("id = " + id);
                System.out.print(", name = \"" + name + "\"");
                System.out.println(", short name = \"" + short_name + "\".");
            }

            // 9) Закрываем соединение с базой.
            //    Это важно, чтобы освободить ресурсы на сервере и в клиенте.
            connection.close();
        }
        catch (Exception e) {
            // 10) В случае любая ошибка выводится в консоль.
            System.out.println(e);
        }
    }
}