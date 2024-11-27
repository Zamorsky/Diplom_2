package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

// Класс OrderClient выполняет действия, связанные с заказами в системе
public class OrderClient extends BaseClient {

    public static final String ORDERS_ENDPOINT = "api/orders";
    public static final String INGREDIENT_API = "api/ingredients/";

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        // Метод создает новый заказ, отправляя POST-запрос с телом, содержащим данные о заказе
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)// Подключаем основную спецификацию для запроса (заголовки, базовый URL)
                .body(order) // Устанавливаем тело запроса с информацией о заказе
                .when()
                .post(ORDERS_ENDPOINT); // Отправляем POST-запрос на эндпоинт заказов
    }

    @Step("Получить заказы конкретного пользователя")
    public Response getOrderList(String accessToken) {
        // Метод запрашивает список всех заказов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .auth().oauth2(accessToken)
                .get(ORDERS_ENDPOINT); // Отправляем GET-запрос на эндпоинт заказов
    }

    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        // Метод запрашивает список всех заказов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .get(INGREDIENT_API); // Отправляем GET-запрос на эндпоинт заказов
    }
}
