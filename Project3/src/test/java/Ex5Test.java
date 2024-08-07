import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class Ex5Test {
    @Test
    public void testParseJson() {
        // 1)Получить ответ от API
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework");

        // 2)Преобразовать ответ в JsonPath
        JsonPath jsonPath = response.jsonPath();

        // 3)Распечатать весь JSON-ответ
        System.out.println(response.asString());

        // 4)Посмотреть текст второго сообщения
        String secondMessage = jsonPath.getString("messages[1].message");
        System.out.println("Второе сообщение: " + secondMessage);
    }
}
