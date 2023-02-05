package com.air.drone.transport.drone;

import com.air.drone.transport.item.Item;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Drone {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    @Column(length = 100, name = "serial_number", unique = true)
    @Size(max = 100, message = "Serial number exceeds 100 characters")
    private String serialNumber;

    @NotNull
    @DroneModelSubset(anyOf = {
            DroneModelType.HEAVY_WEIGHT,
            DroneModelType.LIGHT_WEIGHT,
            DroneModelType.CRUISER_WEIGHT,
            DroneModelType.MIDDLE_WEIGHT
    })
    private DroneModelType model;

    private DroneState state = DroneState.IDLE;

    @Digits(integer = 3, fraction = 2)
    @DecimalMax("100.00")
    @DecimalMin("0.00")
    private BigDecimal batteryCapacity;

    @OneToMany(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER,
            mappedBy = "drone",
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Item> items = new HashSet<>();

    @Transient
    private float maxWeight;

    @Transient
    private boolean available;

    public Drone(){}

    public Drone(DroneModelType model, BigDecimal batteryCapacity, String serialNumber) {
        this.model = model;
        this.batteryCapacity =  batteryCapacity;
        this.serialNumber = serialNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(BigDecimal batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public DroneModelType getModel() {
        return model;
    }

    public void setModel(DroneModelType model) {
        this.model = model;
    }

    public DroneState getState() {
        return state;
    }

    public void setState(DroneState state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return (this.state == DroneState.IDLE
                || this.state == DroneState.LOADING
                || this.state == DroneState.LOADED)
                && this.batteryCapacity.floatValue() >= new BigDecimal("20.00").floatValue();
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setDrone(this);
    }

    public float getMaxWeight() {
        return this.model.maxWeight;
    }

    public Drone deleteItem(Item item) {
        this.items.removeIf(i -> i.getId() == item.getId());
        item.setDrone(null);
        return this;
    }

    public Drone deleteAllItems() {
        this.items.forEach(item -> item.setDrone(null));
        this.items.clear();
        return this;
    }

    public Set<Item> getItems() {
        return this.items;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Drone)) {
            return false;
        }
        Drone d = (Drone) obj;
        return d.getSerialNumber().equals(this.serialNumber) && d.getModel() == this.model;
    }

    @Override
    public String toString() {
        return String.format("Drone{ id= %d, serialNumber= %s, model= %s, status= %s, weightLimit= %f, items= %d}",
                this.id, this.serialNumber, this.model, this.state.name(), this.model.maxWeight, this.items.size());
    }
}