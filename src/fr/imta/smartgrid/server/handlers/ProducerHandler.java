package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Producer;
import java.util.List;
import java.util.ArrayList;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ProducerHandler {
        private EntityManager db;

    public ProducerHandler(EntityManager db) {
        this.db = db;
    }

    public void getProducers(RoutingContext ctx) {
        List<Producer> producers = db.createQuery("SELECT p FROM Producer p", Producer.class).getResultList();
        List<JsonObject> response = new ArrayList<>();
        for (Producer producer : producers) {
            response.add(producer.toJSON());
        }
        ctx.json(response);
    }
}