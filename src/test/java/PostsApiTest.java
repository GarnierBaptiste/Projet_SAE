import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

/**
 * Tests d'intégration pour l'API Smart Grid (Vert.x, port 8080).
 * 
 * IMPORTANT : le serveur Vert.x doit être démarré AVANT de lancer ces tests.
 * Lance d'abord VertxServer.main() ou via Docker Compose, puis mvn test.
 */
class PostsApiTest {

    @BeforeAll
    static void setup() {
        // Ton serveur Vert.x tourne sur localhost:8080
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    // ========================================================
    // Required
    // ========================================================

        // ====================================================
        // Get /grids
        // ====================================================

    @Test
    @DisplayName("GET /grids - retourne 200 et une liste d'IDs")
    void getAllGrids_returns200AndList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/grids")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty())); // la liste n'est pas vide
    }

        // ====================================================
        // Get /grid/{id}
        // ====================================================

    @Test
    @DisplayName("GET /grid/1 - retourne la grille avec id, name, description, users, sensors")
    void getGridById_returnsCorrectGrid() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("name", not(emptyOrNullString()))
            .body("description", notNullValue())
            .body("users", notNullValue())
            .body("sensors", notNullValue());
    }

    @Test
    @DisplayName("GET /grid/9999 - retourne 404 pour une grille inexistante")
    void getGridById_returns404WhenNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/grid/{id}")
        .then()
            .statusCode(404);
    }

        // ====================================================
        // Get /persons
        // ====================================================

    @Test
    @DisplayName("GET /persons - retourne 200 et une liste d'IDs de personnes")
    void getAllPersons_returns200AndList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/persons")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty()));
    }

        // ====================================================
        // Get /person/{id}
        // ====================================================

    @Test
    @DisplayName("GET /person/1 - retourne la personne avec ses champs")
    void getPersonById_returnsCorrectPerson() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/person/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("first_name", not(emptyOrNullString()))
            .body("last_name", not(emptyOrNullString()))
            .body("grid", notNullValue())
            .body("owned_sensors", notNullValue());
    }

    @Test
    @DisplayName("GET /person/9999 - retourne 404 pour une personne inexistante")
    void getPersonById_returns404WhenNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/person/{id}")
        .then()
            .statusCode(404);
    }
}
//     // =========================================================
//     // GRIDS
//     // =========================================================

//     @Test
//     @DisplayName("GET /grid/1/production - retourne 200 et un nombre")
//     void getGridProduction_returns200AndNumber() {
//         given()
//             .accept(ContentType.JSON)
//             .pathParam("id", 1)
//         .when()
//             .get("/grid/{id}/production")
//         .then()
//             .statusCode(200);
//     }

//     @Test
//     @DisplayName("GET /grid/1/consumption - retourne 200 et un nombre")
//     void getGridConsumption_returns200AndNumber() {
//         given()
//             .accept(ContentType.JSON)
//             .pathParam("id", 1)
//         .when()
//             .get("/grid/{id}/consumption")
//         .then()
//             .statusCode(200);
//     }

//     // =========================================================
//     // PERSONS
//     // =========================================================

//     @Test
//     @DisplayName("PUT /person - crée une nouvelle personne et retourne 201 avec son id")
//     void createPerson_returns201AndId() {
//         given()
//             .contentType(ContentType.JSON)
//             .body("{\"first_name\": \"Test\", \"last_name\": \"User\", \"grid\": 1}")
//         .when()
//             .put("/person")
//         .then()
//             .statusCode(201)
//             .contentType(ContentType.JSON)
//             .body("id", notNullValue());
//     }

//     @Test
//     @DisplayName("PUT /person - retourne 500 si first_name manquant")
//     void createPerson_returns500WhenMissingFirstName() {
//         given()
//             .contentType(ContentType.JSON)
//             .body("{\"last_name\": \"User\", \"grid\": 1}")
//         .when()
//             .put("/person")
//         .then()
//             .statusCode(500);
//     }

//     // =========================================================
//     // SENSORS
//     // =========================================================

//     @Test
//     @DisplayName("GET /sensors/SolarPanel - retourne 200 et une liste d'IDs")
//     void getSensorsByKind_SolarPanel_returns200() {
//         given()
//             .accept(ContentType.JSON)
//             .pathParam("kind", "SolarPanel")
//         .when()
//             .get("/sensors/{kind}")
//         .then()
//             .statusCode(200)
//             .contentType(ContentType.JSON)
//             .body("$", notNullValue());
//     }

//     @Test
//     @DisplayName("GET /sensors/WindTurbine - retourne 200 et une liste d'IDs")
//     void getSensorsByKind_WindTurbine_returns200() {
//         given()
//             .accept(ContentType.JSON)
//             .pathParam("kind", "WindTurbine")
//         .when()
//             .get("/sensors/{kind}")
//         .then()
//             .statusCode(200)
//             .contentType(ContentType.JSON)
//             .body("$", notNullValue());
//     }

//     @Test
//     @DisplayName("GET /sensor/9999 - retourne 404 pour un capteur inexistant")
//     void getSensorById_returns404WhenNotFound() {
//         given()
//             .accept(ContentType.JSON)
//             .pathParam("id", 9999)
//         .when()
//             .get("/sensor/{id}")
//         .then()
//             .statusCode(404);
//     }

//     // =========================================================
//     // PRODUCERS & CONSUMERS
//     // =========================================================

//     @Test
//     @DisplayName("GET /producers - retourne 200 et une liste")
//     void getAllProducers_returns200AndList() {
//         given()
//             .accept(ContentType.JSON)
//         .when()
//             .get("/producers")
//         .then()
//             .statusCode(200)
//             .contentType(ContentType.JSON)
//             .body("$", notNullValue());
//     }

//     @Test
//     @DisplayName("GET /consumers - retourne 200 et une liste")
//     void getAllConsumers_returns200AndList() {
//         given()
//             .accept(ContentType.JSON)
//         .when()
//             .get("/consumers")
//         .then()
//             .statusCode(200)
//             .contentType(ContentType.JSON)
//             .body("$", notNullValue());
//     }
// }
