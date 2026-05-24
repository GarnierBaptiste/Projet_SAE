package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Consumer;
import java.util.ArrayList;
import java.util.List;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler pour les opérations liées aux objets {@link Consumer}.
 * Assure la liaison entre l'accès aux données via JPA et l'exposition des
 * endpoints de l'API HTTP gérée par Vert.x.
 */
public class ConsumerHandler {
    private EntityManager db;

    /**
     * Initialise le handler avec l'instance de persistance de l'application.
     * * @param db Le gestionnaire d'entités JPA
     */
    public ConsumerHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Récupère la liste complète de tous les consommateurs d'énergie.
     * Transforme chaque entité en objet JSON pour la réponse HTTP.
     * * @param ctx Le contexte de routage Vert.x
     */
    public void getConsumers(RoutingContext ctx) {
        // Requête JPQL pour récupérer tous les enregistrements Consumer
        List<Consumer> consumers = db.createQuery("SELECT c FROM Consumer c", Consumer.class).getResultList();
        
        // Conversion de la liste d'entités en structure JSON compatible Vert.x
        List<JsonObject> response = new ArrayList<>();
        for (Consumer consumer : consumers) {
            response.add(consumer.toJSON());
        }
        
        // Envoi de la réponse JSON avec un statut HTTP 200 par défaut
        ctx.json(response);
    }
}