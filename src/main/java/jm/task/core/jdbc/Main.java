package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserServiceImpl ser = new UserServiceImpl();
        ser.createUsersTable();
        ser.saveUser("Матвей","Пересторонин", (byte) 21);
        ser.saveUser("Даниил","Чернов", (byte) 26);
        ser.saveUser("Леонид","Малинов", (byte) 19);
        ser.saveUser("Иван","Иванов", (byte) 30);

        ser.cleanUsersTable();

        ser.dropUsersTable();




    }
}
