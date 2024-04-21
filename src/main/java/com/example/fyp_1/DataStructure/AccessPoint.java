package com.example.fyp_1.DataStructure;

public class AccessPoint {
    private long timeStart;
    private long timeFinish;
    private Task task;
    private Truck truck;

    // AccessPoint constructor
    public AccessPoint(long timeStart, long timeFinish, Task task, Truck truck) {
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.task = task;
        this.truck = truck;
    }

    // Getter and setter methods
    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(long timeFinish) {
        this.timeFinish = timeFinish;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }
}
