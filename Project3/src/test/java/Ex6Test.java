import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex6Test {

    @Test
    public void testRestAssured() {
        // 1)Получить ответ от API
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        //  2)Посмотреть значение куки Location в ответе
        String responseLocation = response.getHeader("Location");
        System.out.println("Редирект на URL: "+responseLocation);
    }
}
