package utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

//в этом классе пишем запросы которые нужны для взаимодействия с пользователями
public class UserClient extends BaseClient {

    public static final String REGISTER_ENDPOINT = "api/auth/register"; // Эндпоинт для регистрации пользователя
    public static final String LOGIN_ENDPOINT = "api/auth/login"; // Эндпоинт для логина пользователя
    public static final String USER_ENDPOINT = "api/auth/user"; // Эндпоинт для получения и обновления данных пользователя
    public static final String LOGOUT_ENDPOINT = "api/auth/logout"; // Эндпоинт для выхода пользователя из системы

    /**
     * Метод для создания пользователя
     * @param user объект пользователя, которого нужно создать
     * @return Response объект с ответом от сервера
     */
    @Step("Создание пользователя") // Аннотация Allure для отслеживания шага теста
    public Response createUser(User user) {
        return given()
                .spec(getBaseSpec()) // Используем базовые настройки (например, URL)
                .body(user) // Отправляем тело запроса с данными пользователя
                .when()
                .post(REGISTER_ENDPOINT); // Отправляем POST-запрос на регистрацию
    }

    /**
     * Метод для удаления пользователя
     * @param accessToken токен доступа для авторизации
     * @return Response объект с ответом от сервера
     */
    @Step("Удаление пользователя") // Аннотация Allure
    public Response deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .auth().oauth2(accessToken) // Авторизация с помощью токена
                .delete(USER_ENDPOINT); // Отправляем DELETE-запрос для удаления пользователя
    }

    /**
     * Метод для логина пользователя
     * @param user объект пользователя для логина
     * @return Response объект с ответом от сервера
     */
    @Step("Логин пользователя") // Аннотация Allure
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .body(user) // Отправляем данные пользователя для логина
                .when()
                .post(LOGIN_ENDPOINT); // Отправляем POST-запрос для логина
    }

    /**
     * Метод для обновления данных о пользователе без авторизации
     * @param user объект с новыми данными пользователя
     * @return Response объект с ответом от сервера
     */
    @Step("Обновление данных о пользователе без авторизации") // Аннотация Allure
    public Response updateWithoutAuthUser(User user) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .body(user) // Отправляем данные для обновления
                .when()
                .patch(USER_ENDPOINT); // Отправляем PATCH-запрос для обновления данных
    }

    /**
     * Метод для обновления данных о пользователе с авторизацией
     * @param user объект с новыми данными пользователя
     * @param accessToken токен доступа для авторизации
     * @return Response объект с ответом от сервера
     */
    @Step("Обновление данных о пользователе c авторизацией") // Аннотация Allure
    public Response updateUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .auth().oauth2(accessToken) // Авторизация с помощью токена
                .body(user) // Отправляем данные для обновления
                .when()
                .patch(USER_ENDPOINT); // Отправляем PATCH-запрос для обновления данных
    }

    /**
     * Метод для получения данных о пользователе
     * @param accessToken токен доступа для авторизации
     * @return Response объект с ответом от сервера
     */
    @Step("Получение данных о пользователе") // Аннотация Allure
    public Response dataOfUser(String accessToken) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .auth().oauth2(accessToken) // Авторизация с помощью токена
                .when()
                .get(USER_ENDPOINT); // Отправляем GET-запрос для получения данных пользователя
    }

    /**
     * Метод для выхода пользователя из системы
     * @param refreshToken токен для выхода из системы
     * @return Response объект с ответом от сервера
     */
    @Step("Выход пользователя из системы") // Аннотация Allure
    public Response logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec()) // Базовые настройки
                .body(refreshToken) // Отправляем токен для выхода
                .when()
                .post(LOGOUT_ENDPOINT); // Отправляем POST-запрос для выхода
    }
}
