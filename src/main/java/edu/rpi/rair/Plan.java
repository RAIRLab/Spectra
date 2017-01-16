package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/14/17.
 */
public class Plan {

    private final List<Action> actions;
    private final List<State> expectedStates;
    private final Set<Formula> background;
    public static Plan newEmptyPlan(State currentState, Set<Formula> background){
        List<Action> newActions = CollectionUtils.newEmptyList();
        List<State> newExpectedStates = CollectionUtils.listOf(currentState);
        return new Plan(newActions, newExpectedStates, background);
    }

    public Plan(List<Action> actions, List<State> expectedStates, Set<Formula> background) {
        this.actions = actions;
        this.expectedStates = expectedStates;
        this.background = background;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<State> getExpectedStates() {
        return expectedStates;
    }

    public Plan getPlanByStartingWith(Action action, State state){
        List<Action> newActions = CollectionUtils.newEmptyList();
        newActions.addAll(actions);
        newActions.add(0, action);

        List<State> newExpectedStates = CollectionUtils.newEmptyList();
        newExpectedStates.addAll(expectedStates);
        newExpectedStates.add(0, state);

        return new Plan(newActions, newExpectedStates, background);
    }

    public boolean conflictsWith(Goal goal){

        return expectedStates.stream().anyMatch(state-> Operations.conflicts(background, state,goal.getGoalState()));

    }



    public Set<Goal> getConflictingGoals(Set<Goal> goals){

        return goals.stream().filter(this::conflictsWith).collect(Collectors.toSet());

    }

    public boolean noConflicts(Set<Goal> goals){

        return getConflictingGoals(goals).isEmpty();

    }

    @Override
    public String toString() {
        return "Plan{" +
                "actions=" + actions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plan plan = (Plan) o;

        return actions != null ? actions.equals(plan.actions) : plan.actions == null;
    }

    @Override
    public int hashCode() {
        return actions != null ? actions.hashCode() : 0;
    }
}
