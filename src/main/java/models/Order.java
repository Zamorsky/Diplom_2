package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Класс Order описывает заказ с ингредиентами.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@AllArgsConstructor // Генерирует конструктор с аргументами для всех полей
@NoArgsConstructor // Генерирует конструктор без аргументов
public class Order {
    private List<String> ingredients; // Список идентификаторов ингредиентов
}