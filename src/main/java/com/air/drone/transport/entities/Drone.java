package com.air.drone.transport.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Drone {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;
    @Column(length = 100, name = "serial_number", unique = true)
    @Size(max = 100, message = "Serial number exceeds 100 characters")
    private String serialNumber;

    private DroneModel model;
    private DroneState state = DroneState.IDLE;
    private double batteryCapacity;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="droneId", referencedColumnName = "id")
    private List<Item> items = new ArrayList<>();

    @Transient
    private boolean available;

    public Drone(){}

    public Drone(DroneModel model, double batteryCapacity, String serialNumber) {
        this.model = model;
        this.batteryCapacity =  batteryCapacity;
        this.serialNumber = serialNumber;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public DroneModel getModel() {
        return model;
    }

    public void setModel(DroneModel model) {
        this.model = model;
    }

    public DroneState getState() {
        return state;
    }

    public void setState(DroneState state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return this.state == DroneState.IDLE && this.batteryCapacity >= 25.0;
    }

    public List<Item> getItems() {
        return this.items;
    }
}