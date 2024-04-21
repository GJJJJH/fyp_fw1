package com.example.fyp_1.InstancesGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.example.fyp_1.DataStructure.*;
import com.example.fyp_1.NetworkReader;

public class InstancesGenerator {
    private int refID = 0;
    private Random random = new Random();
    private double mean = 240;
    private double stdDev = 40;

    // Method to generate reference ID
    private String getRefID(boolean increaseID) {
        if (increaseID) {
            return Integer.toString(++refID);
        } else {
            return Integer.toString(refID);
        }
    }

    // Method to generate container ID
    private String getCtnID() {
        return "ctn_" + refID;
    }

    // Method to generate load/unload time
    private double getLoadUnloadTime() {
        double mean = 240;
        double stdDev = 40;
        return mean + stdDev * ThreadLocalRandom.current().nextGaussian();
    }

    public void generateInstructions(Node src, Node dst, int numOfReplications) {
        if (src == dst) {
            return;
        }

        boolean twinContainer;
        int prevTEU = 2, TEU;
        for (int i = 0; i < numOfReplications; i++) {
            String oldRefID = getRefID(true);
            System.out.print(getRefID(false) + "\t" + getCtnID() + "\t" +
                    src.getName() + "\t" + dst.getName() + "\t" +
                    getLoadUnloadTime() + "\t" + getLoadUnloadTime());
            TEU = random.nextInt(2) + 1;
            System.out.print("\t" + TEU);

            twinContainer = random.nextBoolean(); // 1/2 chance for twin container
            if (twinContainer && prevTEU == 1 && TEU == 1) {
                System.out.print("\t" + oldRefID);
            } else {
                System.out.print("\t" + "-1");
            }

            System.out.println();
            prevTEU = TEU;
        }
    }

    // Method to get some yardcranes
    public List<Node> getSomeYC(int num, Network nw) {
        List<Node> selectedYCs = new ArrayList<>();
        List<Node> yardCranes = nw.getYardCraneNodes(); // Assuming a method getYardcranes() exists in Network class
        int ycsNum = yardCranes.size();

        for (int i = 0; i < num; i++) {
            int randomIndex = random.nextInt(ycsNum);
            selectedYCs.add(yardCranes.get(randomIndex));
        }

        return selectedYCs;
    }

    // Method to generate yardcrane to yardcrane instructions
    public void generateYCToYC(int numOfInst, Network nw) {
        int ycsSize = random.nextInt(5) + 1;
        List<Node> ycs1 = getSomeYC(ycsSize, nw);
        List<Node> ycs2 = getSomeYC(ycsSize, nw);

        for (int i = 0; i < ycsSize; i++) {
            generateInstructions(ycs1.get(i), ycs2.get(i), numOfInst / ycsSize);
        }
    }

    // Method to generate quaycrane to yardcrane instructions
    public void generateQCToYC(Node pow, int numOfInst, Network nw) {
        List<Node> ycs = getSomeYC(random.nextInt(5) + 1, nw);
        for (Node yc : ycs) {
            generateInstructions(pow, yc, numOfInst / ycs.size());
        }
    }

    // Method to generate yardcrane to quaycrane instructions
    public void generateYCToQC(Node pow, int numOfInst, Network nw) {
        List<Node> ycs = getSomeYC(random.nextInt(5) + 1, nw);
        for (Node yc : ycs) {
            generateInstructions(yc, pow, numOfInst / ycs.size());
        }
    }

    public void generatePowInstructions(Node pow, Network nw) {
        System.out.println(pow.getName());

        int numOfTasks = random.nextInt(4);
        for (int i = 0; i < numOfTasks; i++) {
            if (random.nextBoolean()) {
                generateYCToQC(pow, random.nextInt(100), nw);
            } else {
                generateQCToYC(pow, random.nextInt(100), nw);
            }
        }
    }

    public void main(String[] args) {
        String netfile = "......";
        NetworkReader networkReader = new NetworkReader(netfile);
        Network nw = networkReader.parseData();

        if (nw == null) {
            System.out.println("Failed to parse network data.");
            return;
        }

        System.out.println("time\t0");


        for (Node shipCrane : nw.getQuayCraneNodes()) {
            generatePowInstructions(shipCrane, nw);
        }

        System.out.println("yc_works");

        generateYCToYC(new Random().nextInt(100), nw);
    }
}
