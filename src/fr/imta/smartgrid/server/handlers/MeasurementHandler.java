package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MeasurementHandler {

    private EntityManager db;

    public MeasurementHandler(EntityManager db) {
        this.db = db;
    }

    public void getById(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.fail(404);
            ctx.json("Measurement not found");
        } else {
            ctx.json(m.toJSON());
        }
    }

    public void getMeasurement(RoutingContext ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        if (db.find(Measurement.class, id) == null) {
            ctx.fail(404);
            ctx.json("Measurement not found");
            return;
        }

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

        if (from != null && to != null) {
            if (from > to){
                ctx.fail(400);
                ctx.json("Invalid date range");
                return;
            }
            sql += " and timestamp >= " + from + " and timestamp <= " + to + " order by timestamp";
        }
        List<Object[]> measurements = db.createNativeQuery(sql).getResultList();
        
        JsonArray values = new JsonArray();

        for (Object[] datapoint : measurements) {
            JsonObject value = new JsonObject();
            value.put("timestamp", datapoint[0]);
            value.put("value", datapoint[1]);
            values.add(value);
        }
        JsonObject response = new JsonObject();
        response.put("sensor_id", id);
        response.put("measurement_id", id);
        response.put("values", values);
        ctx.json(response);   
    }
}

        
