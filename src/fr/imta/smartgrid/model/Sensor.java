package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "grid")
    private Grid grid;

    @ManyToMany(mappedBy = "sensors")
    private List<Person> owners = new ArrayList<>();

    @OneToMany(mappedBy = "sensor")
    private List<Measurement> measurements = new ArrayList<>();

    public JsonObject toJSON() {
        JsonObject res = new JsonObject();

        res.put("id", this.id);
        res.put("name", this.name);
        res.put("description", this.description);
        res.put("kind", this.getClass().getSimpleName());
        res.put("grid", this.grid != null ? this.grid.getId() : null);

        List<Integer> mIds = new ArrayList<>();
        for (Measurement m : this.measurements) {
            mIds.add(m.getId());
        }
        res.put("available_measurements", mIds);

        List<Integer> ownerIds = new ArrayList<>();
        for (Person o : this.owners) {
            ownerIds.add(o.getId());
        }
        res.put("owners", ownerIds);

        if (this instanceof Producer) {
            Producer p = (Producer) this;
            res.put("power_source", p.getPowerSource());
            if (p instanceof SolarPanel) {
                res.put("efficiency", ((SolarPanel) p).getEfficiency());
            } else if (p instanceof WindTurbine) {
                res.put("height", ((WindTurbine) p).getHeight());
                res.put("blade_length", ((WindTurbine) p).getBladeLength());
            }
        }

        if (this instanceof Consumer) {
            res.put("max_power", ((Consumer) this).getMaxPower());
        }

        return res;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Person> getOwners() {
        return owners;
    }

    public void setOwners(List<Person> owners) {
        this.owners = owners;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    
}
