package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.SolarPanel;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler d'ingestion spécialisé pour la réception de flux de données brutes IoT
 * provenant de panneaux solaires.
 * Parse un format texte personnalisé et calcule la valeur de puissance effective transmise.
 */
public class SolarPanelHandler {
    private EntityManager db;

    public SolarPanelHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Endpoint d'entrée (Ingress) pour les données physiques du panneau.
     * Attend un corps de texte formaté sous la forme : {@code id:temperature:power:timestamp}
     */
    public void ingress(RoutingContext ctx){
        String input = ctx.body().asString();
        String[] parts = input.split(":");

        // Validation stricte du protocole de communication découpé par le caractère ":"
        if (parts.length != 4){
            ctx.response().setStatusCode(400);
            ctx.json("Missing Information" + parts.length);
            return;
        }

        try{
            int id = Integer.parseInt(parts[0]);
            double temperature = Double.parseDouble(parts[1]);
            double power = Double.parseDouble(parts[2]);
            long timestamp = Long.parseLong(parts[3]);

            // Vérification de la légitimité de la source matérielle (le capteur existe-t-il ?)
            SolarPanel solarpanel = db.find(SolarPanel.class, id);
            if (solarpanel == null){
                ctx.response().setStatusCode(404);
                ctx.json("Solar Panel not found");
                return;
            }

            // Insertion brute en BDD de la mesure calculée (Température * Puissance théorique)
            // L'ID du datapoint réutilise ici l'ID du composant matériel (Peut générer des conflits de clé primaire selon le schéma de table).
            db.createNativeQuery("INSERT INTO datapoint (id, timestamp, value, type) VALUES (?, ?, ?, ?)")
            .setParameter(1, id)
            .setParameter(2, timestamp)
            .setParameter(3, temperature * power)
            .setParameter(4, 2); // Le type '2' qualifie probablement la nature de la mesure dans le dictionnaire de l'application

            ctx.response().setStatusCode(201); // Created
            ctx.json(new JsonObject().put("status", "success"));
        }
        catch(NumberFormatException e){
            ctx.response().setStatusCode(400);
            ctx.json("Wrong format for the informations");
        }
    }
}