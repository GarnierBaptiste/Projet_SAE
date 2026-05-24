package fr.imta.smartgrid.server;

import java.util.Map;

import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import org.eclipse.persistence.logging.SessionLog;

import fr.imta.smartgrid.server.handlers.ConsumerHandler;
import fr.imta.smartgrid.server.handlers.GridHandler;
import fr.imta.smartgrid.server.handlers.MeasurementHandler;
import fr.imta.smartgrid.server.handlers.PersonHandler;
import fr.imta.smartgrid.server.handlers.ProducerHandler;
import fr.imta.smartgrid.server.handlers.SensorHandler;
import fr.imta.smartgrid.server.handlers.SolarPanelHandler;
import fr.imta.smartgrid.server.handlers.WindTurbineHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class VertxServer {
    private Vertx vertx;
    private EntityManager db; // database object

    public VertxServer() {
        this.vertx = Vertx.vertx();

        // setup database connexion
        Map<String, String> properties = Map.of(
            LOGGING_LEVEL, SessionLog.WARNING_LABEL // change to FINE_LABEL to get details on SQL query to database
        );

        var emf = Persistence.createEntityManagerFactory("smart-grid", properties);
        this.db = emf.createEntityManager();
    }

    public void start() {
        Router router = Router.router(vertx);

        // add handlers for payload parsing and to allow swagger to send requests
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create().addOrigin("*").allowedMethod(HttpMethod.DELETE).allowedMethod(HttpMethod.PUT));

        // create handlers and registers routes for grids
        GridHandler gh = new GridHandler(db);
        router.get("/grids").handler(gh::getIds);
        router.get("/grid/:id").handler(gh::getById);
        router.get("/grid/:id/production").handler(gh::production);
        router.get("/grid/:id/consumption").handler(gh::consumption);

        // create handlers and registers routes for persons
        PersonHandler ph = new PersonHandler(db);
        router.get("/persons").handler(ph::getIds);
        router.get("/person/:id").handler(ph::getById);
        router.put("/person").handler(ph::create);
        router.post("/person/:id").handler(ph::update);
        router.delete("/person/:id").handler(ph::delete);
        
        // create handlers and registers routes for measurements
        MeasurementHandler mh = new MeasurementHandler(db);
        router.get("/measurement/:id").handler(mh::getById);
        router.get("/measurement/:id/values").handler(mh::getMeasurement);

        // create handlers and registers routes for sensors
        SensorHandler sh = new SensorHandler(db);
        router.get("/sensors/:kind").handler(sh::getbyKind);
        router.get("/sensor/:id").handler(sh::getbyID);
        router.post("/sensor/:id").handler(sh::update);

        // create handlers and registers routes for producers and consumers
        ProducerHandler prh = new ProducerHandler(db);
        router.get("/producers").handler(prh::getProducers);
        ConsumerHandler ch = new ConsumerHandler(db);
        router.get("/consumers").handler(ch::getConsumers);

        // create handlers and registers routes for wind turbines and solar panels
        WindTurbineHandler wh = new WindTurbineHandler(db);
        router.post("/ingress/windturbine").handler(wh::ingress);
        SolarPanelHandler sph = new SolarPanelHandler(db);
        router.post("/ingress/solarpanel").handler(sph::ingress);
        
        // start the server
        vertx.createHttpServer().requestHandler(router).listen(8080)
            .onSuccess(e -> 
                System.out.println("Server is listening on localhost:" + e.actualPort())
            ).onFailure(e -> {
                System.out.println("Cannot start server, got error: " + e.getLocalizedMessage());
                System.exit(1);
            });
    }

    public static void main(String[] args) {
        new VertxServer().start();
    }
}
