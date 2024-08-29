package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest {

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

    @Test
    @DisplayName("Тест: Удаление пользователя")
    public void testUserDelete() {
        System.out.println("Тест: Удаление пользователя");

        // 1) Генерация данных для регистрации
        System.out.println("Шаг 1: Генерация данных для регистрации");
        Map<String, String> userData = DataGenerator.getRegistrationData();

        // 2) Регистрация пользователя
        System.out.println("Шаг 2: Регистрация пользователя");
        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateUser, 200); // Проверка успешной регистрации
        System.out.println("Пользователь успешно зарегистрирован.");

        // 3) Получение ID созданного пользователя
        String userId = responseCreateUser.jsonPath().getString("id");

        // 4) Авторизация под созданным пользователем
        System.out.println("Шаг 3: Авторизация под созданным пользователем");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseAuth, 200); // Проверка успешной авторизации
        System.out.println("Пользователь успешно авторизован.");

        // 5) Удаление пользователя
        System.out.println("Шаг 4: Удаление пользователя");
        Response responseDelete = RestAssured
                .given()
                .header("x-csrf-token", responseAuth.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseAuth.getCookie("auth_sid"))
                .delete("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseDelete, 200); // Проверка успешного удаления
        System.out.println("Пользователь успешно удален.");

        // 6) Попытка получить данные удаленного пользователя
        System.out.println("Шаг 5: Попытка получить данные удаленного пользователя");
        Response responseGetDeletedUser = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();

        // 7) Проверка кода ответа
        System.out.println("Шаг 6: Проверка кода ответа");
        try {
            Assertions.assertResponseCodeEquals(responseGetDeletedUser, 404); // Проверка на неправильное значение firstName
            System.out.println("Тест прошел успешно: Код ответа 404 получен:Пользователь не найден");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }
}