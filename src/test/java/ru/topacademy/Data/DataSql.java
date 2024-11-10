package ru.topacademy.Data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class DataSql {

    // Приватный конструктор, чтобы предотвратить создание экземпляров класса и
    // выброса исключения при попытке создать экземпляр класса
    private DataSql() {
        throw new UnsupportedOperationException("Utility class");
    }


    // Константы для подключения к базе данных, получаемые из системных свойств.
    private static final String url = System.getProperty("db.url");
    private static final String user = System.getProperty("db.user");
    private static final String password = System.getProperty("db.password");

    // Метод для очистки таблиц в базе данных.
    public static void clearTables() {
        // SQL-запросы для удаления всех записей из таблиц.
        String deleteOrderEntity = "DELETE FROM order_entity";
        String deletePaymentEntity = "DELETE FROM payment_entity";
        String deleteCreditRequestEntity = "DELETE FROM credit_request_entity";
        QueryRunner runner = new QueryRunner(); // Создание экземпляра QueryRunner для выполнения запросов.

        // Подключение к базе данных и выполнение запросов на удаление.
        //Блок try с Connection автоматически закрывает объект Connection после завершения работы,воизбежание утечек ресурсов.
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            runner.update(conn, deleteOrderEntity);
            runner.update(conn, deletePaymentEntity);
            runner.update(conn, deleteCreditRequestEntity);
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка исключений при работе с базой данных.
        }
    }

    // Метод для поиска статуса платежа в базе данных.
    public static String findPayStatus() {
        String statusSQL = "SELECT status FROM payment_entity"; // SQL-запрос для получения статуса платежа.
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement(); // Создание объекта для выполнения SQL-запросов.
            ResultSet resultSet = statement.executeQuery(statusSQL); // Выполнение запроса и получение результата.
            if (resultSet.next()) {
                return resultSet.getString("status"); // Возврат статуса, если он найден.
            } else {
                return "Статус не найден"; // Возврат сообщения, если статус не найден.
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка исключений.
            return "ОШИБКА: Не удалось получить статус покупки из базы данных."; // Возврат сообщения об ошибке.
        }
    }

    // Метод для поиска статуса кредита в базе данных.
    public static String findCreditStatus() {
        String statusSQL = "SELECT status FROM credit_request_entity"; // SQL-запрос для получения статуса кредита.
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement(); // Создание объекта для выполнения SQL-запросов.
            ResultSet resultSet = statement.executeQuery(statusSQL); // Выполнение запроса и получение результата.
            if (resultSet.next()) {
                return resultSet.getString("status"); // Возврат статуса, если он найден.
            } else {
                return "Статус не найден"; // Возврат сообщения, если статус не найден.
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка исключений.
            return "ОШИБКА: Не удалось получить статус покупки в кредит из базы данных."; // Возврат сообщения об ошибке.
        }
    }

    // Метод для получения данных по выполненному SQL-запросу.


    // Метод для проверки состояния базы данных и получения количества записей в таблице order_entity.
    public static long getOrderEntityCount() {
        String countSQL = "SELECT COUNT(*) FROM order_entity;"; // SQL-запрос для подсчета записей.
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            QueryRunner runner = new QueryRunner();
            Long count = runner.query(conn, countSQL, new ScalarHandler<>()); // Выполнение запроса и получение количества.
            return count != null ? count : 0; // Возврат количества или 0, если результат равен null.
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка исключений.
            return 0; // Возврат 0 в случае ошибки.
        }
    }
}