package com.example.fyp_1;


import com.example.fyp_1.DataStructure.Network;
import com.example.fyp_1.Emulator.Emulator;

import java.util.List;

public class Fyp1Application {

    public static void main(String[] args) {
        String filePath = "C:/Users/xxxx/IdeaProjects/fyp_1/network.txt";
        NetworkReader networkReader = new NetworkReader(filePath);

        Network network = networkReader.parseData();
        network.updateNodesWithAverageLoadUnloadTime();

//        System.out.println("\n Network.task:");
//        for (Task task : network.getTasks()){
//            System.out.println("Task: "+ task.getContainerID() + "ap"+ task.getSrcAp().getTimeStart() + " ds" +task.getDstAp().getTimeFinish());
//        }


//        //打印工作队列的信息
//        System.out.println("\n NODE:");
//        for (Node node : network.getNodes()){
//            System.out.println("NODE: "+ node.getName() + ", loadTime: " +node.getAverageLoadTime() +", unloadTime: " + node.getAverageUnloadTime() );
//        }

        //System.out.println(network.toString());


        List<PortEvent> emuEvents = networkReader.emuEvents();

        double result = Emulator.startEmulator(network,emuEvents);

        System.out.println("Simulated result：" + result);






    }


}
