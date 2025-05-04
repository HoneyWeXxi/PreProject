package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGSERIAL  PRIMARY KEY," +
            "name VARCHAR(50) NOT NULL," +
            "lastName VARCHAR(100) NOT NULL," +
            "age SMALLINT NOT NULL)"; //сделать проверку на отрицательный возраст

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS users";

    private static final String SQL_INSERT = "INSERT INTO users(name, lastname, age) VALUES (?,?,?)";

    private static final String SQL_CLEAN_ALL = "TRUNCATE TABLE users";

    private static final String SQL_REMOVE_BY_ID = "DELETE FROM users WHERE id = ?";


    private static List<User> users = new ArrayList<>();

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        boolean success = false;
        try (Connection connection = Util.jdbcGetConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_TABLE);
            System.out.println("Query executed successfully.");
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        if (success)
            System.out.println("Table created\n");
        else
            System.out.println("Query failed\n");
    }

    public void dropUsersTable() {
        boolean success = false;
        try (Connection connection = Util.jdbcGetConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_DROP_TABLE);
            System.out.println("Query executed successfully.");
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        if (success)
            System.out.println("Table dropped.\n");
        else
            System.out.println("Query failed.\n");
    }

    public void saveUser(String name, String lastName, byte age) {
        if (age >= 0) {
            User user = new User(name, lastName, age);
            long id = -1;
            try (Connection connection = Util.jdbcGetConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setByte(3, age);

                preparedStatement.executeUpdate();
                System.out.println("Query executed successfully.");
                id = jdbcQueryGetId(preparedStatement);
                if (id != -1) {
                    user.setId(id);
                    users.add(user);
                    System.out.println("User saved\n");
                } else
                    System.out.println("User save failed\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            throw new NumberFormatException("Age cannot be negative!\n");

    }


    public void removeUserById(long id) {
        boolean success = false;
        try (Connection connection = Util.jdbcGetConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_REMOVE_BY_ID)) {

            preparedStatement.setLong(1, id);

            int result = preparedStatement.executeUpdate();
            if (result > 0)
                System.out.println("Query executed successfully.");
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        if (success) {
            getAllUsers().removeIf(user -> user.getId() == id);
            System.out.println("User removed\n");
        } else
            System.out.println("Query failed.\n");

    }

    public List<User> getAllUsers() {

        return users;
    }

    public void cleanUsersTable() {
        boolean success = false;
        try (Connection connection = Util.jdbcGetConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_CLEAN_ALL);
            System.out.println("Query executed successfully.");
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        if (success) {
            users.clear();
            System.out.println("Table cleaned\n");
        } else
            System.out.println("Query failed.\n");

    }

    private static Long jdbcQueryGetId(PreparedStatement preparedStatement) throws SQLException {
        long id = -1;
        try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next()) {
                id = resultSet.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
}
