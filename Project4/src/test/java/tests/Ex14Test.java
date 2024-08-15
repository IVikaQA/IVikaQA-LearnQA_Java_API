package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseCaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;

public class Ex14Test extends BaseCaseTest {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
        // 1)Положить в Map вернные данные для авторизации
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        // 2)Пройти авторизацию
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // 3)
        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    public void testAuthUser(){
        /*
        1)Замееняем JsonPath на Response,чтобы использовать методы класса Assertions
         */
        Response responseCheckAuth = RestAssured
                .given()
                //Передаем в нужнуе части запроса значения,которые хранятся в переменных
                //которые определены до меетода с тегом BeforeEach
                .header("x-csrf-token",this.header)
                .cookie("auth_sid",this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();
        /*
        Используем статический метод класса Assertions
        Передав в метод asserJsonByName
        1.объект запроса - responseCheckAuth
        2.Имя парметра, которое ищем
        3.Ожидаемый результат
         */
        Assertions.asserJsonByName(responseCheckAuth,"user_id",this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeAuthUser(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")){
            spec.header("x-csrf-token",this.header);
        } else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        //Получить ответ и взять из него json вместо JsonPath
        Response responseForCheck = spec.get().andReturn();
        Assertions.asserJsonByName(responseForCheck,"user_id",0);
    }
}
