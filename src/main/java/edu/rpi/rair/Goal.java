package edu.rpi.rair;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by naveensundarg on 1/14/17.
 */
public class Goal {

    private final State goalState;
    private final int priority;
    private final String name;

    private static final AtomicInteger nameCounter;
    static {
        nameCounter = new AtomicInteger(0);
    }
    private Goal(State goalState, int priority) {
        this.goalState = goalState;
        this.priority = priority;
        this.name = "G"  + nameCounter.incrementAndGet();
    }

    private Goal(State goalState, int priority, String name) {
        this.goalState = goalState;
        this.priority = priority;
        this.name = name;
    }
    public static Goal makeGoal(State goalState, int priority){

        return new Goal(goalState, priority);

    }

    public static Goal makeGoal(State goalState, int priority, String name){

        return new Goal(goalState, priority, name);

    }


    public State getGoalState() {
        return goalState;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "goalState=" + goalState +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goal goal = (Goal) o;

        if (priority != goal.priority) return false;
        return goalState.equals(goal.goalState);
    }

    @Override
    public int hashCode() {
        int result = goalState.hashCode();
        result = 31 * result + priority;
        return result;
    }
}
