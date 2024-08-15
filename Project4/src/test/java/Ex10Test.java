import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class Ex10Test {

    @Test
    public void testAssertLength(){
        String stroka = "abcdfghabcdfghab";

        // Проверка, что длина строки больше 15.
        assertTrue(stroka.length() > 15, "Ошибка: текст должен быть длиннее 15 символов.");

        // Проверка, что длина строки меньше 20.
        assertFalse(stroka.length() >= 20, "Ошибка: текст должен быть короче 20 символов.");
    }
}