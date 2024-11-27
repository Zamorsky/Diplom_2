package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;



import static io.restassured.RestAssured.given;

//в этом классе пишем запросы которые нужны для взаимодействия с курьерами
public class UserClient extends BaseClient {

    public static final String REGISTER_ENDPOINT = "api/auth/register";
    public static final String LOGIN_ENDPOINT = "api/auth/login";
    public static final String USER_ENDPOINT = "api/auth/user";
    public static final String LOGOUT_ENDPOINT = "api/auth/logout";



    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .delete(USER_ENDPOINT);
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Обновление данных о пользователе без авторизации")
    public Response updateWithoutAuthUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_ENDPOINT);
    }

    @Step("Обновление данных о пользователе c авторизацией")
    public Response updateUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_ENDPOINT);
    }

    @Step("Получение данных о пользователе")
    public Response dataOfUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .get(USER_ENDPOINT);
    }

    @Step("Выход пользователя из системы")
    public Response logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .when()
                .post(LOGOUT_ENDPOINT);
    }
}
