package tests;

import groovy.json.JsonOutput;
import io.restassured.RestAssured;
import lib.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UserRegisterTest extends BaseTestCase {

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
    Проверяем: Можно ли создать пользователя, когда почта уже использована другим пользователем
    НЕГАТИВНЫЙ ТЕСТ
     */
    @Test
    @DisplayName("Тест1: Создание пользователя с уже существующим email")
    public void testCreateUserWithExistingEmail() {
        System.out.println("Тест1: Создание пользователя с уже существующим email");

        System.out.println("Шаг1: Берем почту пользователя, который уже зарегистрирован");
        String email = "vinkotov@example.com";

        System.out.println("Шаг2: Формируем данные для запроса");
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        System.out.println("Шаг3: Отправляем запрос на создание пользователя");
        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        int statusCode = responseCreateAuth.statusCode();

        // Проверяем ожидаемый статус код
        System.out.println("Шаг4: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            System.out.println("- Ожидаемый статус код 400: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Ожидаемый статус код 400, но получен " + statusCode);
        }

        // Смотрим ответ сервера
        String responseBody = responseCreateAuth.asString();
        System.out.println("Шаг5: Смотрим ответ сервера: " + responseBody);

        // Проверяем текст ответа
        System.out.println("Шаг6: Сравниваем ответ сервера");
        String expectedResponseText = "Users with email '" + email + "' already exists";
        try {
            Assertions.assertResponseTextEquals(responseCreateAuth, expectedResponseText);
            System.out.println("- Ожидаемый текст ответа совпадает с фактическим: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Ожидаемый текст ответа не совпадает с фактическим.");
            System.out.println("  Ожидалось: " + expectedResponseText);
            System.out.println("  Получено: " + responseBody);
        }

        // Выводим результат проверки
        System.out.println("Шаг7:Результат прогона теста");
        if (statusCode == 400 && responseBody.contains(expectedResponseText)) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Пользователь с указанным email уже существует");
            System.out.println("Действительный результат: запрос вызвал ошибку '" + expectedResponseText + "'");
            System.out.println();
            countPassed++;
        } else {
            System.out.println("- Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }

    @Test
    @DisplayName("Тест2: Создание пользователя с новым email")
    public void testCreateUserSuccessfully() {
        System.out.println("Тест2: Создание пользователя с новым email");

        System.out.println("Шаг1: Генерация email");
        String email = DataGenerator.getRandomEmail();
        System.out.println("Сгенерированный email: " + email);

        System.out.println("Шаг 2: Подготавливаем данные для нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", email); // Добавляем сгенерированный email

        System.out.println("Шаг 3: Отправляем запрос на создание пользователя");
        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        // Смотрим статус код ответа сервера
        int statusCode = responseCreateAuth.statusCode();
        System.out.println("Шаг4: Статус код ответа: " + statusCode);

        // Проверяем ожидаемый статус код
        System.out.println("Шаг5: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
            System.out.println("- Ожидаемый статус код 200: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Ожидаемый статус код 200, но получен " + statusCode);
        }

        // Проверяем, что в ответе есть поле "id"
        System.out.println("Шаг6: Проверяем наличие поля 'id' в ответе");
        try {
            Assertions.assertJsonHasField(responseCreateAuth, "id");
            System.out.println("- Поле 'id' присутствует в ответе: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Поле 'id' отсутствует в ответе.");
        }

        // Выводим результат проверки
        System.out.println("Шаг7: Результат прогона теста");
        if (statusCode == 200 && responseCreateAuth.jsonPath().getString("id") != null) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Пользователь успешно создан.");
            System.out.println("Действительный результат: пользователь создан с id '" + responseCreateAuth.jsonPath().getString("id") + "'");
            System.out.println();
            countPassed++;
        } else {
            System.out.println("- Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }

    /*
    Ex15
    Создание пользователя с некорректным email - без символа @
     */
    @Test
    @DisplayName("Тест3: Создание пользователя с некорректным email")
    public void testCreateUserWithInvalidEmail() {
        System.out.println("Тест3: Создание пользователя с некорректным email");

        System.out.println("Шаг1: Установка некорректного email");
        String email = "invalidemail.com"; // некорректный email
        System.out.println("Некорректный email: " + email);

        System.out.println("Шаг 2: Подготавливаем данные для нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", email);

        System.out.println("Шаг 3: Отправляем запрос на создание пользователя");
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        // Смотрим статус код ответа сервера
        int statusCode = responseCreateAuth.statusCode();
        System.out.println("Шаг4: Статус код ответа: " + statusCode);

        // Проверяем ожидаемый статус код
        System.out.println("Шаг5: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            System.out.println("- Ожидаемый статус код 400: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Ожидаемый статус код 400, но получен " + statusCode);
        }

        // Проверяем текст ответа
        System.out.println("Шаг6: Проверяем текст ответа");
        try {
            Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
            System.out.println("- Ожидаемый текст ответа: 'Invalid email format': проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("- Ошибка: Ожидаемый текст ответа 'Invalid email format', но получен: " + responseCreateAuth.getBody().asString());
        }

        // Выводим результат проверки
        System.out.println("Шаг7: Результат прогона теста");
        if (statusCode == 400 && responseCreateAuth.getBody().asString().contains("Invalid email format")) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Некорректный email обработан правильно.");
            System.out.println();
            countPassed++;
        } else {
            System.out.println("- Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }

    /*
    Ex15
    Создание пользователя без указания одного из полей
     */
    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "password"})
    @DisplayName("Тест4: Создание пользователя без обязательного поля {username,email,password}")
    public void testCreateUserWithoutRequiredField(String field) {
        System.out.println("Тест4: Создание пользователя без обязательного поля: " + field);

        System.out.println("Шаг 1: Подготавливаем данные для нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();

        System.out.println("Шаг 2: Убираем поле: " + field);
        userData.remove(field);

        System.out.println("Шаг 3: Отправляем запрос на создание пользователя");
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        // Смотрим статус код ответа сервера
        int statusCode = responseCreateAuth.statusCode();
        System.out.println("Шаг 4: Статус код ответа: " + statusCode);

        // Проверяем ожидаемый статус код
        System.out.println("Шаг 5: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            System.out.println("Тест прошел успешно: Ожидаемый статус код 400: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый статус код 400, но получен " + statusCode);
        }

        // Проверяем текст ответа
        System.out.println("Шаг 6: Проверяем текст ответа");
        try {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + field);
            System.out.println("Тест прошел успешно: Ожидаемый текст ответа: 'The following required params are missed: " + field + "': проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый текст ответа не совпадает. Получен: " + responseCreateAuth.getBody().asString());
        }

        // Выводим результат проверки
        System.out.println("Шаг 7: Результат прогона теста");
        if (statusCode == 400 && responseCreateAuth.getBody().asString().contains(field)) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Обязательное поле '" + field + "' обработано правильно.");
            System.out.println();
            countPassed++;
        } else {
            System.out.println("Тест прошел успешно: Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }

    /*
    Ex15
    Создание пользователя с очень коротким именем в один символ
     */
    @Test
    @DisplayName("Тест5: Создание пользователя с коротким именем")
    public void testCreateUserWithShortName() {
        String shortName = "A"; // имя в один символ
        System.out.println("Тест6: Создание пользователя с коротким именем: " + shortName);

        System.out.println("Шаг 1: Подготавливаем данные для нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("username", shortName);

        System.out.println("Шаг 2: Отправляем запрос на создание пользователя");
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        // Смотрим статус код ответа сервера
        int statusCode = responseCreateAuth.statusCode();
        System.out.println("Шаг 3: Статус код ответа: " + statusCode);

        // Проверяем ожидаемый статус код
        System.out.println("Шаг 4: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            System.out.println("Тест прошел успешно: Ожидаемый статус код 400: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый статус код 400, но получен " + statusCode);
        }

        // Проверяем текст ответа
        System.out.println("Шаг 5: Проверяем текст ответа");
        try {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
            System.out.println("Тест прошел успешно: Ожидаемый текст ответа: 'The value of 'username' field is too short': проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый текст ответа не совпадает. Получен: " + responseCreateAuth.getBody().asString());
        }

        // Выводим результат проверки
        System.out.println("Шаг 6: Результат прогона теста");
        if (statusCode == 400 && responseCreateAuth.getBody().asString().contains("The value of 'username' field is too short")) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Тест пройден успешно, короткое имя обработано правильно.");
            System.out.println();
            countPassed++;
        } else {
            System.out.println("Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }

    /*
    Ex15
    Создание пользователя с очень длинным именем - длиннее 250 символов
     */
    @Test
    @DisplayName("Тест6: Создание пользователя с длинным именем")
    public void testCreateUserWithLongName() {
        String longName = "A".repeat(251); // имя длиной 251 символ
        System.out.println("Тест6: Создание пользователя с длинным именем: " + longName);

        System.out.println("Шаг 1: Подготавливаем данные для нового пользователя");
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("username", longName);

        System.out.println("Шаг 2: Отправляем запрос на создание пользователя");
        ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        // Смотрим статус код ответа сервера
        int statusCode = responseCreateAuth.statusCode();
        System.out.println("Шаг 3: Статус код ответа: " + statusCode);

        // Проверяем ожидаемый статус код
        System.out.println("Шаг 4: Проверяем ожидаемый статус код");
        try {
            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            System.out.println("Тест прошел успешно: Ожидаемый статус код 400: проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый статус код 400, но получен " + statusCode);
        }

        // Проверяем текст ответа
        System.out.println("Шаг 5: Проверяем текст ответа");
        try {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
            System.out.println("Тест прошел успешно: Ожидаемый текст ответа: 'The value of 'username' field is too long': проверка прошла успешно.");
        } catch (AssertionError e) {
            System.out.println("Ошибка: Ожидаемый текст ответа не совпадает. Получен: " + responseCreateAuth.getBody().asString());
        }

        // Выводим результат проверки
        System.out.println("Шаг 6: Результат прогона теста");
        if (statusCode == 400 && responseCreateAuth.getBody().asString().contains("The value of 'username' field is too long")) {
            System.out.println("Тест прошел успешно: Ожидаемый результат: Тест пройден успешно, длинное имя обработано правильно.");
            System.out.println();
             countPassed++;
        } else {
            System.out.println("Ошибка: результат не соответствует ожиданиям");
            System.out.println();
            countFailed++;
        }
    }
}
