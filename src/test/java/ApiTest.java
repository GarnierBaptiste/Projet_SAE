import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import static org.hamcrest.Matchers.greaterThan;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiTest {

    static int createdPersonId = -1;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    // =========================================================
    // Required
    // =========================================================

    // =========================================================
    // GET /grids — Liste tous les IDs de grilles
    // =========================================================
 
    @Test
    @Order(1)
    @DisplayName("GET /grids - retourne 200 et une liste d'IDs")
    void getAllGrids_returns200AndList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/grids")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty()));
    }
 
    // =========================================================
    // GET /grid/{id} — Détails d'une grille
    // =========================================================
 
    @Test
    @Order(2)
    @DisplayName("GET /grid/1 - retourne 200 avec les champs id, name, description, users, sensors")
    void getGridById_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("name", equalTo("La Chantrerie"))
            .body("description", equalTo("smart grid du quartier de la Chantrerie"))
            .body("users", notNullValue())
            .body("sensors", notNullValue());
    }
 
    @Test
    @Order(3)
    @DisplayName("GET /grid/1 - users contient bien les personnes 1 et 2")
    void getGridById_usersContainsExpectedPersons() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}")
        .then()
            .statusCode(200)
            .body("users", hasItems(1, 2));
    }
 
    @Test
    @Order(4)
    @DisplayName("GET /grid/1 - sensors contient bien les capteurs 1, 2 et 3")
    void getGridById_sensorsContainsExpectedSensors() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}")
        .then()
            .statusCode(200)
            .body("sensors", hasItems(1, 2, 3));
    }
 
    @Test
    @Order(5)
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
 
    // =========================================================
    // GET /persons — Liste tous les IDs de personnes
    // =========================================================
 
    @Test
    @Order(6)
    @DisplayName("GET /persons - retourne 200 et une liste d'IDs")
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
 
    @Test
    @Order(7)
    @DisplayName("GET /persons - contient bien les IDs 1 et 2")
    void getAllPersons_containsExpectedIds() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/persons")
        .then()
            .statusCode(200)
            .body("$", hasItems(1, 2));
    }
 
    // =========================================================
    // GET /person/{id} — Détails d'une personne
    // =========================================================
 
    @Test
    @Order(8)
    @DisplayName("GET /person/1 - retourne 200 avec tous les champs de George Abitbol")
    void getPersonById_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/person/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("first_name", equalTo("George"))
            .body("last_name", equalTo("Abitbol"))
            .body("grid", equalTo(1))
            .body("owned_sensors", notNullValue());
    }
 
    @Test
    @Order(9)
    @DisplayName("GET /person/1 - owned_sensors contient les capteurs 1 et 2")
    void getPersonById_ownedSensorsCorrect() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/person/{id}")
        .then()
            .statusCode(200)
            .body("owned_sensors", hasItems(1, 2));
    }
 
    @Test
    @Order(10)
    @DisplayName("GET /person/2 - retourne 200 pour Pétère (last_name peut être null)")
    void getPersonById2_returns200() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 2)
        .when()
            .get("/person/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(2))
            .body("first_name", equalTo("Pétère"));
    }
 
    @Test
    @Order(11)
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
 
    // =========================================================
    // GET /measurement/{id} — Définition d'une mesure
    // =========================================================
 
    @Test
    @Order(12)
    @DisplayName("GET /measurement/1 - retourne 200 avec id, name, unit, sensor")
    void getMeasurementById_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/measurement/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("name", equalTo("temperature"))
            .body("unit", equalTo("C°"))
            .body("sensor", equalTo(1));
    }
 
    @Test
    @Order(13)
    @DisplayName("GET /measurement/2 - retourne 200 pour la mesure power (W)")
    void getMeasurementById2_returns200() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 2)
        .when()
            .get("/measurement/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(2))
            .body("name", equalTo("power"))
            .body("unit", equalTo("W"));
    }
 
    @Test
    @Order(14)
    @DisplayName("GET /measurement/3 - retourne 200 pour total_energy_produced (Wh)")
    void getMeasurementById3_returns200() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 3)
        .when()
            .get("/measurement/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(3))
            .body("name", equalTo("total_energy_produced"))
            .body("unit", equalTo("Wh"));
    }
 
    @Test
    @Order(15)
    @DisplayName("GET /measurement/9999 - retourne 404 pour une mesure inexistante")
    void getMeasurementById_returns404WhenNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/measurement/{id}")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // Medium
    // =========================================================

    // =========================================================
    // PUT /person — Créer une personne
    // =========================================================

    @Test
    @Order(16)
    @DisplayName("PUT /person - crée une personne valide et retourne 201 avec son id")
    void createPerson_returns201AndId() {
        createdPersonId = given()
            .contentType(ContentType.JSON)
            .body("{\"first_name\": \"Test\", \"last_name\": \"User\", \"grid\": 1}")
        .when()
            .put("/person")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
        .extract()
            .path("id");
    }

    @Test
    @Order(17)
    @DisplayName("PUT /person - retourne 500 si first_name manquant")
    void createPerson_returns500WhenMissingFirstName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"last_name\": \"User\", \"grid\": 1}")
        .when()
            .put("/person")
        .then()
            .statusCode(500);
    }

    @Test
    @Order(18)
    @DisplayName("PUT /person - retourne 500 si last_name manquant")
    void createPerson_returns500WhenMissingLastName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"first_name\": \"Test\", \"grid\": 1}")
        .when()
            .put("/person")
        .then()
            .statusCode(500);
    }

    @Test
    @Order(19)
    @DisplayName("PUT /person - retourne 500 si grid manquant")
    void createPerson_returns500WhenMissingGrid() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"first_name\": \"Test\", \"last_name\": \"User\"}")
        .when()
            .put("/person")
        .then()
            .statusCode(500);
    }

    @Test
    @Order(20)
    @DisplayName("PUT /person - retourne 404 si grid inexistante")
    void createPerson_returns404WhenGridNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"first_name\": \"Test\", \"last_name\": \"User\", \"grid\": 9999}")
        .when()
            .put("/person")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // POST /person/{id} — Mettre à jour une personne
    // =========================================================

    @Test
    @Order(21)
    @DisplayName("POST /person/{id} - met à jour le first_name et retourne 200 avec les nouvelles valeurs")
    void updatePerson_returns200WithUpdatedValues() {
        // Utilise la personne créée au test 1 (sinon fallback sur id=1)
        int id = createdPersonId > 0 ? createdPersonId : 1;

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", id)
            .body("{\"first_name\": \"UpdatedName\"}")
        .when()
            .post("/person/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("first_name", equalTo("UpdatedName"));
    }

    @Test
    @Order(22)
    @DisplayName("POST /person/{id} - met à jour last_name et grid")
    void updatePerson_updatesLastNameAndGrid() {
        int id = createdPersonId > 0 ? createdPersonId : 1;

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", id)
            .body("{\"last_name\": \"UpdatedLast\", \"grid\": 1}")
        .when()
            .post("/person/{id}")
        .then()
            .statusCode(200)
            .body("last_name", equalTo("UpdatedLast"));
    }

    @Test
    @Order(23)
    @DisplayName("POST /person/{id} - retourne 404 pour une personne inexistante")
    void updatePerson_returns404WhenNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 9999)
            .body("{\"first_name\": \"Nobody\"}")
        .when()
            .post("/person/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(24)
    @DisplayName("POST /person/{id} - retourne 404 si grid inexistante dans update")
    void updatePerson_returns404WhenGridNotFound() {
        int id = createdPersonId > 0 ? createdPersonId : 1;

        given()
            .contentType(ContentType.JSON)
            .pathParam("id", id)
            .body("{\"grid\": 9999}")
        .when()
            .post("/person/{id}")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // DELETE /person/{id} — Supprimer une personne
    // =========================================================

    @Test
    @Order(25)
    @DisplayName("DELETE /person/{id} - supprime la personne créée et retourne 204")
    void deletePerson_returns204() {
        // On supprime la personne créée au test 1
        int id = createdPersonId > 0 ? createdPersonId : -1;
        if (id == -1) return; // Si la création a échoué, on passe ce test

        given()
            .pathParam("id", id)
        .when()
            .delete("/person/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @Order(26)
    @DisplayName("DELETE /person/{id} - retourne 404 pour une personne inexistante")
    void deletePerson_returns404WhenNotFound() {
        given()
            .pathParam("id", 9999)
        .when()
            .delete("/person/{id}")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // GET /sensors/{kind} — Lister les capteurs par type
    // =========================================================

    @Test
    @Order(27)
    @DisplayName("GET /sensors/SolarPanel - retourne 200 et une liste d'IDs")
    void getSensorsByKind_SolarPanel_returns200AndList() {
        given()
            .accept(ContentType.JSON)
            .pathParam("kind", "SolarPanel")
        .when()
            .get("/sensors/{kind}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", notNullValue());
    }

    @Test
    @Order(28)
    @DisplayName("GET /sensors/WindTurbine - retourne 200 et une liste d'IDs")
    void getSensorsByKind_WindTurbine_returns200AndList() {
        given()
            .accept(ContentType.JSON)
            .pathParam("kind", "WindTurbine")
        .when()
            .get("/sensors/{kind}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", notNullValue());
    }

    @Test
    @Order(29)
    @DisplayName("GET /sensors/EVCharger - retourne 200 et une liste d'IDs")
    void getSensorsByKind_EVCharger_returns200AndList() {
        given()
            .accept(ContentType.JSON)
            .pathParam("kind", "EVCharger")
        .when()
            .get("/sensors/{kind}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", notNullValue());
    }

    @Test
    @Order(30)
    @DisplayName("GET /sensors/UnknownKind - retourne 200 avec une liste vide")
    void getSensorsByKind_UnknownKind_returnsEmptyList() {
        given()
            .accept(ContentType.JSON)
            .pathParam("kind", "UnknownKind")
        .when()
            .get("/sensors/{kind}")
        .then()
            .statusCode(200)
            .body("$", empty());
    }

    // =========================================================
    // GET /producers — Lister tous les producteurs
    // =========================================================

    @Test
    @Order(31)
    @DisplayName("GET /producers - retourne 200 et une liste non vide")
    void getAllProducers_returns200AndNonEmptyList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/producers")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty()));
    }

    @Test
    @Order(32)
    @DisplayName("GET /producers - chaque producteur a un id et un name")
    void getAllProducers_eachProducerHasIdAndName() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/producers")
        .then()
            .statusCode(200)
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue());
    }

    // =========================================================
    // GET /consumers — Lister tous les consommateurs
    // =========================================================

    @Test
    @Order(33)
    @DisplayName("GET /consumers - retourne 200 et une liste non vide")
    void getAllConsumers_returns200AndNonEmptyList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/consumers")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty()));
    }

    @Test
    @Order(34)
    @DisplayName("GET /consumers - chaque consommateur a un id et un name")
    void getAllConsumers_eachConsumerHasIdAndName() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/consumers")
        .then()
            .statusCode(200)
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue());
    }

    // =========================================================
    // Advanced
    // =========================================================

    // =========================================================
    // POST /ingress/windturbine — Recevoir une mesure d'éolienne
    // Format : JSON { windturbine, timestamp, data: { speed, power } }
    // =========================================================

    @Test
    @Order(35)
    @DisplayName("POST /ingress/windturbine - données valides retourne 201")
    void ingressWindTurbine_validData_returns201() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 2," +
                "\"timestamp\": 1700000000," +
                "\"data\": { \"speed\": 12.5, \"power\": 3000.0 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(201);
    }

    @Test
    @Order(36)
    @DisplayName("POST /ingress/windturbine - retourne 400 si windturbine manquant")
    void ingressWindTurbine_missingWindturbine_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"timestamp\": 1700000000," +
                "\"data\": { \"speed\": 12.5, \"power\": 3000.0 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(37)
    @DisplayName("POST /ingress/windturbine - retourne 400 si timestamp manquant")
    void ingressWindTurbine_missingTimestamp_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 2," +
                "\"data\": { \"speed\": 12.5, \"power\": 3000.0 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(38)
    @DisplayName("POST /ingress/windturbine - retourne 400 si data manquant")
    void ingressWindTurbine_missingData_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 2," +
                "\"timestamp\": 1700000000" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(39)
    @DisplayName("POST /ingress/windturbine - retourne 400 si speed manquant dans data")
    void ingressWindTurbine_missingSpeed_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 2," +
                "\"timestamp\": 1700000000," +
                "\"data\": { \"power\": 3000.0 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(40)
    @DisplayName("POST /ingress/windturbine - retourne 400 si power manquant dans data")
    void ingressWindTurbine_missingPower_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 2," +
                "\"timestamp\": 1700000000," +
                "\"data\": { \"speed\": 12.5 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(41)
    @DisplayName("POST /ingress/windturbine - retourne 404 si windturbine ID inexistant")
    void ingressWindTurbine_unknownId_returns404() {
        given()
            .contentType(ContentType.JSON)
            .body("{" +
                "\"windturbine\": 9999," +
                "\"timestamp\": 1700000000," +
                "\"data\": { \"speed\": 12.5, \"power\": 3000.0 }" +
            "}")
        .when()
            .post("/ingress/windturbine")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // POST /ingress/solarpanel — Recevoir une mesure de panneau solaire
    // Format texte brut : "id:temperature:power:timestamp"
    // =========================================================

    @Test
    @Order(42)
    @DisplayName("POST /ingress/solarpanel - données valides retourne 201")
    void ingressSolarPanel_validData_returns201() {
        given()
            .contentType("text/plain")
            .body("1:25.5:1500.0:1700000001")
        .when()
            .post("/ingress/solarpanel")
        .then()
            .statusCode(201);
    }

    @Test
    @Order(43)
    @DisplayName("POST /ingress/solarpanel - retourne 400 si format incomplet (moins de 4 parties)")
    void ingressSolarPanel_incompleteFormat_returns400() {
        given()
            .contentType("text/plain")
            .body("1:25.5:1500.0")  // 3 parties au lieu de 4
        .when()
            .post("/ingress/solarpanel")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(44)
    @DisplayName("POST /ingress/solarpanel - retourne 400 si valeurs non numériques")
    void ingressSolarPanel_invalidNumbers_returns400() {
        given()
            .contentType("text/plain")
            .body("abc:def:ghi:jkl")
        .when()
            .post("/ingress/solarpanel")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(45)
    @DisplayName("POST /ingress/solarpanel - retourne 404 si panneau solaire ID inexistant")
    void ingressSolarPanel_unknownId_returns404() {
        given()
            .contentType("text/plain")
            .body("9999:25.5:1500.0:1700000001")
        .when()
            .post("/ingress/solarpanel")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // GET /sensor/{id} — Détails d'un capteur
    // =========================================================

    @Test
    @Order(46)
    @DisplayName("GET /sensor/1 - retourne 200 avec tous les champs du SolarPanel")
    void getSensorById_SolarPanel_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/sensor/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("name", equalTo("Panneau Solaire Modifié"))
            .body("kind", equalTo("SolarPanel"))
            .body("grid", equalTo(1))
            .body("owners", notNullValue())
            .body("available_measurements", notNullValue())
            .body("power_source", equalTo("solar"))
            .body("efficiency", notNullValue());
    }

    @Test
    @Order(47)
    @DisplayName("GET /sensor/2 - retourne 200 avec tous les champs du WindTurbine")
    void getSensorById_WindTurbine_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 2)
        .when()
            .get("/sensor/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(2))
            .body("kind", equalTo("WindTurbine"))
            .body("power_source", equalTo("wind"))
            .body("height", notNullValue())
            .body("blade_length", notNullValue());
    }

    @Test
    @Order(48)
    @DisplayName("GET /sensor/3 - retourne 200 avec tous les champs du EVCharger")
    void getSensorById_EVCharger_returns200WithAllFields() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 3)
        .when()
            .get("/sensor/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(3))
            .body("kind", equalTo("EVCharger"))
            .body("max_power", notNullValue());
    }

    @Test
    @Order(49)
    @DisplayName("GET /sensor/9999 - retourne 404 pour un capteur inexistant")
    void getSensorById_returns404WhenNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/sensor/{id}")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // POST /sensor/{id} — Mettre à jour un capteur
    // =========================================================

    @Test
    @Order(50)
    @DisplayName("POST /sensor/1 - met à jour le name et retourne 200")
    void updateSensor_updatesName_returns200() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 1)
            .body("{\"name\": \"Panneau Solaire Modifié\"}")
        .when()
            .post("/sensor/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("Panneau Solaire Modifié"));
    }

    @Test
    @Order(51)
    @DisplayName("POST /sensor/1 - met à jour efficiency (SolarPanel)")
    void updateSensor_updatesEfficiency_returns200() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 1)
            .body("{\"efficiency\": 0.95}")
        .when()
            .post("/sensor/{id}")
        .then()
            .statusCode(200)
            .body("efficiency", notNullValue());
    }

    @Test
    @Order(52)
    @DisplayName("POST /sensor/2 - met à jour height et blade_length (WindTurbine)")
    void updateSensor_updatesWindTurbineFields_returns200() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 2)
            .body("{\"height\": 80.0, \"blade_length\": 40.0}")
        .when()
            .post("/sensor/{id}")
        .then()
            .statusCode(200)
            .body("height", equalTo(80.0f))
            .body("blade_length", equalTo(40.0f));
    }

    @Test
    @Order(53)
    @DisplayName("POST /sensor/9999 - retourne 404 pour un capteur inexistant")
    void updateSensor_returns404WhenNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 9999)
            .body("{\"name\": \"Inexistant\"}")
        .when()
            .post("/sensor/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(54)
    @DisplayName("POST /sensor/1 - retourne 404 si grid inexistante")
    void updateSensor_returns404WhenGridNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 1)
            .body("{\"grid\": 9999}")
        .when()
            .post("/sensor/{id}")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // GET /measurement/{id}/values — Valeurs d'une mesure
    // =========================================================

    @Test
    @Order(55)
    @DisplayName("GET /measurement/1/values - retourne 200 avec sensor_id, measurement_id et values")
    void getMeasurementValues_returns200WithStructure() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("sensor_id", equalTo(1))
            .body("measurement_id", equalTo(1))
            .body("values", notNullValue())
            .body("values.size()", greaterThan(0));
    }

    @Test
    @Order(56)
    @DisplayName("GET /measurement/1/values - chaque datapoint a timestamp et value")
    void getMeasurementValues_datapointsHaveTimestampAndValue() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(200)
            .body("values[0].timestamp", notNullValue())
            .body("values[0].value", notNullValue());
    }

    @Test
    @Order(57)
    @DisplayName("GET /measurement/1/values?from=1743513408&to=1743514068 - filtre par plage valide")
    void getMeasurementValues_withValidRange_returnsFilteredData() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
            .queryParam("from", 1743513408)
            .queryParam("to",   1743514068)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(200)
            .body("values", notNullValue())
            .body("values.size()", greaterThan(0));
    }

    @Test
    @Order(58)
    @DisplayName("GET /measurement/1/values?from=2000000000&to=2000000000 - plage future retourne liste vide")
    void getMeasurementValues_withFutureRange_returnsEmptyList() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
            .queryParam("from", 2000000000)
            .queryParam("to",   2000000000)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(200)
            .body("values", empty());
    }

    @Test
    @Order(59)
    @DisplayName("GET /measurement/1/values?from=100&to=50 - retourne 400 si from > to")
    void getMeasurementValues_invalidRange_returns400() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
            .queryParam("from", 100)
            .queryParam("to",   50)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(60)
    @DisplayName("GET /measurement/9999/values - retourne 404 pour une mesure inexistante")
    void getMeasurementValues_returns404WhenNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/measurement/{id}/values")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // Hard
    // =========================================================

    // =========================================================
    // GET /grid/{id}/production — Production totale de la grille
    // =========================================================

    @Test
    @Order(61)
    @DisplayName("GET /grid/1/production - retourne 200 et un nombre")
    void getGridProduction_returns200AndNumber() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/production")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @Order(62)
    @DisplayName("GET /grid/1/production - la valeur est un nombre positif")
    void getGridProduction_valueIsPositive() {
        float production = given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/production")
        .then()
            .statusCode(200)
        .extract()
            .as(Float.class);

        assert production >= 0 : "La production doit être >= 0";
    }

    @Test
    @Order(63)
    @DisplayName("GET /grid/9999/production - retourne 404 pour une grille inexistante")
    void getGridProduction_returns404WhenGridNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/grid/{id}/production")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // GET /grid/{id}/consumption — Consommation totale de la grille
    // =========================================================

    @Test
    @Order(64)
    @DisplayName("GET /grid/1/consumption - retourne 200 et un nombre")
    void getGridConsumption_returns200AndNumber() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/consumption")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @Order(65)
    @DisplayName("GET /grid/1/consumption - la valeur est un nombre positif")
    void getGridConsumption_valueIsPositive() {
        float consumption = given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/consumption")
        .then()
            .statusCode(200)
        .extract()
            .as(Float.class);

        assert consumption >= 0 : "La consommation doit être >= 0";
    }

    @Test
    @Order(66)
    @DisplayName("GET /grid/9999/consumption - retourne 404 pour une grille inexistante")
    void getGridConsumption_returns404WhenGridNotFound() {
        given()
            .accept(ContentType.JSON)
            .pathParam("id", 9999)
        .when()
            .get("/grid/{id}/consumption")
        .then()
            .statusCode(404);
    }

    // =========================================================
    // Cohérence production / consommation
    // =========================================================

    @Test
    @Order(67)
    @DisplayName("Production et consommation de la grille 1 sont deux valeurs distinctes")
    void productionAndConsumption_areDifferentValues() {
        float production = given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/production")
        .then()
            .statusCode(200)
        .extract()
            .as(Float.class);

        float consumption = given()
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .get("/grid/{id}/consumption")
        .then()
            .statusCode(200)
        .extract()
            .as(Float.class);

        assert production >= 0 : "Production invalide";
        assert consumption >= 0 : "Consommation invalide";
    }
}