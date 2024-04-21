package com.example.fyp_1;

import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.Emulator.Emulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkReader {
    private Network network;
    private String filePath;

    private ParseDatas parseDatas;

    public NetworkReader(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        this.filePath = filePath;
        this.network = new Network();
    }

    public Network parseData() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            String[] topLine = new String[0];
            int section = 0;
            parseDatas = new ParseDatas(network);
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    section++;
                    continue;
                }

                switch (section) {
                    case 0:
                        parseDatas.parseNode(line);
                        break;
                    case 1:
                        parseDatas.parseTask(line);
                        break;
                    case 2:
                        String[] nodesTimesLine = line.split("\\s+");
                        if (nodesTimesLine[0] == ""){
                            topLine = nodesTimesLine;
                    }
                        parseDatas.parseNodesTimes(nodesTimesLine, topLine);
                        break;
                    case 3:
                        parseDatas.parseTruck(line);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return network;
    }

    public List<Task> parseTasks() {
        return parseData().getTasks();
    }

    public List<PortEvent> emuEvents(){
        List<PortEvent> emuEvents = parseDatas.getEmuEvents();
        return emuEvents;
    }
}

class ParseDatas {
    private Network network;
    private List<Task> tasks;
    private List<PortEvent> emuEvents;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<PortEvent> getEmuEvents() {
        return emuEvents;
    }

    public void setEmuEvents(List<PortEvent> emuEvents) {
        this.emuEvents = emuEvents;
    }
    public ParseDatas(Network network) {
        this.network = network;
        this.tasks = new ArrayList<>();
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void parseNode(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length <= 3) {
            String nodei = parts[0];
            String nodeId = parts[1];
            String nodeTypeStr = parts[2];

            NodeType nodeType = null;
            switch (nodeTypeStr) {
                case "truck_Area":
                    nodeType = NodeType.TRUCK_AREA;
                    break;
                case "yard_crane":
                    nodeType = NodeType.YARD_CRANE;
                    break;
                case "quay_crane":
                    nodeType = NodeType.QUAY_CRANE;
                    break;
            }


            Node node = new Node(Integer.parseInt(nodei)-1, nodeId, nodeType);
            network.getNodes().add(node);

            switch (nodeType) {
                case TRUCK_AREA:
                    network.getTruckAreaNodes().add(node);
                    break;
                case YARD_CRANE:
                    network.getYardCraneNodes().add(node);
                    break;
                case QUAY_CRANE:
                    network.getQuayCraneNodes().add(node);
                    break;
            }
        }
    }

    public void parseTask(String line) {
        emuEvents = Emulator.getEmuEvents();
        String[] items = line.split("\\s+");
        int s = items.length;

        if (s == 0 || "#".equals(items[0])) {
            return;
        }

        if (s == 1) {
            if (emuEvents.isEmpty()) {
                PortEvent event = new PortEvent();
                event.setTime(0);
                event.setType(EventType.TASK_IMPORT);
                event.setWorkQueues(new ArrayList<>());
                emuEvents.add(event);
            }

            List<WorkQueue> currentQueues = emuEvents.get(emuEvents.size() - 1).getWorkQueues();
            String queueName = items[0];
            System.out.println("Adding a queue: " + queueName);

            WorkQueue tq = new WorkQueue();
            tq.setQueueName(queueName);
            currentQueues.add(tq);
        } else if (s == 7 || s == 8 || s == 9) {
            WorkQueue tq = null;
            if (emuEvents.isEmpty()) {
                System.out.println("Creating a new event");
                PortEvent event = new PortEvent();
                event.setTime(0);
                event.setType(EventType.TASK_IMPORT);
                event.setWorkQueues(new ArrayList<>());
                emuEvents.add(event);
            }

            List<WorkQueue> currentQs = Emulator.emuEvents.get(Emulator.emuEvents.size()-1).getWorkQueues();
            String queueName = items[2];
            for (int i = 0; i < currentQs.size(); i++) {
                if (currentQs.get(i).getQueueName().equals(queueName)) {
                    tq = currentQs.get(i);
                    break;
                }
            }

            if (tq == null) {
                tq = new WorkQueue();
                tq.setQueueName(queueName);
                currentQs.add(tq);
            }


            int WIRefNo = Integer.parseInt(items[0]);
            String containerID = items[1];
            Node srcNode = network.findNodeByName(items[2]);
            Node dstNode = network.findNodeByName(items[3]);

            if (srcNode == null || dstNode == null) {
                return;
            }

            long loadTime = Long.parseLong(items[4]);
            long unloadTime = Long.parseLong(items[5]);

            Node quayNode;
            if (srcNode.getType() == dstNode.getType()) {
                quayNode = null;
            } else if (srcNode.getType() == NodeType.QUAY_CRANE) {
                quayNode = srcNode;
            } else {
                quayNode = dstNode;
            }

            int TEUs = Integer.parseInt(items[6]);

            int subqueueID = 0;
            if (items.length == 9) {
                subqueueID = Integer.parseInt(items[8]);
            }

            Task task = new Task();
            task.setWIRefNo(WIRefNo);
            task.setContainerID(containerID);
            task.setSrcNode(srcNode);
            task.setDstNode(dstNode);
            task.setLoadTime(loadTime);
            task.setUnloadTime(unloadTime);
            task.setQuayNode(quayNode);
            task.setTEUs(TEUs);
            task.setSeq(task.getSeq());
            task.setWorkQueueName(quayNode != null ? quayNode.getName() : "");
            task.setSubqueueID(subqueueID);

            task.setMergedTask(null);
            task.setDispatchLocation(null);
            task.setDispatchTime(-1);
            task.setSrcArrival(-1);
            task.setDstArrival(-1);
            task.getSrcAp().setTask(task);
            task.getDstAp().setTask(task);
            task.getSrcAp().setTruck(null);
            task.getDstAp().setTruck(null);
            task.getSrcAp().setTimeFinish(-1);
            task.getSrcAp().setTimeStart(-1);
            task.getDstAp().setTimeFinish(-1);
            task.getDstAp().setTimeStart(-1);

            network.getTasks().add(task);

//            WorkQueue networkWorkQueue = network.findWorkQueueByName(task.getWorkQueueName());
//            if (networkWorkQueue == null) {
//                networkWorkQueue = new WorkQueue();
//                networkWorkQueue.setQueueName(task.getWorkQueueName());
//                networkWorkQueue.setMaxTruck(0);
//                network.getWorkQueues().add(networkWorkQueue);
//            }
//
//            networkWorkQueue.getTasks().add(task);
//            networkWorkQueue.setMaxTruck(networkWorkQueue.getMaxTruck()+1);

//            WorkQueue networkWorkQueue = network.findWorkQueueByName("networkWorkQueue");
//            if (networkWorkQueue == null) {
//                networkWorkQueue = new WorkQueue();
//                networkWorkQueue.setQueueName("networkWorkQueue");
//                network.getWorkQueues().add(networkWorkQueue);
//            }
//
//            networkWorkQueue.getTasks().add(task);


            if (task.getSeq() == 1) {
                task.getWorkQueue().setPow(task.getQuayNode());
            }
        } else if (s == 2) {
            System.out.println("Adding an event at time " + items[1]);
            PortEvent event = new PortEvent();
            event.setTime(Long.parseLong(items[1]));
            event.setType(EventType.TASK_IMPORT);
            event.setWorkQueues(new ArrayList<>());
            emuEvents.add(event);
            System.out.println("Emu_events v " + emuEvents.get(emuEvents.size() - 1).getTime());
        }
    }

    public void parseNodesTimes(String[] lines,String[] topLines) {
        if (topLines != lines && topLines!= null){
            String craneId1 = lines[0];
            for (int i = 1; i < lines.length; i++) {
                String craneId2 = topLines[i];
                int duration = Integer.parseInt(lines[i]);

                if (!craneId1.equals(craneId2)) {
                    Node node1 = network.findNodeByName(craneId1);
                    Node node2 = network.findNodeByName(craneId2);

                    if (node1 != null && node2 != null) {
                        NodesTime nodesTime = new NodesTime(node1, node2, duration);
                        network.getNodesTimes().add(nodesTime);
                    }
                }
            }
        }
    }



    public void parseTruck(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 6) {
            int index = Integer.parseInt(parts[0]);
            String truckID = parts[1];
            int maxTEU = Integer.parseInt(parts[2]);
            String currentPositionID = parts[3];
            String currentTaskID = parts[4];
            String statusStr = parts[5];

            Node currentPosition = network.findNodeByName(currentPositionID);
            Task currentTask = network.findTask(currentTaskID);

            TruckStatus status = parseTruckStatus(statusStr);

            Truck truck = new Truck(index, truckID, maxTEU, currentPosition, status, currentTask);
            network.getTrucks().add(truck);
        }
    }

    private TruckStatus parseTruckStatus(String statusStr) {
        switch (statusStr) {
            case "TRUCK_BROKEN":
                return TruckStatus.TRUCK_BROKEN;
            case "TRUCK_DEADHEADING":
                return TruckStatus.TRUCK_DEADHEADING;
            case "TRUCK_HALFLOAD_DEADHEADING":
                return TruckStatus.TRUCK_HALFLOAD_DEADHEADING;
            case "TRUCK_XSHIPPING":
                return TruckStatus.TRUCK_XSHIPPING;
            case "TRUCK_HALFLOAD_XSHIPPING":
                return TruckStatus.TRUCK_HALFLOAD_XSHIPPING;
            case "TRUCK_AVAILABLE":
                return TruckStatus.TRUCK_AVAILABLE;
            case "TRUCK_QUEUEING":
                return TruckStatus.TRUCK_QUEUEING;
            default:
                return TruckStatus.TRUCK_AVAILABLE;
        }
    }
}
