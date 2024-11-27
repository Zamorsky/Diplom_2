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

import static org.hamcrest.Matchers.equalTo;

public class UserEditingTests {

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
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Проверка, что попытка изменить имя пользователя с авторизацией будет успешной")
    public void updateNameWithAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);

        // Проверяем, что ответ от сервера успешный (статус 200)
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        // Получаем токен авторизации после создания пользователя
        String accessToken = SharedSteps.getAccessToken(response);

        // Генерация нового имени для пользователя для изменения
        User editedDataUser = new User(user.getEmail(), user.getPassword(), UserGen.generateRandomUser().getName());

        // Отправляем запрос на обновление данных пользователя (изменение имени)
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))  // Проверяем, что имя обновилось
                .body("success", equalTo(true));

        // Проверяем возможность логина с новыми данными (проверка корректности изменений)
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует

        // Возвращаем имя пользователю, чтобы он мог быть удален после теста
        user.setName(editedDataUser.getName());
    }

    @Test
    @DisplayName("Изменение почты пользователя с авторизацией")
    @Description("Проверка, что попытка изменить почту пользователя с авторизацией будет успешной")
    public void updateEmailWithAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);

        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        String accessToken = SharedSteps.getAccessToken(response); //получаем токен авторизации

        // Генерация новой почты для изменения
        User editedDataUser = new User(UserGen.generateRandomUser().getEmail(), user.getPassword(), user.getName());

        // Отправляем запрос на обновление данных пользователя (изменение почты)
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))
                .body("success", equalTo(true));

        // Проверка возможности логина с новой почтой
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует

        // Возвращаем email пользователю, чтобы он мог быть удален после теста
        user.setEmail(editedDataUser.getEmail());
    }

    @Test
    @DisplayName("Изменение почты пользователя на уже занятую почту с авторизацией")
    @Description("Проверка, что попытка изменить почту пользователя с авторизацией будет успешной")
    public void updateToExistingEmailWithAuthUsersTest() {
        // Отправляем запрос на создание первого пользователя
        Response response = userClient.createUser(user);

        // Создаем второго пользователя с занятым email
        User user2 = UserGen.generateRandomUser();
        userClient.createUser(user2);

        String accessToken = SharedSteps.getAccessToken(response); //получаем токен авторизации первого пользователя

        // Пытаемся изменить почту первого пользователя на почту второго
        User editedDataUser = new User(user2.getEmail(), user.getPassword(), user.getName());

        // Отправляем запрос на обновление данных пользователя (почта)
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(403) // Ожидаем статус 403 (Forbidden)
                .body("message", equalTo("User with such email already exists")) // Сообщение об ошибке
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    @Description("Проверка, что попытка изменить пароль пользователя с авторизацией будет успешной")
    public void updatePassWithAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);

        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        String accessToken = SharedSteps.getAccessToken(response); //получаем токен авторизации

        // Генерация нового пароля для изменения
        User editedDataUser = new User(user.getEmail(), UserGen.generateRandomUser().getPassword(), user.getName());

        // Отправляем запрос на обновление данных пользователя (пароль)
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))
                .body("success", equalTo(true));

        // Проверка возможности логина с новым паролем
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует

        // Возвращаем пароль пользователю, чтобы он мог быть удален после теста
        user.setPassword(editedDataUser.getPassword());
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизацией")
    @Description("Проверка, что при попытке изменить данные пользователя без авторизации вернется 401 Unauthorized")
    public void updateDataWithoutAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        SharedSteps.checkResponseStatusCode(response, 200);
        SharedSteps.checkBodyContainsSuccess(response);

        // Пытаемся изменить данные пользователя без авторизации
        User editedDataUser = new User(user.getEmail(), user.getPassword(), UserGen.generateRandomUser().getName());
        Response responseUpdate = userClient.updateWithoutAuthUser(editedDataUser);

        responseUpdate.then()
                .statusCode(401) // Ожидаем статус 401 (Unauthorized)
                .body("message", equalTo("You should be authorised")) // Сообщение об ошибке
                .body("success", equalTo(false));
    }

    @After
    public void tearDown() {
        // Удаляем пользователя после теста, если он был создан
        if (user != null) {
            Response responseLogin = userClient.loginUser(user);
            String accessToken = SharedSteps.getAccessToken(responseLogin); // Логинимся для получения токена
            Response responseDelete = userClient.deleteUser(accessToken); // Удаляем пользователя
            SharedSteps.checkResponseStatusCode(responseDelete, 202); // Проверяем, что удаление прошло успешно
            SharedSteps.checkBodyContainsSuccess(responseDelete);
            responseDelete.then().body("message", equalTo("User successfully removed"));
        }
    }
}
