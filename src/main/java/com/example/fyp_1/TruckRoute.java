package com.example.fyp_1;

import com.example.fyp_1.DataStructure.Task;
import com.example.fyp_1.DataStructure.Truck;

import java.util.ArrayList;
import java.util.List;

public class TruckRoute {
    private Truck tr;
    private List<Task> tasks;
    private long dhTime;
    private long hlTime;

    public Truck getTr() {
        return tr;
    }

    public void setTr(Truck tr) {
        this.tr = tr;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setDhTime(long dhTime) {
        this.dhTime = dhTime;
    }

    public void setHlTime(long hlTime) {
        this.hlTime = hlTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    private long waitTime;

    public TruckRoute() {
        tasks = new ArrayList<>();
        dhTime = 0;
        hlTime = 0;
        waitTime = 0;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public long getDhTime() {
        return dhTime;
    }

    public long getHlTime() {
        return hlTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void removeTasksWithoutMark() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task ti = tasks.get(i);
            if (ti == tr.getCurrentTask()) {
                continue;
            }
            if (ti.getMark() !=0) {
                tasks.remove(i);
            }
        }
    }
}
