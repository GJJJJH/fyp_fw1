package com.example.fyp_1;


import com.example.fyp_1.DataStructure.Network;
import com.example.fyp_1.Emulator.Emulator;

import java.util.List;

public class Fyp1Application {

    public static void main(String[] args) {
        String filePath = "C:/Users/xxxx/IdeaProjects/fyp_1/test1-1-2.txt";
        NetworkReader networkReaderT = new NetworkReader(filePath);
        Network networkT = networkReaderT.parseData();
        networkT.updateNodesWithAverageLoadUnloadTime();

        NetworkReader networkReaderF = new NetworkReader(filePath);
        Network networkF = networkReaderF.parseData();
        networkF.updateNodesWithAverageLoadUnloadTime();

        List<PortEvent> emuEventsT = networkReaderT.emuEvents();
        List<PortEvent> emuEventsF = networkReaderF.emuEvents();

        List resultT = Emulator.startEmulator(networkT,emuEventsT,true);
        double resultTret = Double.valueOf(resultT.get(0).toString());
        double resultTBusyTime = Double.valueOf(resultT.get(1).toString());
        double resultTTotalCraneOperationEndTime = Double.valueOf(resultT.get(2).toString());
        List resultF = Emulator.startEmulator(networkF,emuEventsF,false);
        double resultFret = Double.valueOf(resultF.get(0).toString());
        double resultFBusyTime = Double.valueOf(resultF.get(1).toString());
        double resultFotalCraneOperationEndTime = Double.valueOf(resultF.get(2).toString());

//        boolean isTorF = (resultTBusyTime < resultFBusyTime) ? false: true;
//
        System.out.println("\n-----------------------------------------");
//        //System.out.println("\nSameSrcTask: " + isTorF + "\n");
////        if (isTorF) System.out.println("Final Simulate Time: " +  (int) resultTret +"    " +  (int) resultFret + "\nBusyTime: "+ resultTBusyTime +"    "+resultFBusyTime);
////        else System.out.println("Final Simulate Time: " +  (int) resultFret +"    "+ (int) resultTret +"\n BusyTime: "+ resultFBusyTime+"    "+resultTBusyTime);

        System.out.println("Final Simulate Time:               " +  (int) resultFret + "    "+ (int) resultTret +
                "\nBusyTime:                          "+ (int) resultTBusyTime+"    "+(int) resultFBusyTime +
                "\nTotal Crane Operation End Time:    "+ (int) resultTTotalCraneOperationEndTime+"    "+(int) resultFotalCraneOperationEndTime);
        System.out.println("-----------------------------------------");
    }


}
