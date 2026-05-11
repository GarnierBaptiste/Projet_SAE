package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MeasurementHandler {
    private EntityManager db;

    public MeasurementHandler(EntityManager db) {
        this.db = db;
    }

    public void getById(RoutingContext ctx){
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.fail(404);
            ctx.json("Measurement not found");
        } else {
            ctx.json(m.toJSON());
        }
    }

    public void getMeasurement(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));

        List<DataPoint> measurements = db.createNativeQuery("select timestamp, value from datapoint where measurement =" + id)
                .setParameter("id", id)
                .getResultList();
        ctx.json(measurements);

        // Measurement measurement = db.find(Measurement.class, id);
        // if (measurement == null) {
        //     ctx.fail(404);
        //     ctx.json("Measurement not found");
        //     return;
        // }

        // Integer from = null;
        // Integer to = null;

        // String fromStr = ctx.request().getParam("from");
        // String toStr = ctx.request().getParam("to");

        // if (fromStr != null) {
        //     from = Integer.parseInt(fromStr);
        // }
        // if (toStr != null) {
        //     to = Integer.parseInt(toStr);
        // }

        // StringBuilder jpql = new StringBuilder("SELECT d FROM DataPoint d WHERE d.measurement.id = :id");
        // if (from != null) {
        //     jpql.append(" AND d.timestamp >= :from");
        // }
        // if (to != null) {
        //     jpql.append(" AND d.timestamp <= :to");
        // }
        // // jpql.append(" ORDER BY d.timestamp");

        // var query = db.createQuery(jpql.toString(), DataPoint.class)
        //     .setParameter("id", id);

        // if (from != null) {
        //     query.setParameter("from", from.longValue());
        // }
        // if (to != null) {
        //     query.setParameter("to", to.longValue());
        // }

        // List<DataPoint> datapoints = query.getResultList();

        // JsonArray values = new JsonArray();
        // for (DataPoint datapoint : datapoints) {
        //     JsonObject value = new JsonObject();
        //     value.put("timestamp", datapoint.getTimestamp());
        //     value.put("value", datapoint.getValue());
        //     values.add(value);
        // }

        // JsonObject response = new JsonObject();
        // response.put("sensor_id", measurement.getSensor().getId());
        // response.put("measurement_id", measurement.getId());
        // response.put("values", values);

        // ctx.json(response);

    }
}
