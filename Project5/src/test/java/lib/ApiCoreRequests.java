package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

/*
Класс содержит методы для выполнения HTTP-запросов (GET и POST) с использованием библиотеки RestAssured
 */
public class ApiCoreRequests {

    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request for user registration")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request for user login")
    public Response makePostRequestForLogin(String url, Map<String, String> loginData) {
        return given()
                .filter(new AllureRestAssured())
                .body(loginData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request to update user data")
    public Response makePostRequestToUpdateUser(String url, Map<String, String> userData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a GET-request to retrieve user data")
    public Response makeGetRequestForUser(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Получение данных другого пользователя с ID {userId} как авторизованный пользователь")
    public Response getUserDataAsAuthorized(String userId, String header, String cookie) {
        return given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Register a new user")
    public Response registerUser(Map<String, String> userData) {
        return makePostRequest("https://playground.learnqa.ru/api/user/", userData);
    }
    @Step("Login user")
    public Response loginUser(Map<String, String> loginData) {
        return makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", loginData);
    }

    @Step("Edit user data")
    public Response editUser(String userId, Map<String, String> data, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(data)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Get user data")
    public Response getUser(String userId, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Delete user by ID")
    public Response deleteUser(String userId, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .delete("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }
}