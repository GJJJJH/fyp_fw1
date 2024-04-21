package com.example.fyp_1;


import com.example.fyp_1.DataStructure.Node;
import com.example.fyp_1.DataStructure.Truck;
import com.example.fyp_1.DataStructure.WorkQueue;

import java.util.List;

public class PortEvent {
    private long time;
    private EventType type;
    private List<WorkQueue> workQueues;
    private Truck truck;
    private Node eventLocation;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public List<WorkQueue> getWorkQueues() {
        return workQueues;
    }

    public void setWorkQueues(List<WorkQueue> workQueues) {
        this.workQueues = workQueues;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Node getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Node eventLocation) {
        this.eventLocation = eventLocation;
    }
}
