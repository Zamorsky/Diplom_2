package models;

import lombok.*;


@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString// Генерирует конструктор с аргументами для всех полей
@NoArgsConstructor // Генерирует конструктор без аргументов

public class User {

    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }
//для отображения в отчёте Allure
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}