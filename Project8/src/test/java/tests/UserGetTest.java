package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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

    private static int tests = 0;
    private static int countPassed = 0;
    private static int countFailed = 0;

    @BeforeEach
    public void incrementTestCounter() {
        tests++;
        System.out.println("Запущен тест #" + tests);
    }

    @AfterAll
    public static void printTestResults() {
        System.out.println("Запущено всего тестов: " + tests);
        System.out.println("Провалено тестов: " + countFailed);
        System.out.println("Успешно пройдено тестов: " + countPassed);
    }


    /*
    Получение данных пользователя,когда мы НЕ АВТОРИЗОВАНЫ
     */
    @Test
    @DisplayName("Тест: Получение данных пользователя без авторизации")
    public void testGetUserDataNotAuth() {
        System.out.println("Тест: Получение данных пользователя без авторизации");

        System.out.println("Шаг 1: Отправляем запрос на получение данных пользователя с ID 2");
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();

        // Смотрим результат
        System.out.println("Шаг 2: Ответ сервера: " + responseUserData.asString());

        // Проверяем наличие поля username
        System.out.println("Шаг 3: Проверяем наличие поля 'username'");
        try {
            Assertions.assertJsonHasField(responseUserData, "username");
            System.out.println("Тест прошел успешно: Поле 'username' присутствует в ответе.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
            return; // Завершаем тест при ошибке
        }

        // Проверяем отсутствие других полей
        System.out.println("Шаг 4: Проверяем отсутствие полей 'firstName', 'lastName', 'email'");
        try {
            Assertions.assertJsonHasNotField(responseUserData, "firstName");
            Assertions.assertJsonHasNotField(responseUserData, "lastName");
            Assertions.assertJsonHasNotField(responseUserData, "email");
            System.out.println("Тест прошел успешно: Поля 'firstName', 'lastName', 'email' отсутствуют в ответе.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
            return; // Завершаем тест при ошибке
        }

        // Если все проверки прошли успешно
        System.out.println("Тест завершен успешно: Все проверки пройдены.");
        countPassed++;
    }

    /*
    Получение данных,когда мы АВТОРИЗОВАНЫ
    Чтобы получить данные,например для пользователя с id = 2
    Мы должны сделать запрос на авторизацию с логинн="https://playground.learnqa.ru/api/user/"
    и пароль=1234
   и в последующие запросы подставлять куки и header
     */
    @Test
    @DisplayName("Тест: Получение данных пользователя с авторизацией")
    public void testGetUserDetailsWithAuth() {
        System.out.println("Тест: Получение данных пользователя с авторизацией");

        // 1) Положили данные в Map
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // 2) Залогинились
        System.out.println("Шаг 1: Залогиниваемся с данными: " + authData);
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();

        // 3) Получаем заголовок и куки
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 4) Получаем данные как авторизованный пользователь
        System.out.println("Шаг 2: Получаем данные пользователя с ID 2");
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();

        // 5) Проверяем наличие полей в ответе
        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        System.out.println("Шаг 3: Проверяем наличие полей в ответе: " + Arrays.toString(expectedFields));

        try {
            Assertions.assertJsonHasFields(responseUserData, expectedFields);
            System.out.println("Тест прошел успешно: Все ожидаемые поля присутствуют в ответе.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
            return; // Завершаем тест при ошибке
        }

        // Если все проверки прошли успешно
        System.out.println("Тест завершен успешно: Все проверки пройдены.");
        countPassed++;
    }

    /*
    Ex16
    Метод авторизуется под одним пользователем и запрашивает данные другого пользователя
     */
    @Test
    @DisplayName("Тест: Получение данных другого пользователя с авторизацией")
    public void testGetOtherUserDataWithAuth() {
        System.out.println("Тест: Получение данных другого пользователя с авторизацией");

        // 1) Данные для авторизации
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // 2) Залогинимся
        System.out.println("Шаг 1: Залогиниваемся с данными: " + authData);
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();

        // 3) Получаем куки и заголовок
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 4) Запрашиваем данные другого пользователя (например, с ID 2)
        System.out.println("Шаг 2: Получаем данные пользователя с ID 2");
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseUserData = apiCoreRequests.getUserDataAsAuthorized("2", header, cookie);

        // 5) Проверяем наличие полей в ответе
        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        System.out.println("Шаг 3: Проверяем наличие полей в ответе: " + Arrays.toString(expectedFields));

        try {
            for (String field : expectedFields) {
                Assertions.assertJsonHasField(responseUserData, field);
            }
            System.out.println("Тест прошел успешно: Все ожидаемые поля присутствуют в ответе.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
            return; // Завершаем тест при ошибке
        }

        // Если все проверки прошли успешно
        System.out.println("Тест завершен успешно: Все проверки пройдены.");
        countPassed++;
    }
}
