package com.example.fyp_1.DataStructure;

public class NodesTime {


    private Node craneId1;
    private Node craneId2;
    private int duration;

    public NodesTime(Node craneId1, Node craneId2, int duration) {
        this.craneId1 = craneId1;
        this.craneId2 = craneId2;
        this.duration = duration;
    }
    public void setCraneId1(Node craneId1) {
        this.craneId1 = craneId1;
    }

    public void setCraneId2(Node craneId2) {
        this.craneId2 = craneId2;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public Node getCraneId1() {
        return craneId1;
    }

    public Node getCraneId2() {
        return craneId2;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "NodesTime{" +
                "craneId1=" + craneId1.getName() +
                ", craneId2=" + craneId2.getName() +
                ", duration=" + duration +
                '}';
    }

}
