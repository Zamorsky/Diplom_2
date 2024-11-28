import generators.UserGen;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.SharedSteps;
import utils.UserClient;

import static org.hamcrest.Matchers.equalTo;

public class UserCreatingTests {

    private UserClient userClient; // Класс для взаимодействия с API (отправка запросов к серверу)
    private User user; // Пользователь, которого будем создавать (объект для тестов)

    @Before
    public void setUp() {
        // Инициализация клиента для отправки запросов
        userClient = new UserClient();
    }

    /**
     * Тест на создание уникального пользователя.
     * Генерируется случайный пользователь, отправляется запрос на создание, и проверяется успешность операции.
     */
    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест проверяет возможность создания уникального пользователя")
    public void createUniqueUserTest() {
        // Генерация уникального пользователя
        user = UserGen.generateRandomUser();

        // Отправка запроса на создание пользователя
        Response response = userClient.createUser(user);

        // Проверка, что статус-код 200 и в теле ответа success=true
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);
    }

    /**
     * Тест на попытку создания пользователя с уже зарегистрированным email.
     * Проверяется возврат ошибки 409, когда пользователь с таким email уже существует.
     */
    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Тест проверяет возможность создания пользователя, который уже зарегистрирован;")
    public void cannotCreateDuplicateUsersTest() {
        user = UserGen.generateRandomUser(); // Генерация уникального пользователя
        Response response = userClient.createUser(user); // Отправка запроса на создание пользователя
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        // Пытаемся создать пользователя с тем же email, ожидаем ошибку 403
        Response responseDuplicate = userClient.createUser(user);
        responseDuplicate.then()
                .statusCode(403)
                .body("message", equalTo("User already exists"))
                .body("success", equalTo(false));
    }

    /**
     * Тест на создание пользователя без email.
     * Ожидается ошибка 403, так как email является обязательным полем.
     */
    @Test
    @DisplayName("Попытка создания пользователя без email")
    @Description("Проверка, что создание пользователя без email вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = new User(null, "hOdOr", "Brandon Stark"); // Создаем пользователя без email
        Response response = userClient.createUser(userWithoutEmail); // Отправка запроса

        // Проверяем, что статус 403, а в теле ошибки указано, что email обязателен
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }

    /**
     * Тест на создание пользователя без пароля.
     * Ожидается ошибка 403, так как пароль является обязательным полем.
     */
    @Test
    @DisplayName("Попытка создания пользователя без пароля")
    @Description("Проверка, что создание пользователя без пароля вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")
    public void testCreateUserWithoutPassword() {
        User userWithoutPassword = new User("John@Stark.GOT", null, "John Stark"); // Создаем пользователя без пароля
        Response response = userClient.createUser(userWithoutPassword); // Отправка запроса

        // Проверяем, что статус 403, а в теле ошибки указано, что пароль обязателен
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }

    /**
     * Тест на создание пользователя без имени.
     * Ожидается ошибка 403, так как имя является обязательным полем.
     */
    @Test
    @DisplayName("Попытка создания пользователя без имени")
    @Description("Проверка, что создание пользователя без имени вызывает ошибку")
    @Issue("Баг - в теле ответа лишняя запятая после password")
    public void testCreateUserWithoutName() {
        User girlWithoutName = new User("Arya@Stark.GOT", "ValarMorghulis", null); // Создаем пользователя без имени
        Response response = userClient.createUser(girlWithoutName); // Отправка запроса

        // Проверяем, что статус 403, а в теле ошибки указано, что имя обязательно
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password, and name are required fields"));
    }

    /**
     * Удаление пользователя после выполнения тестов.
     * Этот метод будет вызван после выполнения всех тестов, чтобы удалить созданного пользователя.
     */
    @After
    public void tearDown() {
        // Удаляем пользователя, если он был создан
        if (user != null) {
            Response responseLogin = userClient.loginUser(user); // Логинимся для получения токена
            String accessToken = SharedSteps.getAccessToken(responseLogin); // Получаем токен доступа
            Response responseDelete = userClient.deleteUser(accessToken); // Отправляем запрос на удаление пользователя
            SharedSteps.checkResponseStatusCode(responseDelete, 202);
            SharedSteps.checkBodyContainsSuccess(responseDelete);
            responseDelete.then().body("message", equalTo("User successfully removed"));
        }
    }
}
