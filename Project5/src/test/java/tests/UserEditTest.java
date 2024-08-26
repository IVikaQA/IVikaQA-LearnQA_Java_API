package tests;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

    /*
    В этом классе методы для редактирования существующих пользователей
    Замечание: Нельзя редактировать пользователей с id < 10
    */
public class UserEditTest extends BaseTestCase {
    /*
    В этом тесте будем:
    1)Создавать пользователя
    2)Редактировать этого пользователя и проверить, что успешно отредактировали
    3)Проверять,что успешно отредактировали
    4)Авторизоваться под созданным пользователем,чтобы получить его данные
     */
    @Test
    public void testEditJustCreated(){
        //1)GENERATE USER - Создание нового пользователя
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        //Достаем из JSON-ответа значение поля id
        String userId = responseCreateAuth.getString("id");

        //2)LOGIN -Авторизация пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT - Редактирование данных пользователя
        /*
         * Токен, авторизационый куки и значение,нна которое хотим поменять
         */
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName",newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token",this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET - Получение данных пользователя и сравнение
        //Для этого мы делаем авторизованный запрос
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //Смотрим ответ от сервера с новым значением поля - firstName
        //System.out.println(responseUserData.asString());
        /*
        Проверяем,что в JSON-ответе, поле firstName имеет значение = newName
         */
        Assertions.asserJsonByName(responseUserData,"firstName",newName);
    }

    /*
    Ex17
    Попытаемся изменить данные пользователя, будучи неавторизованными
     */
    @Test
    public void testEditUserWithoutAuthorization() {
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "New Name");

        Response responseEdit = RestAssured
                .given()
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/1") // Используйте ID пользователя < 10
                .andReturn();

        Assertions.assertResponseCodeEquals(responseEdit, 401); // Проверка на неавторизованный доступ
    }

    /*
    Ex17
    Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем
     */
    @Test
    public void testEditUserAsDifferentUser() {
        // Создаем двух пользователей
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        // Регистрация первого пользователя
        Response responseCreateUser1 = RestAssured
                .given()
                .body(userData1)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        // Регистрация второго пользователя
        Response responseCreateUser2 = RestAssured
                .given()
                .body(userData2)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId1 = responseCreateUser1.jsonPath().getString("id");
        String userId2 = responseCreateUser2.jsonPath().getString("id");

        // Авторизация под вторым пользователем
        Map<String, String> authData2 = new HashMap<>();
        authData2.put("email", userData2.get("email"));
        authData2.put("password", userData2.get("password"));

        Response responseAuth2 = RestAssured
                .given()
                .body(authData2)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // Попытка редактирования первого пользователя под авторизацией второго
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "Changed Name");

        Response responseEdit = new ApiCoreRequests().editUser(userId1, editData,
                this.getHeader(responseAuth2, "x-csrf-token"),
                this.getCookie(responseAuth2, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseEdit, 403); // Проверка на доступ запрещен
    }

    /*
    Ex17
    Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем,
    на новый email без символа @
     */
    @Test
    public void testEditEmailWithoutAtSymbol() {
        // Создаем пользователя и авторизуемся под ним
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreate = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreate.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // Попытка изменить email на некорректный
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "invalidEmailWithoutAtSymbol");

        Response responseEdit = new ApiCoreRequests().editUser(userId, editData,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseEdit, 400); // Проверка на неправильный email
    }

    /*
    Ex17
    Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем,
    на очень короткое значение в один символ
     */
    @Test
    public void testEditFirstNameToShortValue() {
        // Создаем пользователя и авторизуемся под ним
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreate = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreate.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // Попытка изменить firstName на слишком короткое значение
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "A"); // Один символ

        Response responseEdit = new ApiCoreRequests().editUser(userId, editData,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseEdit, 400); // Проверка на неправильное значение firstName
    }
}