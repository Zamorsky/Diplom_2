import generators.UserGen;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.SharedSteps;
import utils.UserClient;

import static org.hamcrest.Matchers.*;

/**
 * Тесты для проверки функциональности авторизации пользователей.
 * Сценарии включают успешный логин, логин с неверным паролем и логин с несуществующим логином.
 */
public class UserAuthTests {

    private UserClient userClient; // Класс для взаимодействия с API
    private User user; // Пользователь, которого будем использовать для тестов

    /**
     * Метод, выполняющийся перед каждым тестом.
     * Здесь мы инициализируем клиента для работы с API и генерируем уникального пользователя.
     */
    @Before
    public void setUp() {
        // Инициализация клиента для запросов
        userClient = new UserClient();

        // Генерация уникального пользователя с помощью генератора
        user = UserGen.generateRandomUser();
    }

    /**
     * Тест для проверки успешного логина пользователя.
     * Ожидаем успешный ответ с токенами доступа и refresh.
     */
    @Test
    @DisplayName("Успешный логин пользователя")
    @Description("Проверка, что при успешном логине возвращается success = true, accessToken, refreshToken и информация о пользователе")
    public void testSuccessfulLogin() {
        // Создаем пользователя на сервере
        userClient.createUser(user);

        // Логин пользователя
        Response loginResponse = userClient.loginUser(user);

        // Проверяем успешный статус-код 200 и структуру ответа
        SharedSteps.checkResponseStatusCode(loginResponse, 200);

        // Проверяем, что поля success и accessToken присутствуют и корректны
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("accessToken", notNullValue()) // AccessToken не null
                .body("accessToken", startsWith("Bearer ")) // AccessToken начинается с Bearer
                .body("refreshToken", notNullValue()) // RefreshToken не null
                .body("user.email", equalTo(user.getEmail())) // Проверка email
                .body("user.name", equalTo(user.getName())); // Проверка имени
    }

    /**
     * Тест для проверки логина с неверным паролем.
     * Ожидаем ошибку 401 Unauthorized.
     */
    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что при вводе неверного пароля возвращается ошибка 401 и success = false")
    public void incorrectPasswordUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        // Создаем пользователя с некорректным паролем, но с правильным email
        User incorrectUser = new User(user.getEmail(), UserGen.generateRandomUser().getPassword(), user.getName());

        // Пытаемся залогиниться с неверным паролем
        Response responseLogin = userClient.loginUser(incorrectUser);

        // Проверяем статус-код и сообщение об ошибке
        responseLogin.then()
                .statusCode(401) // Ожидаем ошибку 401 Unauthorized
                .body("message", equalTo("email or password are incorrect")) // Сообщение об ошибке
                .body("success", equalTo(false)); // success = false
    }

    /**
     * Тест для проверки логина с несуществующим логином.
     * Ожидаем ошибку 401 Unauthorized.
     */
    @Test
    @DisplayName("Логин с несуществующим логином")
    @Description("Проверка, что при вводе неверного логина возвращается ошибка 401 и success = false")
    public void incorrectLoginUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        // Создаем пользователя с неправильным email (несуществующий логин), но с правильным паролем
        User incorrectUser = new User(UserGen.generateRandomUser().getEmail(), user.getPassword(), user.getName());

        // Пытаемся залогиниться с неверным логином
        Response responseLogin = userClient.loginUser(incorrectUser);

        // Проверяем статус-код и сообщение об ошибке
        responseLogin.then()
                .statusCode(401) // Ожидаем ошибку 401 Unauthorized
                .body("message", equalTo("email or password are incorrect")) // Сообщение об ошибке
                .body("success", equalTo(false)); // success = false
    }

    /**
     * Метод, выполняющийся после каждого теста.
     * Удаляет пользователя, если он был создан для теста.
     */
    @After
    public void tearDown() {
        // Удаляем пользователя после теста, если он был создан
        if (user != null ) {
            // Логинимся для получения токена доступа
            Response responseLogin = userClient.loginUser(user);
            String accessToken = SharedSteps.getAccessToken(responseLogin);

            // Удаляем пользователя по токену доступа
            Response responseDelete = userClient.deleteUser(accessToken);

            // Проверяем, что пользователь был успешно удален
            SharedSteps.checkResponseStatusCode(responseDelete, 202);
            SharedSteps.checkBodyContainsSuccess(responseDelete);
            responseDelete.then().body("message", equalTo("User successfully removed")); // Сообщение об успешном удалении
        }
    }
}
