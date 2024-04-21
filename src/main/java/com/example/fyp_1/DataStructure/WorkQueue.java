package com.example.fyp_1.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class WorkQueue {
    private String queueName;
    private Node pow;
    private List<Task> tasks;
    private List<Truck> truckBindings;
    private char taskMerging;
    private int maxTruck;

    // Constructor
    public WorkQueue() {
        pow = null;
        tasks = new ArrayList<>();
        truckBindings = new ArrayList<>();
        taskMerging = 3;
        maxTruck = -1;
    }

    // Getter and setter methods
    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Node getPow() {
        return pow;
    }

    public void setPow(Node pow) {
        this.pow = pow;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Truck> getTruckBindings() {
        return truckBindings;
    }

    public void setTruckBindings(List<Truck> truckBindings) {
        this.truckBindings = truckBindings;
    }

    public char getTaskMerging() {
        return taskMerging;
    }

    public void setTaskMerging(char taskMerging) {
        this.taskMerging = taskMerging;
    }

    public int getMaxTruck() {
        return maxTruck;
    }

    public void setMaxTruck(int maxTruck) {
        this.maxTruck = maxTruck;
    }

    // Additional method
    public boolean verifyBinding(Truck tr) {
        return truckBindings.contains(tr);
    }

    public Task findTaskByConID(String containerId) {
        for (int j = 0; j < tasks.size(); j++) {
            if (tasks.get(j).getContainerID().equals(containerId)) {
                Task res = tasks.get(j);
                tasks.remove(j);
                return res;
            }
        }
        return null;
    }

    public WorkQueue(WorkQueue workQueue){
        this.queueName = workQueue.queueName;
        this.pow = workQueue.pow;
        this.tasks = workQueue.tasks;
    }
}
