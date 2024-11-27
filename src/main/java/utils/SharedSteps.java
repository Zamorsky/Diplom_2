package utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;

public class SharedSteps {


    @Step("Проверяем, что в ответе есть success")
    public static void checkBodyContainsSuccess(Response response) {
        response.then()
                .body("success", equalTo(true)); // Проверка, что список заказов не пуст
    }

    @Step("Получить accessToken")
    public static String getAccessToken(Response response) {
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

    @Step("Проверка статус-кода")
    public static void checkResponseStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }



}
