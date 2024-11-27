import clients.UserClient;
import generators.UserGen;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserCreatingTests {

    private UserClient userClient; // Класс для взаимодействия с API
    private User user; // Пользователь, которого будем создавать

    @Before
    public void setUp() {
        // Инициализация клиента для запросов
        userClient = new UserClient();
    }


    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест проверяет возможность создания уникального пользователя")
    public void createUniqueUserTest() {
        user = UserGen.generateRandomUser(); // Генерация уникального пользователя
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        // Проверяем, что статус-код 200
        checkStatusCode200(response);
        checkBodyContainsSuccess(response);  // Проверяем, что ответ содержит поле success=true
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Тест проверяет возможность создания пользователя, который уже зарегистрирован;")
    public void cannotCreateDuplicateUsersTest() {
        user = UserGen.generateRandomUser(); // Генерация уникального пользователя
        Response response = userClient.createUser(user); // Отправляем запрос на создание пользователя
        checkStatusCode200(response);
        checkBodyContainsSuccess(response);

        // Пытаемся создать пользователя с тем же логином и проверяем, что возвращается ошибка 409
        Response responseDuplicate = userClient.createUser(user);
        responseDuplicate.then()
                .statusCode(403)
                .body("message", equalTo("User already exists"))
                .body("success", equalTo(false));
    }


    @Test
    @DisplayName("Попытка создания пользователя без email")
    @Description("Проверка, что создание пользователя без email вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")

    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = new User(null, "hOdOr1!", "Brandon Stark");
        Response response = userClient.createUser(userWithoutEmail);

        response.then()
                .statusCode(403) // Ожидаем статус 403 Forbidden
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }

    @Test
    @DisplayName("Попытка создания пользователя без пароля")
    @Description("Проверка, что создание пользователя без пароля вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")

    public void testCreateUserWithoutPassword() {
        User userWithoutPassword = new User("John@Stark.GOT", null, "John Stark");
        Response response = userClient.createUser(userWithoutPassword);

        response.then()
                .statusCode(403) // Ожидаем статус 403 Forbidden
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }

    @Test
    @DisplayName("Попытка создания пользователя без имени")
    @Description("Проверка, что создание пользователя без имени вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")

    public void testCreateUserWithoutName() {
        User girlWithoutName = new User("Arya@Stark.GOT", "password123", null);
        Response response = userClient.createUser(girlWithoutName);

        response.then()
                .statusCode(403) // Ожидаем статус 403 Forbidden
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }


    @After
    public void tearDown() {
        // Удаляем пользователя после теста, если он был создан
        if (user != null ) {
            Response responseLogin = userClient.loginUser(user);
            String accessToken = getAccessToken(responseLogin); // Логинимся для получения токена
            Response responseDelete = userClient.deleteUser(accessToken); // Удаляем пользователя
            checkStatusCode202(responseDelete);
            checkBodyContainsSuccess(responseDelete);
            responseDelete.then().body("message", equalTo("User successfully removed")); // Проверка, что список заказов не пуст
        }
    }


    @Step("Проверяем, что ответ с кодом 200")
    public void checkStatusCode200(Response response) {
        response.then()
                .statusCode(200); // Ожидаем статус-код 200
    }

    @Step("Проверяем, что ответ с кодом 202")
    public void checkStatusCode202(Response response) {
        response.then()
                .statusCode(202); // Ожидаем статус-код 200
    }


    @Step("Проверяем, что в ответе есть success")
    public void checkBodyContainsSuccess(Response response) {
        response.then()
                .body("success", equalTo(true)); // Проверка, что список заказов не пуст
    }

    @Step("Получить accessToken")
    public String getAccessToken(Response response) {
        String bearerToken = response.then()
                .contentType("application/json") // Указываем ожидаемый тип ответа
                .extract()
                .path("accessToken"); // Извлекаем значение accessToken

        // Убираем префикс "Bearer ", если он есть
        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Возвращаем токен без "Bearer "
        }
        return bearerToken; // Если префикса нет, возвращаем токен как есть
    }


}