package com.example.fyp_1.DataStructure;

public class Task {
    private String containerID;
    private Node srcNode;
    private Node dstNode;
    private Node quayNode;
    private long loadTime;
    private long unloadTime;
    private int TEUs;
    private long seq;
    private String workQueueName;
    private int subqueueID;
    private Task above;
    private Task below;
    private Task mergedTask;
    private String srcNodeIdStr;
    private String dstNodeIdStr;
    private String truckId;
    private long dispatchTime;
    private Node dispatchLocation;
    private String dispatchLocationStr;
    private long srcArrival;
    private AccessPoint srcAp;
    private long dstArrival;
    private AccessPoint dstAp;
    private int WIRefNo;
    private int TwinWIRefNo;
    private WorkQueue workQueue;
    int mark;

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(Node srcNode) {
        this.srcNode = srcNode;
    }

    public Node getDstNode() {
        return dstNode;
    }

    public void setDstNode(Node dstNode) {
        this.dstNode = dstNode;
    }

    public Node getQuayNode() {
        return quayNode;
    }

    public void setQuayNode(Node quayNode) {
        this.quayNode = quayNode;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }

    public long getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(long unloadTime) {
        this.unloadTime = unloadTime;
    }

    public int getTEUs() {
        return TEUs;
    }

    public void setTEUs(int TEUs) {
        this.TEUs = TEUs;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getWorkQueueName() {
        return workQueueName;
    }

    public void setWorkQueueName(String workQueueName) {
        this.workQueueName = workQueueName;
    }

    public int getSubqueueID() {
        return subqueueID;
    }

    public void setSubqueueID(int subqueueID) {
        this.subqueueID = subqueueID;
    }

    public Task getAbove() {
        return above;
    }

    public void setAbove(Task above) {
        this.above = above;
    }

    public Task getBelow() {
        return below;
    }

    public void setBelow(Task below) {
        this.below = below;
    }

    public Task getMergedTask() {
        return mergedTask;
    }

    public void setMergedTask(Task mergedTask) {
        this.mergedTask = mergedTask;
    }

    public String getSrcNodeIdStr() {
        return srcNodeIdStr;
    }

    public void setSrcNodeIdStr(String srcNodeIdStr) {
        this.srcNodeIdStr = srcNodeIdStr;
    }

    public String getDstNodeIdStr() {
        return dstNodeIdStr;
    }

    public void setDstNodeIdStr(String dstNodeIdStr) {
        this.dstNodeIdStr = dstNodeIdStr;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public long getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(long dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public Node getDispatchLocation() {
        return dispatchLocation;
    }

    public void setDispatchLocation(Node dispatchLocation) {
        this.dispatchLocation = dispatchLocation;
    }

    public String getDispatchLocationStr() {
        return dispatchLocationStr;
    }

    public void setDispatchLocationStr(String dispatchLocationStr) {
        this.dispatchLocationStr = dispatchLocationStr;
    }

    public long getSrcArrival() {
        return srcArrival;
    }

    public void setSrcArrival(long srcArrival) {
        this.srcArrival = srcArrival;
    }

    public AccessPoint getSrcAp() {
        return srcAp;
    }

    public void setSrcAp(AccessPoint srcAp) {
        this.srcAp = srcAp;
    }

    public long getDstArrival() {
        return dstArrival;
    }

    public void setDstArrival(long dstArrival) {
        this.dstArrival = dstArrival;
    }

    public AccessPoint getDstAp() {
        return dstAp;
    }

    public void setDstAp(AccessPoint dstAp) {
        this.dstAp = dstAp;
    }

    public int getWIRefNo() {
        return WIRefNo;
    }

    public void setWIRefNo(int WIRefNo) {
        this.WIRefNo = WIRefNo;
    }

    public int getTwinWIRefNo() {
        return TwinWIRefNo;
    }

    public void setTwinWIRefNo(int twinWIRefNo) {
        TwinWIRefNo = twinWIRefNo;
    }

    public void setWorkQueue(WorkQueue workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public String toString() {
        String srcName = (srcNode != null) ? srcNode.getName() : "null";
        String dstName = (dstNode != null) ? dstNode.getName() : "null";

        return "Task{" +
                "containerID='" + containerID + '\'' +
                ", srcNode=" + srcName +
                ", dstNode=" + dstName +
                ", loadTime=" + loadTime +
                ", unloadTime=" + unloadTime +
                ", TEUs=" + TEUs +
                ", workQueueName='" + workQueueName + '\'' +
                ", subqueueID=" + subqueueID +
                ", mark=" + mark +
                '}';
    }



    // Task constructor
    public Task() {
        this.above = null;
        this.below = null;
        this.mergedTask = null;
        this.dispatchLocation = null;
        this.dispatchTime = -1;
        this.srcArrival = -1;
        this.dstArrival = -1;
        this.srcAp = new AccessPoint(-1, -1, this, null);
        this.dstAp = new AccessPoint(-1, -1, this, null);
        this.dstNode = null;
        this.srcNode = null;
        this.mark = 0;
    }

    // Method to check task status
    public int status() {
        int status = 7; // Initial value
        // Check and decrement based on conditions
        if (dstAp.getTimeFinish() == -1) status--;
        if (dstAp.getTimeStart() == -1) status--;
        if (dstArrival == -1) status--;
        if (srcAp.getTimeFinish() == -1) status--;
        if (srcAp.getTimeStart() == -1) status--;
        if (srcArrival == -1) status--;
        if (dispatchTime == -1) status--;
        return status;
    }

    // Method to create a copy of the task
    public Task copyTask() {
        Task copy = new Task();
        copy.containerID = this.containerID;
        copy.srcNode = this.srcNode;
        copy.dstNode = this.dstNode;
        copy.loadTime = this.loadTime;
        copy.unloadTime = this.unloadTime;
        copy.TEUs = this.TEUs;

        copy.above = this.above;
        copy.below = this.below;
        copy.mergedTask = this.mergedTask;

        copy.srcNodeIdStr = this.srcNodeIdStr;
        copy.dstNodeIdStr = this.dstNodeIdStr;
        copy.truckId = this.truckId;

        copy.dispatchTime = this.dispatchTime;
        copy.dispatchLocation = this.dispatchLocation;
        copy.dispatchLocationStr = this.dispatchLocationStr;

        copy.srcArrival = this.srcArrival;
        copy.srcAp = new AccessPoint(this.srcAp.getTimeStart(), this.srcAp.getTimeFinish(), copy, null);

        copy.dstArrival = this.dstArrival;
        copy.dstAp = new AccessPoint(this.dstAp.getTimeStart(), this.dstAp.getTimeFinish(), copy, null);

        return copy;
    }

    // Method to convert task from TaskFeature and return a new object
    public Task convertTask(Network nw) {
        Task nt = copyTask();
        nt.srcNode = nw.findNodeByName(srcNodeIdStr);
        nt.dstNode = nw.findNodeByName(dstNodeIdStr);

        if (nt.srcNode == null || nt.dstNode == null) {
            return null;
        }

        return nt;
    }

    public boolean twinWiMergeable() {
        return smallCtnMergeable() && (TwinWIRefNo != 0);
    }

    public boolean smallCtnMergeable() {
        return (TEUs == 1) && (mergedTask == null);
    }

    public WorkQueue getWorkQueue() {
        if (workQueue == null) {
            workQueue = new WorkQueue();
        }
        return workQueue;
    }

    public void addToWorkQueue() {
        WorkQueue queue = getWorkQueue();
        queue.getTasks().add(this);
    }

}
