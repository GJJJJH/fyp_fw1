package com.example.fyp_1.DataStructure;

public enum InstructionType {
    // 前往场地起重机节点
    DEADHEAD_TO_YARD_CRANE,
    // 装载至场地起重机节点
    LOADED_TO_YARD_CRANE,
    // 前往船舶起重机节点
    DEADHEAD_TO_QUAY_CRANE,
    // 装载至船舶起重机节点
    LOADED_TO_QUAY_CRANE,
    // 将集装箱放入卡车
    PUT_CONTAINER_TO_TRUCK,
    // 从卡车取出集装箱
    TAKE_CONTAINER_FROM_TRUCK,
    // 前往仓库
    TO_DEPOT,
    // 暂停操作
    HOLD,
    TAKE_CONTAINER_FROM_TRUCK_FINISH,
    PUT_CONTAINER_TO_TRUCK_FINISH
}
