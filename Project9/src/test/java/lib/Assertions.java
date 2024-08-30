package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    /*
    Проверить, что код ответа сервера равен ожидаемому
    */
    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response status code is not as expected"
        );
    }

    /*
    Метод проверяет значение int в JSON-ответе
    На вход метод получает объект с ответом сервера, имя которое нужно найти в ответе,ожидаемое значение
     */
    public static void asserJsonByName(Response Response,String name, int expectedValue){
        //1) Получаем объект с ответом сервера
        Response.then().assertThat().body("$",hasKey(name));
        //2)Получаем значение нужного параметра,передав его имя
        int value = Response.jsonPath().getInt(name);
        //3) Сравниваем ожидаемое значение со значением из ответа сервера,если не совпадает
        //то выводим сообщение
        assertEquals(expectedValue,value,"JSON value is not equal to expected value");
    }

    /*
    Метод проверяет значение String в JSON-ответе
     */
    public static void asserJsonByName(Response Response,String name, String expectedValue){
        //1) Получаем объект с ответом сервера
        Response.then().assertThat().body("$",hasKey(name));
        //2)Получаем значение нужного параметра,передав его имя
        String value = Response.jsonPath().getString(name);
        //3) Сравниваем ожидаемое значение со значением из ответа сервера,если не совпадает
        //то выводим сообщение
        assertEquals(expectedValue,value,"JSON value is not equal to expected value");
    }

    /*
  Проверить, что текст ответа сервера равен ожидаемому
   */
    public static void assertResponseTextEquals(Response Response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    /*
        Проверить,что ответ содержит поле с определенным именем
    */
    public static void assertJsonHasField(Response Response,String expectedFieldName){
        Response.then().assertThat().body("$",hasKey(expectedFieldName));
    }

    /*
        Проверить наличие в ответе сразу всех полей c указанными именами
        На вход:
        1. Response Response: Это объект, представляющий ответ от API (обычно это JSON-ответ).
        Он содержит данные, которые вы хотите проверить.
        В зависимости от используемой библиотеки, это может быть объект типа Response
        из таких библиотек, как RestAssured или аналогичных.
        2. String[] expectedFieldNames: Это массив строк, представляющий имена полей,
        которые вы ожидаете найти в JSON-ответе. Метод будет проверять наличие каждого
        из этих полей в ответе.
    */
    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames){
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(Response,expectedFieldName);
        }
    }

    /*
    Убеждаемся о наличии или отсутствии полей
     */
    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName){
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}