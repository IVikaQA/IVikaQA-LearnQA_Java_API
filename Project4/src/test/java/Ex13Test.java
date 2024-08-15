import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseCaseTest;
import lib.FileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ex13Test extends BaseCaseTest{

    @ParameterizedTest
    @ValueSource(strings = {"platform", "browser", "device"})
    @DisplayName("Проверка наличия ключей в ответе API user_agent_check")
    public void testCheckUserAgentKeys(String nameKey) {

        //Cписок неверных User Agent
        List<String> incorrectUserAgents = new ArrayList<>();

        //Здесь хранятся верные User Agent и значения из файла user_agents.txt
        Map<String, Map<String, String>> results = new HashMap<>();

        try {
            // 1)Ложим в список строки из файла user_agents.txt
            List<String> userAgents = FileReader.readUserAgents();

            // 2)Перебираем строки из списка userAgents
            for (String userAgent : userAgents) {
                Response response = RestAssured
                        .given()
                        .header("User-Agent", userAgent)
                        .get("https://playground.learnqa.ru/ajax/api/user_agent_check");

                String responseBody = response.asString();

                // 3)Проверяем ответ на наличие ошибок
                if (responseBody.contains("error")) {
                    //Положить в список неверных User Agent
                    incorrectUserAgents.add(userAgent + " Ошибка в параметрах");
                } else {
                    // Получаем значение по ключу
                    String value = response.jsonPath().getString(nameKey);
                    if (value == null) {
                        incorrectUserAgents.add(userAgent + " отсутствует ключ: " + nameKey);
                    } else {
                        results.computeIfAbsent(userAgent, k -> new HashMap<>()).put(nameKey, value);
                    }
                }
            }

            // 4)Выводим результаты
            for (Map.Entry<String, Map<String, String>> entry : results.entrySet()) {
                String userAgent = entry.getKey();
                String resultValue = entry.getValue().get(nameKey);
                System.out.println("User-Agent: " + userAgent + ", " + nameKey + ": " + resultValue);
            }

            // 5)Выводим список неправильных User-Agent
            if (incorrectUserAgents.isEmpty()) {
                System.out.println("Все User-Agent корректны.");
            } else {
                System.out.println("Некорректные User-Agent:");
                incorrectUserAgents.forEach(System.out::println);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}