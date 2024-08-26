package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest {

    @Test
    public void testUserDelete() {
        // Генерация данных для регистрации
        Map<String, String> userData = DataGenerator.getRegistrationData();

        // Регистрация пользователя
        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateUser, 200); // Проверка успешной регистрации

        // Получение ID созданного пользователя
        String userId = responseCreateUser.jsonPath().getString("id");

        // Авторизация под созданным пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseAuth, 200); // Проверка успешной авторизации

        // Удаление пользователя
        Response responseDelete = RestAssured
                .given()
                .header("x-csrf-token", responseAuth.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseAuth.getCookie("auth_sid"))
                .delete("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseDelete, 200); // Проверка успешного удаления

        // Попытка получить данные удаленного пользователя
        Response responseGetDeletedUser = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseGetDeletedUser, 404); // Проверка, что пользователь не найден
    }
}