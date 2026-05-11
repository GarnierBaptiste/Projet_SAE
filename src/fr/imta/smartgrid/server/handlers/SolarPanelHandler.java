package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.SolarPanel;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class SolarPanelHandler {
    private EntityManager db;

    public SolarPanelHandler(EntityManager db) {
        this.db = db;
    }

    public void ingress(RoutingContext ctx){
        String input = ctx.body().asString();
        String[] parts = input.split(":");

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

            SolarPanel solarpanel = db.find(SolarPanel.class, id);
            if (solarpanel == null){
                ctx.response().setStatusCode(404);
                ctx.json("Solar Panel not found");
                return;
            }

            db.createNativeQuery("INSERT INTO datapoint (id, timestamp, value, type) VALUES (?, ?, ?, ?)")
            .setParameter(1, id)
            .setParameter(2, timestamp)
            .setParameter(3, temperature * power)
            .setParameter(4, 2);

            ctx.response().setStatusCode(201);
            ctx.json(new JsonObject().put("status", "success"));
        }
        catch(NumberFormatException e){
            ctx.response().setStatusCode(400);
            ctx.json("Wrong format for the informations");
        }
    }
}
