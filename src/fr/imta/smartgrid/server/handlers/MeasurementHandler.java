package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Measurement;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler dédié au traitement des métadonnées des mesures ({@link Measurement}) et
 * à l'extraction des séries temporelles associées (points de données / datapoints).
 */
public class MeasurementHandler {

    private EntityManager db;

    public MeasurementHandler(EntityManager db) {
        this.db = db;
    }    

    /**
     * Récupère les métadonnées d'une mesure (comme sa configuration) par son ID.
     */
    public void getById(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.response().setStatusCode(404).end("Measurement not found");
        } else {
            ctx.json(m.toJSON());
        }
    }

    /**
     * Récupère l'historique des points de données (Time, Value) pour un instrument de mesure donné.
     * Supporte le filtrage optionnel par plage temporelle via les query parameters `from` et `to`.
     */
    public void getMeasurement(RoutingContext ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        // Validation de l'existence de la mesure parente
        if (db.find(Measurement.class, id) == null) {
            ctx.response().setStatusCode(404).end("Measurement not found");
            return;
        }

        // Construction dynamique de la requête SQL native
        // /!\ Attention : Concaténation de l'ID à risque d'injection si l'ID n'était pas préalablement casté en entier.
        String sql = "select timestamp, value from datapoint where measurement = " + id;

        Integer from = null;
        Integer to = null;
        String fromStr = ctx.request().getParam("from");
        String toStr = ctx.request().getParam("to");

        if (fromStr != null) {
            from = Integer.parseInt(fromStr);
        }
        if (toStr != null) {
            to = Integer.parseInt(toStr);
        }

        // Application des filtres de dates s'ils sont fournis en paramètres de requête
        if (from != null && to != null) {
            if (from > to){
                ctx.response().setStatusCode(400).end("Invalid date range");
                return;
            }
            sql += " and timestamp >= " + from + " and timestamp <= " + to + " order by timestamp";
        }
        
        // Exécution de la requête native et formatage du résultat
        List<Object[]> measurements = db.createNativeQuery(sql).getResultList();
        JsonArray values = new JsonArray();

        for (Object[] datapoint : measurements) {
            JsonObject value = new JsonObject();
            value.put("timestamp", datapoint[0]);
            value.put("value", datapoint[1]);
            values.add(value);
        }
        
        // Structuration du payload JSON final de retour
        JsonObject response = new JsonObject();
        response.put("sensor_id", id);
        response.put("measurement_id", id);
        response.put("values", values);
        ctx.json(response);   
    }
}