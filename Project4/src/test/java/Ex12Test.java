import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import org.junit.jupiter.api.Test;

public class Ex12Test {
    @Test
    public void testResponseHeadersContainExpectedValues(){
        // 1)Получить ответ от API
        Response responseFindHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        // 2) Смотреть, какие заголовки вернулись в ответе
        System.out.println(responseFindHeader.getHeaders());

        // 3) Проверить значение заголовка Content-Length
        Assertions.assertHeaderInResponseByNameAndValueInt(
                responseFindHeader,
                "Content-Length",
                15);
        // Проверить значение заголовка Content-Type
        Assertions.assertHeaderInResponseByNameAndValueString(
                responseFindHeader,
                "Content-Type",
                "application/json");


    }
}
