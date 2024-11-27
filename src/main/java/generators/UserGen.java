package generators;

import models.User;
import java.util.UUID;

public class UserGen {

    /**
     * Метод для генерации рандомного пользователя с уникальным email, паролем и именем.
     */
//    public static User generateRandomUser() {
//        String randomEmail = "user_" + UUID.randomUUID() + "@example.com"; // Уникальный email
//        String randomPassword = "password_" + UUID.randomUUID().toString().substring(5, 8); // Пароль длиной 8 символов
//        String randomName = "User_" + UUID.randomUUID().toString().substring(3, 5); // Уникальное имя
//
//        return new User(randomEmail, randomPassword, randomName); // Передаём правильные переменные
//    }

    public static User generateRandomUser() {
        String randomEmail = "user_" + UUID.randomUUID().toString().substring(3, 8) + "@example.com"; // Уникальный email
        String randomPassword = "password_" + UUID.randomUUID().toString().substring(5, 8); // Пароль длиной 8 символов
        String randomName = "User_" + UUID.randomUUID().toString().substring(3, 5); // Уникальное имя

        return new User(randomEmail, randomPassword, randomName); // Передаём правильные переменные
    }
}
