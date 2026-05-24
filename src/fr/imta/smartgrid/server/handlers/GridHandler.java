package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GridHandler {
    private EntityManager db;
    public GridHandler(EntityManager db) {
        this.db = db;
    }

    public void getIds(RoutingContext ctx) {
        @SuppressWarnings("unchecked")
        List<Integer> grids = db.createNativeQuery("SELECT g.id from grid as g").getResultList();

        ctx.json(grids);
    }

    public void getById(RoutingContext ctx) {
        Grid g = db.find(Grid.class, Integer.parseInt(ctx.pathParam("id")));
        if (g == null) {
            ctx.response().setStatusCode(404).end("grid not found");
        } else {
            ctx.json(g.toJSON());
        }
    }

    public void production(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Grid grid = db.find(Grid.class, id);
        if (grid == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Grid not found");
            return;
        }
        Double sum = (Double) db.createNativeQuery("select sum(max_val) from(select max(value) as max_val from datapoint as d join measurement as m on d.measurement = m.id where m.name = 'total_energy_produced' group by d.measurement) sub;").getResultList().get(0);
        ctx.json(sum);
    }

    public void consumption(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Grid grid = db.find(Grid.class, id);
        if (grid == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Grid not found");
            return;
        }
        Double sum = (Double) db.createNativeQuery("select sum(max_val) from(select max(value) as max_val from datapoint as d join measurement as m on d.measurement = m.id where m.name = 'total_energy_consumed' group by d.measurement) sub;").getResultList().get(0);
        ctx.json(sum);
    }
}

//select sum(d.value) from datapoint as d where d.measurement in (select m.id from measurement as m where m.sensor in (SELECT s.id FROM sensor as s where s.grid = " + id + " and s.id in (select c.id from consumer as c)))"