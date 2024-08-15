package lib;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {

    // Проверить значение header, если оно Int
    public static void assertHeaderInResponseByNameAndValueInt(Response response, String headerName, int expectedValue) {
        // 1) Проверяем, что заголовок присутствует в ответе
        if (!response.getHeaders().hasHeaderWithName(headerName)) {
            throw new AssertionError("Header '" + headerName + "' is not present in the response.");
        }

        // 2) Получаем значение нужного заголовка
        int valueHeader = Integer.parseInt(response.getHeader(headerName));

        // 3) Сравниваем полученное значение с ожидаемым
        if (valueHeader != expectedValue) {
            throw new AssertionError("Expected value: " + expectedValue + ", but got: " + valueHeader);
        }
    }

    // Проверить значение header, если оно String
    public static void assertHeaderInResponseByNameAndValueString(Response response, String headerName, String expectedValue) {
        // 1) Проверяем, что заголовок присутствует в ответе
        if (!response.getHeaders().hasHeaderWithName(headerName)) {
            throw new AssertionError("Header '" + headerName + "' is not present in the response.");
        }

        // 2) Получаем значение нужного заголовка
        String valueHeader = response.getHeader(headerName);

        // 3) Сравниваем полученное значение с ожидаемым
        if (!valueHeader.equals(expectedValue)) { // Используем equals для сравнения строк
            throw new AssertionError("Expected value: " + expectedValue + ", but got: " + valueHeader);
        }
    }

    //Проверить сущуствование ключей
    public static void assertKeyExists(JsonPath jsonPath, String key) {
        assertTrue(jsonPath.getString(key) != null, key + " Unknown");
    }

    /*
На вход метод получает объект с ответом сервера, имя которое нужно найти в ответе,ожидаемое значение
 */
    public static void asserJsonByName(Response Response,String name, int expectedValue){
        //1) Получаем объект с ответом сервера
        Response.then().assertThat().body("$",hasKey(name));
        //2)Получаем значение нужного параметра,передав его имя
        int value = Response.jsonPath().getInt(name);
        //3) Сравнием ожидаемое значение со значением из ответа сервера,если не совпадает
        //то выводим сообщение
        assertEquals(expectedValue,value,"JSON value is not equal to expected value");
    }

}