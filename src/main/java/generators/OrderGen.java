package generators;

import utils.OrderClient;
import io.restassured.response.Response;
import models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGen {

    /**
     * Метод для генерации заказа с валидными ингредиентами.
     * return объект Order с валидными ингредиентами.
     */
    public static Order generateValidOrder() {
        // Получаем список доступных ингредиентов через API
        List<String> ingredients = getValidIngredients();

        // Генерируем случайный набор ингредиентов
        List<String> randomIngredients = getRandomIngredients(ingredients, 3); // Например, 3 ингредиента

        return new Order(randomIngredients); // Создаем заказ с выбранными ингредиентами
    }

    /**
     * Метод для генерации заказа без ингредиентов.
     * return объект Order с пустым списком ингредиентов.
     */
    public static Order generateOrderWithoutIngredients() {
        return new Order(new ArrayList<>()); // Пустой список ингредиентов
    }

    /**
     * Метод для генерации заказа с невалидным идентификатором ингредиента.
     * return объект Order с невалидным идентификатором ингредиента.
     */
    public static Order generateOrderWithInvalidIngredient() {
        List<String> invalidIngredients = new ArrayList<>();
        invalidIngredients.add("invalid_ingredient_hash"); // Невалидный hash
        return new Order(invalidIngredients);
    }

    /**
     * Метод для получения списка валидных ингредиентов из API.
     * return список строк с идентификаторами ингредиентов.
     */
    private static List<String> getValidIngredients() {
        OrderClient orderClient = new OrderClient(); // Создаем клиент для работы с заказами
        Response response = orderClient.getIngredients(); // Запрашиваем список ингредиентов

        // Извлекаем список ингредиентов из ответа
        return response.then()
                .statusCode(200) // Проверяем, что статус-код 200
                .extract()
                .path("data._id"); // Извлекаем список идентификаторов ингредиентов

    }

    /**
     * Метод для случайного выбора ингредиентов из общего списка.
     * param ingredients полный список валидных ингредиентов.
     * param count количество ингредиентов для выбора.
     * return список случайно выбранных идентификаторов ингредиентов.
     */
    private static List<String> getRandomIngredients(List<String> ingredients, int count) {
        List<String> randomIngredients = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(ingredients.size());
            randomIngredients.add(ingredients.get(randomIndex));
        }

        return randomIngredients;
    }
}
