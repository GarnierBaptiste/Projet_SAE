package fr.imta.smartgrid.server.handlers;

import java.util.ArrayList;
import java.util.List;

import fr.imta.smartgrid.model.Producer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler d'accès en lecture pour les ressources de type {@link Producer} (Unités de production).
 */
public class ProducerHandler {
    private EntityManager db;

    public ProducerHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Retourne la liste complète de tous les producteurs d'énergie.
     */
    public void getProducers(RoutingContext ctx) {
        List<Producer> producers = db.createQuery("SELECT p FROM Producer p", Producer.class).getResultList();
        List<JsonObject> response = new ArrayList<>();
        for (Producer producer : producers) {
            response.add(producer.toJSON());
        }
        ctx.json(response);
    }
}