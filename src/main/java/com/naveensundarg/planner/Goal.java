package org.rairlab.planner;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by naveensundarg on 1/14/17.
 */
public class Goal {

    private final State goalState;
    private final double priority;
    private final String name;
    private final String description;


    private static final AtomicInteger nameCounter;
    static {
        nameCounter = new AtomicInteger(0);
    }
    private Goal(State goalState, double priority) {
        this.goalState = goalState;
        this.priority = priority;
        this.name = "G"  + nameCounter.incrementAndGet();
        this.description = goalState.toString();

    }

    private Goal(State goalState, double priority, String name) {
        this.goalState = goalState;
        this.priority = priority;
        this.name = name;
        this.description = goalState.toString();
    }

    private Goal(State goalState, double priority, String name, String description) {
        this.goalState = goalState;
        this.priority = priority;
        this.name = name;
        this.description = description;
    }
    public static Goal makeGoal(State goalState, double priority){

        return new Goal(goalState, priority);

    }

    public static Goal makeGoal(State goalState, double priority, String name, String description){

        return new Goal(goalState, priority, name, description);

    }


    public State getGoalState() {
        return goalState;
    }

    public double getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        return  "(" + name + ": " + description + ": " + priority+ ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goal goal = (Goal) o;

        if (Double.compare(goal.priority, priority) != 0) return false;
        if (goalState != null ? !goalState.equals(goal.goalState) : goal.goalState != null) return false;
        return name != null ? name.equals(goal.name) : goal.name == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = goalState != null ? goalState.hashCode() : 0;
        temp = Double.doubleToLongBits(priority);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
