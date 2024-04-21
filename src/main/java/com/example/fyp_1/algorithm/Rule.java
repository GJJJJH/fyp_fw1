package com.example.fyp_1.algorithm;

public class Rule {
    private Feature qstate; // 学习状态
    private AlgActionIndex action; // 低级启发式索引

    // 构造函数
    public Rule(Feature qstate, AlgActionIndex action) {
        this.qstate = qstate;
        this.action = action;
    }

    // 拷贝构造函数
    public Rule(Rule rule) {
        this.qstate = new Feature(rule.qstate);
        this.action = rule.action;
    }

    // 清除特征
    public void clearFeatures() {
        qstate.clearBuffer();
    }
}
