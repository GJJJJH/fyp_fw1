package com.example.fyp_1.Emulator;

import com.example.fyp_1.*;
import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.algorithm.*;

import java.util.ArrayList;
import java.util.List;

public class Emulator {
    public static Network nw = null;
    public static List<List<Task>> containerQueues = new ArrayList<>();
    public static long currentSimulationTime = 1;
    public static EvolutionFitness emuSolution = null;
    public static List<PortEvent> emuEvents = new ArrayList<>();
    public static List<Task> finishedTasks = new ArrayList<>();
    public static boolean outputNextEventTime = false;
    public static boolean outputTravellingTime = false;
    public static boolean outputEventType = true;
    public static boolean outputEventTriggerInfo = true;
    public static boolean outputInstructionHandling = true;
    public static int eventNum = 0;
    private static long FinalTime;

    public Network getNw() {
        return nw;
    }

    public void setNw(Network nw) {
        this.nw = nw;
    }

    public static List<List<Task>> getContainerQueues() {
        return containerQueues;
    }

    public void setContainerQueues(List<List<Task>> containerQueues) {
        this.containerQueues = containerQueues;
    }

    public long getCurrentSimulationTime() {
        return currentSimulationTime;
    }

    public void setCurrentSimulationTime(long currentSimulationTime) {
        this.currentSimulationTime = currentSimulationTime;
    }

    public EvolutionFitness getEmuSolution() {
        return emuSolution;
    }

    public void setEmuSolution(EvolutionFitness emuSolution) {
        this.emuSolution = emuSolution;
    }

    public static List<PortEvent> getEmuEvents() {
        return emuEvents;
    }

    public void setEmuEvents(List<PortEvent> emuEvents) {
        this.emuEvents = emuEvents;
    }

    public List<Task> getFinishedTasks() {
        return finishedTasks;
    }

    public void setFinishedTasks(List<Task> finishedTasks) {
        this.finishedTasks = finishedTasks;
    }

    public boolean isOutputNextEventTime() {
        return outputNextEventTime;
    }

    public void setOutputNextEventTime(boolean outputNextEventTime) {
        this.outputNextEventTime = outputNextEventTime;
    }

    public boolean isOutputTravellingTime() {
        return outputTravellingTime;
    }

    public void setOutputTravellingTime(boolean outputTravellingTime) {
        this.outputTravellingTime = outputTravellingTime;
    }

    public boolean isOutputEventType() {
        return outputEventType;
    }

    public void setOutputEventType(boolean outputEventType) {
        this.outputEventType = outputEventType;
    }

    public boolean isOutputEventTriggerInfo() {
        return outputEventTriggerInfo;
    }

    public void setOutputEventTriggerInfo(boolean outputEventTriggerInfo) {
        this.outputEventTriggerInfo = outputEventTriggerInfo;
    }

    public boolean isOutputInstructionHandling() {
        return outputInstructionHandling;
    }

    public void setOutputInstructionHandling(boolean outputInstructionHandling) {
        this.outputInstructionHandling = outputInstructionHandling;
    }

    public static boolean topOfContainerQueue(Task t) {
        if (t.getSeq() == 0) {
            return true;
        }
        for (int i = 0; i < nw.getWorkQueues().size(); i++) {
            if (nw.getWorkQueues().get(i).getQueueName().equals(t.getWorkQueueName())) {
                if (!containerQueues.get(i).isEmpty() && containerQueues.get(i).get(0).getSeq() == t.getSeq()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void removeFromContainerQueue(Task task) {
        for (int i = 0; i < nw.getWorkQueues().size(); i++) {
            WorkQueue workQueue = nw.getWorkQueues().get(i);
            if (!workQueue.getQueueName().equals(task.getWorkQueueName())) {
                continue;
            }

            List<Task> containerQueue = containerQueues.get(i);
            for (int j = 0; j < containerQueue.size(); j++) {
                if (containerQueue.get(j).getSeq() == task.getSeq()) {
                    containerQueue.remove(j);
                    if (task.getMergedTask() == null || task.getTwinWIRefNo() != 0) {
                        break;
                    }
                }
                if (containerQueue.get(j).getSeq() == task.getMergedTask().getSeq()) {
                    containerQueue.remove(j);
                    break;
                }
            }
            break;
        }
    }

    public static double startEmulator(String netfile, int numOfTrucks,Boolean isSameSrcTask) {
        initialiseEmulator();

        NetworkReader networkReader = new NetworkReader(netfile);

        if (networkReader.parseData() == null) {
            System.out.println("Failed");
        }

        if (networkReader.parseData().getTasks().isEmpty()) {
            System.out.println("NoTask");
        }

        nw.addTrucks(2, numOfTrucks);
        double ret;
        ret = startEmulator(isSameSrcTask);
        nw = null;
        return ret;
    }

    public static double startEmulator(Network simNetwork, List<PortEvent> emuEventsFi, Boolean isSameSrcTask) {
        initialiseEmulator();
        nw = simNetwork;
        emuEvents = new ArrayList<>(emuEventsFi);
        return startEmulator(isSameSrcTask);
    }

    public static void sleep(int seconds) {
        long startTime = System.currentTimeMillis();

        long sleepTime = seconds * 1000;
        while (System.currentTimeMillis() - startTime < sleepTime) {

        }
    }

    private static double startEmulator(Boolean isSameSrcTask) {
        Dispatcher.setNw(nw);
        Dispatcher.setTimeProvider(new SimulatedTimeProvider());
        SA_Map sam = new SA_Map();
        Dispatcher.setDispatcher_sa_map(sam);
        Dispatcher.setIsSameSrcTask(isSameSrcTask);

        System.out.println(" \n SIMULATION START:\n");
        for (Truck truck : nw.getTrucks()) {
            truck.setStatus(TruckStatus.TRUCK_AVAILABLE);
        }
        long currentSimulationTime = 0;
        emuSolution = new EvolutionFitness(nw, currentSimulationTime);
        ArrayList<Instruction> instructions = new ArrayList<>();
        while (eventNum < emuEvents.size()) {
            PortEvent event_i = emuEvents.get(eventNum);
            triggerEvent(event_i);
            Dispatcher.handleEvent(event_i, instructions);
            for (Instruction instruction : instructions) {
                handleInstruction(instruction);
            }
            sleep(0);
            instructions.clear();
            eventNum++;
        }
        outputAnalysis(emuSolution, nw);
        double ret = FinalTime;
        emuSolution = null;
        return ret;
    }

    private static void initialiseContainerQueues() {
        int wqSize = nw.getWorkQueues().size();
        containerQueues = new ArrayList<>(wqSize);
        for (int i = 0; i < wqSize; i++) {
            containerQueues.add(nw.getWorkQueues().get(i).getTasks());
        }
    }

    private static void initialiseEmulator() {
        containerQueues.clear();
        currentSimulationTime = 0;
        emuEvents = null;
        eventNum = 0;
        if (emuSolution != null) {
            emuSolution = null;
        }
    }

    private static double outputAnalysis(EvolutionFitness sol, Network nw) {
        System.out.println("----- Output Analysis -----");
        initializeTotalValues(sol);
        printUnfinishedTasks(nw);
        printTruckInformation(sol, nw);
        printNodeAccessPointInformation(sol, nw);
        return calculateBusyRateAndReturn(sol);
    }

    private static void initializeTotalValues(EvolutionFitness sol) {
        sol.setTotalTravellingTime(sol.setTotalCraneWait(sol.setTotalTruckWait(0)));
    }

    private static void printUnfinishedTasks(Network nw) {
        if (!nw.getWorkQueues().isEmpty()) {
            System.out.println("Unfinished tasks:");
            for (WorkQueue wq : nw.getWorkQueues()) {
                for (Task task : wq.getTasks()) {
                    System.out.println(nw.taskToString(task));
                }
            }
            System.out.println("End of unfinished tasks");
        }
    }

    private static void printTruckInformation(EvolutionFitness sol, Network nw) {
        System.out.println("Truck ID\tDeadhead Time\tHeavy Load Time\tWait Time");
        for (Truck truck : nw.getTrucks()) {
            TruckRoute route = sol.getRoutes().get(truck.getTruckIndex()-1);
            sol.setTotalTravellingTime(sol.getTotalTravellingTime() + route.getDhTime() + route.getHlTime());
            sol.setTotalTruckWait(sol.getTotalTruckWait() + route.getWaitTime());
            System.out.println(truck.getTruckID() + '\t' + route.getDhTime() + '\t'
                    + route.getHlTime() + '\t' + route.getWaitTime());
        }
    }

    private static void printNodeAccessPointInformation(EvolutionFitness sol, Network nw) {
//        long ftSum = 0;
//        long btSum = 0;
//        long ltSum = 0;
//        for (Node node : nw.getNodes()) {
//            if (node.getType() != NodeType.QUAY_CRANE || sol.getAccPoints().get(node.getNode_index()).isEmpty()) {
//                continue;
//            }
//            System.out.println("Access points at " + node.getName() + ":");
//            long freeTime = 0;
//            long busyTime = 0;
//            long prevFinishTime = 0;
//            for (AccessPoint ap : sol.getAccPoints().get(node.getNode_index())) {
//                if (prevFinishTime != ap.getTimeStart()) {
//                    System.out.print("*");
//                }
//                System.out.print(ap.getTimeStart() + '-' + ap.getTimeFinish() + '('
//                        + ap.getTask().getContainerID() + ':' + ap.getTask().getSrcNode().getName() + '-' + ap.getTask().getDstNode().getName());
//                if (ap.getTask().getMergedTask() != null) {
//                    System.out.print("  " + ap.getTask().getMergedTask().getContainerID() + ':'
//                            + ap.getTask().getMergedTask().getSrcNode().getName() + '-' + ap.getTask().getMergedTask().getDstNode().getName());
//                }
//                System.out.print(") ");
//                busyTime += (ap.getTimeFinish() - ap.getTimeStart());
//                if (prevFinishTime != 0) {
//                    freeTime += ap.getTimeStart() - prevFinishTime;
//                }
//                prevFinishTime = ap.getTimeFinish();
//            }
//            System.out.println("\nNode busy time: " + busyTime + " seconds, Total time: " + (freeTime + busyTime) + " seconds");
//            ftSum += freeTime;
//            btSum += busyTime;
//            ltSum += sol.getAccPoints().get(node.getNode_index()).get(sol.getAccPoints().get(node.getNode_index()).size() - 1).getTimeFinish();
//        }
//        System.out.println("Total crane operation end time: " + ltSum);
//        sol.setTotalCraneWait(ftSum);
//        System.out.println("Total crane busy rate: " + calculateBusyRate(btSum, ftSum));
        System.out.println("Total travelling time: " + sol.getTotalTravellingTime());
        FinalTime = currentSimulationTime;
        System.out.println("Final travelling time: " + currentSimulationTime);
        System.out.println("Result data: " + sol.getTotalTravellingTime() + '\t' + travellingTimeUpperbound() + '\t' + currentSimulationTime);
    }

    private static double calculateBusyRateAndReturn(EvolutionFitness sol) {
        double ftSum = sol.getTotalCraneWait();
        double btSum = sol.getTotalTravellingTime() - ftSum;
        return btSum / (ftSum + btSum);
    }

    private static double calculateBusyRate(long btSum, long ftSum) {
        return (double) btSum / (ftSum + btSum);
    }

    private static long travellingTimeUpperbound() {
        long totalTime = 0;
        for (Task task : finishedTasks) {
            if (task.getMergedTask() != null && !task.getMergedTask().getSrcNode().equals(task.getSrcNode())) {
                totalTime += nw.getTravellingTimePassingNodes(task.getSrcNode(), task.getMergedTask().getSrcNode());
                totalTime += nw.getTravellingTimePassingNodes(task.getMergedTask().getSrcNode(), task.getMergedTask().getDstNode());
            } else {
                totalTime += nw.getTravellingTimePassingNodes(task.getSrcNode(), task.getDstNode());
            }
        }
        totalTime *= 2;
        System.out.println("Total travelling time (estimated using task data): " + totalTime);
        return totalTime;
    }

    private static void printEventType(PortEvent pe) {
        if (outputEventType) {
            switch (pe.getType()) {
                case TASK_IMPORT:
                    System.out.println("TASK_IMPORT");
                    break;
                case YC_WORKING_POINT_ARRIVAL:
                    System.out.println("YC_WORKING_POINT_ARRIVAL");
                    break;
                case QC_WORKING_POINT_ARRIVAL:
                    System.out.println("SC_WORKING_POINT_ARRIVAL");
                    break;
                case CRANE_PUTTING_TO_VEHICLE_SUCCESS:
                    System.out.println("CRANE_PUTTING_TO_VEHICLE_SUCCESS");
                    break;
                case CRANE_UNLOADING_FROM_VEHICLE_SUCCESS:
                    System.out.println("CRANE_UNLOADING_FROM_VEHICLE_SUCCESS");
                    break;
                case CRANE_UNLOADING_FROM_VEHICLE_FINISH:
                    System.out.println("CRANE_UNLOADING_FROM_VEHICLE_FINISH");
                    break;
                case CRANE_PUTTING_TO_VEHICLE_FINISH:
                    System.out.println("CRANE_PUTTING_TO_VEHICLE_FINISH");
                    break;
                default:
                    System.out.println("undefined");
            }
        }
    }

    private static void handleDeadheadToQuayCraneInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        truck.setStatus(TruckStatus.TRUCK_DEADHEADING);
        truck.setCurrentTask(ins.getTask());
        truck.getCurrentTask().setDispatchLocation(truck.getCurrentPosition());
        truck.getCurrentTask().setDispatchTime(currentSimulationTime);

        // Calculate arrival time at ship crane
        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), ins.getTask().getSrcNode());
        long arrivalTime = currentSimulationTime + travelTime;

        // Create ship crane arrival event
        PortEvent sca = new PortEvent();
        sca.setTime(arrivalTime);
        sca.setType(EventType.QC_WORKING_POINT_ARRIVAL);
        sca.setTruck(truck);
        sca.setEventLocation(ins.getTask().getSrcNode());
        addEvent(sca);
    }

    private static void handleLoadedToQuayCraneInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);

        if(ins.getTask() != truck.getCurrentTask()){
            truck.setCurrentTask(ins.getTask());
        }

        Node destination = ins.getTask().getDstNode();
        if(ins.getTask().getSrcNode().getType()==NodeType.QUAY_CRANE)  destination = ins.getTask().getSrcNode();

        destination.getQueueTasks().add(destination.getQueueTasks().size(),ins.getTask());
        destination.getTruckQueue().add(truck);
        //destination.getQueueTasks().remove(ins.getTask());

        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), destination);
        long arrivalTime = currentSimulationTime + travelTime;

        PortEvent sca = new PortEvent();
        sca.setTime(arrivalTime);
        sca.setType(EventType.QC_WORKING_POINT_ARRIVAL);
        sca.setTruck(truck);
        sca.setEventLocation(destination);
        addEvent(sca);
    }

    private static void handleDeadheadToYardInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        truck.setStatus(TruckStatus.TRUCK_DEADHEADING);
        truck.setCurrentTask(ins.getTask());
        truck.getCurrentTask().setDispatchLocation(truck.getCurrentPosition());
        truck.getCurrentTask().setDispatchTime(currentSimulationTime);

        // Calculate arrival time at yard
        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), ins.getTask().getSrcNode());
        long arrivalTime = currentSimulationTime + travelTime;

        // Create yard arrival event
        PortEvent yae = new PortEvent();
        yae.setTime(arrivalTime);
        yae.setType(EventType.YC_WORKING_POINT_ARRIVAL);
        yae.setTruck(truck);
        yae.setEventLocation(ins.getTask().getSrcNode());
        addEvent(yae);
    }

    private static void handleLoadedToYardInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);

        if(ins.getTask() != truck.getCurrentTask()){
            truck.setCurrentTask(ins.getTask());
        }

        Node destination = ins.getTask().getDstNode();
        if(ins.getTask().getSrcNode().getType() == NodeType.YARD_CRANE) destination = ins.getTask().getSrcNode();

        destination.getQueueTasks().add(destination.getQueueTasks().size(),ins.getTask());
//        destination.getTruckQueue().add(truck);

        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), destination);
        long arrivalTime = currentSimulationTime + travelTime;

        PortEvent yae = new PortEvent();
        yae.setTime(arrivalTime);
        yae.setType(EventType.YC_WORKING_POINT_ARRIVAL);
        yae.setTruck(truck);
        yae.setEventLocation(destination);
        addEvent(yae);
    }

    private static void handlePutContainerToTruckInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        Task task = ins.getTask();
        Node src = task.getSrcNode();

        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);
        truck.setCurrentTask(task);

        task.setDispatchLocation(truck.getCurrentPosition());
        task.setDispatchTime(currentSimulationTime);

        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), src);
        long arrivalTime = currentSimulationTime + travelTime;


        PortEvent sca = new PortEvent();
        sca.setTime(arrivalTime);
        sca.setType(EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS);
        sca.setTruck(truck);
        sca.setEventLocation(src);
        addEvent(sca);
    }

    private static void handleTakeContainerFromTruckFinishInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        Task task = truck.getCurrentTask();
        Node dst = task.getDstNode();

        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);

        dst.getQueueTasks().remove(task);

        long unloadTime = dst.getAverageUnloadTime();
        long arrivalTime = currentSimulationTime + unloadTime;

        PortEvent yae = new PortEvent();
        yae.setTime(arrivalTime);
        yae.setType(EventType.CRANE_UNLOADING_FROM_VEHICLE_FINISH);
        yae.setTruck(truck);
        yae.setEventLocation(dst);
        addEvent(yae);
    }

    private static void handlePutContainerToTruckFinishInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        Task task = ins.getTask();
        Node src = task.getSrcNode();

        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);
        truck.setCurrentTask(task);
        src.getQueueTasks().remove(task);

        task.setDispatchLocation(truck.getCurrentPosition());
        task.setDispatchTime(currentSimulationTime);

        long loadTime = src.getAverageLoadTime();
        long arrivalTime = currentSimulationTime + loadTime;

        PortEvent sca = new PortEvent();
        sca.setTime(arrivalTime);
        sca.setType(EventType.CRANE_PUTTING_TO_VEHICLE_FINISH);
        sca.setTruck(truck);
        sca.setEventLocation(src);
        addEvent(sca);
    }

    private static void handleTakeContainerFromTruckInstruction(Instruction ins) {
        Truck truck = ins.getTruck();
        Task task = truck.getCurrentTask();
        Node dst = task.getDstNode();

        truck.setStatus(TruckStatus.TRUCK_XSHIPPING);

        dst.getQueueTasks().remove(task);

        long travelTime = nw.getTravellingTimePassingNodes(truck.getCurrentPosition(), dst);
        long arrivalTime = currentSimulationTime + travelTime;

        PortEvent yae = new PortEvent();
        yae.setTime(arrivalTime);
        yae.setType(EventType.CRANE_UNLOADING_FROM_VEHICLE_SUCCESS);
        yae.setTruck(truck);
        yae.setEventLocation(dst);
        addEvent(yae);
    }

    private static void handleHoldInstruction(Instruction i) {
        Truck truck = i.getTruck();
        Node destination = i.getDst();
        Task task = truck.getCurrentTask();

        if(task!=null) task.setDispatchTime(currentSimulationTime);

        if (destination != null) {
            destination.getTruckQueue().add(truck);
            if (destination.getCurrentTask()!=truck.getCurrentTask() && destination == truck.getCurrentTask().getSrcNode()){
                truck.setStatus(TruckStatus.TRUCK_XSHIPPING);
                int maxIndex = -1;
                long maxTime = Long.MIN_VALUE;

                for (int j = 0; j < eventNum; j++) {
                    PortEvent currentEvent = emuEvents.get(j);
                    if (currentEvent.getType() == EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS &&
                            currentEvent.getEventLocation() == destination) {
                        long currentTime = currentEvent.getTime();
                        if (currentTime > maxTime) {
                            maxTime = currentTime;
                            maxIndex = j;
                        }
                    }
                }

                if(maxIndex==-1){
                    for (int j = 0 ; j<eventNum; j++){
                        if ((emuEvents.get(j).getType()== EventType.QC_WORKING_POINT_ARRIVAL)&&emuEvents.get(j).getEventLocation() ==destination){
                            long waitTime = emuEvents.get(j).getTime() + destination.getAverageLoadTime();

                            task.setDispatchTime(waitTime);

                            PortEvent yae = new PortEvent();
                            yae.setTime(waitTime);
                            yae.setType(EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS);
                            yae.setTruck(truck);
                            yae.setEventLocation(destination);
                            addEvent(yae);
                        }

                    }
                }

                if (maxIndex != -1) {
                    PortEvent maxEvent = emuEvents.get(maxIndex);
                    long waitTime = maxEvent.getTime() + destination.getAverageLoadTime();

                    task.setDispatchTime(waitTime);

                    PortEvent newEvent = new PortEvent();
                    newEvent.setTime(waitTime);
                    newEvent.setType(EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS);
                    newEvent.setTruck(truck);
                    newEvent.setEventLocation(destination);
                    addEvent(newEvent);
                }


            } else if(destination.getCurrentTask()!=truck.getCurrentTask() && destination == truck.getCurrentTask().getDstNode()){
                truck.setStatus(TruckStatus.TRUCK_XSHIPPING);
                int maxIndex = -1;
                long maxTime = Long.MIN_VALUE;

                for (int j = 0; j < eventNum; j++) {
                    PortEvent currentEvent = emuEvents.get(j);
                    if (currentEvent.getType() == EventType.CRANE_UNLOADING_FROM_VEHICLE_SUCCESS &&
                            currentEvent.getEventLocation() == destination) {
                        long currentTime = currentEvent.getTime();
                        if (currentTime > maxTime) {
                            maxTime = currentTime;
                            maxIndex = j;
                        }
                    }
                }

                if (maxIndex == -1) {
                    for (int j = 0 ; j<eventNum; j++){
                        if ((emuEvents.get(j).getType()== EventType.YC_WORKING_POINT_ARRIVAL)&&emuEvents.get(j).getEventLocation() ==destination){
                            long waitTime = emuEvents.get(j).getTime() + destination.getAverageUnloadTime();

                            task.setDispatchTime(waitTime);

                            PortEvent yae = new PortEvent();
                            yae.setTime(waitTime);
                            yae.setType(EventType.CRANE_UNLOADING_FROM_VEHICLE_SUCCESS);
                            yae.setTruck(truck);
                            yae.setEventLocation(destination);
                            addEvent(yae);
                        }
                    }
                }

                if (maxIndex != -1) {
                    PortEvent maxEvent = emuEvents.get(maxIndex);
                    long waitTime = maxEvent.getTime() + destination.getAverageUnloadTime();

                    task.setDispatchTime(waitTime);

                    PortEvent newEvent = new PortEvent();
                    newEvent.setTime(waitTime);
                    newEvent.setType(EventType.CRANE_UNLOADING_FROM_VEHICLE_SUCCESS);
                    newEvent.setTruck(truck);
                    newEvent.setEventLocation(destination);
                    addEvent(newEvent);
                }
            }
        }

        if (outputInstructionHandling) {
            System.out.println("\temulator:\ttruck\t" + truck.getTruckID() + "\twait at\t" + truck.getCurrentPosition().getName());
        }
    }

    private static void handleInstruction(Instruction i) {
        switch (i.getInsCode()) {
            case DEADHEAD_TO_YARD_CRANE:
                handleDeadheadToYardInstruction(i);
                break;
            case LOADED_TO_YARD_CRANE:
                handleLoadedToYardInstruction(i);
                break;
            case DEADHEAD_TO_QUAY_CRANE:
                handleDeadheadToQuayCraneInstruction(i);
                break;
            case LOADED_TO_QUAY_CRANE:
                handleLoadedToQuayCraneInstruction(i);
                break;
            case PUT_CONTAINER_TO_TRUCK:
                handlePutContainerToTruckInstruction(i);
                break;
            case TAKE_CONTAINER_FROM_TRUCK:
                handleTakeContainerFromTruckInstruction(i);
                break;
            case PUT_CONTAINER_TO_TRUCK_FINISH:
                handlePutContainerToTruckFinishInstruction(i);
                break;
            case TAKE_CONTAINER_FROM_TRUCK_FINISH:
                handleTakeContainerFromTruckFinishInstruction(i);
                break;
            case TO_DEPOT:
                break;
            case HOLD:
                handleHoldInstruction(i);
                break;
        }
        System.out.println();
    }

    private static void triggerTaskImportEvent(PortEvent event_i) {
        nw.mergeToQueue(nw.getWorkQueues(), event_i.getWorkQueues());

        int wqsize = nw.getWorkQueues().size();
        int trcksize = nw.getTrucks().size();
        for (int i = 0; i < wqsize; i++) {
            for (int j = 0; j < trcksize; j++) {
                nw.getWorkQueues().get(i).getTruckBindings().add(nw.getTrucks().get(j));
            }
            nw.getWorkQueues().get(i).setMaxTruck(nw.getTrucks().size());
            nw.getWorkQueues().get(i).getTasks().addAll(nw.findTaskByNode(nw.getWorkQueues().get(i).getQueueName()));
        }

        for (int i = 0; i < nw.getWorkQueues().size(); i++) {
            WorkQueue qi = nw.getWorkQueues().get(i);
            for (int j = 0; j < qi.getTasks().size(); j++) {
                Task task = qi.getTasks().get(j);
                task.setSrcArrival(task.getSrcAp().getTimeFinish());
                task.setDstArrival(task.getDstAp().getTimeFinish());
            }
        }

        System.out.println("\nTask list:\n");
        for (int i = 0; i < nw.getWorkQueues().size(); i++) {
            WorkQueue qi = nw.getWorkQueues().get(i);
            System.out.println("work queue: " + qi.getQueueName() + '\n');
            for (int j = 0; j < qi.getTasks().size(); j++) {
                System.out.println(nw.taskToString(qi.getTasks().get(j)) + '\n');
            }
        }
        System.out.println("End of task list\n");

        initialiseContainerQueues();

        System.out.println("Truck list:\n");
        for (int i = 0; i < nw.getTrucks().size(); i++) {
            Truck tr = nw.getTrucks().get(i);
            System.out.println(tr.getTruckID() + "\t CurrentPosition: " + tr.getCurrentPosition().getName() + "\n");
        }
        System.out.println("End of truck list");
    }

    private static void triggerYardCraneWorkingPointArrivalEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        updateTruckTravellingTime(truck,
                nw.getTravellingTimePassingNodes(truck.getCurrentPosition(),event_i.getEventLocation()));


        truck.setCurrentPosition(event_i.getEventLocation());

        if (outputEventTriggerInfo) {
            System.out.println("\ttruck\t" + truck.getTruckID() + "\treached yard crane\t" + truck.getCurrentPosition().getName());
        }

        Task currentTask = truck.getCurrentTask();
        Node eventLocation = event_i.getEventLocation();
        if (eventLocation.equals(currentTask.getSrcNode())) {
            currentTask.setSrcArrival(event_i.getTime());
        } else if (eventLocation.equals(currentTask.getDstNode())) {
            currentTask.setDstArrival(event_i.getTime());
        } else if (currentTask.getMergedTask() != null && currentTask.getMergedTask().getSrcNode().equals(eventLocation)) {
            currentTask.getMergedTask().setSrcArrival(event_i.getTime());
        } else {
            currentTask.getMergedTask().setDstArrival(event_i.getTime());
        }

    }

    private static void triggerQuayCraneWorkingPointArrivalEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        Node currentPos = truck.getCurrentPosition();
        Node eventLocation = event_i.getEventLocation();

        updateTruckTravellingTime(truck, nw.getTravellingTimePassingNodes(currentPos,eventLocation));

        truck.setCurrentPosition(eventLocation);

        if (outputEventTriggerInfo) {
            System.out.println("\ttruck\t" + truck.getTruckID() + "\treached quay crane\t" + truck.getCurrentPosition().getName());
        }

        Task currentTask = truck.getCurrentTask();
        if (eventLocation.equals(currentTask.getSrcNode())) {
            currentTask.setSrcArrival(event_i.getTime());
        } else if (eventLocation.equals(currentTask.getDstNode())) {
            currentTask.setDstArrival(event_i.getTime());
        } else if (currentTask.getMergedTask() != null && currentTask.getMergedTask().getSrcNode().equals(eventLocation)) {
            currentTask.getMergedTask().setSrcArrival(event_i.getTime());
        } else {
            currentTask.getMergedTask().setDstArrival(event_i.getTime());
        }
    }

    private static void triggerCranePuttingToVehicleSuccessEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        if (truck.getCurrentTask().getQuayNode() == event_i.getEventLocation()) {
            removeFromContainerQueue(truck.getCurrentTask());


            StringBuilder sb = new StringBuilder();
            sb.append("\ttruck: \t").append(truck.getTruckID());
            if (event_i.getTime() == truck.getCurrentTask().getSrcArrival()&&event_i.getTime() == truck.getCurrentTask().getDispatchTime()){
                sb.append("\ttoke: \t").append(truck.getCurrentTask().getContainerID());
            }else{
                if(event_i.getEventLocation() == truck.getCurrentTask().getSrcNode()&&truck.getCurrentTask().getMergedTask()!=null
                && event_i.getTime() != truck.getCurrentTask().getDispatchTime()) {
                    sb.append("\ttoke: \t").append(truck.getCurrentTask().getMergedTask().getContainerID());
                }
                else sb.append("\ttoke: \t").append(truck.getCurrentTask().getContainerID());
            }

            if (outputEventTriggerInfo) {
                System.out.println(sb);
            }

//            event_i.getEventLocation().setCurrentTask(null);
        }
    }

    private static void triggerCraneUnloadingFromVehicleSuccessEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        if (truck.getCurrentTask().getQuayNode() == event_i.getEventLocation()) {
            removeFromContainerQueue(truck.getCurrentTask());

            //finishedTasks.add(truck.getCurrentTask());
            //truck.setCurrentTask(null);
            //truck.setStatus(TruckStatus.TRUCK_AVAILABLE);

            event_i.getEventLocation().setCurrentTask(null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\ttruck\t").append(truck.getTruckID());
        sb.append("\tunloaded\t").append(truck.getCurrentTask().getContainerID());

        if (outputEventTriggerInfo) {
            System.out.println(sb);
        }
    }

    private static void triggerEvent(PortEvent event_i) {
        System.out.println("\n@" + event_i.getTime() + "\t");
        currentSimulationTime = event_i.getTime();
        printEventType(event_i);

        switch (event_i.getType()) {
            case TASK_IMPORT:
                triggerTaskImportEvent(event_i);
                break;
            case YC_WORKING_POINT_ARRIVAL:
                triggerYardCraneWorkingPointArrivalEvent(event_i);
                break;
            case QC_WORKING_POINT_ARRIVAL:
                triggerQuayCraneWorkingPointArrivalEvent(event_i);
                break;
            case CRANE_PUTTING_TO_VEHICLE_SUCCESS:
                triggerCranePuttingToVehicleSuccessEvent(event_i);
                break;
            case CRANE_UNLOADING_FROM_VEHICLE_SUCCESS:
                triggerCraneUnloadingFromVehicleSuccessEvent(event_i);
                break;
            case CRANE_PUTTING_TO_VEHICLE_FINISH:
                triggerCranePuttingToVehicleFinishEvent(event_i);
                break;
            case CRANE_UNLOADING_FROM_VEHICLE_FINISH:
                triggerCraneUnloadingFromVehicleFinishEvent(event_i);
                break;
        }

        System.out.println("\n");
    }

    private static void triggerCraneUnloadingFromVehicleFinishEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        finishedTasks.add(truck.getCurrentTask());
        if (truck.getCurrentTask().getQuayNode() == event_i.getEventLocation()) {
            removeFromContainerQueue(truck.getCurrentTask());

            truck.setCurrentTask(null);
            truck.setStatus(TruckStatus.TRUCK_AVAILABLE);

            event_i.getEventLocation().setCurrentTask(null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\ttruck\t").append(truck.getTruckID());
        sb.append("\tunloaded\t").append(truck.getCurrentTask().getContainerID());
        sb.append("\t*** unlocked crane\t").append(event_i.getEventLocation().getName());

        if (outputEventTriggerInfo) {
            System.out.println(sb);
        }

    }

    private static void triggerCranePuttingToVehicleFinishEvent(PortEvent event_i) {
        Truck truck = event_i.getTruck();
        if (truck.getCurrentTask().getSrcNode() == event_i.getEventLocation()) {
            removeFromContainerQueue(truck.getCurrentTask());

            StringBuilder sb = new StringBuilder();
            sb.append("\ttruck: \t").append(truck.getTruckID());
            if (event_i.getTime()== truck.getCurrentTask().getDispatchTime()+truck.getCurrentTask().getLoadTime()){
                sb.append("\ttoke: \t").append(truck.getCurrentTask().getContainerID());
            }else if (truck.getCurrentTask().getMergedTask()!=null){
                sb.append("\ttoke: \t").append(truck.getCurrentTask().getMergedTask().getContainerID());
            }else {
                sb.append("\ttoke: \t").append(truck.getCurrentTask().getContainerID());
            }
            sb.append("\t*** unlocked crane: \t").append(event_i.getEventLocation().getName());

            if (outputEventTriggerInfo) {
                System.out.println(sb);
            }

            event_i.getEventLocation().setCurrentTask(event_i.getTruck().getCurrentTask());
        }
    }

    // 更新卡车行驶时间
    private static void updateTruckTravellingTime(Truck t, long time) {
        if (outputTravellingTime)
            System.out.println("travelled " + time + " seconds");
        TruckRoute r = emuSolution.getRoutes().get(t.getTruckIndex()-1);
        if (t.getStatus() == TruckStatus.TRUCK_XSHIPPING) {
            r.setHlTime(r.getDhTime()+ time);
        } else {
            r.setDhTime(r.getDhTime()+ time);
        }
    }

    // 添加事件
    private static void addEvent(PortEvent pe) {
        if (outputNextEventTime)
            System.out.println("\tnext event time " + pe.getTime());

        if (pe.getTime() < currentSimulationTime) {
            System.out.println("wrong event time " + pe.getTime());
            return;
        }

        for (int i = 0; i < emuEvents.size(); i++) {
            PortEvent existingEvent = emuEvents.get(i);
            if (pe.getTime() < existingEvent.getTime()) {
                emuEvents.add(i, pe);
                checkEventPrecedence(pe, i);
                return;
            }
            if (pe.getTime() == existingEvent.getTime()) {
                if ((existingEvent.getType() == EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS ||
                        existingEvent.getType() == EventType.CRANE_UNLOADING_FROM_VEHICLE_SUCCESS) &&
                        pe.getType() != EventType.TASK_IMPORT &&
                        existingEvent.getTruck().equals(pe.getTruck()) &&
                        existingEvent.getEventLocation().equals(pe.getEventLocation())) {
                    emuEvents.add(i + 1, pe);
                    return;
                } else if (existingEvent.getType() == EventType.CRANE_PUTTING_TO_VEHICLE_SUCCESS &&
                        pe.getType() == EventType.CRANE_PUTTING_TO_VEHICLE_FINISH &&
                        existingEvent.getEventLocation().equals(pe.getEventLocation())){
                    emuEvents.add(i, pe);
                    return;
                } else if (existingEvent.getType() == pe.getType()) {
                    emuEvents.add(i, pe);
                    return;
                }
            }
        }
        emuEvents.add(pe);
    }

    private static void checkEventPrecedence(PortEvent pe, int currentIndex) {
        if (pe.getType() != EventType.TASK_IMPORT) {
            Truck trki = pe.getTruck();
            for (int j = currentIndex + 1; j < emuEvents.size(); j++) {
                PortEvent nextEvent = emuEvents.get(j);
                if (nextEvent.getType() != EventType.TASK_IMPORT) {
                    Truck trkj = nextEvent.getTruck();
                    if (trki.equals(trkj)) {
                        System.out.println("sorry event precedence for " + trki.getTruckID() + " is wrong");
                    }
                }
            }
        }
    }


}



