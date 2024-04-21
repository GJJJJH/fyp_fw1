package com.example.fyp_1.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class Node{
    private int node_index;
    private String id;
    private NodeType type;
    private long averageLoadTime;
    private long averageUnloadTime;
    private List<Truck> truckQueue;
    private List<Task> queueTasks;
    private Task currentTask;


    public Node(int node_index, String name, NodeType type, long loadTime, long unloadTime) {
        this.id = name;
        this.type = type;
        this.node_index = node_index;
        this.averageLoadTime = loadTime;
        this.averageUnloadTime = unloadTime;
        this.truckQueue = new ArrayList<>();
        this.queueTasks = new ArrayList<>();
        this.currentTask = null;
    }

    public Node(int node_index, String id, NodeType type) {
        this(node_index,id, type, 0, 0);
    }

    public Node(Node node){
        this.id = node.id;
        this.type = node.type;
        this.node_index = node.node_index;
        this.averageLoadTime = node.averageLoadTime;
        this.averageUnloadTime = node.averageUnloadTime;
        this.truckQueue =node.truckQueue;
        this.queueTasks = node.queueTasks;
        this.currentTask = node.currentTask;
    }

    public int getNode_index() {
        return node_index;
    }

    public void setNode_index(int node_index) {
        this.node_index = node_index;
    }

    public String getName() {
        return id;
    }

    public void setName(String name) {
        this.id = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public long getAverageLoadTime() {
        return averageLoadTime;
    }

    public void setAverageLoadTime(long loadTime) {
        this.averageLoadTime = loadTime;
    }

    public long getAverageUnloadTime() {
        return averageUnloadTime;
    }

    public void setAverageUnloadTime(long unloadTime) {
        this.averageUnloadTime = unloadTime;
    }

    public List<Truck> getTruckQueue() {
        return truckQueue;
    }

    public void setTruckQueue(List<Truck> truckQueue) {
        this.truckQueue = truckQueue;
    }

    public List<Task> getQueueTasks() {
        return queueTasks;
    }

    public void setQueueTasks(List<Task> queueTasks) {
        this.queueTasks = queueTasks;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    // toString method
//    @Override
//    public String toString() {
//        return "Node{" +
//                "node_index=" + node_index +
//                ", name='" + id + '\'' +
//                ", type=" + type +
//                ", load_time=" + averageLoadTime +
//                ", unload_time=" + averageUnloadTime +
//                ", truck_queue=" + truckQueue +
//                ", queue_tasks=" + queueTasks +
//                ", current_task=" + currentTask +
//                '}';
//    }
}
