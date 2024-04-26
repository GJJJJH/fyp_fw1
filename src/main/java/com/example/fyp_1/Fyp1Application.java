package com.example.fyp_1;


import com.example.fyp_1.DataStructure.Network;
import com.example.fyp_1.Emulator.Emulator;

import java.util.List;

public class Fyp1Application {

    public static void main(String[] args) {
        String filePath = "C:/Users/xxxx/IdeaProjects/fyp_1/network.txt";
        NetworkReader networkReaderT = new NetworkReader(filePath);
        Network networkT = networkReaderT.parseData();
        networkT.updateNodesWithAverageLoadUnloadTime();

        NetworkReader networkReaderF = new NetworkReader(filePath);
        Network networkF = networkReaderF.parseData();
        networkF.updateNodesWithAverageLoadUnloadTime();

        List<PortEvent> emuEventsT = networkReaderT.emuEvents();
        List<PortEvent> emuEventsF = networkReaderF.emuEvents();

        double resultT = Emulator.startEmulator(networkT,emuEventsT,true);
        double resultF = Emulator.startEmulator(networkF,emuEventsF,false);
        boolean isTorF = (resultT> resultF) ? false: true;

        System.out.println("\n-----------------------------------------");
        System.out.println("\nSameSrcTask: " + isTorF + "\n");
        if (isTorF) System.out.println("Final Simulate Time: " + (int) resultT);
        else System.out.println("Final Simulate Time: " + (int) resultF);
        System.out.println("-----------------------------------------");
    }


}
