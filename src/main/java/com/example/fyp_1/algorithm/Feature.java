package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.Network;
import com.example.fyp_1.DataStructure.Node;
import com.example.fyp_1.DataStructure.Task;
import com.example.fyp_1.DataStructure.WorkQueue;

import java.util.ArrayList;
import java.util.List;

public class Feature {
    private List<List<Double>> buffers; // 缓冲区状态
    private List<List<Integer>> buffer_sup; // 缓冲区供给
    private List<List<Integer>> buffer_demand; // 缓冲区需求
    private List<Long> bfr_tws; // 缓冲区时间检查点
    private List<Integer> work_queue_len; // 工作队列长度
    private List<Integer> queue_priority; // 工作队列优先级
    private Node truck_position; // 卡车位置
    private Network nw; // 网络对象

    // Getters and setters

    public List<List<Double>> getBuffers() {
        return buffers;
    }

    public void setBuffers(List<List<Double>> buffers) {
        this.buffers = buffers;
    }

    public List<List<Integer>> getBuffer_sup() {
        return buffer_sup;
    }

    public void setBuffer_sup(List<List<Integer>> buffer_sup) {
        this.buffer_sup = buffer_sup;
    }

    public List<List<Integer>> getBuffer_demand() {
        return buffer_demand;
    }

    public void setBuffer_demand(List<List<Integer>> buffer_demand) {
        this.buffer_demand = buffer_demand;
    }

    public List<Long> getBfr_tws() {
        return bfr_tws;
    }

    public void setBfr_tws(List<Long> bfr_tws) {
        this.bfr_tws = bfr_tws;
    }

    public List<Integer> getWork_queue_len() {
        return work_queue_len;
    }

    public void setWork_queue_len(List<Integer> work_queue_len) {
        this.work_queue_len = work_queue_len;
    }

    public List<Integer> getQueue_priority() {
        return queue_priority;
    }

    public void setQueue_priority(List<Integer> queue_priority) {
        this.queue_priority = queue_priority;
    }

    public Node getTruck_position() {
        return truck_position;
    }

    public void setTruck_position(Node truck_position) {
        this.truck_position = truck_position;
    }

    public Network getNw() {
        return nw;
    }

    public void setNw(Network nw) {
        this.nw = nw;
    }

    // Constructors

    public Feature(List<Long> bfr_tws, Network nw) {
        this.bfr_tws = bfr_tws;
        this.nw = nw;
        initializeBuffers();
    }

    public Feature(int num_of_check_points, Network nw) {
        generateCheckPoints(num_of_check_points);
        this.nw = nw;
        initializeBuffers();
    }

    public Feature(Feature f) {
        this.bfr_tws = new ArrayList<>(f.bfr_tws);
        this.buffer_sup = new ArrayList<>(f.buffer_sup);
        this.buffer_demand = new ArrayList<>(f.buffer_demand);
        this.buffers = new ArrayList<>(f.buffers);
        this.work_queue_len = new ArrayList<>(f.work_queue_len);
        this.queue_priority = new ArrayList<>(f.queue_priority);
        this.truck_position = f.truck_position;
        this.nw = f.nw;
    }

    // Other methods

    public void clearBuffer() {
        buffers.clear();
        buffer_demand.clear();
        buffer_sup.clear();
    }

    public boolean bfrInfo(int[] minBfr, int[] median, int[] mean) {
        minBfr[0] = -1;
        int minBfri = -1;
        boolean demandExists = false;

        for (int i = 0; i < buffer_sup.size(); i++) {
            if (buffer_demand.get(i).get(0) == 0) continue;
            int bfriSize = buffer_sup.get(i).get(0) - buffer_demand.get(i).get(0);
            if (minBfri == -1 || minBfr[0] > bfriSize) {
                minBfri = i;
                minBfr[0] = bfriSize;
            }
            demandExists = true;
        }

        return demandExists;
    }

    public boolean maxQueueLen(int[] maxLen) {
        if (work_queue_len.isEmpty()) {
            return false;
        }

        maxLen[0] = 0;
        for (int len : work_queue_len) {
            if (len > maxLen[0]) {
                maxLen[0] = len;
            }
        }

        return true;
    }

    public static double compare(Feature ft1, Feature ft2) {
        int ft1BfrSize = ft1.buffer_sup.size();
        int ft2BfrSize = ft2.buffer_sup.size();

        int maxBfrSize = Math.max(ft1BfrSize, ft2BfrSize);
        double rs = 0;
        for (int i = 0; i < maxBfrSize; i++) {
            int ft1I = (i >= ft1BfrSize) ? 0 : ft1.buffer_sup.get(i).get(0) - ft1.buffer_demand.get(i).get(0);
            int ft2I = (i >= ft2BfrSize) ? 0 : ft2.buffer_sup.get(i).get(0) - ft2.buffer_demand.get(i).get(0);
            rs += Math.pow((double) (ft1I - ft2I), 2);
        }
        rs = Math.sqrt(rs);

        return rs;
    }

    public int qcIndex(WorkQueue wq) {
        for (int i = 0; i < nw.getQuayCraneNodes().size(); i++) {
            if (wq.getPow() == nw.getQuayCraneNodes().get(i)) {
                return i;
            }
        }
        return -1;
    }

    public int qcIndex(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getQuayNode() != null) {
                WorkQueue sc = task.getWorkQueue();
                for (int i = 0; i < nw.getQuayCraneNodes().size(); i++) {
                    if (sc == nw.getWorkQueues().get(i)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private void initializeBuffers() {
        int num_of_check_points = bfr_tws.size();
        int num_of_quaycranes = nw.getQuayCraneNodes().size();
        buffers = new ArrayList<>(num_of_check_points);
        buffer_sup = new ArrayList<>(num_of_check_points);
        buffer_demand = new ArrayList<>(num_of_check_points);

        for (int i = 0; i < num_of_check_points; i++) {
            buffers.add(new ArrayList<>(num_of_quaycranes));
            buffer_sup.add(new ArrayList<>(num_of_quaycranes));
            buffer_demand.add(new ArrayList<>(num_of_quaycranes));

            for (int j = 0; j < num_of_quaycranes; j++) {
                buffers.get(i).add(0.0);
                buffer_sup.get(i).add(0);
                buffer_demand.get(i).add(0);
            }
        }
    }

    private void generateCheckPoints(int num_of_check_points) {
        bfr_tws = new ArrayList<>();
        for (int i = 1; i <= num_of_check_points; i++) {
            bfr_tws.add((long) (i * 300));
        }
    }
}
