import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex8Test {

    @Test
    public void testTokens() {

        //Токен
        String token = "";

        try {
            // 1) Создать задачу и запустить таймер
            Response createJobResponse = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .andReturn();

            // 2) Извлечь данные из response
            token = createJobResponse.jsonPath().getString("token");
            System.out.println("Создана задача с токеном: " + token);
            int seconds = createJobResponse.jsonPath().getInt("seconds");
            System.out.println("Задача будет выполнена через " + seconds + " секунд.");

            // 3) Проверять статус задачи до ее готовности
            boolean isJobReady = false;
            boolean hasPrintedNotReadyMessage = false; // Переменная для отслеживания вывода сообщения о неготовности
            long endTime = System.currentTimeMillis() + seconds * 1000; // Время окончания проверки

            while (System.currentTimeMillis() < endTime) {
                Response checkJobResponse = RestAssured
                        .given()
                        .queryParam("token", token)
                        .when()
                        .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                        .andReturn();

                String status = checkJobResponse.jsonPath().getString("status");

                if ("ready".equals(status)) {
                    isJobReady = true;
                    break; // Выход из цикла, если задача готова
                }

                // Выводим сообщение о том, что задача не готова, только один раз
               if (!hasPrintedNotReadyMessage) {
                    System.out.println("Статус задачи: Job is NOT ready");
                    hasPrintedNotReadyMessage = true; // Устанавливаем флаг, что сообщение выведено
               }
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            // 4) Проверяем статус задачи после конца ожидания
            Response finalStatusResponse = RestAssured
                    .given()
                    .queryParam("token", token)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .then()
                    .statusCode(200)
                    .extract().response();

            String finalStatus = finalStatusResponse.jsonPath().getString("status");
        System.out.println("Статус задачи: " + finalStatus);
    }
}