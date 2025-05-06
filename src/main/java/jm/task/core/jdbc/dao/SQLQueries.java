package jm.task.core.jdbc.dao;

public final class SQLQueries {
    private SQLQueries() {}

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "name VARCHAR(50) NOT NULL," +
                    "lastName VARCHAR(100) NOT NULL," +
                    "age SMALLINT NOT NULL)";

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS users";

    public static final String TRUNCATE_TABLE =
            "TRUNCATE TABLE users";

    public static final String INSERT =
            "INSERT INTO users(name, lastname, age) VALUES (?, ?, ?)";

    public static final String REMOVE_BY_ID =
            "DELETE FROM users WHERE id = ?";

    public static final String SELECT_ALL =
            "SELECT id, name, lastname, age FROM users";
}