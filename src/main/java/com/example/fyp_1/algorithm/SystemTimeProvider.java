package com.example.fyp_1.algorithm;

class SystemTimeProvider implements TimeProvider {
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}