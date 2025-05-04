package jm.task.core.jdbc.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Util {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Csgoraki228#";

    private static final String DRIVER = "org.postgresql.Driver";



    private static SessionFactory sessionFactory;


    public static SessionFactory getSessionFactory() {
        if (Util.sessionFactory == null) {
            try {
                return new Configuration()
                        .addAnnotatedClass(jm.task.core.jdbc.model.User.class)
                        .setProperty("hibernate.connection.driver_class", DRIVER)
                        .setProperty("hibernate.connection.url", URL)
                        .setProperty("hibernate.connection.username", USER)
                        .setProperty("hibernate.connection.password", PASSWORD)
                        .setProperty("hibernate.hbm2ddl.auto", "update")
                        .buildSessionFactory();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return sessionFactory;
    }


    public static Connection jdbcGetConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

