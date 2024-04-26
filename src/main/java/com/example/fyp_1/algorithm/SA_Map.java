package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.Emulator.Emulator;
import com.example.fyp_1.EvolutionFitness;

import java.util.ArrayList;
import java.util.List;

public class SA_Map {
    private List<Rule> ruleset;
    private List<Feature> features;

    public List<Rule> getRuleset() {
        return ruleset;
    }

    public void setRuleset(List<Rule> ruleset) {
        this.ruleset = ruleset;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<AlgActionIndex> getActions() {
        return actions;
    }

    public void setActions(List<AlgActionIndex> actions) {
        this.actions = actions;
    }

    public static StepSolveFunction[] getStepSolvFuncs() {
        return stepSolvFuncs;
    }

    public static void setStepSolvFuncs(StepSolveFunction[] stepSolvFuncs) {
        SA_Map.stepSolvFuncs = stepSolvFuncs;
    }

    private List<AlgActionIndex> actions;

    private static StepSolveFunction[] stepSolvFuncs = {
            (sol, truck, ft, queue) -> InsertMinCraneWait.stepSolveDynamic(sol, truck, ft, queue),
    };

    interface StepSolveFunction {
        boolean stepSolve(EvolutionFitness sol, Truck truck, Feature ft,String queue);
    }



    public SA_Map() {
        this.ruleset = new ArrayList<>();
        this.features = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public void addFeature(Feature ft, AlgActionIndex i) {
        features.add(ft);
        actions.add(i);
    }

    public void removeFeature(int index) {
        if (index >= 0 && index < features.size()) {
            features.remove(index);
            actions.remove(index);
        }
    }



    public int getAction(Feature ft) {
        int min_i = -1;
        if (features.isEmpty()) {
            min_i = AlgActionIndex.MIN_CWAIT.ordinal();
        } else {
            double min_val = Double.MAX_VALUE;
            for (int i = 0; i < features.size(); i++) {
                double val = Feature.compare(ft, features.get(i));
                if (min_i == -1 || min_val < val) {
                    min_i = i;
                    min_val = val;
                }
            }
        }
        return min_i;
    }


    public void solveAll(int algIndex, EvolutionFitness sol, Feature ft, long timeLimit) {
        Network nw = sol.getNw();
        boolean oneTruckAssigned = false;
        int ri = 0;
        int rSize = sol.getRoutes().size();

        while (sol.numUnfinished() > 0 && rSize > 0) {
            if (ri >= rSize) {
                if (!oneTruckAssigned) break;
                ri = 0;
                oneTruckAssigned = false;
            }

            if (timeLimit > 0) {
                if (sol.getRoutes().get(ri).getTasks().isEmpty()) {
                    if (sol.getCurrentTime() > timeLimit) {
                        ri++;
                        continue;
                    }
                } else if (sol.getRoutes().get(ri).getTasks().get(sol.getRoutes().get(ri).getTasks().size() - 1).getDstAp().getTimeFinish() > timeLimit) {
                    ri++;
                    continue;
                }
            }

            String resultContainerId = null;
            if (!stepSolvFuncs[algIndex].stepSolve(sol, nw.getTrucks().get(ri++), ft, resultContainerId))
                break;
            else
                oneTruckAssigned = true;
        }

        long[] busy = new long[0];
        long[] idle = new long[0];
        long[] makespan = new long[0];
        long[] totalMakespan = new long[0];
        int[] apCount = new int[0];
        long[] wait = new long[0];
        long[] travellingTime = new long[0];
        int[] taskCount = new int[0];

        sol.evaluateAllQuayCranes(busy, idle, wait, makespan, totalMakespan, apCount);
        System.out.println("busy time: " + busy +
                "\nidle time: " + idle +
                "\nmakespan: " + makespan +
                "\nap_count: " + apCount);

        sol.evaluateAllRoute(travellingTime, taskCount);
        System.out.println("wait time: " + wait + "\nTravelling_time: " + travellingTime + "\ntask count: " + taskCount);
    }



    public double fitness(Network nw,Boolean isSameSrcTask) {
        Network emulation_nw = cloneNetwork(nw);
        Emulator emulator = new Emulator();
        Emulator.startEmulator(emulation_nw,Emulator.getEmuEvents(),isSameSrcTask);
        return Emulator.emuSolution.getTotalCraneWait();
    }

    // Helper method to clone the network
    private Network cloneNetwork(Network nw) {
        Network emulation_nw = new Network();
        emulation_nw.setNodes(nw.getNodes());
        emulation_nw.setTruckAreaNodes(nw.getTruckAreaNodes());
        emulation_nw.setYardCraneNodes(nw.getYardCraneNodes());
        emulation_nw.setQuayCraneNodes(nw.getQuayCraneNodes());
        emulation_nw.setTasks(nw.getTasks());
        emulation_nw.setNodesTimes(nw.getNodesTimes());
        emulation_nw.setTrucks(nw.getTrucks());

        // Clone work queues
        List<WorkQueue> workQueues = new ArrayList<>();
        for (WorkQueue queue : nw.getWorkQueues()) {
            WorkQueue clonedQueue = new WorkQueue();
            clonedQueue.setTaskMerging(queue.getTaskMerging());
            clonedQueue.setPow(queue.getPow());
            clonedQueue.setQueueName(queue.getQueueName());
            clonedQueue.setTruckBindings(queue.getTruckBindings());
            clonedQueue.setMaxTruck(queue.getMaxTruck());

            // Clone tasks
            ArrayList<Task> clonedTasks = new ArrayList<>();
            for (Task task : queue.getTasks()) {
                clonedTasks.add(task.copyTask());
            }
            clonedQueue.setTasks(clonedTasks);

            workQueues.add(clonedQueue);
        }
        emulation_nw.setWorkQueues(workQueues);

        return emulation_nw;
    }
}