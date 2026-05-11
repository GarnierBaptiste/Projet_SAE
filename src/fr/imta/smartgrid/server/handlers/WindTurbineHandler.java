package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class WindTurbineHandler {
    private EntityManager db;

    public WindTurbineHandler(EntityManager db) {
        this.db = db;
    }

    public void ingress(RoutingContext ctx){
        JsonObject input = ctx.body().asJsonObject();
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

          Object result = db.createNativeQuery("SELECT COALESCE(MAX(id), 0) FROM datapoint").getSingleResult();
          Long maxId = result != null ? ((Number) result).longValue() : 0L;
        
          db.createNativeQuery("INSERT INTO datapoint (id, timestamp, value, type) VALUES (?, ?, ?, ?)")
              .setParameter(1, maxId + 1)
              .setParameter(2, timestamp)
              .setParameter(3, speed * power)
              .setParameter(4, 2);

        ctx.response().setStatusCode(201);
        ctx.json("success");
    }
}
