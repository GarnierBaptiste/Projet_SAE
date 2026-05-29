package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler d'ingestion pour les données de télémétrie issues d'éoliennes.
 * Contrairement aux panneaux solaires, ce module traite un payload structuré en JSON
 * pour extraire la vitesse du vent et la puissance générée.
 */
public class WindTurbineHandler {
    private EntityManager db;

    public WindTurbineHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Analyse le JSON de télémétrie, calcule la métrique et insère un nouvel enregistrement
     * dans la table des points de données.
     */
    public void ingress(RoutingContext ctx){
        JsonObject input = ctx.body().asJsonObject();
        
        // --- VALIDATIONS DE COHÉRENCE ET DE STRUCTURE DU PAYLOAD JSON ---
        if (!input.containsKey("windturbine")){
            ctx.response().setStatusCode(400);
            ctx.json("Need windturbine id");
            return;
        }
        if (!input.containsKey("timestamp")){
            ctx.response().setStatusCode(400);
            ctx.json("Need for timestamp");
            return;
        }
        JsonObject data = input.getJsonObject("data");
        if (data == null) {
            ctx.response().setStatusCode(400);
            ctx.json("Need data object");
            return;
        }
        if (!data.containsKey("speed")){
            ctx.response().setStatusCode(400);
            ctx.json("Need for speed");
            return;
        }
        if (!data.containsKey("power")){
            ctx.response().setStatusCode(400);
            ctx.json("Need for power");
            return;
        }

        // Récupération sécurisée et typée de l'identifiant de la turbine
        Integer turbineId = input.getInteger("windturbine");
        if (turbineId == null) {
            try {
                turbineId = Integer.parseInt(input.getValue("windturbine").toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("windturbine must be an integer id");
                return;
            }
        }

        Sensor turbine = db.find(Sensor.class, turbineId);
        if (turbine == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Wind turbine not found");
            return;
        }

        // Extraction et typage des autres variables du message IoT
        Long timestamp = input.getLong("timestamp");
        if (timestamp == null) {
            try {
                timestamp = Long.parseLong(input.getValue("timestamp").toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("timestamp must be an integer");
                return;
            }
        }

        Double speed = data.getDouble("speed");
        if (speed == null) {
            try {
                speed = Double.parseDouble(data.getValue("speed").toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("speed must be a number");
                return;
            }
        }

        Double power = data.getDouble("power");
        if (power == null) {
            try {
                power = Double.parseDouble(data.getValue("power").toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("power must be a number");
                return;
            }
        }

        // --- GENERATION DE LA CLÉ PRIMAIRE ET INSERTION ---
        // Requête native pour récupérer la valeur maximale de l'ID actuel (Calcul manuel d'auto-incrémentation)
        Object result = db.createNativeQuery("SELECT COALESCE(MAX(id), 0) FROM datapoint").getSingleResult();
        Long maxId = result != null ? ((Number) result).longValue() : 0L;
    
        // Insertion en base du nouveau Datapoint (valeur = vitesse * puissance)
        db.getTransaction().begin();
        db.createNativeQuery("INSERT INTO datapoint (timestamp, value, measurement) VALUES (?, ?, ?)")
            .setParameter(1, timestamp)
            .setParameter(2, speed * power)
            .setParameter(3, 5)
            .executeUpdate();
        db.getTransaction().commit();

        ctx.response().setStatusCode(201);
        ctx.json("success");
    }
}