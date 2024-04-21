package com.example.fyp_1;

import com.example.fyp_1.DataStructure.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

// 负责评估进化算法的适应度
public class EvolutionFitness {
    private Network nw;
    private List<TruckRoute> routes;
    private List<List<AccessPoint>> accPoints;
    private List<WorkQueue> workQueues;
    private long currentTime;

    private double totalTravellingTime;
    private double totalCraneWait;
    private double totalTruckWait;

    public Network getNw() {
        return nw;
    }

    public void setNw(Network nw) {
        this.nw = nw;
    }

    public List<TruckRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<TruckRoute> routes) {
        this.routes = routes;
    }

    public List<List<AccessPoint>> getAccPoints() {
        return accPoints;
    }

    public void setAccPoints(List<List<AccessPoint>> accPoints) {
        this.accPoints = accPoints;
    }

    public List<WorkQueue> getWorkQueues() {
        return workQueues;
    }

    public void setWorkQueues(List<WorkQueue> workQueues) {
        this.workQueues = workQueues;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public double getTotalTravellingTime() {
        return totalTravellingTime;
    }

    public void setTotalTravellingTime(double totalTravellingTime) {
        this.totalTravellingTime = totalTravellingTime;
    }

    public double getTotalCraneWait() {
        return totalCraneWait;
    }

    public double setTotalCraneWait(double totalCraneWait) {
        this.totalCraneWait = totalCraneWait;
        return totalCraneWait;
    }

    public double getTotalTruckWait() {
        return totalTruckWait;
    }

    public double setTotalTruckWait(double totalTruckWait) {
        this.totalTruckWait = totalTruckWait;
        return totalTruckWait;
    }


    public EvolutionFitness(Network network, long time) {
        nw = network;
        workQueues = new ArrayList<>();
        for (WorkQueue queue : nw.getWorkQueues()) {
            WorkQueue copyQueue = new WorkQueue();
            copyQueue.setQueueName(queue.getQueueName());
            copyQueue.setPow(queue.getPow());
            copyQueue.setTasks(new ArrayList<>(queue.getTasks()));
            copyQueue.setTruckBindings(new ArrayList<>(queue.getTruckBindings()));
            copyQueue.setTaskMerging(queue.getTaskMerging());
            copyQueue.setMaxTruck(queue.getMaxTruck());
            workQueues.add(copyQueue);
        }

        currentTime = time;

        accPoints = new ArrayList<>(nw.getNodes().size());
        for (int i = 0; i < nw.getNodes().size(); i++) {
            accPoints.add(new ArrayList<>());
        }

        routes = new ArrayList<>(nw.getTrucks().size());
        for (int i = 0; i < nw.getTrucks().size(); i++) {
            routes.add(new TruckRoute());
        }

        totalTravellingTime = 0;
        totalCraneWait = 0;
        totalTruckWait = 0;
    }

    public void finalize() {
        for (WorkQueue queue : workQueues) {
            queue.setTasks(new ArrayList<>());
            queue.setTruckBindings(new ArrayList<>());
        }
    }

    public static long craneIdleTime(Node n, List<List<AccessPoint>> accPoints, long currentTime) {
        int index = n.getNode_index();
        if (!accPoints.get(index).isEmpty() && accPoints.get(index).get(accPoints.get(index).size() - 1).getTimeFinish() > currentTime) {
            return accPoints.get(index).get(accPoints.get(index).size() - 1).getTimeFinish();
        } else {
            return currentTime;
        }
    }

    public void insertAP(Node n, long arrivalTime, long len, AccessPoint ap, List<List<AccessPoint>> accPoints, long currentTime) {
        long baseIfp = Math.max(arrivalTime, currentTime);
        List<AccessPoint> aps = accPoints.get(n.getNode_index());

        for (int i = 0; i < aps.size(); i++) {
            AccessPoint api = aps.get(i);

            if (i == 0 && baseIfp + len < api.getTimeStart()) {
                ap.setTimeStart(baseIfp);
                ap.setTimeFinish(ap.getTimeStart() + len);
                aps.add(0, ap);
                return;
            }

            baseIfp = Math.max(baseIfp, api.getTimeFinish());

            if (i == aps.size() - 1) {
                ap.setTimeStart(baseIfp);
                ap.setTimeFinish(ap.getTimeStart() + len);
                aps.add(ap);
                return;
            } else {
                if (aps.get(i + 1).getTimeStart() - baseIfp > len) {
                    ap.setTimeStart(baseIfp);
                    ap.setTimeFinish(ap.getTimeStart() + len);
                    aps.add(i + 1, ap);
                    return;
                }
            }
        }

        ap.setTimeStart(baseIfp);
        ap.setTimeFinish(ap.getTimeStart() + len);
        aps.add(ap);
    }

    // 计算访问点信息（根据是源节点还是目标节点）
    public void calcAccessPoint(boolean isSrcNode, Task ta, Truck tr, AccessPoint ap, List<List<AccessPoint>> accPoints, long currentTime) {
        ap.setTask(ta);
        ap.setTruck(tr);

        long arrivalTime, len;
        if (isSrcNode) {
            arrivalTime = ta.getSrcArrival();
            len = ta.getLoadTime();
        } else {
            arrivalTime = ta.getDstArrival();
            len = ta.getUnloadTime();
        }

        Node n = isSrcNode ? ta.getSrcNode() : ta.getDstNode();
        if (n.getType() == NodeType.QUAY_CRANE && ta.getAbove() != null) {
            Task above = ta.getAbove();
            if (above.getSrcNode() == n) {
                long t = above.getSrcAp().getTimeFinish();
                arrivalTime = Math.max(arrivalTime, t);
            } else {
                long t = above.getDstAp().getTimeFinish();
                arrivalTime = Math.max(arrivalTime, t);
            }
        }

        insertAP(n, arrivalTime, len, ap, accPoints, currentTime);
    }

    // 更新合并任务的时间信息
    public void updateMergedTimes(Task ta) {
        Task ti = ta.getMergedTask();
        while (ti != null) {
            ti.setDispatchTime(ta.getDispatchTime());
            if (ti.getSrcAp().getTimeStart() == ti.getSrcAp().getTimeFinish()) {
                ti.setSrcAp(ta.getSrcAp());
            }
            if (ti.getDstAp().getTimeStart() == ti.getDstAp().getTimeFinish()) {
                ti.setDstAp(ta.getDstAp());
            }
            ti = ti.getMergedTask();
        }
    }

    // 获取工作队列的索引
    public int getQueueIndex(String queueName, List<WorkQueue> workQueues) {
        for (int i = 0; i < workQueues.size(); i++) {
            if (workQueues.get(i).getQueueName().equals(queueName)) {
                return i;
            }
        }
        return -1;
    }

    // 将任务放入路线
    public void putTask(Truck tr, Task ta) {
        long dispatchTime = currentTime;
        Node lastNode = null;
        if (!routes.get(tr.getTruckIndex()).getTasks().isEmpty()) {
            lastNode = routes.get(tr.getTruckIndex()).getTasks().get(routes.get(tr.getTruckIndex()).getTasks().size() - 1).getDstNode();
            dispatchTime = craneIdleTime(lastNode, accPoints, currentTime);
        }

        for (Task ti = ta, pt = null; ti != null; pt = ti, ti = ti.getMergedTask()) {
            if (pt != null && pt.getSrcNode() == ti.getSrcNode()) continue;

            Node srci = ti.getSrcNode();
            if (lastNode != null) {
                ti.setSrcArrival(nw.getTravellingTimePassingNodes(lastNode, srci));
            } else {
                ti.setSrcArrival(nw.getTravellingTimePassingNodes(tr.getCurrentPosition(), srci));
            }
            calcAccessPoint(true, ti, tr, ti.getSrcAp(), accPoints, currentTime);
            lastNode = srci;
        }

        for (Task ti = ta, pt = null; ti != null; pt = ti, ti = ti.getMergedTask()) {
            if (pt != null && pt.getDstNode() == ti.getDstNode()) continue;

            Node dsti = ti.getDstNode();
            ti.setDstArrival(nw.getTravellingTimePassingNodes(lastNode, dsti));
            calcAccessPoint(false, ti, tr, ti.getDstAp(), accPoints, currentTime);
            lastNode = dsti;
        }

        updateMergedTimes(ta);
        routes.get(tr.getTruckIndex()).getTasks().add(ta);
    }

    // 移除指定节点的访问点
    public boolean removeAccessPoint(AccessPoint ap, Node n) {
        List<AccessPoint> accPoint = accPoints.get(n.getNode_index());
        if (accPoint == null) return false;

        for (int i = 0; i < accPoint.size(); i++) {
            if (accPoint.get(i).getTask() == ap.getTask()) {
                accPoint.remove(i);
                return true;
            }
        }
        return false;
    }

    // 从路线中移除任务
    public void removeTask(Truck tr, int inroute_i) {
        List<Task> tasks = routes.get(tr.getTruckIndex()).getTasks();

        // 从指定索引处开始迭代任务
        for (int i = inroute_i; i < tasks.size(); i++) {
            Task taski = tasks.get(i);
            Node srci = taski.getSrcNode();

            // 重置任务时间
            taski.setDispatchTime(-1);
            taski.setSrcArrival(-1);
            taski.setDstArrival(-1);

            // 移除访问点
            removeAccessPoint(taski.getSrcAp(), srci);
            removeAccessPoint(taski.getDstAp(), taski.getDstNode());
        }

        System.out.println("Removed task " + tasks.get(inroute_i).getContainerID() + " from " + tr.getTruckIndex());

        // 从路线中移除任务
        tasks.remove(inroute_i);
    }

    // 根据任务移除任务
    public void removeTask(Truck tr, Task ta) {
        List<Task> tasks = routes.get(tr.getTruckIndex()).getTasks();

        // 在路线中查找任务的索引
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i) == ta) {
                index = i;
                break;
            }
        }

        // 如果找到任务，则从路线中移除
        if (index != -1) {
            removeTask(tr, index);
        }
    }

    // 获取工作队列的任务数量
    public int workQueueSize() {
        int size = 0;
        for (int i = 0; i < workQueues.size(); i++) {
            size += workQueues.get(i).getTasks().size();
        }
        return size;
    }

    // 评估起重机的情况
    public void evaluateCrane(int craneIndex, long[] busy, long[] idle, long[] wait, long[] makespan, long[] totalMakespan, int[] apCount) {
        List<AccessPoint> aps = accPoints.get(craneIndex);
        for (int i = 0; i < aps.size(); i++) {
            AccessPoint ap = aps.get(i);
            if (ap.getTask().getSrcAp() == ap) {
                wait[0] += ap.getTimeStart() - ap.getTask().getSrcArrival();
            } else {
                wait[0] += ap.getTimeStart() - ap.getTask().getSrcArrival();
            }
            busy[0] += ap.getTimeFinish() - ap.getTimeStart();
            if (i == 0) {
                if (ap.getTimeStart() > this.currentTime) {
                    idle[0] += ap.getTimeStart() - this.currentTime;
                } else {
                    idle[0] += 0;
                }
            } else {
                idle[0] += ap.getTimeStart() - aps.get(i - 1).getTimeFinish();
            }

            if (i == aps.size() - 1) {
                totalMakespan[0] += ap.getTimeFinish();
                if (makespan[0] < ap.getTimeFinish()) {
                    makespan[0] = ap.getTimeFinish();
                }
            }
        }
        apCount[0] += aps.size();
    }

    // 计算时间差
    private long calculateTimeDifference(long start, long end) {
        return end - start;
    }

    private long calculateTimeDifference(long start, LocalDateTime end) {
        return Duration.between(LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC), end).getSeconds();
    }

    // 评估所有起重机的情况
    public void evaluateAllQuayCranes(
            long[] busy, long[] idle, long[] wait, long[] makespan, long[] totalMakespan, int[] apCount) {

        for (Node node : nw.getNodes()) {
            if (node.getType() == NodeType.QUAY_CRANE) {
                evaluateCrane(node.getNode_index(), busy, idle, wait, makespan, totalMakespan, apCount);
            }
        }
    }

    // 评估路线
    public void evaluateRoute(int routeIndex, long[] travTime, int[] taskCount) {
        Truck tr = routes.get(routeIndex).getTr();
        Node prevLocation = tr.getCurrentPosition();
        List<Task> r = routes.get(routeIndex).getTasks();

        for (int i = 0; i < r.size(); i++) {
            Task ta = r.get(i);
            if (ta == tr.getCurrentTask()) {
                prevLocation = ta.getDstNode();
                continue;
            }

            if (ta.getMergedTask() != null) {
                taskCount[0] += 2;
            } else {
                taskCount[0] += 1;
            }

            travTime[0] += nw.getTravellingTimePassingNodes(prevLocation, ta.getSrcNode());

            // 如果任务合并，则计算到达合并任务源节点和目的节点的时间
            if (ta.getMergedTask() != null) {
                travTime[0] += nw.getTravellingTimePassingNodes(ta.getSrcNode(), ta.getMergedTask().getSrcNode());
                travTime[0] += nw.getTravellingTimePassingNodes(ta.getMergedTask().getSrcNode(), ta.getDstNode());
            } else {
                travTime[0] += nw.getTravellingTimePassingNodes(ta.getSrcNode(), ta.getDstNode());
            }

            prevLocation = ta.getDstNode();
        }
    }

    // 评估所有路线
    public void evaluateAllRoute(long[] travellingTime, int[] taskCount) {
        for (int i = 0; i < routes.size(); i++) {
            evaluateRoute(i, travellingTime, taskCount);
        }
    }

    public int numUnfinished() {
        int size = 0;
        for (int i = 0; i < workQueues.size(); i++) {
            size += workQueues.get(i).getTasks().size();
        }

        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getTr().getCurrentTask() != null) {
                size += 1;
            }
        }
        return size;
    }
}
