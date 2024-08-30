package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/*
В этом классе методы для редактирования существующих пользователей
Замечание: Нельзя редактировать пользователей с id < 10
*/
@Feature("Редактирование пользователя")
public class UserEditTest extends BaseTestCase {
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
    В этом тесте будем:
    1)Создавать пользователя
    2)Редактировать этого пользователя и проверить, что успешно отредактировали
    3)Проверять, что успешно отредактировали
    4)Авторизоваться под созданным пользователем, чтобы получить его данные
     */

    @Test
    @DisplayName("Тест: Редактирование только что созданного пользователя")
    public void testEditJustCreated() {
        System.out.println("Тест: Редактирование только что созданного пользователя");

        // 1) GENERATE USER - Создание нового пользователя
        System.out.println("Шаг 1: Создание нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");
        System.out.println("Создан пользователь с ID: " + userId);

        // 2) LOGIN - Авторизация пользователя
        System.out.println("Шаг 2: Авторизация пользователя");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 3) EDIT - Редактирование данных пользователя
        System.out.println("Шаг 3: Редактирование данных пользователя");
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // 4) GET - Получение данных пользователя и сравнение
        System.out.println("Шаг 4: Получение данных пользователя для проверки изменений");
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // 5) Проверяем, что поле firstName имеет значение newName
        System.out.println("Шаг 5: Проверка поля 'firstName' в ответе");
        try {
            Assertions.asserJsonByName(responseUserData, "firstName", newName);
            System.out.println("Тест прошел успешно: Поле 'firstName' изменено на '" + newName + "'.");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }

    @Test
    @DisplayName("Тест: Редактирование пользователя без авторизации")
    @TmsLink("Ex17")
    public void testEditUserWithoutAuthorization() {
        System.out.println("Тест: Редактирование пользователя без авторизации");

        // 1) Подготовка данных для редактирования
        System.out.println("Шаг 1: Подготовка данных для редактирования");
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "New Name");

        // 2) Попытка редактирования данных пользователя
        System.out.println("Шаг 2: Попытка редактирования данных пользователя без авторизации");
        Response responseEdit = RestAssured
                .given()
                .body(editData)
                // Используйте ID пользователя > 10
                .put("https://playground.learnqa.ru/api/user/11") // Используйте ID пользователя < 10
                .andReturn();

        // 3) Проверка кода ответа
        System.out.println("Шаг 3: Проверка кода ответа на неавторизованный доступ");
        try {
            Assertions.assertResponseCodeEquals(responseEdit, 400); // Проверка на неавторизованный доступ
            System.out.println("Тест прошел успешно: Код ответа 400 получен.");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }

    /*
    Ex17
    Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем
     */
    @Test
    @DisplayName("Тест: Редактирования первого пользователя под авторизацией второго")
    @TmsLink("Ex17")
    public void testEditUserAsDifferentUser() {
        System.out.println("Тест: Редактирование пользователя другим пользователем");

        // 1) Создаем двух пользователей
        System.out.println("Шаг 1: Создание двух пользователей");
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        // Регистрация первого пользователя
        Response responseCreateUser1 = RestAssured
                .given()
                .body(userData1)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        // Регистрация второго пользователя
        Response responseCreateUser2 = RestAssured
                .given()
                .body(userData2)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId1 = responseCreateUser1.jsonPath().getString("id");
        String userId2 = responseCreateUser2.jsonPath().getString("id");

        // 2) Авторизация под вторым пользователем
        System.out.println("Шаг 2: Авторизация под вторым пользователем");
        Map<String, String> authData2 = new HashMap<>();
        authData2.put("email", userData2.get("email"));
        authData2.put("password", userData2.get("password"));

        Response responseAuth2 = RestAssured
                .given()
                .body(authData2)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 3) Попытка редактирования первого пользователя под авторизацией второго
        System.out.println("Шаг 3: Попытка редактирования первого пользователя");
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "Changed Name");

        Response responseEdit = new ApiCoreRequests().editUser(userId1, editData,
                this.getHeader(responseAuth2, "x-csrf-token"),
                this.getCookie(responseAuth2, "auth_sid"));

        // 4) Проверка кода ответа
        System.out.println("Шаг 4: Проверка кода ответа на попытку редактирования");
        try {
            Assertions.assertResponseCodeEquals(responseEdit, 403); // Ожидаем код 403 (доступ запрещен)
            System.out.println("Тест прошел успешно: Код ответа 403 получен.");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }

    @Test
    @DisplayName("Тест: Изменение email на некорректный (без символа @)")
    @TmsLink("Ex17")
    public void testEditEmailWithoutAtSymbol() {
        System.out.println("Тест: Изменение email на некорректный (без символа @)");

        // 1) Создаем пользователя и авторизуемся под ним
        System.out.println("Шаг 1: Создание пользователя и авторизация");
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreate = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreate.jsonPath().getString("id");
        //2) Авторизуемся под только что созданным пользователем
        System.out.println("Шаг 2: Авторизация под только что созданным пользователем");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 3) Попытка изменить email на некорректный
        System.out.println("Шаг 3: Попытка изменить email на некорректный (без символа @)");
        Map<String, String> editData = new HashMap<>();
        //editData.put("email", "invalidEmailWithoutAtSymbol");
        editData.put("email","invalidEmailWithoutAtSymbol");

        Response responseEdit = new ApiCoreRequests().editUser(userId, editData,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));

        // 3) Проверка кода ответа
        System.out.println("Шаг 3: Проверка кода ответа на попытку изменения email");
        try {
            Assertions.assertResponseCodeEquals(responseEdit, 400); // Проверка на неправильный email
            System.out.println("Тест прошел успешно: Код ответа 400 получен.");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }
    
    @Test
    @DisplayName("Тест: Изменение firstName на слишком короткое значение")
    @TmsLink("Ex17")
    public void testEditFirstNameToShortValue() {
        System.out.println("Тест: Изменение firstName на слишком короткое значение");

        // 1) Создаем пользователя и авторизуемся под ним
        System.out.println("Шаг 1: Создание пользователя и авторизация");
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreate = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreate.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 2) Попытка изменить firstName на слишком короткое значение
        System.out.println("Шаг 2: Попытка изменить firstName на слишком короткое значение (1 символ)");
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "A"); // Один символ

        Response responseEdit = new ApiCoreRequests().editUser(userId, editData,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));

        // 3) Проверка кода ответа
        System.out.println("Шаг 3: Проверка кода ответа на попытку изменения firstName");
        try {
            Assertions.assertResponseCodeEquals(responseEdit, 400); // Проверка на неправильное значение firstName
            System.out.println("Тест прошел успешно: Код ответа 400 получен.");
            countPassed++;
        } catch (AssertionError e) {
            System.out.println("Ошибка: " + e.getMessage());
            countFailed++;
        }
    }
}