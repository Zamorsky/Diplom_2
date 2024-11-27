import generators.OrderGen;
import generators.UserGen;
import io.qameta.allure.Description;
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

import static org.hamcrest.Matchers.*;

public class IngredientsGetTests {

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

        // Создаем хотя бы один заказ для пользователя
        Order order = OrderGen.generateValidOrder(); // Генерация валидного заказа
        Response createOrderResponse = orderClient.createOrderWithAuth(order, accessToken);
        SharedSteps.checkResponseStatusCode(createOrderResponse, 200); // Проверка успешного создания заказа
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    @Description("Проверяем, что список заказов пользователя корректно возвращается при запросе с авторизацией")
    public void getOrdersWithAuthTest() {
        // Отправляем запрос на получение заказов
        Response response = orderClient.getOrderWithAuthList(accessToken);

        // Проверяем успешность запроса
        response.then()
                .statusCode(200) // Ожидаем статус 200 (OK)
                .body("success", equalTo(true)) // Поле success должно быть true
                .body("orders", not(empty())) // Убедимся, что список заказов не пуст
                .body("orders[0].number", notNullValue()); // Убедимся, что у первого заказа есть номер
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя без авторизации")
    @Description("Проверяем, что список заказов пользователя не возвращается при запросе без авторизации")
    public void getOrdersWithOutAuthTest() {
        // Отправляем запрос на получение заказов без авторизации
        Response response = orderClient.getOrderWithoutAuthList();

        // Проверяем, что запрос отклонен (статус 401)
        response.then()
                .statusCode(401) // Ожидаем статус 401 (Unauthorized)
                .body("success", equalTo(false)) // Поле success должно быть false
                .body("message", equalTo("You should be authorised")); // Сообщение о необходимости авторизации
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
