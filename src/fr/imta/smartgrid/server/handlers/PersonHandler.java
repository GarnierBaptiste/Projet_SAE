package fr.imta.smartgrid.server.handlers;


import java.util.List;
import java.util.Set;
import java.util.HashSet;

import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class PersonHandler {
    private EntityManager db;

    public PersonHandler(EntityManager db) {
        this.db = db;
    }

    public void getIds(RoutingContext ctx) {
        List<Integer> personIds = db.createNativeQuery("SELECT id from person").getResultList();
        ctx.json(personIds);
    }

    public void getById(RoutingContext ctx) {
        Person person = db.find(Person.class, Integer.parseInt(ctx.pathParam("id")));

        if (person == null) {
            ctx.fail(404);
            ctx.json("Person not found");
        } else {
            ctx.json(person.toJSON());
        }
    }

    public void create(RoutingContext ctx){
        JsonObject input = ctx.body().asJsonObject();
        if (!input.containsKey("first_name")) {
            ctx.response().setStatusCode(500);
            ctx.json("Missing first_name");
            return;
        }
        if (!input.containsKey("last_name")){
            ctx.response().setStatusCode(500);
            ctx.json("Missing last_name");
            return;
        }
        if (!input.containsKey("grid")){
            ctx.response().setStatusCode(500);
            ctx.json("Missing grid");
            return;
        }

        String first = input.getString("first_name");
        String last = input.getString("last_name");
        Integer gridId = input.getInteger("grid");
        if (gridId == null) {
            try {
                gridId = Integer.parseInt(input.getValue("grid").toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("grid must be an integer id");
                return;
            }
        }

        Grid g = db.find(Grid.class, gridId);
        if (g == null) {
            ctx.response().setStatusCode(404);
            ctx.json("grid not found");
            return;
        }

        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setGrid(g);

        if (input.containsKey("owned_sensors")) {
            try {
                var arr = input.getJsonArray("owned_sensors");
                for (int i = 0; i < arr.size(); i++) {
                    Integer sid = null;
                    try {
                        sid = Integer.parseInt(arr.getValue(i).toString());
                    } catch (Exception ex) {
                        ctx.response().setStatusCode(400);
                        ctx.json("owned_sensors must be an array of integer ids");
                        return;
                    }
                    Sensor s = db.find(Sensor.class, sid);
                    if (s == null) {
                        ctx.response().setStatusCode(404);
                        ctx.json("sensor not found: " + sid);
                        return;
                    }
                    p.getSensors().add(s);
                    s.getOwners().add(p);
                }
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("owned_sensors must be a JSON array");
                return;
            }
        }

        Integer id = ((Number) db.createNativeQuery("SELECT max(id) FROM person").getSingleResult()).intValue();

        p.setId(id+1);
        var tx = db.getTransaction();
        try {
            tx.begin();
            db.persist(p);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            ctx.response().setStatusCode(500);
            ctx.json("unable to create person: " + e.getMessage());
            return;
        }

        ctx.response().setStatusCode(201);
        ctx.json(p.toJSONID());
    }

    public void update(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));

        Person p = db.find(Person.class, id);
        if (p == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Person not found");
            return;
        }

        JsonObject input = ctx.body().asJsonObject();

        if (input.containsKey("first_name")) {
            p.setFirstName(input.getString("first_name"));
        }
        if (input.containsKey("last_name")) {
            p.setLastName(input.getString("last_name"));
        }
        if (input.containsKey("grid")) {
            Integer gridId = input.getInteger("grid");
            if (gridId == null) {
                try {
                    gridId = Integer.parseInt(input.getValue("grid").toString());
                } catch (Exception e) {
                    ctx.response().setStatusCode(400);
                    ctx.json("grid must be an integer id");
                    return;
                }
            }
            Grid g = db.find(Grid.class, gridId);
            if (g == null) {
                ctx.response().setStatusCode(404);
                ctx.json("grid not found");
                return;
            }
            p.setGrid(g);
        }

        if (input.containsKey("owned_sensors")) {
            var arr = input.getJsonArray("owned_sensors");
            Set<Integer> newIds = new HashSet<>();
            try {
                for (int i = 0; i < arr.size(); i++) {
                    newIds.add(Integer.parseInt(arr.getValue(i).toString()));
                }
            } catch (Exception ex) {
                ctx.response().setStatusCode(400);
                ctx.json("owned_sensors must be an array of integer ids");
                return;
            }

            var existingSensors = List.copyOf(p.getSensors());
            for (Sensor s : existingSensors) {
                if (!newIds.contains(s.getId())) {
                    s.getOwners().removeIf(owner -> owner.getId() == p.getId());
                    p.getSensors().removeIf(existing -> existing.getId() == s.getId());
                }
            }

            for (Integer sid : newIds) {
                boolean already = p.getSensors().stream().anyMatch(existing -> existing.getId() == sid);
                if (!already) {
                    Sensor s = db.find(Sensor.class, sid);
                    if (s == null) {
                        ctx.response().setStatusCode(404);
                        ctx.json("sensor not found: " + sid);
                        return;
                    }
                    p.getSensors().add(s);
                    s.getOwners().add(p);
                }
            }
        }

        var tx = db.getTransaction();
        try {
            tx.begin();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            ctx.response().setStatusCode(500);
            ctx.json("unable to update person: " + e.getMessage());
            return;
        }

        ctx.response().setStatusCode(200);
        ctx.json(p.toJSON());
    }

    public void delete(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));

        Person p = db.find(Person.class, id);
        if (p == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Person not found");
            return;
        }

        var tx = db.getTransaction();
        try {
            tx.begin();
            db.remove(p);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            ctx.response().setStatusCode(500);
            ctx.json("unable to delete person: " + e.getMessage());
            return;
        }

        ctx.response().setStatusCode(204);
        ctx.json("Success");
    }
}