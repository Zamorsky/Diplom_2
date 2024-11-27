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

        checkStatusCode200(response);
        checkBodyContainsSuccess(response);

        String accessToken = getAccessToken(response); //получаем токен авторизации

        //меняем имя у сгенерированного юзера для запроса
        User editedDataUser = new User(user.getEmail(), user.getPassword(), UserGen.generateRandomUser().getName());

        //меняем имя у сгенерированного юзера на сервере с помощью запроса updateUser
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))
                .body("success", equalTo(true));
        //нужно проверить возможность логина под новыми данными
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует


        user.setName(editedDataUser.getName()); //возвращаем новый name исходному юзеру, чтобы он мог удалиться

    }

    @Test
    @DisplayName("Изменение почты пользователя с авторизацией")
    @Description("Проверка, что попытка изменить почту пользователя с авторизацией будет успешной")
    public void updateEmailWithAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);

        checkStatusCode200(response);
        checkBodyContainsSuccess(response);

        String accessToken = getAccessToken(response); //получаем токен авторизации

        //меняем почту у сгенерированного юзера для запроса
        User editedDataUser = new User(UserGen.generateRandomUser().getEmail(), user.getPassword(), user.getName());

        //меняем почту у сгенерированного юзера на сервере с помощью запроса updateUser
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))
                .body("success", equalTo(true));
        //нужно проверить возможность логина под новыми данными
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует


        user.setEmail(editedDataUser.getEmail()); //возвращаем новый email исходному юзеру, чтобы он мог удалиться
    }

    @Test
    @DisplayName("Изменение почты пользователя на уже занятую почту с авторизацией")
    @Description("Проверка, что попытка изменить почту пользователя с авторизацией будет успешной")
    public void updateToExistingEmailWithAuthUsersTest() {
        // Отправляем запрос на создание первого пользователя
        Response response = userClient.createUser(user);
        //создаем второго юзера, чтобы у нас появился занятый email
        User user2 = UserGen.generateRandomUser();
        userClient.createUser(user2);

        String accessToken = getAccessToken(response); //получаем токен авторизации первого пользователя

        //меняем почту у сгенерированного юзера для запроса
        User editedDataUser = new User(user2.getEmail(), user.getPassword(), user.getName());

        //меняем почту у перового юзера на почту второго на сервере с помощью запроса updateUser
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(403)
                .body("message", equalTo("User with such email already exists"))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    @Description("Проверка, что попытка изменить пароль пользователя с авторизацией будет успешной")
    public void updatePassWithAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);

        checkStatusCode200(response);
        checkBodyContainsSuccess(response);

        String accessToken = getAccessToken(response); //получаем токен авторизации

        //меняем пароль у сгенерированного юзера для запроса
        User editedDataUser = new User(user.getEmail(), UserGen.generateRandomUser().getPassword(), user.getName());

        //меняем пароль у сгенерированного юзера на сервере с помощью запроса updateUser
        Response responseUpdate = userClient.updateUser(editedDataUser, accessToken);
        responseUpdate.then()
                .statusCode(200)
                .body("user.email", equalTo(editedDataUser.getEmail()))
                .body("user.name", equalTo(editedDataUser.getName()))
                .body("success", equalTo(true));

        //нужно проверить возможность логина под новыми данными
        Response loginResponse = userClient.loginUser(editedDataUser);
        loginResponse.then()
                .body("success", equalTo(true)) // Поле success = true
                .body("user.email", equalTo(editedDataUser.getEmail())) // Email соответствует
                .body("user.name", equalTo(editedDataUser.getName())); // Имя соответствует

        user.setPassword(editedDataUser.getPassword()); //возвращаем новый пароль исходному юзеру, чтобы он мог удалиться в After

    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизацией")
    @Description("Проверка, что при попытке изменить данные пользователя без авторизации вернется 401 Unauthorized")
    public void updateDataWithoutAuthUsersTest() {
        // Отправляем запрос на создание пользователя
        Response response = userClient.createUser(user);
        checkStatusCode200(response);
        checkBodyContainsSuccess(response);
        //меняем имя у сгенерированного юзера для запроса
        User editedDataUser = new User(user.getEmail(), user.getPassword(), UserGen.generateRandomUser().getName());
        //меняем имя у сгенерированного юзера на сервере с помощью запроса Patch

        Response responseUpdate = userClient.updateWithoutAuthUser(editedDataUser);

        responseUpdate.then()
                .statusCode(401) //401 Unauthorized
                .body("message", equalTo("You should be authorised"))
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