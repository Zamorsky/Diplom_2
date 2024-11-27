package utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

// Класс OrderClient выполняет действия, связанные с заказами в системе
public class OrderClient extends BaseClient {

    // Константы для URL эндпоинтов
    public static final String ORDERS_ENDPOINT = "api/orders"; // Эндпоинт для работы с заказами
    public static final String INGREDIENT_API = "api/ingredients"; // Эндпоинт для работы с ингредиентами

    // Метод для создания заказа с авторизацией
    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(Order order, String accessToken) {
        // Метод создает новый заказ, отправляя POST-запрос с телом, содержащим данные о заказе
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса (заголовки, базовый URL)
                .auth().oauth2(accessToken) // Подключаем токен авторизации для запроса
                .body(order) // Устанавливаем тело запроса с информацией о заказе
                .when()
                .post(ORDERS_ENDPOINT); // Отправляем POST-запрос на эндпоинт заказов
    }

    // Метод для создания заказа без авторизации
    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        // Метод создает новый заказ, отправляя POST-запрос с телом, содержащим данные о заказе
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .body(order) // Устанавливаем тело запроса с информацией о заказе
                .when()
                .post(ORDERS_ENDPOINT); // Отправляем POST-запрос на эндпоинт заказов
    }

    // Метод для получения заказов пользователя с авторизацией
    @Step("Получить заказы конкретного пользователя c авторизацией")
    public Response getOrderWithAuthList(String accessToken) {
        // Метод запрашивает список всех заказов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .auth().oauth2(accessToken) // Добавляем авторизацию для получения доступа
                .get(ORDERS_ENDPOINT); // Отправляем GET-запрос на эндпоинт заказов
    }

    // Метод для получения заказов пользователя без авторизации
    @Step("Получить заказы конкретного пользователя без авторизации")
    public Response getOrderWithoutAuthList() {
        // Метод запрашивает список всех заказов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .get(ORDERS_ENDPOINT); // Отправляем GET-запрос на эндпоинт заказов
    }

    // Метод для получения всех ингредиентов
    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        // Метод запрашивает список всех ингредиентов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .get(INGREDIENT_API); // Отправляем GET-запрос на эндпоинт ингредиентов
    }
}
