package lib;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import java.util.Map;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Данный класс будет делать вещи,которые в дальнейшем мы будем использовать в тестах,начиная с HelloWorld7Test
//Например, в этом классе есть методы для получения значений  cookie и header из ответа по имени
public class BaseTestCase {

    //Общее описание методов 1 и 2
    //Суть метода: Передаем объект ответа от запроса имя по которому будем получать header или cookie
    //Метод будет сам понимать,есть там такие переменные в ответе или нет
    //Если нет,то метод будет падать; Если данные есть (cookie,header), то метод
    //будет их возвращать

    //1)В метод передаем header
    protected String getHeader(Response Response, String name){
        //Передаем headers
        Headers headers = Response.getHeaders();
        //Проверяем,пришел ли header c указанным name?
        assertTrue(headers.hasHeaderWithName(name),"Response doesn't header with name " + name);
        //Если пришел,то возвращаем значение; Если нет, то метод падает с ошибкой
        return headers.getValue(name);
    }

    //2)В метод передаем cookie
    protected String getCookie(Response Response, String name){
        //Передаем cookies
        Map<String,String> cookies = Response.getCookies();
        //Проверяем,пришел ли cookie c указанным name?
        assertTrue(cookies.containsKey(name),"Response doesn't cookie with name " + name);
        //Если пришел,то возвращаем значение; Если нет, то метод падает с ошибкой
        return cookies.get(name);
    }

    //3) На вход принимаем ответ запроса (Response)
    //и название поля json, котрое хотим получить
    protected int getIntFromJson(Response Response,String name){
        //Ищем ключь с именем name, благодаря знаку $ в корне ответа
        Response.then().assertThat().body("$",hasKey(name));
        //Возвращаем значение поля,название которого мы получили -  name
        return Response.jsonPath().getInt(name);
    }
}