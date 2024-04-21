package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.Emulator.Emulator;
import com.example.fyp_1.PortEvent;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
    static final long FEATURE_TW = 900;
    static boolean output_instructions = true;
    static int double_ctn_lookup_depth = 2;
    static SA_Map dispatcher_sa_map = null;
    static Network nw = null;
    private static StringBuilder msg;
    private static long currentTime;
    static List<Node> nodes = new ArrayList<>();

    static TimeProvider timeProvider = new SimulatedTimeProvider();

    public static boolean isOutput_instructions() {
        return output_instructions;
    }

    public static void setOutput_instructions(boolean output_instructions) {
        Dispatcher.output_instructions = output_instructions;
    }

    public static int getDouble_ctn_lookup_depth() {
        return double_ctn_lookup_depth;
    }

    public static void setDouble_ctn_lookup_depth(int double_ctn_lookup_depth) {
        Dispatcher.double_ctn_lookup_depth = double_ctn_lookup_depth;
    }

    public static SA_Map getDispatcher_sa_map() {
        return dispatcher_sa_map;
    }

    public static void setDispatcher_sa_map(SA_Map dispatcher_sa_map) {
        Dispatcher.dispatcher_sa_map = dispatcher_sa_map;
    }

    public static Network getNw() {
        return nw;
    }

    public static void setNw(Network nw) {
        Dispatcher.nw = nw;
    }

    public static StringBuilder getMsg() {
        return msg;
    }

    public static void setMsg(StringBuilder msg) {
        Dispatcher.msg = msg;
    }

    public static long getCurrentTime() {
        return currentTime;
    }

    public static void setCurrentTime(long currentTime) {
        Dispatcher.currentTime = currentTime;
    }

    public static TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public static void setTimeProvider(TimeProvider timeProvider) {
        Dispatcher.timeProvider = timeProvider;
    }

    public static void instructionToString(Instruction inst) {
        if (output_instructions) {
            switch (inst.getInsCode()) {
                case DEADHEAD_TO_YARD_CRANE:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(e)\tgoto yard_crane\t"
                            + inst.getDst().getName() + "\ttask:\t" + inst.getTask().getContainerID());
                    break;
                case LOADED_TO_YARD_CRANE:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\tgoto yard_crane\t"
                            + inst.getDst().getName() + "\ttask:\t" + inst.getTask().getContainerID());
                    break;
                case DEADHEAD_TO_QUAY_CRANE:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(e)\tgoto ship_crane\t"
                            + inst.getDst().getName() + "\ttask:\t" + inst.getTask().getContainerID());
                    break;
                case LOADED_TO_QUAY_CRANE:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\tgoto ship_crane\t"
                            + inst.getDst().getName() + "\ttask:\t" + inst.getTask().getContainerID());
                    break;
                case PUT_CONTAINER_TO_TRUCK:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(e)\tready to receive\t"
                            + inst.getTask().getContainerID() + "\tfrom\t" + inst.getDst().getName());
                    break;
                case TAKE_CONTAINER_FROM_TRUCK:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\tready to unload\t"
                            + inst.getTask().getContainerID() + "\tto\t" + inst.getDst().getName());
                    break;
                case HOLD:
                    if (inst.getTask() != null) {
                        System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\twait with container\t"
                                + inst.getTask().getContainerID());
                    } else {
                        System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(e)\twait");
                    }
                    break;
                case TO_DEPOT:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(e)\tgoto depot\t"
                            + inst.getDst().getName());
                    break;
                case TAKE_CONTAINER_FROM_TRUCK_FINISH:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\thave unloaded\t"
                            + inst.getTruck().getCurrentTask().getContainerID() + "\tfrom\t" + inst.getDst().getName());
                    break;
                case PUT_CONTAINER_TO_TRUCK_FINISH:
                    System.out.println("\tdispatcher:\t" + inst.getTruck().getTruckID() + "\t(l)\thave received\t"
                            + inst.getTask().getContainerID() + "\tfrom\t" + inst.getDst().getName());
                    break;
            }
        }
    }

    private static void notifyTrucksInQueues(PortEvent pe, List<Instruction> instructions, List<Node> nodes) {
        List<Truck> craneQueue = pe.getEventLocation().getTruckQueue();
        for (int i = 0; i < craneQueue.size(); i+=2) {
            Truck truck = craneQueue.get(i);
            Task task = truck.getCurrentTask();
            if (pe.getEventLocation().getType() == NodeType.QUAY_CRANE && !Emulator.topOfContainerQueue(task)) {
                continue;
            }

            Task cuTask =  pe.getTruck().getCurrentTask();
            cuTask.setDispatchTime(currentTime);
            //System.out.println("Truck " + pe.getTruck().getTruckID() + " with " + nw.taskToString(pe.getTruck().getCurrentTask()) + " is notified");
            craneQueue.remove(i);

            Instruction inst = null;
            if(nodes.get(pe.getEventLocation().getNode_index()).getCurrentTask()!=null ){
                    inst = new Instruction(InstructionType.HOLD, pe.getTruck(), pe.getTruck().getCurrentPosition(),cuTask);
                }
            else {
                if (cuTask.getSrcNode() == pe.getEventLocation()) {
                    //if (nodes.get(pe.getEventLocation().getNode_index()).getCurrentTask()!=null) {
                        nodes.get(pe.getEventLocation().getNode_index()).setCurrentTask(cuTask);
                    //}
                    inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK_FINISH, pe.getTruck(), pe.getTruck().getCurrentPosition(), cuTask);
                } else {
                    //if (nodes.get(pe.getEventLocation().getNode_index()).getCurrentTask()!=null) {
                        nodes.get(pe.getEventLocation().getNode_index()).setCurrentTask(cuTask);
                    //}
                    inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK_FINISH, pe.getTruck(), pe.getTruck().getCurrentPosition(), cuTask);
                }
            }
            instructions.add(inst);
            instructionToString(inst);
            return;
        }
    }

    public static void evalFeature(Truck tr, List<Long> bfrTws, Feature ft) {
        int scraneSize = nw.getQuayCraneNodes().size();

        // Feature 1: Work Instruction Queue Length
        List<WorkQueue> workQueues = nw.getWorkQueues();
        List<Integer> workQueueLen = new ArrayList<>();
        for (WorkQueue queue : workQueues) {
            workQueueLen.add(queue.getTasks().size());
        }
        ft.setWork_queue_len(workQueueLen);

        // Feature 2: Current Position
        ft.setTruck_position(tr.getCurrentPosition());

        // Initialize all buffers
        int numOfCheckPoints = bfrTws.size();
        for (int n = 0; n < numOfCheckPoints; n++) {
            for (int i = 0; i < scraneSize; i++) {
                ft.getBuffer_sup().get(n).set(i, 0);
                ft.getBuffer_demand().get(n).set(i, 0);
            }
        }

        // Feature 3.1: Buffer Demand
        for (List<Task> tqi : Emulator.containerQueues) {
            if (tqi.isEmpty())
                continue;

            int featureIndex = ft.qcIndex(tqi);
            if (featureIndex == -1)
                continue;

            int demands = 0, twLevel = 0;
            long operationTime = 0;
            for (Task t : tqi) {
                if (t.getQuayNode() == null)
                    continue;
                else if (t.getQuayNode() == t.getSrcNode())
                    operationTime += t.getQuayNode().getAverageLoadTime();
                else
                    operationTime += t.getQuayNode().getAverageUnloadTime();

                if (operationTime > bfrTws.get(twLevel)) {
                    ft.getBuffer_demand().get(twLevel).set(featureIndex, demands);
                    twLevel++;
                    if (twLevel == bfrTws.size()) {
                        break;
                    }
                }
                demands++;
            }

            for (; twLevel < bfrTws.size(); twLevel++) {
                ft.getBuffer_demand().get(twLevel).set(featureIndex, demands);
            }
        }

        // Feature 3.2: Buffer Supply
        for (Truck truck : nw.getTrucks()) {
            if (!truck.isAssigned())
                continue;

            Task ta = truck.getCurrentTask();
            int passedStatus;

            if (ta.getQuayNode() == ta.getSrcNode())
                passedStatus = 4;
            else if (ta.getQuayNode() == ta.getDstNode())
                passedStatus = 7;
            else
                continue;

            if (ta.status() >= passedStatus)
                continue;

            int scIndex = truck.getCurrentTask().getQuayNode().getNode_index();
            int bfrI = 0;
            for (; bfrI < scraneSize; bfrI++) {
                if (nw.getQuayCraneNodes().get(bfrI).getNode_index() == scIndex)
                    break;
            }

            for (int j = 0; j < numOfCheckPoints; j++) {
                ft.getBuffer_sup().get(j).set(bfrI, ft.getBuffer_sup().get(j).get(bfrI) + 1);
            }
        }

        System.out.println("buffer_overview");
        int scIndex = nw.getQuayCraneNodes().size();
        for (int i = 0; i < scIndex; i++) {
            if (ft.getBuffer_demand().get(0).get(i) != 0 || ft.getBuffer_sup().get(0).get(i) != 0) {
                System.out.println(nw.getQuayCraneNodes().get(i).getName() + " d: " +
                        ft.getBuffer_demand().get(0).get(i) + ", s: " + ft.getBuffer_sup().get(0).get(i));
            }
        }
    }

    public void setDispatcherSaMap(SA_Map sam) {
        dispatcher_sa_map = sam;
    }

    public static int handleEvent(PortEvent event_i, ArrayList<Instruction> instructions) {

        switch (event_i.getType()) {
            case TASK_IMPORT:
                processTaskImport(event_i, instructions);
                for (Node node : nw.getNodes()) {
                    if (node == null) continue;
                    Node nodeCopy = new Node(node);
                    nodes.add(node.getNode_index(), nodeCopy);
                }
                break;
            case YC_WORKING_POINT_ARRIVAL:
                processYcWorkingPointArrival(event_i, instructions);
                break;
            case QC_WORKING_POINT_ARRIVAL:
                processQcWorkingPointArrival(event_i, instructions);
                break;
            case CRANE_PUTTING_TO_VEHICLE_SUCCESS:
                processCranePuttingToVehicleSuccess(event_i, instructions,nodes);
                break;
            case CRANE_UNLOADING_FROM_VEHICLE_SUCCESS:
                processCraneUnloadingFromVehicleSuccess(event_i, instructions);
                break;
            case CRANE_UNLOADING_FROM_VEHICLE_FINISH:
                processCraneUnloadingFromVehicleFinish(event_i, instructions);
                break;
            case CRANE_PUTTING_TO_VEHICLE_FINISH:
                processCranePuttingToVehicleFinish(event_i, instructions);
                break;
        }
        return 0;
    }

    public  static  void appendSrcSameSmallContainer(Task nextTask, Truck tr){
        WorkQueue queue = new WorkQueue();
        for (int i = 0; i < nw.getWorkQueues().size(); i++) {
            WorkQueue qi = nw.getWorkQueues().get(i);
            if(qi.getQueueName().equals(nextTask.getSrcNode().getName())){
                queue = qi;
            }
        }

        int seekCount = 0;
        for (int i = 0; i < queue.getTasks().size() && seekCount < 2; i++, seekCount++) {
            Task tai = queue.getTasks().get(i);

            if (!tai.smallCtnMergeable()) {
                if (tai.getMergedTask() != null && tai.getTwinWIRefNo() != 0) {
                    seekCount++;
                }
                continue;
            }

            if (nextTask.getTEUs() + tai.getTEUs() <= tr.getMaxTEU() &&
                    nextTask.getSrcNode().equals(tai.getSrcNode()) &&
                    nextTask.getSubqueueID() == tai.getSubqueueID()) {
                if (nextTask.getSrcNode().equals(nextTask.getQuayNode()) &&
                        tai.getSrcNode().equals(tai.getQuayNode()) &&
                        (queue.getTaskMerging() == 2 || queue.getTaskMerging() == 3)) {
                    nextTask.setMergedTask(tai);
                    queue.getTasks().remove(i);
                    break;
                } else if (nextTask.getDstNode().equals(nextTask.getQuayNode()) &&
                        tai.getDstNode().equals(tai.getQuayNode()) &&
                        nextTask.getSrcNode().equals(tai.getSrcNode()) &&
                        (queue.getTaskMerging() == 1 || queue.getTaskMerging() == 3)) {
                    nextTask.setMergedTask(tai);
                    queue.getTasks().remove(i);
                    break;
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
    }

    public static void appendNextTask(Task nextTask, Truck tr) {
        if (nextTask.getTEUs() == 1) {
            List result = CraneSelect.findTaskMinWait(nw, tr, msg, nextTask);
            String secondContainerId = result.get(0).toString();
            nw = (Network) result.get(1);
            Task secondTask = nw.findTask(secondContainerId);

            if (secondTask!=null){
                System.out.println("!!!!" + secondTask.getContainerID());
                System.out.println("-----------------------------------");

                if (secondTask.smallCtnMergeable()) {
                    if (secondTask.getMergedTask() == null && secondTask.getTwinWIRefNo() == 0) {
                        if (nextTask.getTEUs() + secondTask.getTEUs() <= tr.getMaxTEU()) {
                            nextTask.setMergedTask(secondTask);
                            WorkQueue queue = nw.findQueueForTask(nw, secondTask);
                            if (queue != null) {
                                queue.getTasks().remove(secondTask);
                            }
                        }
                    }
                }
            }

        }
    }

    public static void appendSmallContainer(List<WorkQueue> workQueueList, Task nextTask, Truck tr) {
        if (!nextTask.smallCtnMergeable())
            return;

        WorkQueue reWorkQueue = new WorkQueue();
        for (WorkQueue workQueue: workQueueList){
            if (workQueue.getQueueName().equals(nextTask.getSrcNode().getName())) {
                reWorkQueue.setQueueName(workQueue.getQueueName());
                reWorkQueue.setTasks(workQueue.getTasks());
            }
        }
        if (reWorkQueue.getTasks().size()!=0) {
            appendSrcSameSmallContainer(nextTask, tr);
        }
        else {
            appendNextTask(nextTask, tr);
        }
    }

    public static void getInstruction(Truck tr, long time, List<Instruction> insts) {
        msg = new StringBuilder("Dispatcher message:\n");
        msg.append(currentTaskMsg());
        msg.append(queueTopTaskMsg());

        currentTime = time;
        List result = CraneSelect.findTaskMinWait(nw, tr, msg, tr.getCurrentTask());

        String resultContainerId = result.get(0).toString();
        nw = (Network) result.get(1);

        System.out.println("!!!!" + resultContainerId);
        System.out.println("-----------------------------------");

        convertToInstruction(nw, resultContainerId, tr, true, insts);

//        Task firstTask = nw.findTask(resultContainerId);
//        if (firstTask != null && firstTask.getTEUs() == 1) {
//            String secondContainerId = CraneSelect.findTaskMinWait(nw, tr, msg);
//            if (secondContainerId != null) {
//                convertToInstruction(nw, secondContainerId, tr, true, insts);
//            }
//        }
    }

    private static void processTaskImport(PortEvent pe, List<Instruction> instructions) {
        int fleetSize = nw.getTrucks().size();
        for (int i = 0; i < fleetSize; i++) {
            Truck truck = nw.getTrucks().get(i);
            if (truck.getStatus() != TruckStatus.TRUCK_AVAILABLE) {
                continue;
            }
            getInstruction(truck, timeProvider.getCurrentTime(), instructions);
        }
        for (Instruction instruction : instructions) {
            instructionToString(instruction);
        }

        for (Instruction instruction : instructions) {
            if (instruction.getInsCode() != InstructionType.HOLD) {
                Truck truck = instruction.getTruck();
                truck.setStatus(TruckStatus.TRUCK_DEADHEADING);
                Task currentTask = instruction.getTask();
                truck.setCurrentTask(currentTask);
                currentTask.setDispatchTime(timeProvider.getCurrentTime());
                currentTask.setDispatchLocation(truck.getCurrentPosition());
                if (currentTask.getMergedTask() != null) {
                    currentTask.getMergedTask().setDispatchLocation(truck.getCurrentPosition());
                    currentTask.getMergedTask().setDispatchTime(currentTask.getDispatchTime());
                }
            }
        }
    }

    private static void processQueueArrival(PortEvent pe, List<Instruction> instructions) {
        Truck t = pe.getTruck();
        Node queue = pe.getEventLocation();

        if (t.getCurrentTask().getQuayNode() == t.getCurrentTask().getSrcNode()) {
            if (!queue.getQueueTasks().isEmpty()) {
                Task task = queue.getQueueTasks().remove(0);
                Instruction inst = new Instruction(InstructionType.DEADHEAD_TO_QUAY_CRANE, t, task.getDstNode(), task);
                instructions.add(inst);
                instructionToString(inst);
            } else {
                Instruction inst = new Instruction(InstructionType.HOLD, t, queue, t.getCurrentTask());
                instructions.add(inst);
                instructionToString(inst);
            }
        } else {
            Instruction inst = new Instruction(InstructionType.LOADED_TO_QUAY_CRANE, t, t.getCurrentTask().getDstNode(), t.getCurrentTask());
            instructions.add(inst);
            instructionToString(inst);
        }
    }

    private static void processYcWorkingPointArrival(PortEvent pe, List<Instruction> instructions) {
        Truck t = pe.getTruck();
        Task task = t.getCurrentTask();

        if (task.getSrcNode() == task.getDstNode()) {
            if (task.getSrcAp().getTimeStart() == -1) {
                Node src = task.getSrcNode();
                if (!src.getTruckQueue().isEmpty() || src.getCurrentTask() != null) {
                    Instruction inst = new Instruction(InstructionType.HOLD, t, src, task);
                    instructions.add(inst);
                    instructionToString(inst);
                    return;
                } else {
                    Instruction inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK, t, src, t.getCurrentTask());
                    src.setCurrentTask(task);
                    instructions.add(inst);
                    instructionToString(inst);
                    return;
                }
            } else {
                Node dst = task.getDstNode();
                if (dst.getCurrentTask() != null || !dst.getTruckQueue().isEmpty()) {
                    Instruction inst = new Instruction(InstructionType.HOLD, t, dst, task);
                    instructions.add(inst);
                    instructionToString(inst);
                    return;
                } else {
                    Instruction inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK, t, dst, task);
                    dst.setCurrentTask(task);
                    instructions.add(inst);
                    instructionToString(inst);
                    return;
                }
            }
        }

        if (task.getDstNode() == pe.getEventLocation()) {
            Node dst = task.getDstNode();
            if (dst.getCurrentTask() != null || !dst.getTruckQueue().isEmpty()) {
                Instruction inst = new Instruction(InstructionType.HOLD, t, dst, task);
                instructions.add(inst);
                instructionToString(inst);
                return;
            } else {
                Instruction inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK, t, dst, task);
                dst.setCurrentTask(task);
                instructions.add(inst);
                instructionToString(inst);
                return;
            }
        } else if (task.getSrcNode() == pe.getEventLocation()) {
            Node src = task.getSrcNode();
            if (src.getCurrentTask() != null || !Emulator.topOfContainerQueue(task)) {
                Instruction inst = new Instruction(InstructionType.HOLD, t, src, task);
                instructions.add(inst);
                instructionToString(inst);
            } else {
                Instruction inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK, t, src, task);
                src.setCurrentTask(task);
                instructions.add(inst);
                instructionToString(inst);
            }
        } else if (task.getMergedTask() != null && task.getMergedTask().getSrcNode() == pe.getEventLocation()) {
            Node src = task.getMergedTask().getSrcNode();
            if (!src.getTruckQueue().isEmpty() || src.getCurrentTask() != null) {
                Instruction inst = new Instruction(InstructionType.HOLD, t, src, task.getMergedTask());
                instructions.add(inst);
                instructionToString(inst);
                return;
            } else {
                Instruction inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK, t, src, task.getMergedTask());
                src.setCurrentTask(task);
                instructions.add(inst);
                instructionToString(inst);
                return;
            }
        } else {
            System.out.println("Unexpected condition met at yard crane arrival. Check your program!");
        }
    }

    private static void processQcWorkingPointArrival(PortEvent pe, List<Instruction> instructions) {
        Truck tr = pe.getTruck();
        Task task = tr.getCurrentTask();

        task.setDispatchTime(currentTime);
        if (task.getDstNode() == pe.getEventLocation()) {
            Node dst = task.getDstNode();
            if (dst.getCurrentTask() != null || !Emulator.topOfContainerQueue(task)) {
                Instruction inst = new Instruction(InstructionType.HOLD, tr, dst, task);
                instructions.add(inst);
                instructionToString(inst);
            } else {
                Instruction inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK, tr, dst, task);
                dst.setCurrentTask(task);
                instructions.add(inst);
                instructionToString(inst);
            }
        } else if (task.getSrcNode() == pe.getEventLocation()) {
            Node src = task.getSrcNode();
            if (src.getCurrentTask() != null || !Emulator.topOfContainerQueue(task)) {
                Instruction inst = new Instruction(InstructionType.HOLD, tr, src, task);
                instructions.add(inst);
                instructionToString(inst);
            } else {
                Instruction inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK, tr, src, task);
                src.setCurrentTask(task);
                instructions.add(inst);
                instructionToString(inst);
            }
        } else {
            System.out.println("Unexpected condition met at ship crane arrival. Check your program!");
        }
    }

    private static void processCranePuttingToVehicleSuccess(PortEvent pe, List<Instruction> instructions,List<Node> nodes) {
        Node eventLocation = pe.getEventLocation();
        eventLocation.setCurrentTask(null);
        notifyTrucksInQueues(pe, instructions,nodes);
    }

    private static void processCraneUnloadingFromVehicleSuccess(PortEvent pe, List<Instruction> instructions) {
        Node eventLocation = pe.getEventLocation();
        //pe.getEventLocation().setCurrentTask(null);
        Instruction inst;
        inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK_FINISH, pe.getTruck(),pe.getTruck().getCurrentTask().getDstNode(),eventLocation.getCurrentTask());
        instructions.add(inst);
    }

    private static void processCraneUnloadingFromVehicleFinish(PortEvent pe, List<Instruction> instructions) {
        Truck tr = pe.getTruck();
        Task currentTask = tr.getCurrentTask();
        pe.getEventLocation().setCurrentTask(null);

        if (currentTask.getMergedTask() != null && pe.getTime() == currentTask.getUnloadTime()+currentTask.getDstArrival()) {
            Task mergedTask = currentTask.getMergedTask();
            if (mergedTask.getDstNode().equals(currentTask.getDstNode())) {
                currentTask.setMergedTask(null);
                tr.setPreTask(null);
                Instruction inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK, tr, mergedTask.getDstNode(), mergedTask);
                nodes.get(mergedTask.getDstNode().getNode_index()).setCurrentTask(null);
                instructions.add(inst);
                return;
            }
            else {
                Instruction inst = new Instruction(InstructionType.LOADED_TO_YARD_CRANE, tr, mergedTask.getDstNode(), mergedTask);
                nodes.get(mergedTask.getDstNode().getNode_index()).setCurrentTask(null);
                instructions.add(inst);
                return;
            }
        }else if (currentTask.getMergedTask() == null
                && tr.getPreTask()!=currentTask
                && tr.getPreTask()!=null
                && pe.getTime() == currentTask.getUnloadTime()+currentTask.getDstArrival()) {
            tr.getPreTask().setMergedTask(null);
            Task fTask = tr.getPreTask();
            tr.setPreTask(null);
            Instruction inst = new Instruction(InstructionType.LOADED_TO_YARD_CRANE, tr, fTask.getDstNode(), fTask);
            nodes.get(fTask.getDstNode().getNode_index()).setCurrentTask(null);
            instructions.add(inst);
            return;
        }else if (currentTask.getMergedTask() == null && tr.getPreTask()!=null && pe.getTime() != currentTask.getUnloadTime()+currentTask.getDstArrival()){
            Task fTask = tr.getPreTask();
            tr.setPreTask(null);
            Instruction inst = new Instruction(InstructionType.TAKE_CONTAINER_FROM_TRUCK, tr, fTask.getDstNode(), fTask);
            nodes.get(fTask.getDstNode().getNode_index()).setCurrentTask(null);
            instructions.add(inst);
            return;
        }
        else {
            getInstruction(tr, timeProvider.getCurrentTime(), instructions);
            Instruction insi = instructions.get(instructions.size() - 1);
            if (insi.getInsCode() != InstructionType.HOLD) {
                tr.setStatus(TruckStatus.TRUCK_DEADHEADING);
                tr.setCurrentTask(insi.getTask());
                tr.getCurrentTask().setDispatchTime(timeProvider.getCurrentTime());
                tr.getCurrentTask().setDispatchLocation(tr.getCurrentPosition());
                if (tr.getCurrentTask().getMergedTask() != null) {
                    tr.getCurrentTask().getMergedTask().setDispatchLocation(tr.getCurrentPosition());
                    tr.getCurrentTask().getMergedTask().setDispatchTime(tr.getCurrentTask().getDispatchTime());
                }
            }
            instructionToString(insi);
        }
    }

    private static void processCranePuttingToVehicleFinish(PortEvent pe, List<Instruction> instructions) {
        Truck tr = pe.getTruck();
        Task task = tr.getCurrentTask();

        if(task.getDstNode() == pe.getEventLocation().getCurrentTask().getDstNode()){
            Node dst = task.getDstNode();
            if (dst.getCurrentTask() != null || !dst.getTruckQueue().isEmpty()) {
                Instruction inst = new Instruction(InstructionType.HOLD, tr, dst, task);
                instructions.add(inst);
                instructionToString(inst);
                return;
            }else if (task.getMergedTask() != null && task.getMergedTask().status() <= 5 && pe.getTime() == task.getLoadTime()+task.getDispatchTime()) {
                task.getSrcNode().setCurrentTask(null);
                tr.setPreTask(task);
                if (task.getMergedTask().getSrcNode().equals(task.getSrcNode())) {
                    Instruction inst = new Instruction(InstructionType.PUT_CONTAINER_TO_TRUCK, tr, task.getMergedTask().getSrcNode(), task.getMergedTask());
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                }
                else if (!task.getMergedTask().getSrcNode().equals(task.getSrcNode()) && task.getMergedTask().getSrcNode().getType() == NodeType.QUAY_CRANE){
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_QUAY_CRANE, tr, task.getMergedTask().getSrcNode(), task.getMergedTask());
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                }
                else if (!task.getMergedTask().getSrcNode().equals(task.getSrcNode()) && task.getMergedTask().getSrcNode().getType() == NodeType.YARD_CRANE){
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_YARD_CRANE, tr, task.getMergedTask().getSrcNode(), task.getMergedTask());
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                }
            }else if(tr.getPreTask()!=null) {

                Task preTask = tr.getPreTask();

                long timeToCurrentTaskDstNode = nw.getTravellingTimePassingNodes(tr.getCurrentPosition(), tr.getCurrentTask().getDstNode());
                long timeToPreTaskDstNode = nw.getTravellingTimePassingNodes(tr.getCurrentPosition(), preTask.getDstNode());

                Node fDstNode;
                Task fTask;
                if (timeToCurrentTaskDstNode <= timeToPreTaskDstNode) {
                    fDstNode = tr.getCurrentTask().getDstNode();
                    fTask = tr.getCurrentTask();
                    tr.setPreTask(tr.getPreTask());
                } else {
                    fDstNode = preTask.getDstNode();
                    fTask = preTask;
                    tr.setPreTask(tr.getCurrentTask());
                }
                task.getSrcNode().setCurrentTask(null);
                if (tr.getCurrentTask().getQuayNode() == tr.getCurrentTask().getSrcNode()) {
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_YARD_CRANE, tr, fDstNode, fTask);
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                } else {
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_QUAY_CRANE, tr, fDstNode, fTask);
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                }
            }
            else {
                if (tr.getCurrentTask().getQuayNode() == tr.getCurrentTask().getSrcNode()) {
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_YARD_CRANE, tr, task.getDstNode(), task);
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;

                } else {
                    Instruction inst = new Instruction(InstructionType.LOADED_TO_QUAY_CRANE, tr, pe.getEventLocation().getCurrentTask().getDstNode(), pe.getEventLocation().getCurrentTask());
                    nodes.get(task.getSrcNode().getNode_index()).setCurrentTask(null);
                    instructions.add(inst);
                    return;
                }
            }
        }

    }

    public static String currentTaskMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----current task msg-----\n");
        sb.append("current time is ").append(System.currentTimeMillis()).append("\n");
        for (Truck truck : nw.getTrucks()) {
            sb.append("TruckID: " + truck.getTruckID() + ", CurrentTask: " +truck.getCurrentTask() +", Status:  " + truck.getStatus()).append("\n");
        }
        return sb.toString();
    }

    public static String queueTopTaskMsg() {
        StringBuilder sb = new StringBuilder("-----queue top tasks-----\n");
        for (WorkQueue workQueue : nw.getWorkQueues()) {
            if (!workQueue.getTasks().isEmpty()) {
                Task topTask = workQueue.getTasks().get(0);
                sb.append(workQueue.getQueueName()+ ": "+ topTask.getWIRefNo() + ", " + topTask.getSrcNode()+ ", " + topTask.getDstNode()).append("\n");
            }
        }
        return sb.toString();
    }

    public static void convertToInstruction(Network network, String containerId, Truck tr, boolean appendCtn, List<Instruction> insts) {

        Task nextTask = network.findTaskFromQueue(containerId, network.getWorkQueues());

        List<WorkQueue> workQueueList = new ArrayList<>();
        for (WorkQueue workQueue: network.getWorkQueues()){
            WorkQueue copyWorkQueue = new WorkQueue(workQueue);
            workQueueList.add(copyWorkQueue);
        }

        if (nextTask == null) {
            Instruction inst = new Instruction(InstructionType.HOLD, tr, null, null);
            insts.add(inst);
            return;
        } else {
            if (appendCtn) {
                appendSmallContainer(workQueueList,nextTask, tr);
            }
            network.linkTaskPrecedence(false);
            if (nextTask.getSrcNode().getType() == NodeType.QUAY_CRANE) {
                nextTask.getSrcNode().getQueueTasks().add(nextTask);
                nextTask.getSrcNode().getTruckQueue().add(tr);
                nextTask.getSrcNode().getQueueTasks().add(nextTask);
                nextTask.getSrcNode().getTruckQueue().add(tr);
                Instruction inst = new Instruction(InstructionType.DEADHEAD_TO_QUAY_CRANE,
                        tr, nextTask.getSrcNode(), nextTask);
                insts.add(inst);
                return;
            } else {
                nextTask.getSrcNode().getQueueTasks().add(nextTask);
                nextTask.getSrcNode().getTruckQueue().add(tr);
                Instruction inst = new Instruction(InstructionType.DEADHEAD_TO_YARD_CRANE,
                        tr, nextTask.getSrcNode(), nextTask);
                insts.add(inst);
                return;
            }
        }
    }
}