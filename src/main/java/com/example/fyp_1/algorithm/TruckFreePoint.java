package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.Truck;
import com.example.fyp_1.DataStructure.*;
class TruckFreePoint {
    Truck tr;
    Node location;
    long time;
    Task nextTask;

    public Task getNextTask() {
        return nextTask;
    }

    public void setNextTask(Task nextTask) {
        this.nextTask = nextTask;
    }

    public TruckFreePoint(){
        this.tr = null;
        this.location = null;
        this.time = 0;
        this.nextTask = null;
    }

    public TruckFreePoint(Truck tr, Node location, long time) {
        this.tr = tr;
        this.location = location;
        this.time = time;
    }

    public Truck getTr() {
        return tr;
    }

    public void setTr(Truck tr) {
        this.tr = tr;
    }

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}