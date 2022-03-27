package com.book_it.step_definitions;

import com.book_it.pages.SelfPage;
import com.book_it.utilities.BookItUtils;
import com.book_it.utilities.ConfigurationReader;
import com.book_it.utilities.DBUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;

public class ApiStepDefs {

    String token;
    Response response;
    String globalEmail;
    Map<String, Object> apiMap = new HashMap<>();
    Map<String, Object> uiMap = new HashMap<>();
    Map<String, Object> dbMap = new HashMap<>();

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email , String password) {
        token = BookItUtils.getToken(email,password);
        globalEmail = email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        String url = ConfigurationReader.get("qa3api.uri") + "/api/users/me";
        response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when().get(url);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expectedStatusCode) {
        Assert.assertEquals(response.statusCode(), expectedStatusCode);
    }

    @Then("Information about current user from api and database should match")
    public void informationAboutCurrentUserFromApiAndDatabaseShouldMatch() {

        String query = "select id, firstname, lastname, role\n" +
                "from users\n" +
                "where email='" + globalEmail + "';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);

        System.out.println("rowMap = " + rowMap);

        JsonPath jsonPath = response.jsonPath();

        long expectedId = (long) rowMap.get("id");
        String expectedFName = (String) rowMap.get("firstname");
        String expectedLNAme = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        long actualIdApi = jsonPath.getLong("id");
        String actualFirstNameApi= jsonPath.getString("firstName");
        String actualLastNameApi = jsonPath.get("lastName");
        String actualRoleApi = jsonPath.get("role");

        assertEquals(expectedId, actualIdApi);
        assertEquals(expectedFName, actualFirstNameApi);
        assertEquals(expectedLNAme, actualLastNameApi);
        assertEquals(expectedRole, actualRoleApi);

        Map<String, Object> map = response.as(Map.class);

        System.out.println(map.toString());
    }

    @Then("UI, Api and DB user information must match")
    public void uiApiAndDBUserInformationMustMatch() {
        String query = "select id, firstname, lastname, role\n" +
                "from users\n" +
                "where email='" + globalEmail + "';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);

        System.out.println("rowMap = " + rowMap);

        JsonPath jsonPath = response.jsonPath();

        long expectedId = (long) rowMap.get("id");
        String expectedFName = (String) rowMap.get("firstname");
        String expectedLNAme = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        long actualIdApi = jsonPath.getLong("id");
        String actualFirstNameApi= jsonPath.getString("firstName");
        String actualLastNameApi = jsonPath.get("lastName");
        String actualRoleApi = jsonPath.get("role");

        assertEquals(expectedId, actualIdApi);
        assertEquals(expectedFName, actualFirstNameApi);
        assertEquals(expectedLNAme, actualLastNameApi);
        assertEquals(expectedRole, actualRoleApi);

        SelfPage selfPage = new SelfPage();
        String actualFullName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();
        String expectedFullName = expectedFName + " " + expectedLNAme;

        //UI vs DB
        assertEquals(expectedFullName, actualFullName);
        assertEquals(expectedRole, actualUIRole);


        //UI vs API
        String actualFullNameApi = actualFirstNameApi + " " + actualLastNameApi;
        assertEquals(actualFullNameApi, actualFullName);
        assertEquals(actualRoleApi, actualUIRole);
    }

    @And("I assign Api response into a map")
    public void iAssignDetailsIntoAMap() {
        assignFullNameAndRole();
        assignBatch();
        assignTeam();
        assignCampus();
        System.out.println("apiMap = " + apiMap);
    }

    @And("I get UI response")
    public void iGetUIResponse() {
        SelfPage selfPage = new SelfPage();
        // get user details from POM model SelfPage
        uiMap.put("name", selfPage.name.getText());
        uiMap.put("role", selfPage.role.getText());
        uiMap.put("team", selfPage.team.getText());
        uiMap.put("batch", selfPage.batch.getText().substring(1));
        uiMap.put("campus", selfPage.campus.getText());
        System.out.println("uiMap = " + uiMap);
    }



    public void assignFullNameAndRole(){
        String url = ConfigurationReader.get("qa3api.uri") + "/api/users/me";
        response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when().get(url);

        JsonPath jsonPath = response.jsonPath();
        apiMap.put("name", jsonPath.get("firstName") + " " + jsonPath.get("lastName"));
        apiMap.put("role", jsonPath.get("role"));
    }


    public void assignTeam(){
        Response response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(ConfigurationReader.get("qa3api.uri") + "/api/teams/my");

        assertEquals(response.statusCode(), 200);
        apiMap.put("team", response.path("name"));
    }

    public void assignBatch(){
       Response response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(ConfigurationReader.get("qa3api.uri") + "/api/batches/my");


        apiMap.put("batch", response.path("number").toString());
    }

    public void assignCampus(){
        Response response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(ConfigurationReader.get("qa3api.uri") + "/api/campuses/my");

        apiMap.put("campus", response.path("location"));
    }


    @Then("Verify all layers response")
    public void verifyAllLayersResponse() {
        // API vs UI
        assertEquals(apiMap,uiMap);

        //UI vs DB
        assertEquals(dbMap, uiMap);

        // API vs DB
        assertEquals(dbMap, apiMap);
    }

    @And("I get DB response")
    public void iGetDBResponse() {
        String query = "select firstname || ' ' || lastname as name, role,  location as campus, batch_number as batch, name as team\n" +
                "from users u\n" +
                "join campus c\n" +
                "on u.campus_id=c.id\n" +
                "join team t\n" +
                "on u.team_id=t.id\n" +
                "where email='sbirdbj@fc2.com';";

        dbMap = DBUtils.getRowMap(query);

        dbMap.put("batch", dbMap.get("batch").toString());

        System.out.println("dbMap = " + dbMap);
    }
}
