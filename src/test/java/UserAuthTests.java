import clients.UserClient;
import generators.UserGen;
import io.qameta.allure.Description;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;


public class UserAuthTests {

    private UserClient userClient; // Класс для взаимодействия с API
    private User user; // Пользователь, которого будем создавать

    @Before
    public void setUp() {
        // Инициализация клиента для запросов
        userClient = new UserClient();

        // Генерация уникального пользователя
        user = UserGen.generateRandomUser();
    }

    @Test
    @DisplayName("Успешный логин пользователя")
    @Description("Проверка, что при успешном логине возвращается success = true, accessToken, refreshToken и информация о пользователе")
    public void testSuccessfulLogin() {
        // создание пользователя на сервере
        userClient.createUser(user);

        // Логин пользователя
        Response loginResponse = userClient.loginUser(user);
        checkStatusCode200(loginResponse);
        // Проверка структуры ответа
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("accessToken", notNullValue()) // AccessToken не null
                .body("accessToken", startsWith("Bearer ")) // AccessToken начинается с Bearer
                .body("refreshToken", notNullValue()) // RefreshToken не null
                .body("user.email", equalTo(user.getEmail())) // Email соответствует
                .body("user.name", equalTo(user.getName())); // Имя соответствует
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что при вводе неверного пароля возвращается ошибка 401 и success = false")
    public void incorrectPasswordUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        checkStatusCode200(response);
        checkBodyContainsSuccess(response);
        //Создаём юзера с некорректным паролем, но email от свежего
        User incorrectUser = new User(user.getEmail(), UserGen.generateRandomUser().getPassword(), user.getName());
        Response responseLogin = userClient.loginUser(incorrectUser);
        responseLogin.then()
                .statusCode(401) //401 Unauthorized
                .body("message", equalTo("email or password are incorrect"))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Логин с несуществующим логином")
    @Description("Проверка, что при вводе неверного логина возвращается ошибка 401 и success = false")

    public void incorrectLoginUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        checkStatusCode200(response);
        checkBodyContainsSuccess(response);
        //Создаём юзера с корректным паролем и email, но неправильным логином
        User incorrectUser = new User(UserGen.generateRandomUser().getEmail(), user.getPassword(), user.getName());
        Response responseLogin = userClient.loginUser(incorrectUser);
        responseLogin.then()
                .statusCode(401) //401 Unauthorized
                .body("message", equalTo("email or password are incorrect"))
                .body("success", equalTo(false));
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