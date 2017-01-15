package edu.rpi.rair;

/**
 * Created by naveensundarg on 1/14/17.
 */
public class Goal {

    private final State goalState;
    private final int priority;

    private Goal(State goalState, int priority) {
        this.goalState = goalState;
        this.priority = priority;
    }

    public static Goal makeGoal(State goalState, int priority){

        return new Goal(goalState, priority);

    }

    public State getGoalState() {
        return goalState;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "goalState=" + goalState +
                ", priority=" + priority +
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
