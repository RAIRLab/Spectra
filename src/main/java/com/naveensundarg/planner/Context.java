package org.rairlab.planner;

import java.util.List;

public class Context {

    private final List<PlanMethod> planMethods;
    private final boolean workFromScratch;


    public Context(List<PlanMethod> planMethods, boolean workFromScratch) {
        this.planMethods = planMethods;
        this.workFromScratch = workFromScratch;
    }

    public List<PlanMethod> getPlanMethods() {
        return planMethods;
    }

    public boolean isWorkFromScratch() {
        return workFromScratch;
    }
}
