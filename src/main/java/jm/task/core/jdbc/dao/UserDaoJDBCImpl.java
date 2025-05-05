package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoJDBCImpl.class.getName());

    public UserDaoJDBCImpl() {}

    public void createUsersTable() {
        try (Connection connection = Util.getJdbcConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQLQueries.CREATE_TABLE);
            logger.info("Таблица создана.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании таблицы.", e);
        }
    }

    public void dropUsersTable() {
        try (Connection connection = Util.getJdbcConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQLQueries.DROP_TABLE);
            logger.info("Таблица удалена.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении таблицы.", e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        if (age < 0) {
            throw new IllegalArgumentException("Возраст не может быть отрицательным.");
        }

        try (Connection connection = Util.getJdbcConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.INSERT)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            logger.info("Пользователь с именем " + name + " добавлен в базу данных.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при сохранении пользователя.", e);
        }
    }

    public void removeUserById(long id) {
        try (Connection connection = Util.getJdbcConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.REMOVE_BY_ID)) {
            preparedStatement.setLong(1, id);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.info("Пользователь с ID " + id + " удалён.");
            } else {
                logger.warning("Пользователь с ID " + id + " не найден.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении пользователя.", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = Util.getJdbcConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQLQueries.SELECT_ALL)) {
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getByte("age")
                );
                user.setId(resultSet.getLong("id"));
                users.add(user);
            }
            logger.info("Загружено пользователей: " + users.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении пользователей.", e);
        }
        return users;
    }

    public void cleanUsersTable() {
        try (Connection connection = Util.getJdbcConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQLQueries.TRUNCATE_TABLE);
            logger.info("Таблица очищена.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при очистке таблицы.", e);
        }
    }
}