package fr.imta.smartgrid.server.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.model.Sensor;
import fr.imta.smartgrid.model.SolarPanel;
import fr.imta.smartgrid.model.WindTurbine;
import java.util.List;
import java.util.Set;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class SensorHandler {
    private EntityManager db;

    public SensorHandler(EntityManager db) {
        this.db = db;
    }

    public void getbyKind(RoutingContext ctx) {
        String kind = ctx.pathParam("kind");
        List<Integer> sensorIds = db.createNativeQuery("SELECT id FROM Sensor where dtype = '" + kind + "'").getResultList();
        ctx.json(sensorIds);
    }

    public void getbyID(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Sensor sensor = db.find(Sensor.class, id);
        if (sensor == null){
            ctx.response().setStatusCode(404);
            ctx.json("Sensor not found");
        }
        else{
            ctx.json(sensor.toJSON());
        }

    }

    public void update(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Sensor sensor = db.find(Sensor.class, id);
        if (sensor == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Sensor not found");
            return;
        }

        JsonObject input = ctx.body().asJsonObject();

        Grid grid = null;
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

            grid = db.find(Grid.class, gridId);
            if (grid == null) {
                ctx.response().setStatusCode(404);
                ctx.json("grid not found");
                return;
            }
        }

        boolean updatingOwners = input.containsKey("owners");
        Set<Integer> ownerIds = new HashSet<>();
        List<Person> newOwners = new ArrayList<>();
        if (updatingOwners) {
            var arr = input.getJsonArray("owners");

            try {
                for (int i = 0; i < arr.size(); i++) {
                    Integer ownerId = Integer.parseInt(arr.getValue(i).toString());
                    if (ownerIds.add(ownerId)) {
                        Person owner = db.find(Person.class, ownerId);
                        if (owner == null) {
                            ctx.response().setStatusCode(404);
                            ctx.json("person not found: " + ownerId);
                            return;
                        }
                        newOwners.add(owner);
                    }
                }
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("owners must be an array of integer ids");
                return;
            }
        }

        String powerSource = null;
        Float efficiency = null;
        Double height = null;
        Double bladeLength = null;
        Double maxPower = null;

        if (sensor instanceof Producer) {
            if (input.containsKey("power_source")) {
                powerSource = input.getString("power_source");
            }

            if (sensor instanceof SolarPanel && input.containsKey("efficiency")) {
                try {
                    Object rawEfficiency = input.getValue("efficiency");
                    if (rawEfficiency == null) {
                        ctx.response().setStatusCode(400);
                        ctx.json("efficiency must be a number");
                        return;
                    }
                    efficiency = rawEfficiency instanceof Number
                            ? ((Number) rawEfficiency).floatValue()
                            : Float.parseFloat(rawEfficiency.toString());
                } catch (Exception e) {
                    ctx.response().setStatusCode(400);
                    ctx.json("efficiency must be a number");
                    return;
                }
            }

            if (sensor instanceof WindTurbine) {
                if (input.containsKey("height")) {
                    try {
                        Object rawHeight = input.getValue("height");
                        if (rawHeight == null) {
                            ctx.response().setStatusCode(400);
                            ctx.json("height must be a number");
                            return;
                        }
                        height = rawHeight instanceof Number
                                ? ((Number) rawHeight).doubleValue()
                                : Double.parseDouble(rawHeight.toString());
                    } catch (Exception e) {
                        ctx.response().setStatusCode(400);
                        ctx.json("height must be a number");
                        return;
                    }
                }

                if (input.containsKey("blade_length")) {
                    try {
                        Object rawBladeLength = input.getValue("blade_length");
                        if (rawBladeLength == null) {
                            ctx.response().setStatusCode(400);
                            ctx.json("blade_length must be a number");
                            return;
                        }
                        bladeLength = rawBladeLength instanceof Number
                                ? ((Number) rawBladeLength).doubleValue()
                                : Double.parseDouble(rawBladeLength.toString());
                    } catch (Exception e) {
                        ctx.response().setStatusCode(400);
                        ctx.json("blade_length must be a number");
                        return;
                    }
                }
            }
        }

        if (sensor instanceof Consumer && input.containsKey("max_power")) {
            try {
                Object rawMaxPower = input.getValue("max_power");
                if (rawMaxPower == null) {
                    ctx.response().setStatusCode(400);
                    ctx.json("max_power must be a number");
                    return;
                }
                maxPower = rawMaxPower instanceof Number
                        ? ((Number) rawMaxPower).doubleValue()
                        : Double.parseDouble(rawMaxPower.toString());
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("max_power must be a number");
                return;
            }
        }

        var tx = db.getTransaction();
        try {
            tx.begin();

            if (input.containsKey("name")) {
                sensor.setName(input.getString("name"));
            }
            if (input.containsKey("description")) {
                sensor.setDescription(input.getString("description"));
            }
            if (grid != null) {
                sensor.setGrid(grid);
            }
            if (updatingOwners) {
                List<Person> existingOwners = List.copyOf(sensor.getOwners());

                for (Person owner : existingOwners) {
                    boolean keep = ownerIds.contains(owner.getId());
                    if (!keep) {
                        owner.getSensors().removeIf(existing -> existing.getId() == sensor.getId());
                        sensor.getOwners().removeIf(existing -> existing.getId() == owner.getId());
                    }
                }

                for (Person owner : newOwners) {
                    boolean alreadyLinked = sensor.getOwners().stream().anyMatch(existing -> existing.getId() == owner.getId());
                    if (!alreadyLinked) {
                        owner.getSensors().add(sensor);
                        sensor.getOwners().add(owner);
                    }
                }
            }

            if (sensor instanceof Producer producer) {
                if (powerSource != null) {
                    producer.setPowerSource(powerSource);
                }
                if (sensor instanceof SolarPanel solarPanel && efficiency != null) {
                    solarPanel.setEfficiency(efficiency);
                }
                if (sensor instanceof WindTurbine windTurbine) {
                    if (height != null) {
                        windTurbine.setHeight(height);
                    }
                    if (bladeLength != null) {
                        windTurbine.setBladeLength(bladeLength);
                    }
                }
            }

            if (sensor instanceof Consumer consumer && maxPower != null) {
                consumer.setMaxPower(maxPower);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            ctx.response().setStatusCode(500);
            ctx.json("unable to update sensor: " + e.getMessage());
            return;
        }

        ctx.json(sensor.toJSON());
    }
}