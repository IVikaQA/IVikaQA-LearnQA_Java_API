package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    /*
    Проверяем: Можно ли создать пользователя, когда почта уже использована другим пользователем
    НЕГАТИВНЫЙ ТЕСТ
     */
    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        //Заполняем данными
        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        //Создаем пользователя
        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        //Смотрим статус код ответа сервера
        //System.out.println(responseCreateAuth.statusCode());
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        //Смотрим ответ сервера
        //System.out.println(responseCreateAuth.asString());
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    /*
    Проверяем:Что с уникальной почтой создается пользователь
     */
    @Test
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        //Заполняем данными
        Map<String,String> userData = DataGenerator.getRegistrationData();

        //Создаем пользователя
        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        //Смотрим статус код ответа сервера
        //System.out.println(responseCreateAuth.statusCode());
        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        //System.out.println(responseCreateAuth.asString());
        //Проверяем наличие поля в JSON ответа
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }
    /*
    Создание пользователя с некорректным email - без символа @
     */
    @Test
    public void testCreateUserWithInvalidEmail() {
        String email = "invalidemail.com"; // некорректный email

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", email);

        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    /*
    Создание пользователя без указания одного из полей
     */
    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "password"})
    public void testCreateUserWithoutRequiredField(String field) {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        // Убираем нужное поле
        userData.remove(field);

        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following fields are required: " + field);
    }

    @Test
    public void testCreateUserWithShortName() {
        String shortName = "A"; // имя в один символ

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("username", shortName);

        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Username is too short"); // Проверьте текст ошибки
    }

    @Test
    public void testCreateUserWithLongName() {
        String longName = "A".repeat(251); // имя длиной 251 символ

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("username", longName);

        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Username is too long"); // Проверьте текст ошибки
    }
}
