package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoJDBCImpl.class.getName());

    public UserDaoJDBCImpl() {}

    @Override
    public void createUsersTable() {
        executeStatement(SQLQueries.CREATE_TABLE,
                "Таблица создана.",
                "Ошибка при создании таблицы.");
    }

    @Override
    public void dropUsersTable() {
        executeStatement(SQLQueries.DROP_TABLE, "Таблица удалена.",
                "Ошибка при удалении таблицы.");
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        if (age < 0) throw new IllegalArgumentException("Возраст не может быть отрицательным.");

        executePreparedStatement(SQLQueries.INSERT, ps -> {
                    try {
                        ps.setString(1, name);
                        ps.setString(2, lastName);
                        ps.setByte(3, age);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, "Пользователь с именем " + name + " добавлен в базу данных.",
                "Ошибка при сохранении пользователя.");
    }

    @Override
    public void removeUserById(long id) {
        executePreparedStatement(SQLQueries.REMOVE_BY_ID, ps -> {
                    try {
                        ps.setLong(1, id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, "Пользователь с ID " + id + " удалён.",
                "Ошибка при удалении пользователя.");
    }

    @Override
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

    @Override
    public void cleanUsersTable() {
        executeStatement(SQLQueries.TRUNCATE_TABLE, "Таблица очищена.",
                "Ошибка при очистке таблицы.");
    }

    private void executeStatement(String sql, String successMessage, String errorMessage) {
        try (Connection connection = Util.getJdbcConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info(successMessage);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, errorMessage, e);
        }
    }

    private void executePreparedStatement(String sql, Consumer<PreparedStatement> preparer, String successMessage, String errorMessage) {
        try (Connection connection = Util.getJdbcConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparer.accept(preparedStatement);
            preparedStatement.executeUpdate();
            logger.info(successMessage);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, errorMessage, e);
        }
    }
}