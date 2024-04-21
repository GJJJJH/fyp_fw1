package com.example.fyp_1.DataStructure;

import java.util.*;

public class Network {
    private List<Node> nodes;
    private List<Node> truckAreaNodes;
    private List<Node> yardCraneNodes;
    private List<Node> quayCraneNodes;
    private List<Task> tasks;
    private List<NodesTime> nodesTimes;
    private List<Truck> trucks;
    private List<WorkQueue> workQueues;



    public Network() {
        this.nodes = new ArrayList<>();
        this.truckAreaNodes = new ArrayList<>();
        this.yardCraneNodes = new ArrayList<>();
        this.quayCraneNodes = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.nodesTimes = new ArrayList<>();
        this.trucks = new ArrayList<>();
        this.workQueues = new ArrayList<>();
    }

    public Network(Network network) {
        this.nodes = new ArrayList<>(network.getNodes());
        this.truckAreaNodes = new ArrayList<>(network.getTruckAreaNodes());
        this.yardCraneNodes = new ArrayList<>(network.getYardCraneNodes());
        this.quayCraneNodes = new ArrayList<>(network.getQuayCraneNodes());
        this.tasks = new ArrayList<>(network.getTasks());
        this.nodesTimes = new ArrayList<>(network.getNodesTimes());
        this.trucks = new ArrayList<>(network.getTrucks());
        this.workQueues = new ArrayList<>(network.getWorkQueues());
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void setTruckAreaNodes(List<Node> truckAreaNodes) {
        this.truckAreaNodes = truckAreaNodes;
    }

    public void setYardCraneNodes(List<Node> yardCraneNodes) {
        this.yardCraneNodes = yardCraneNodes;
    }

    public void setQuayCraneNodes(List<Node> quayCraneNodes) {
        this.quayCraneNodes = quayCraneNodes;
    }

    public void setTasks(List<Task> tasks) {
        tasks = tasks;
    }

    public void setNodesTimes(List<NodesTime> nodesTimes) {
        this.nodesTimes = nodesTimes;
    }

    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }

    public void setWorkQueues(List<WorkQueue> workQueues) {
        this.workQueues = workQueues;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Node> getTruckAreaNodes() {
        return truckAreaNodes;
    }

    public List<Node> getYardCraneNodes() {
        return yardCraneNodes;
    }

    public List<Node> getQuayCraneNodes() {
        return quayCraneNodes;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<NodesTime> getNodesTimes() {
        return nodesTimes;
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public List<WorkQueue> getWorkQueues() {
        return workQueues;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Nodes:\n");
        for (Node node : nodes) {
            sb.append(node).append("\n");
        }

        sb.append("\nTruck Area Nodes:\n");
        for (Node node : truckAreaNodes) {
            sb.append(node).append("\n");
        }

        sb.append("\nYard Crane Nodes:\n");
        for (Node node : yardCraneNodes) {
            sb.append(node).append("\n");
        }

        sb.append("\nQuay Crane Nodes:\n");
        for (Node node : quayCraneNodes) {
            sb.append(node).append("\n");
        }

        sb.append("\nTasks:\n");
        for (Task task : tasks) {
            sb.append(task).append("\n");
        }

        sb.append("\nNodes Times:\n");
        for (NodesTime nodesTime : nodesTimes) {
            sb.append(nodesTime).append("\n");
        }

        sb.append("\nTrucks:\n");
        for (Truck truck : trucks) {
            sb.append(truck).append("\n");
        }

        sb.append("\nWork Queues:\n");
        for (WorkQueue workQueue : workQueues) {
            sb.append(workQueue).append("\n");
        }

        return sb.toString();
    }

    // 释放资源
    public void releaseResources() {
        // 释放所有卡车资源
        for (Truck truck : trucks) {
            truck = null;
        }
        // 释放所有节点资源
        for (Node node : nodes) {
            node = null;
        }
        // 释放所有卡车区节点资源
        for (Node node : truckAreaNodes) {
            node = null;
        }
        // 释放所有堆场起重机节点资源
        for (Node node : yardCraneNodes) {
            node = null;
        }
        // 释放所有码头起重机节点资源
        for (Node node : quayCraneNodes) {
            node = null;
        }
        // 释放所有任务资源
        for (Task task : tasks) {
            task = null;
        }
        // 释放所有节点间时间资源
        for (NodesTime nodesTime : nodesTimes) {
            nodesTime = null;
        }

        // 清空工作队列
        clearWorkQueues();

        // 强制进行垃圾回收
        System.gc();
    }

    // 清空工作队列
    public void clearWorkQueues() {
        for (int i = 0; i < workQueues.size(); i++) {
            WorkQueue tqi = workQueues.get(i);
            // 清空每个工作队列中的任务
            for (int j = 0; j < tqi.getTasks().size(); j++) {
                Task task = tqi.getTasks().get(j);
            }
            // 移除当前工作队列
            workQueues.remove(i);
            i--;
        }

        // 清空工作队列列表
        workQueues.clear();
    }

    // 将任务转换为字符串形式的方法
    public String taskToString(Task t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getContainerID()).append(" ").append(t.getWIRefNo());
        String res = sb.toString();
        res += " <" + t.getSrcNode() + "->" + t.getDstNode() + ">";
        if (Objects.nonNull(t.getMergedTask())) {
            res += "(m: " + taskToString(t.getMergedTask()) + ")";
        }
        return res;
    }

    // 查找指定ID的卡车
    public Truck findTruck(String id) {
        for (Truck truck : trucks) {
            if (id.equals(truck.getTruckID())) return truck;
        }
        return null;
    }

    // 查找指定任务ID的任务
    public Task findTask(String taskID) {
        for (Task task : tasks) {
            if (taskID.equals(task.getContainerID())) return task;
        }
        return null;
    }

    // 根据类型查找节点
    public Node findNodeByType(String type) {
        for (Node node : nodes) {
            if (node.getType().equals(type)) return node;
        }
        return null;
    }

    public Node findNodeByName(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) return node;
        }
        return null;
    }

    public WorkQueue findWorkQueueByName(String name){
        for (WorkQueue workQueue: workQueues){
            if (workQueue.getQueueName().equals(name)) return workQueue;
        }
        return null;
    }

    public List<Task> findTaskByNode(String src) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getSrcNode().getName().equals(src)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }


    // 估算任务到达指定节点的时间
    public long estimatePowArrival(Task t) {
        // 若任务没有指定码头节点或调度时间，则返回-1
        if (t.getQuayNode() == null || t.getDispatchTime() == -1) {
            return -1;
        }
        // 若任务的起始节点即为码头节点，则返回任务调度时间加上到达码头节点所需的时间
        if (t.getSrcNode().equals(t.getQuayNode())) {
            return t.getDispatchTime() + getTravellingTimePassingNodes(t.getDispatchLocation(), t.getSrcNode());
        } else {
            // 否则返回任务调度时间加上到达码头节点和目的地节点所需的时间之和
            return t.getDispatchTime() + getTravellingTimePassingNodes(t.getDispatchLocation(), t.getSrcNode())
                    + getTravellingTimePassingNodes(t.getSrcNode(), t.getDstNode());
        }
    }

    // 获取通过指定节点到达目的节点所需的时间
    public long getTravellingTimePassingNodes(Node currentPosition, Node srcNode) {
        // 遍历节点间时间列表，查找到达目的节点的时间
        for (NodesTime nodesTime : nodesTimes) {
            if ((nodesTime.getCraneId1().equals(currentPosition) && nodesTime.getCraneId2().equals(srcNode))) {
                return nodesTime.getDuration();
            }
        }
        return 0;
    }

    // 将任务之间建立前后关系，更新任务顺序
    public void linkTaskPrecedence(boolean updateSeq) {
        for (int i = 0; i < workQueues.size(); i++) {
            List<Task> queueI = workQueues.get(i).getTasks();
            for (int j = 0; j < queueI.size(); j++) {
                Task task1 = queueI.get(j);
                // 设置当前任务的上一个任务
                if (j == 0) {
                    task1.setAbove(null);
                } else {
                    task1.setAbove(queueI.get(j - 1));
                }
                // 设置当前任务的下一个任务
                if (j == queueI.size() - 1) {
                    task1.setBelow(null);
                } else {
                    task1.setBelow(queueI.get(j + 1));
                }
                // 更新任务顺序
                if (updateSeq) {
                    task1.setSeq(j);
                    if (task1.getMergedTask() != null) {
                        task1.getMergedTask().setSeq(j);
                    }
                }
            }
        }
    }

    // 合并任务
    public void mergeTasks() {
        for (int i = 0; i < workQueues.size(); i++) {
            List<Task> queueI = workQueues.get(i).getTasks();
            for (int j = 0; j < queueI.size(); j++) {
                Task tj = queueI.get(j);
                // 若当前任务不可合并，则继续下一轮循环
                if (!tj.twinWiMergeable()) continue;
                for (int k = j + 1; k < queueI.size(); k++) {
                    Task tk = queueI.get(k);
                    // 若当前任务不可合并，则继续下一轮循环
                    if (!tk.twinWiMergeable()) continue;
                    // 如果任务tk与tj的孪生任务，则将tk合并到tj中
                    if (tk.getWIRefNo() == tj.getTwinWIRefNo()) {
                        tj.setMergedTask(tk);
                        queueI.remove(k);
                        break;
                    }
                }
            }
        }
        // 更新任务顺序
        linkTaskPrecedence(true);
    }

    // 合并任务到目标工作队列中
    public void mergeToQueue(List<WorkQueue> dstQueues, List<WorkQueue> srcQueues) {
        // 遍历源工作队列列表
        for (int srcIndex = 0; srcIndex < srcQueues.size(); srcIndex++) {
            WorkQueue srcQueue = srcQueues.get(srcIndex);
            // 遍历目标工作队列列表
            for (int dstIndex = 0; dstIndex < dstQueues.size(); dstIndex++) {
                WorkQueue dstQueue = dstQueues.get(dstIndex);
                // 如果目标工作队列与源工作队列名称相同，则合并工作队列
                if (dstQueue.getQueueName().equals(srcQueue.getQueueName())) {
                    System.out.println(dstQueue.getQueueName() + " will be merged");
                    // 将源工作队列中的任务添加到目标工作队列中
                    for (int i = 0; i < srcQueue.getTasks().size(); i++) {
                        Task task = srcQueue.getTasks().get(i);
                        task.setSeq(dstQueue.getTasks().size());
                        dstQueue.getTasks().add(task);
                    }
                    // 清空源工作队列中的任务
                    srcQueue.getTasks().clear();
                    // 将源工作队列设为null
                    srcQueues.set(srcIndex, null);
                    break;
                }
            }
        }

        // 将剩余的源工作队列添加到目标工作队列列表中
        for (int srcIndex = 0; srcIndex < srcQueues.size(); srcIndex++) {
            WorkQueue srcQueue = srcQueues.get(srcIndex);
            if (srcQueue != null) {
                dstQueues.add(srcQueue);
                srcQueue = null;
            }
        }
        // 合并任务
        this.mergeTasks();
    }

    // 更新工作队列
    public void updateWorkQueues(List<WorkQueue> newQueues) {
        // 清空工作队列
        clearWorkQueues();
        // 更新工作队列列表
        mergeToQueue(workQueues, newQueues);
    }

    // 移除所有卡车
    public void removeTrucks() {
        trucks.clear();
    }

    // 添加指定数量的卡车
    public boolean addTrucks(int maxTEU, int numOfTrucks) {
        // 循环添加指定数量的虚拟卡车
        for (int i = 0; i < numOfTrucks; i++) {
            Truck truck = new Truck("virtual_" + i, maxTEU);
            truck.setMaxTEU(maxTEU);

            // 若存在卡车区节点，则将当前位置设置为第一个卡车区节点，否则返回false
            if (!truckAreaNodes.isEmpty()) {
                truck.setCurrentPosition(truckAreaNodes.get(0));
            } else {
                return false;
            }

            truck.setCurrentTask(null);
            truck.setStatus(TruckStatus.TRUCK_AVAILABLE);
            truck.setTruckIndex(trucks.size());
            trucks.add(truck);

            // 将卡车绑定到所有工作队列中
            for (WorkQueue workQueue : workQueues) {
                workQueue.getTruckBindings().add(truck);
            }
        }
        return true;
    }

    // 添加指定ID和位置的卡车
    public boolean addTruck(int maxTEU, String position, String id, List<String> pows) {
        Truck truck = new Truck(id, maxTEU);

        // 查找指定位置的节点
        Node currentPosition = findNodeByName(position);
        // 若位置节点不存在，则将当前位置设置为第一个卡车区节点
        if (currentPosition == null) {
            if (!truckAreaNodes.isEmpty()) {
                currentPosition = truckAreaNodes.get(0);
            } else {
                return false;
            }
        }

        truck.setCurrentPosition(currentPosition);
        truck.setStatus(TruckStatus.TRUCK_AVAILABLE);
        truck.setTruckIndex(trucks.size());

        trucks.add(truck);

        // 将卡车绑定到对应的工作队列中
        for (String pow : pows) {
            Node powNode = findNodeByName(pow);
            if (powNode != null && powNode.getType() == NodeType.QUAY_CRANE) {
                for (WorkQueue workQueue : workQueues) {
                    if (powNode == workQueue.getPow()) {
                        workQueue.getTruckBindings().add(truck);
                    }
                }
            }
        }

        return true;
    }

    // 添加虚拟卡车，并分配任务
    public boolean addTrucks(int maxTEU, List<List<Task>> taskListArray) {
        // 遍历任务列表数组
        for (List<Task> taskList : taskListArray) {
            // 若任务列表不为空
            if (taskList != null) {
                // 遍历任务列表中的任务
                for (Task task : taskList) {
                    Truck truck = new Truck("virtual_" + trucks.size(), maxTEU);
                    truck.setCurrentPosition(null);
                    truck.setStatus(TruckStatus.TRUCK_XSHIPPING);
                    truck.setTruckIndex(trucks.size());

                    // 将任务转换为卡车的当前任务
                    Task convertedTask = task.convertTask(this);
                    truck.setCurrentTask(convertedTask);

                    trucks.add(truck);
                }
            }
        }

        return true;
    }

    // 更新卡车列表
    public void updateTrucks(List<Truck> updatedTrucks) {
        // 移除所有卡车
        removeTrucks();
        // 添加更新后的卡车列表
        trucks.addAll(updatedTrucks);
    }

    public Task findTaskFromQueue(String containerId, List<WorkQueue> workQueues) {
        for (WorkQueue workQueue : workQueues) {
            Task task = workQueue.findTaskByConID(containerId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    public static WorkQueue findQueueForTask(Network nw, Task task) {
        for (WorkQueue queue : nw.getWorkQueues()) {
            if (queue.getQueueName().equals(task.getSrcNode().getName())) {
                return queue;
            }
        }
        return null;
    }

    public void updateNodesWithAverageLoadUnloadTime() {
        Map<Node, List> averageLoadUnloadTimeMap = calculateAverageLoadUnloadTime();

        for (Node node : nodes) {
            if (averageLoadUnloadTimeMap.containsKey(node)) {
                List nodeInfo = averageLoadUnloadTimeMap.get(node);
                long averageLoadTime = (long) nodeInfo.get(0);
                long averageUnloadTime = (long) nodeInfo.get(1);
                node.setAverageLoadTime(averageLoadTime);
                node.setAverageUnloadTime(averageUnloadTime);
            }
        }

        for (Task task : tasks) {
            Node srcNode = task.getSrcNode();
            if (srcNode != null && averageLoadUnloadTimeMap.containsKey(srcNode)) {
                List nodeInfo = averageLoadUnloadTimeMap.get(srcNode);
                long averageLoadTime = (long) nodeInfo.get(0);
                task.setLoadTime(averageLoadTime);
            }

            Node dstNode = task.getDstNode();
            if (dstNode != null && averageLoadUnloadTimeMap.containsKey(dstNode)) {
                List nodeInfo = averageLoadUnloadTimeMap.get(dstNode);
                long averageUnloadTime = (long) nodeInfo.get(1);
                task.setUnloadTime(averageUnloadTime);
            }
        }
    }


    public Map<Node, List> calculateAverageLoadUnloadTime() {
        Map<Node, Long> loadTimeMap = new HashMap<>();
        Map<Node, Long> unloadTimeMap = new HashMap<>();
        Map<Node, Integer> loadCountMap = new HashMap<>();
        Map<Node, Integer> unloadCountMap = new HashMap<>();

        for (Task task : tasks) {
            Node sourceNode = task.getSrcNode();
            Node destinationNode = task.getDstNode();

            long loadTime = task.getLoadTime();
            loadTimeMap.put(sourceNode, loadTimeMap.getOrDefault(sourceNode, 0L) + loadTime);
            loadCountMap.put(sourceNode, loadCountMap.getOrDefault(sourceNode, 0) + 1);

            long unloadTime = task.getUnloadTime();
            unloadTimeMap.put(destinationNode, unloadTimeMap.getOrDefault(destinationNode, 0L) + unloadTime);
            unloadCountMap.put(destinationNode, unloadCountMap.getOrDefault(destinationNode, 0) + 1);
        }

        Map<Node, List> averageLoadUnloadTimeMap = new HashMap<>();
        for (Node node : loadTimeMap.keySet()) {
            long totalLoadTime = loadTimeMap.getOrDefault(node, 0L);
            int loadCount = loadCountMap.getOrDefault(node, 0);
            long averageLoadTime = loadCount > 0 ? totalLoadTime / loadCount : 100;

            long totalUnloadTime = unloadTimeMap.getOrDefault(node, 0L);
            int unloadCount = unloadCountMap.getOrDefault(node, 0);
            long averageUnloadTime = unloadCount > 0 ? totalUnloadTime / unloadCount : 100;

            List<Long> listTime = new ArrayList<>();
            listTime.add(averageLoadTime);
            listTime.add(averageUnloadTime);
            averageLoadUnloadTimeMap.put(node, listTime);
        }

        for (Node node : unloadTimeMap.keySet()) {
            long totalLoadTime = loadTimeMap.getOrDefault(node, 0L);
            int loadCount = loadCountMap.getOrDefault(node, 0);
            long averageLoadTime = loadCount > 0 ? totalLoadTime / loadCount : 100;

            long totalUnloadTime = unloadTimeMap.getOrDefault(node, 0L);
            int unloadCount = unloadCountMap.getOrDefault(node, 0);
            long averageUnloadTime = unloadCount > 0 ? totalUnloadTime / unloadCount : 100;

            List<Long> listTime = new ArrayList<>();
            listTime.add(averageLoadTime);
            listTime.add(averageUnloadTime);
            averageLoadUnloadTimeMap.put(node, listTime);
        }

        return averageLoadUnloadTimeMap;
    }


}








