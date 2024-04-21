package com.example.fyp_1.DataStructure;

public class Instruction {
    // 指令代码
    // 1: 前往场地起重机节点
    // 2: 前往船舶起重机节点（应始终从队列操作，因为它是出队操作）
    // 3: 前往场地起重机节点装载集装箱
    // 4: 前往仓库休息
    private InstructionType insCode;

    // 指令的对象：
    // - 卡车司机：要去哪里开
    // - 队列节点：需要告诉卡车哪些任务
    // - 起重机操作员：是否现在执行下一个任务
    private Truck truck; // insCode: 1, 2, 3, 4
    private Node dst; // insCode: 1, 2, 3, 4
    private Task task; // insCode: 3

    // 构造函数
    public Instruction(InstructionType insCode, Truck truck, Node dst, Task task) {
        this.insCode = insCode;
        this.truck = truck;
        this.dst = dst;
        this.task = task;
    }

    // Getter和Setter方法
    public InstructionType getInsCode() {
        return insCode;
    }

    public void setInsCode(InstructionType insCode) {
        this.insCode = insCode;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Node getDst() {
        return dst;
    }

    public void setDst(Node dst) {
        this.dst = dst;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
