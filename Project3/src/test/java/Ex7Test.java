import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7Test {

    @Test
    public void testLongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        Response response;
        int statusCode;

        do {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(true)
                    .when()
                    .get(url)
                    .andReturn();

            statusCode = response.getStatusCode();
            System.out.println("Статус-код: " + statusCode);

            // Если статус-код не 200, извлекаем новый URL из заголовка Location
            if (statusCode != 200) {
                url = response.getHeader("Location");
                System.out.println("Редирект на URL: " + url);
            }

            // Продолжаем, пока статус-код не 200
        } while (statusCode != 200);

        System.out.println("Получен окончательный ответ с кодом 200: " + statusCode);
    }
}