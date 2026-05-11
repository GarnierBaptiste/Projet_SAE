package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Consumer;
import java.util.ArrayList;
import java.util.List;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ConsumerHandler {
    private EntityManager db;

    public ConsumerHandler(EntityManager db) {
        this.db = db;
    }

    public void getConsumers(RoutingContext ctx) {
        List<Consumer> consumers = db.createQuery("SELECT c FROM Consumer c", Consumer.class).getResultList();
        List<JsonObject> response = new ArrayList<>();
        for (Consumer consumer : consumers) {
            response.add(consumer.toJSON());
        }
        ctx.json(response);
    }
}