import generators.OrderGen;
import generators.UserGen;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.OrderClient;
import utils.SharedSteps;
import utils.UserClient;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreatingTests {

    private User user; // Пользователь для теста
    private String accessToken; // Токен авторизации
    private UserClient userClient; // Клиент для работы с пользователями
    private OrderClient orderClient; // Клиент для работы с заказами

    @Before
    public void setUp() {
        // Инициализация клиентов для работы с API
        userClient = new UserClient(); // Клиент для работы с пользователями
        orderClient = new OrderClient(); // Клиент для работы с заказами

        // Создание уникального пользователя
        user = UserGen.generateRandomUser();

        // Создаем пользователя и логинимся для получения токена
        Response createUserResponse = userClient.createUser(user);
        SharedSteps.checkResponseStatusCode(createUserResponse, 200); // Проверка успешного создания пользователя
        accessToken = SharedSteps.getAccessToken(createUserResponse); // Получаем токен доступа после успешного создания пользователя
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверяем, что заказ успешно создается, если пользователь авторизован")
    public void createOrderWithAuthTest() {
        // Генерация заказа с корректными ингредиентами
        Order order = OrderGen.generateValidOrder();

        // Отправляем запрос на создание заказа с авторизацией
        Response response = orderClient.createOrderWithAuth(order, accessToken);

        // Проверка успешного создания заказа
        response.then()
                .statusCode(200) // Проверка кода статуса 200 (успешно)
                .body("success", equalTo(true)) // Поле success должно быть true
                .body("order.number", notNullValue()); // Номер заказа должен быть не null
    }

    @Test
    @Issue("При создании заказа без авторизации должна вернуться ошибка 401, но он создается")
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверяем, что заказ не создается, если пользователь не авторизован")
    public void createOrderWithoutAuthTest() {
        // Генерация заказа с корректными ингредиентами
        Order order = OrderGen.generateValidOrder();

        // Отправляем запрос на создание заказа без авторизации
        Response response = orderClient.createOrderWithoutAuth(order);

        // Проверка, что заказ не был создан и вернулась ошибка
        response.then()
                .statusCode(401) // Ожидаем ошибку 401 (Unauthorized)
                .body("success", equalTo(false)); // Ответ должен содержать success = false
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверяем, что заказ успешно создается, если пользователь авторизован")
    public void createOrderWithIngredientsTest() {
        // Генерация заказа с корректными ингредиентами
        Order order = OrderGen.generateValidOrder();

        // Отправляем запрос на создание заказа с авторизацией
        Response response = orderClient.createOrderWithAuth(order, accessToken);

        // Проверка успешного создания заказа
        response.then()
                .statusCode(200) // Проверка кода статуса 200 (успешно)
                .body("success", equalTo(true)) // Поле success должно быть true
                .body("order.number", notNullValue()) // Номер заказа должен быть не null
                .body("order.ingredients._id", equalTo(order.getIngredients())); // Проверка ингредиентов в заказе
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверяем, что заказ не создается, если нет ингредиентов")
    public void createOrderWithoutIngedientsTest() {
        // Генерация заказа без ингредиентов
        Order order = OrderGen.generateOrderWithoutIngredients();

        // Отправляем запрос на создание заказа
        Response response = orderClient.createOrderWithAuth(order, accessToken);

        // Проверка, что заказ не был создан
        response.then()
                .statusCode(400) // Ошибка 400 (Bad Request)
                .body("success", equalTo(false)) // Ответ должен содержать success = false
                .body("message", equalTo("Ingredient ids must be provided")); // Сообщение о том, что ингредиенты обязаны быть предоставлены
    }

    @Test
    @DisplayName("Создание заказа с неверным хешом ингредиентов")
    @Description("Проверяем, что заказ не создается, если передан неверный хеш ингредиента")
    public void createOrderWithIncorrectHashTest() {
        // Генерация заказа с неверными ингредиентами
        Order order = OrderGen.generateOrderWithInvalidIngredient();

        // Отправляем запрос на создание заказа
        Response response = orderClient.createOrderWithAuth(order, accessToken);

        // Проверка ошибки 500 на сервере
        response.then()
                .statusCode(500); // Ошибка 500 (Internal Server Error)
    }

    @After
    public void tearDown() {
        // Удаляем пользователя после выполнения теста
        if (accessToken != null) {
            Response deleteResponse = userClient.deleteUser(accessToken);
            SharedSteps.checkResponseStatusCode(deleteResponse, 202); // Проверка успешного удаления пользователя
        }
    }
}
