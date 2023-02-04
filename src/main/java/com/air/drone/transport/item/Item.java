package com.air.drone.transport.item;

import com.air.drone.transport.drone.Drone;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table
public class Item {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private long id;
    @Pattern(regexp = "([a-zA-Z1-9-_]*)", message = "Item name should only contain letters, numbers, - and _")
    private String name;

    @Pattern(regexp = "([A-Z1-9_]*)", message = "Item code should contain only capital letters and _")
    private String code;
    private float weight;
    private String image;

//    @Column(name = "droneId")
    @ManyToOne (cascade = CascadeType.REMOVE)
    @JoinColumn (name = "drone_id", referencedColumnName = "id")
    @JsonBackReference
    private Drone drone;

    public Item() {}

    public Item(String name, String code, float weight, String image) {
        this.name = name;
        this.code = code;
        this.weight = weight;
        this.image = image;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long itemId) {
        this.id = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }
    public Drone getDrone() {
        return drone;
    }
}
