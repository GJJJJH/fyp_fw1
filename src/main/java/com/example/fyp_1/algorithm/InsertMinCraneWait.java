package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.EvolutionFitness;

import java.util.List;

public class InsertMinCraneWait {

    public static boolean stepSolveDynamic(EvolutionFitness sol, Truck tr, Feature ft, String containerId) {
        Network nw = sol.getNw();
        int bestQueue = -1;
        int minBuffer = 0;
        int maxMinimumTruck = 0;
        long minSrcArrivalTime = 0;

        for (int i = 0; i < sol.getWorkQueues().size(); i++) {
            WorkQueue tqi = sol.getWorkQueues().get(i);

            if (tqi.getTasks().size() == 0 || !tqi.verifyBinding(tr))
                continue;

            int buffer = 0;
            int bufferSup = 0;
            int minTruckSatisfactory = 0;

            for (int j = 0; j < nw.getQuayCraneNodes().size(); j++) {
                if (nw.getQuayCraneNodes().get(j).getName().equals(tqi.getQueueName())) {
                    bufferSup = ft.getBuffer_sup().get(ft.getBuffer_sup().size() - 1).get(j);
                    buffer = ft.getBuffer_sup().get(0).get(j) - ft.getBuffer_demand().get(0).get(j);
                    minTruckSatisfactory = Math.max(0 - bufferSup, 0);
                    break;
                }
            }

            if (tqi.getMaxTruck() != -1 && bufferSup > tqi.getMaxTruck())
                continue;


            long srcArrivalTime = sol.getNw().getTravellingTimePassingNodes(tr.getCurrentPosition(),tqi.getTasks().get(0).getSrcNode());

            if (bestQueue == -1 || minTruckSatisfactory > maxMinimumTruck) {
                maxMinimumTruck = minTruckSatisfactory;
                minBuffer = buffer;
                minSrcArrivalTime = srcArrivalTime;
                bestQueue = i;
            } else if (minTruckSatisfactory == maxMinimumTruck) {
                if (buffer < minBuffer) {
                    minBuffer = buffer;
                    minSrcArrivalTime = srcArrivalTime;
                    bestQueue = i;
                } else if (buffer == minBuffer && srcArrivalTime < minSrcArrivalTime) {
                    minSrcArrivalTime = srcArrivalTime;
                    bestQueue = i;
                }
            }
        }

        if (bestQueue != -1) {
            Node sc = sol.getWorkQueues().get(bestQueue).getTasks().get(0).getQuayNode();
            if (sc != null) {
                for (int i = 0; i < nw.getQuayCraneNodes().size(); i++) {
                    if (sc.equals(nw.getQuayCraneNodes().get(i))) {
                        ft.getBuffer_sup().get(0).set(i, ft.getBuffer_sup().get(0).get(i) + 1);
                        break;
                    }
                }
            }

            containerId.indent(Integer.parseInt(sol.getWorkQueues().get(bestQueue).getTasks().get(0).getContainerID()));
            return true;
        } else {
            containerId.indent(Integer.parseInt("NOT_FOUND"));
            return false;
        }
    }
}
