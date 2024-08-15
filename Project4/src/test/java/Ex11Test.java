import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex11Test {

    @ParameterizedTest
    @ValueSource(strings = {"cookie"})
    public void testHomeWorkCookieValue(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/homework_cookie");

        Response responseForCheck = spec.get().andReturn();

        // Получаем все cookies
        Map<String, String> cookies = responseForCheck.getCookies();

        // Печать cookies и их значения в консоль
        System.out.println("Cookies: " + cookies);

        //Проверить,что то значение cookie с именем HomeWork равно hw_value
        assertEquals("hw_value",cookies.get("HomeWork"),"Expect cookie is not found");
    }

    /*
    Сделать второй пример,который будет возвращать название cookie
    Третий метод будет принимать значение от второго и сравнивать его
    с этолонным. Типа нам сказали, какой cookie ждать
     */

    @Test
    public void testVerifyHomeWorkCookieNameAndValue() {
        // 1) Получить ответ от API
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        // 2) Посмотреть пришедшие куки
        Map<String, String> cookies = response.getCookies();
        System.out.println(cookies);

        // 3) Положить название куки и получить значение куки по названию
        String cookieName = "HomeWork"; // Замените на нужное имя куки, если оно другое
        String cookieValue = cookies.get(cookieName);

        // 4) Вывести значения
        System.out.println("Название куки: " + cookieName);
        System.out.println("Значение куки: " + cookieValue);

        // 5) Проверить, что название куки - HomeWork
        assertEquals("HomeWork", cookieName,"Name of cookie is not equal");

        // 6)Проверить,что значение куки HomeWork равно hw_value
        assertEquals("hw_value",cookieValue,"Value of cookie is not equal");
    }


    //Прелположила,что название куки мне передали
    @ParameterizedTest
    @ValueSource(strings = {"HomeWork"})
    public void testVerifyHomeWorkCookieNameAndValueWithParametr(String cookieName) {
        // 1) Получить ответ от API
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        // 2) Смотреть, какие куки вернулись в ответе
        System.out.println(response.getCookies());

        // 3) Смотреть значение куки
        String cookieValue = response.getCookie(cookieName);

        // 4) Проверить, что кука присутствует
        assertTrue(cookieValue != null, "Cookie is not present");

        // 5) Проверить, что значение куки есть
        System.out.println(cookieValue);
        assertTrue(!cookieValue.isEmpty(), "Cookie's value is empty");
    }

}