package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
 * Handler pour la gestion des ressources de type {@link Grid} (Grilles électriques).
 * Propose des endpoints pour l'analyse globale des sous-systèmes, l'accès unitaire
 * et le calcul de métriques agrégées (production/consommation).
 */
public class GridHandler {
    private EntityManager db;

    /**
     * Constructeur injectant l'{@code EntityManager} pour les opérations DB.
     */
    public GridHandler(EntityManager db) {
        this.db = db;
    }

    /**
     * Récupère l'ensemble des identifiants (IDs) des grilles existantes.
     * * @param ctx Le contexte de routage Vert.x
     */
    public void getIds(RoutingContext ctx) {
        // Utilisation d'une requête native SQL pour optimiser les performances (évite le chargement complet des objets)
        @SuppressWarnings("unchecked")
        List<Integer> grids = db.createNativeQuery("SELECT g.id from grid as g").getResultList();

        ctx.json(grids);
    }

    /**
     * Récupère les détails complets d'une grille spécifique via son identifiant unique passé en paramètre de chemin.
     * * @param ctx Le contexte de routage Vert.x
     */
    public void getById(RoutingContext ctx) {
        Grid g = db.find(Grid.class, Integer.parseInt(ctx.pathParam("id")));
        if (g == null) {
            // Retourne une erreur 404 si la ressource n'existe pas en base
            ctx.response().setStatusCode(404).end("grid not found");
        } else {
            ctx.json(g.toJSON());
        }
    }

    /**
     * Calcule la production d'énergie totale agrégée pour la grille spécifiée.
     * Récupère la valeur maximale enregistrée pour chaque capteur de type 'total_energy_produced'.
     * * @param ctx Le contexte de routage Vert.x
     */
    public void production(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Grid grid = db.find(Grid.class, id);
        if (grid == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Grid not found");
            return;
        }
        
        // Requête native SQL effectuant un groupement par mesure pour isoler la dernière/plus haute valeur de production,
        // puis somme ces valeurs maximales.
        Double sum = (Double) db.createNativeQuery("select sum(max_val) from(select max(value) as max_val from datapoint as d join measurement as m on d.measurement = m.id where m.name = 'total_energy_produced' group by d.measurement) sub;").getResultList().get(0);
        ctx.json(sum);
    }

    /**
     * Calcule la consommation d'énergie totale agrégée sur la grille spécifiée.
     * Récupère la valeur maximale enregistrée pour chaque capteur de type 'total_energy_consumed'.
     * * @param ctx Le contexte de routage Vert.x
     */
    public void consumption(RoutingContext ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Grid grid = db.find(Grid.class, id);
        if (grid == null) {
            ctx.response().setStatusCode(404);
            ctx.json("Grid not found");
            return;
        }
        
        // Même logique d'agrégation que pour la production, filtrée sur l'indicateur de consommation 'total_energy_consumed'
        Double sum = (Double) db.createNativeQuery("select sum(max_val) from(select max(value) as max_val from datapoint as d join measurement as m on d.measurement = m.id where m.name = 'total_energy_consumed' group by d.measurement) sub;").getResultList().get(0);
        ctx.json(sum);
    }
}