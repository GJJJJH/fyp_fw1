package com.example.fyp_1.algorithm;

import com.example.fyp_1.Emulator.Emulator;

public class SimulatedTimeProvider implements TimeProvider {
    @Override
    public long getCurrentTime() {
        return Emulator.emuSolution.getCurrentTime();
    }
}
