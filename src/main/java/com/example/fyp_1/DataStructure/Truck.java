package com.example.fyp_1.DataStructure;

public class Truck {
    private int truckIndex;
    private String truckID;
    private int maxTEU;
    private Node currentPosition;
    private TruckStatus status;
    private Task currentTask;
    private int remainingTEU;
    private Task preTask;

    public Task getPreTask() {
        return preTask;
    }

    public void setPreTask(Task preTask) {
        this.preTask = preTask;
    }

    public int getRemainingTEU() {
        return remainingTEU;
    }

    public void setRemainingTEU(int remainingTEU) {
        this.remainingTEU = remainingTEU;
    }

    // Truck constructor
    public Truck(String truckID, int maxTEU) {
        this.truckID = truckID;
        this.maxTEU = maxTEU;
        this.currentPosition = null;
        this.status = TruckStatus.TRUCK_AVAILABLE;
        this.currentTask = null;
        this.remainingTEU = maxTEU;
        this.preTask = null;
    }

    public Truck(int truckIndex, String truckID, int maxTEU, Node currentPosition, TruckStatus status, Task currentTask) {
        this.truckIndex = truckIndex;
        this.truckID = truckID;
        this.maxTEU = maxTEU;
        this.currentPosition = currentPosition;
        this.status = status;
        this.currentTask = currentTask;
        this.preTask = null;
    }


    public boolean isAssigned() {
        return status != TruckStatus.TRUCK_BROKEN && status != TruckStatus.TRUCK_AVAILABLE && currentTask != null;
    }

    // Getters and setters for private members
    public int getTruckIndex() {
        return truckIndex;
    }

    public void setTruckIndex(int truckIndex) {
        this.truckIndex = truckIndex;
    }

    public String getTruckID() {
        return truckID;
    }

    public void setTruckID(String truckID) {
        this.truckID = truckID;
    }

    public int getMaxTEU() {
        return maxTEU;
    }

    public void setMaxTEU(int maxTEU) {
        this.maxTEU = maxTEU;
    }

    public Node getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Node currentPosition) {
        this.currentPosition = currentPosition;
    }

    public TruckStatus getStatus() {
        return status;
    }

    public void setStatus(TruckStatus status) {
        this.status = status;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "truckIndex=" + truckIndex +
                ", truckID='" + truckID + '\'' +
                ", maxTEU=" + maxTEU +
                ", currentPosition=" + currentPosition.getName() +
                ", status=" + status +
                ", currentTask=" + currentTask +
                '}';
    }
}
