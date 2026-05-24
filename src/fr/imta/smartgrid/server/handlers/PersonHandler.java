package fr.imta.smartgrid.server.handlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler assurant le cycle de vie CRUD complet pour les entités {@link Person}.
 * Inclus la gestion des relations complexes avec les réseaux (Grids) et l'attribution/Révocation
 * des capteurs possédés (owned_sensors).
 */
public class PersonHandler {
    private EntityManager db;

    public PersonHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Liste l'intégralité des identifiants des personnes enregistrées.
     */
    public void getIds(RoutingContext ctx) {
        List<Integer> personIds = db.createNativeQuery("SELECT id from person").getResultList();
        ctx.json(personIds);
    }

    /**
     * Récupère le profil d'une personne par son ID.
     */
    public void getById(RoutingContext ctx) {
        Person person = db.find(Person.class, Integer.parseInt(ctx.pathParam("id")));

        if (person == null) {
            ctx.response().setStatusCode(404).end("Person not found");
        } else {
            ctx.json(person.toJSON());
        }
    }

    /**
     * Crée une nouvelle entité Person en base à partir d'un payload JSON.
     * Valide la présence des champs obligatoires et lie les éventuels capteurs associés.
     */
    public void create(RoutingContext ctx){
        JsonObject input = ctx.body().asJsonObject();
        
        // --- VALIDATIONS DE SURFACE ---
        if (!input.containsKey("first_name")) {
            ctx.response().setStatusCode(500); // Devrait idéalement être un code 400 (Bad Request)
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
        
        // Gestion adaptative du type si l'ID de la grille est passé sous forme de chaîne
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

        // Instanciation et initialisation du modèle
        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setGrid(g);

        // --- GESTION DES CAPTEURS ASSOCIES (Relation Many-To-Many / One-To-Many) ---
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
                    // Établissement de la relation bidirectionnelle
                    p.getSensors().add(s);
                    s.getOwners().add(p);
                }
            } catch (Exception e) {
                ctx.response().setStatusCode(400);
                ctx.json("owned_sensors must be a JSON array");
                return;
            }
        }

        // Calcul de l'ID manuel (simulation d'auto-incrément)
        Integer id = ((Number) db.createNativeQuery("SELECT max(id) FROM person").getSingleResult()).intValue();
        p.setId(id+1);
        
        // --- EXECUTION TRANSACTIONNELLE ---
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

    /**
     * Met à jour les informations d'une personne existante (mise à jour partielle / comportement de type PATCH).
     */
    public void update(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));

        Person p = db.find(Person.class, id);
        if (p == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Person not found");
            return;
        }

        JsonObject input = ctx.body().asJsonObject();

        // Application conditionnelle des modifications structurelles
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

        // Synchronization de l'état de la collection des capteurs possédés (Ajout / Révocation)
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

            // Phase 1 : Suppression des relations obsolètes qui ne figurent plus dans le nouveau payload
            var existingSensors = List.copyOf(p.getSensors());
            for (Sensor s : existingSensors) {
                if (!newIds.contains(s.getId())) {
                    s.getOwners().removeIf(owner -> owner.getId() == p.getId());
                    p.getSensors().removeIf(existing -> existing.getId() == s.getId());
                }
            }

            // Phase 2 : Ajout des nouvelles relations manquantes
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

        // Validation des modifications en base (Dirty checking automatique de JPA lors du commit)
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

    /**
     * Supprime une personne de l'application.
     */
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
            db.remove(p); // Suppression de l'entité
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            ctx.response().setStatusCode(500);
            ctx.json("unable to delete person: " + e.getMessage());
            return;
        }

        ctx.response().setStatusCode(204); // No Content
        ctx.json("Success");
    }
}