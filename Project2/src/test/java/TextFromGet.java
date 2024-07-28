import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.Test;

public class TextFromGet {

    RequestSpecification requestSpecification;
    Response response;

    @Test
    public void testTextFromGetZapros() {
        // Домен
        RestAssured.baseURI = "https://playground.learnqa.ru";

        // Создать запрос
        requestSpecification = RestAssured.given();

        // Выполнить запрос для указанного URI
        response = requestSpecification.get("/api/get_text"); // Указываем путь к API

        // Настроить Логирование
        String log4jConfPath = "src/test/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        // Получить ответ и вывести текст из ответа на консоль
        String textResponse = response.getBody().asString();
        System.out.println("Response Text: " + textResponse); 
    }
}