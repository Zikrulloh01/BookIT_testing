package com.book_it.utilities;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookItUtils {


    public static String getToken(String email, String password ){

        Response response = given().queryParams("email", email)
                .and()
                .queryParams("password", password)
                .when()
                .get(ConfigurationReader.get("qa3api.uri") + "/sign");

        String token = response.path("accessToken");

        String finalToken = "Bearer " + token;

        return finalToken;
    }

}
