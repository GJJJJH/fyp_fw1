package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.Task;
import com.example.fyp_1.DataStructure.Truck;

public class FP2SCTime {
    private Truck truck;
    private Task task;
    private long timeqc;
    private long timesrc;

    public FP2SCTime(Truck truck, Task task, long timeqc, long timesrc) {
        this.truck = truck;
        this.task = task;
        this.timeqc = timeqc;
        this.timesrc = timesrc;
    }

    public FP2SCTime() {
        this.truck = null;
        this.task = null;
        this.timeqc = 0;
        this.timesrc = 0;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public long getTimeqc() {
        return timeqc;
    }

    public void setTimeqc(long timeqc) {
        this.timeqc = timeqc;
    }

    public long getTimesrc() {
        return timesrc;
    }

    public void setTimesrc(long timesrc) {
        this.timesrc = timesrc;
    }

    @Override
    public String toString() {
        if (truck != null && task != null) {
            return "FP2SCTime(" + task.getWorkQueueName() + ", " + truck.getTruckID() + "): " + timeqc;
        } else {
            return "FP2SCTime(null)";
        }
    }
}