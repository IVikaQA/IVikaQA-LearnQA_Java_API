package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/*
Просмотр деталей пользователя
Суть:
Мы посылаем URL, где в конце user_id и послав такой запрос
Условие:Если мы посылаем авторизованный запрос и сервер понимает
что мы авторизованы и посылаем запрос из под того же пользователя
то мы должны увидеть все поля: имя, логин, email
Если мы запрашиваем данные чужого пользователя, то должны видеть
только логин.
 */
public class UserGetTest extends BaseTestCase {

    /*
    Получение данных пользователя,когда мы НЕ АВТОРИЗОВАНЫ
     */
    @Test
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        //Смотрим результат
        System.out.println(responseUserData.asString());
        //Зафиксируем, есть поле username, НО нет других полей
        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasNotField(responseUserData,"firstName");
        Assertions.assertJsonHasNotField(responseUserData,"lastName");
        Assertions.assertJsonHasNotField(responseUserData,"email");

    }

    /*
    Получение данных,когда мы АВТОРИЗОВАНЫ
    Чтобы получить данные,например для пользователя с id = 2
    Мы должны сделать запрос на авторизацию с логинн="https://playground.learnqa.ru/api/user/"
    и пароль=1234
   и в последующие запросы подставлять куки и header
     */
    @Test
    public void testGetUserDetailsWithAuth(){
        //1)Положили данные в Map
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        //2)Залогинились
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //3)
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        //4)Чтобы получить данные как авторизованный пользователь
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid",cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        //5)Проверим наличие полей в ответе - это вариант 1
        //Assertions.assertJsonHasField(responseUserData,"username");
        //Assertions.assertJsonHasField(responseUserData,"email");
        //Assertions.assertJsonHasField(responseUserData,"firstName");
        //Assertions.assertJsonHasField(responseUserData,"lastName");

        //6)Проверим наличие полей в ответе - это вариант 2
        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }

    /*
    Ex16
    Метод авторизуется под одним пользователем и запрашивает данные другого пользователя
     */
    @Test
    public void testGetOtherUserDataWithAuth() {
        // 1) Данные для авторизации
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // 2) Залогинимся
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 3) Получаем куки и заголовок
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 4) Запрашиваем данные другого пользователя (например, с ID 2)
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseUserData = apiCoreRequests.getUserDataAsAuthorized("2", header, cookie);

        // 5) Проверяем наличие полей в ответе
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
