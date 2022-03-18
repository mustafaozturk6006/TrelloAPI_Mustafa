package Trello_API;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigurationReader;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Random;

public class Task {

    String boardId="";
    String listId="";
    String[] cardsIdArr = new String[2];

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = ConfigurationReader.getProperty("baseURI");
        RestAssured.basePath = ConfigurationReader.getProperty("basePath");
    }

    //Trello üzerinde bir board oluşturunuz.
    @Test(priority=1)
    public void CreateBoard(){
        boardId =
                given()
                        .contentType("application/json").
                        when()
                        .queryParam("key", ConfigurationReader.getProperty("key"))
                        .queryParam("token", ConfigurationReader.getProperty("token"))
                        .queryParam("name", "NewBoard")
                        .post("/boards").
                        then()
                        .statusCode(200)
                        .contentType(ContentType.JSON).
                        assertThat()
                        .body("name", equalTo("NewBoard"))
                        .extract().path("id");
    }

    //Card oluşturabilmek için bir list oluşturunuz.
    @Test(priority=2)
    public void CreateListOnTheBoard(){

        listId=
                given()
                .contentType("application/json")
                .when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .queryParam("name","NewList")
                .post("/boards/"+boardId+"/lists")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body("name", equalTo("NewList"))
                .extract().path("id");
    }

    //Oluşturduğunuz board’ a iki tane kart oluşturunuz.
    @Test(priority=3)
    public void CreateCardsOnTheBoard(){

        for (int i = 0; i < 2; i++) {
            cardsIdArr[i] = given()
                    .contentType("application/json").
                    when()
                    .queryParam("key", ConfigurationReader.getProperty("key"))
                    .queryParam("token", ConfigurationReader.getProperty("token"))
                    .queryParam("name","NewCard"+i)
                    .queryParam("idList",listId)
                    .queryParam("desc","initial text")
                    .post("/cards").
                    then()
                    .statusCode(200)
                    .contentType(ContentType.JSON).
                    assertThat()
                    .body("name", equalTo("NewCard"+i))
                    .extract().path("id");
        }
    }

    //Oluştrduğunuz bu iki karttan random olacak sekilde bir tanesini güncelleyiniz.
    @Test(priority=4)
        public void EditCardOnTheBoard() {

        Random rd = new Random();
        int randomCardId = rd.nextInt(cardsIdArr.length);

        given()
                .contentType("application/json").
                when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .queryParam("name", "NewCard"+ randomCardId)
                .queryParam("desc", "edited text")
                .put("/cards/" + cardsIdArr[randomCardId]).
                then()
                .statusCode(200)
                .contentType(ContentType.JSON).
                assertThat()
                .body("desc", equalTo("edited text"))
                .extract().path("id");

    }

    //Oluşturduğunuz kartları siliniz.
    @Test(priority=5)
    public void DeleteCardOnTheBoard() {

        for (int i = 0; i < cardsIdArr.length; i++) {
            given()
                    .contentType("application/json").
                    when()
                    .queryParam("key", ConfigurationReader.getProperty("key"))
                    .queryParam("token", ConfigurationReader.getProperty("token"))
                    .delete("/cards/" + cardsIdArr[i]).
                    then()
                    .statusCode(200);
        }
    }

    //Oluşturduğunuz board’ u siliniz.
    @Test(priority=6)
    public void DeleteTheBoard() {

            given()
                    .contentType("application/json").
                    when()
                    .queryParam("key", ConfigurationReader.getProperty("key"))
                    .queryParam("token", ConfigurationReader.getProperty("token"))
                    .delete("/boards/" + boardId).
                    then()
                    .statusCode(200);

    }
    }



