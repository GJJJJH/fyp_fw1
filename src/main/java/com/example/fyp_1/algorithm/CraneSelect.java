package com.example.fyp_1.algorithm;

import com.example.fyp_1.DataStructure.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CraneSelect {

    static boolean isSrcTaskFiniesh = false;
    public static void printTruckInfo(List<TruckFreePoint> tfps) {
        System.out.println("Truck\tLocation\tTime");
        for (TruckFreePoint tfp : tfps) {
            System.out.printf("%s\t%s\t%s%n", tfp.getTr().getTruckID(), tfp.getLocation().getName(), tfp.getTime());
        }
    }

    public static void calcFp(Network nw, TruckFreePoint tfp) {
        if (tfp.getTr().getCurrentTask() == null) {
            //tfp.setLocation(tfp.getTr().getCurrentPosition());
            tfp.setTime(0);
            return;
        }

        Task t = tfp.getTr().getCurrentTask();
        tfp.setLocation(t.getDstNode());

        int status = t.status();
        switch (status) {
            case 0:
                break;
            case 1:
                if (t.getDispatchLocation() != null) {
                    tfp.setTime(nw.getTravellingTimePassingNodes(t.getDispatchLocation(), t.getSrcNode()));
                } else if (tfp.getTr().getCurrentPosition() != null) {
                    tfp.setTime(nw.getTravellingTimePassingNodes(tfp.getTr().getCurrentPosition(), t.getSrcNode()));
                } else {
                    System.err.println("calcFp: No current position and no dispatch location for truck!");
                    tfp.setTime(0);
                }
                break;
            case 2:
                if (status < 2) {

                } else {
                    tfp.setTime(0);
                }
                break;
            case 3:
                if (status < 3) {
                    tfp.setTime(tfp.getTime() + t.getSrcNode().getAverageLoadTime());
                } else {
                    tfp.setTime(0);
                }
                break;
            case 4:
                if (status < 4) {
                    tfp.setTime(tfp.getTime() + nw.getTravellingTimePassingNodes(t.getSrcNode(), t.getDstNode()));
                } else {
                    tfp.setTime(0);
                }
                break;
            case 5:
                if (status < 5) {
                } else {
                    tfp.setTime(0);
                }
                break;
            case 6:
                if (status < 6) {
                    tfp.setTime(tfp.getTime() + t.getDstNode().getAverageUnloadTime());
                } else {
                    tfp.setTime(0);
                }
                break;
            case 7:
                if (status == 7) {
                    tfp.setTime(0);
                }
                break;
            default:
                System.err.println("Unexpected status code when calculating tfps!");
                break;
        }
    }

    public static void calcFPs(Network network, List<TruckFreePoint> tfps,Node nowNode) {
        tfps.clear();
        long currentTime = Dispatcher.timeProvider.getCurrentTime();
        for (Truck tr : network.getTrucks()) {
            TruckFreePoint tfp = new TruckFreePoint();
            tfp.setTime(currentTime);
            tfp.setTr(tr);
            tfp.setLocation(network.findNodeByName(nowNode.getName()));
            calcFp(network, tfp);
            tfps.add(tfp);
        }
        printTruckInfo(tfps);
    }

    public static void calcFP2taskTime(Network nw, TruckFreePoint fp, WorkQueue q, FP2SCTime fp2t) {
        if (q.getTasks().isEmpty() || !q.verifyBinding(fp.getTr())) {
            fp2t.setTruck(fp.getTr());
            return;
        }

        //if (fp.getTr().getCurrentPosition() == fp.getLocation()) {
        fp2t.setTruck(fp.getTr());
        Task task = q.getTasks().get(0);
        fp2t.setTask(task);

        if (task.getSrcNode().getType() == task.getDstNode().getType()) {
            fp2t.setTimeqc(-1);
        } else if (task.getSrcNode().getType() == NodeType.QUAY_CRANE) {
            long time = nw.getTravellingTimePassingNodes(fp.getLocation(), task.getSrcNode());
            fp2t.setTimeqc(fp.getTime() + nw.getTravellingTimePassingNodes(fp.getLocation(), task.getSrcNode()));
        } else {
            long timeToSrc = nw.getTravellingTimePassingNodes(fp.getLocation(), task.getSrcNode());
            long timeToDst = nw.getTravellingTimePassingNodes(task.getSrcNode(), task.getDstNode());
            fp2t.setTimeqc(fp.getTime() + timeToSrc + task.getSrcNode().getAverageLoadTime() + timeToDst);
        }

        fp2t.setTimesrc(fp.getTime() + nw.getTravellingTimePassingNodes(fp.getLocation(), task.getSrcNode()));

    }

    public static void calcFp2taskTable(Network nw, List<List<FP2SCTime>> fp2taskTable,Node nowNode) {
        List<TruckFreePoint> fps = new ArrayList<>();
        calcFPs(nw, fps, nowNode);

        int numOfTrucks = fps.size();
        int numOfTqs = nw.getWorkQueues().size();

        fp2taskTable.clear();
        for (int i = 0; i < numOfTqs; i++) {
            List<FP2SCTime> row = new ArrayList<>();
            for (int j = 0; j < numOfTrucks; j++) {
                row.add(new FP2SCTime());
            }
            fp2taskTable.add(row);
        }

        for (int i = 0; i < numOfTqs; i++) {
            FP2SCTime minTime = fp2taskTable.get(i).get(0);
            for (int j = 0; j < numOfTrucks; j++) {
                FP2SCTime fp2sctime = fp2taskTable.get(i).get(j);
                calcFP2taskTime(nw, fps.get(j), nw.getWorkQueues().get(i), fp2sctime);
                if (fp2sctime.getTimeqc() < minTime.getTimeqc()) {
                    minTime = fp2sctime;
                }
            }
            Collections.sort(fp2taskTable.get(i), Comparator.comparingLong(FP2SCTime::getTimeqc).reversed());
        }

        //Collections.sort(fp2taskTable, Comparator.comparingLong(row -> row.get(0).getTimeqc()));
    }


    public static void calcPowTables(Network nw, List<Integer> powaTable, List<Long> powdTable, List<Integer> powtTable,Task nTask) {
        int numOfTqs = nw.getWorkQueues().size();
        powaTable.clear();
        powdTable.clear();
        powtTable.clear();

        for (int i = 0; i < numOfTqs; i++) {
            powaTable.add(0);
            powdTable.add(0L);
            powtTable.add(0);
        }

        int numOfTrucks = nw.getTrucks().size();
        for (int i = 0; i < numOfTrucks; i++) {
            Truck truck = nw.getTrucks().get(i);
            Task ta = new Task();
            if (nTask==null) {
                if (!truck.isAssigned()) {
                    continue;
                }
                ta = truck.getCurrentTask();
            }else {
                ta = nTask;
            }

            int status = ta.status();
            boolean updatePowa;
            if ((ta.getQuayNode() == ta.getSrcNode() && status >= 4) ||
                    (ta.getQuayNode() == ta.getDstNode() && status >= 7)) {
                updatePowa = true;
            } else {
                updatePowa = false;
            }

            for (int j = 0; j < numOfTqs; j++) {
                if (nw.getWorkQueues().get(j).getQueueName().equals(ta.getWorkQueueName())) {
                    powtTable.set(j, powtTable.get(j) + 1);
                    if (updatePowa) {
                        powaTable.set(j, powaTable.get(j) + 1);
                    }

                    if (ta.getQuayNode() == ta.getSrcNode()) {
                        powdTable.set(j, powdTable.get(j) + ta.getSrcNode().getAverageLoadTime());
                    } else {
                        powdTable.set(j, powdTable.get(j) + ta.getSrcNode().getAverageUnloadTime());
                    }
                }
            }

        }
    }

    public static int fp2scColRank(Truck tr, List<FP2SCTime> fp2scCol, long trFp2qcTime, long trFp2srcTime) {
        int rank = -1;
        for (FP2SCTime fti : fp2scCol) {
            if (fti.getTruck() == tr) {
                trFp2srcTime = fti.getTimesrc();
                trFp2qcTime = fti.getTimeqc();
                rank = 0;
                continue;
            }

            if (rank == -1)
                continue;

            if (fti.getTimeqc() < trFp2qcTime) {
                rank++;
            }
        }

        return rank;
    }

    public static String printTables(List<List<FP2SCTime>> fp2taskTable,
                                     List<Integer> powaTable, List<Long> powdTable,
                                     List<Integer> powtTable, Truck tr, Network nw) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- powa powd fp2task table ---\n");
        sb.append("Truck ").append(tr.getTruckID()).append(":\n");

        int fp2ttSize = fp2taskTable.size();
        for (int i = 0; i < fp2ttSize; i++) {
            long trFp2qcTime = -1;
            long trFp2srcTime = -1;
            if (nw.getWorkQueues().get(i).getTasks().isEmpty()) {
                sb.append("*Empty* ");
            }
            sb.append("powa: ").append(powaTable.get(i))
                    .append("\tpowd: ").append(powdTable.get(i))
                    .append("\tpowt: ").append(powtTable.get(i))
                    .append("\tpow_name: ").append(nw.getWorkQueues().get(i).getQueueName())
                    //.append("\tfp2sc_col_rank: ")
                    //.append(fp2scColRank(tr, fp2taskTable.get(i), trFp2qcTime, trFp2srcTime))
                    .append('\t');

            List<FP2SCTime> coli = fp2taskTable.get(i);
            int coliSize = coli.size();
            for (int j = 0; j < coliSize; j++) {
                FP2SCTime tij = coli.get(j);
                sb.append(tij.getTruck().getTruckID()).append('(');
                if (tij.getTask() == null) {
                    sb.append("-)\t");
                } else {
                    sb.append(tij.getTimeqc()).append(",")
                            .append(tij.getTimesrc()).append(")\t");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    public static List findTaskMinWait(Network nw, Truck tr, StringBuilder msg, Task cuTask) {
        StringBuilder oss = new StringBuilder();
        oss.append("--- starting algorithm ---\n");
        oss.append("...desired_wq_trucks: ").append("8").append("\n");

        List<Integer> powaTable = new ArrayList<>();
        List<Long> powdTable = new ArrayList<>();
        List<Integer> powtTable = new ArrayList<>();

        Node nowNode = tr.getCurrentPosition();
        Task nTask = new Task();
        if (cuTask!=null && cuTask.getSrcNode().getType()!=NodeType.TRUCK_AREA){
            nowNode = cuTask.getSrcNode();
            nTask = cuTask;
        }

        calcPowTables(nw, powaTable, powdTable, powtTable, nTask);
        List<List<FP2SCTime>> fp2taskTable = new ArrayList<>();
        calcFp2taskTable(nw, fp2taskTable, nowNode);
        oss.append(printTables(fp2taskTable, powaTable, powdTable, powtTable, tr, nw));

        oss.append("--- Entering selection loop ---\n");
        int colNum = powaTable.size();
        String containerId = "NOT_FOUND";
        int maxNumToMinTruck = 0;
        long minScore = Long.MAX_VALUE;
        for (int i = 0; i < colNum; i++) {
            WorkQueue tqi = nw.getWorkQueues().get(i);
            oss.append("checking queue ").append(i).append(" : ").append(tqi.getQueueName())
                    .append(" and max truck ").append(tqi.getMaxTruck())
                    .append("\n");

            if (!tqi.verifyBinding(tr) || tqi.getTasks().isEmpty()) {
                continue;
            }

            List<Task> tasks = tqi.getTasks();
            for (int j = 0; j < tasks.size(); j++) {
                Task task = tasks.get(j);

                List<Task> srcTasks = new ArrayList<>();
                if (task.getSrcNode().getType()==NodeType.QUAY_CRANE) srcTasks.add(task);

                for (Task srcTask : srcTasks) {

                    int numToMinTruck = Math.max(0 - powaTable.get(i), 0);

                    if (tqi.getMaxTruck() != -1 && powaTable.get(i) > tqi.getMaxTruck()) {
                        continue;
                    }

                    long trFp2srcTime = -1;
                    long trFp2qcTime = -1;
                    int fp2scRank = fp2scColRank(tr, fp2taskTable.get(i), trFp2srcTime, trFp2qcTime);
                    if (fp2scRank < 0) {
                        continue;
                    }

                    trFp2srcTime = fp2taskTable.get(i).get(0).getTimesrc();
                    long score = trFp2srcTime;
                    if (powaTable.get(i) < 3) {
                        score += powaTable.get(i);
                    } else {
                        score += 100000;
                    }

                    if (powtTable.get(i) >= 3) {
                        score += 200000;
                    }

                    if (containerId.equals("NOT_FOUND") || numToMinTruck > maxNumToMinTruck ||
                            (numToMinTruck == maxNumToMinTruck && minScore > score)) {
                        maxNumToMinTruck = numToMinTruck;
                        minScore = score;
                        containerId = srcTask.getContainerID();
                        tr.setRemainingTEU(tr.getMaxTEU()-srcTask.getTEUs());
                    }
                }
                if (srcTasks.size()==0){
                    if (i <= colNum-1) isSrcTaskFiniesh = true;
                    else continue;
                }
            }

        }

        if (isSrcTaskFiniesh == true && containerId.equals("NOT_FOUND")){

            for (int i = 0; i < colNum; i++) {
                WorkQueue tqi = nw.getWorkQueues().get(i);
                oss.append("checking queue ").append(i).append(" : ").append(tqi.getQueueName())
                        .append(" and max truck ").append(tqi.getMaxTruck())
                        .append("\n");

                if (!tqi.verifyBinding(tr) || tqi.getTasks().isEmpty()) {
                    continue;
                }

                List<Task> tasks = tqi.getTasks();
                for (int j = 0; j < tasks.size(); j++) {
                    Task task = tasks.get(j);

                    List<Task> dstTasks = new ArrayList<>();
                    if (task.getSrcNode().getType()==NodeType.YARD_CRANE) dstTasks.add(task);

                    if (dstTasks.size()==0) continue;

                    for (Task dstTask : dstTasks) {

                        int numToMinTruck = Math.max(0 - powaTable.get(i), 0);

                        if (tqi.getMaxTruck() != -1 && powaTable.get(i) > tqi.getMaxTruck()) {
                            continue;
                        }

                        long trFp2srcTime = -1;
                        long trFp2qcTime = -1;
                        int fp2scRank = fp2scColRank(tr, fp2taskTable.get(i), trFp2srcTime, trFp2qcTime);
                        if (fp2scRank < 0) {
                            continue;
                        }

                        trFp2srcTime = fp2taskTable.get(i).get(0).getTimesrc();
                        long score = trFp2srcTime;
                        if (powaTable.get(i) < 3) {
                            score += powaTable.get(i);
                        } else {
                            score += 100000;
                        }

                        if (powtTable.get(i) >= 3) {
                            score += 200000;
                        }

                        if (containerId.equals("NOT_FOUND") || numToMinTruck > maxNumToMinTruck ||
                                (numToMinTruck == maxNumToMinTruck && minScore > score)) {
                            maxNumToMinTruck = numToMinTruck;
                            minScore = score;
                            containerId = dstTask.getContainerID();
                            tr.setRemainingTEU(tr.getMaxTEU()-dstTask.getTEUs());
                        }
                    }
                }
            }
        }

        oss.append("--- exiting selection loop ---\n");

        isSrcTaskFiniesh = false;
        System.out.println(oss.toString());

        msg.append(oss.toString());
        List result = new ArrayList<>();
        result.add(0,containerId);
        result.add(1,nw);
        return result;
    }


}