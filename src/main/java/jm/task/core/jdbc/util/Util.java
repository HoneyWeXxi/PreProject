package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    private static final Properties jdbcProps = new Properties();
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.addProperties(loadHibernateProperties());
            configuration.addAnnotatedClass(User.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex.getMessage());
        }
    }

    private static Properties loadHibernateProperties() {
        Properties props = new Properties();
        try (InputStream input = Util.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузки hibernate.properties", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Connection getJdbcConnection() throws SQLException {
        return DriverManager.getConnection(
                jdbcProps.getProperty("jdbc.url"),
                jdbcProps.getProperty("jdbc.username"),
                jdbcProps.getProperty("jdbc.password")
        );
    }
}