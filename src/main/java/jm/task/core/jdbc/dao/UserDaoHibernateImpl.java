package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class UserDaoHibernateImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoHibernateImpl.class);

    private void doInTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при выполнении транзакции", e);
        }
    }

    private <T> T doInTransaction(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при выполнении транзакции", e);
            return null;
        }
    }

    @Override
    public void createUsersTable() {
        doInTransaction(session -> {
            session.createNativeQuery(SQLQueries.CREATE_TABLE).executeUpdate();
            logger.info("Таблица пользователей создана.");
        });
    }

    @Override
    public void dropUsersTable() {
        doInTransaction(session -> {
            session.createNativeQuery(SQLQueries.DROP_TABLE).executeUpdate();
            logger.info("Таблица пользователей удалена.");
        });
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        doInTransaction(session -> {
            session.save(new User(name, lastName, age));
            logger.info("Пользователь " + name + " добавлен в базу данных.");
        });
    }

    @Override
    public void removeUserById(long id) {
        doInTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                logger.info("Пользователь с ID " + id + " удалён.");
            } else {
                logger.warn("Пользователь с ID " + id + " не найден.");
            }
        });
    }

    @Override
    public List<User> getAllUsers() {
        return doInTransaction((Function<Session, List<User>>) session ->
                session.createQuery("FROM User", User.class).getResultList()
        );
    }

    @Override
    public void cleanUsersTable() {
        doInTransaction(session -> {
            session.createNativeQuery(SQLQueries.TRUNCATE_TABLE).executeUpdate();
            logger.info("Таблица пользователей очищена.");
        });
    }
}