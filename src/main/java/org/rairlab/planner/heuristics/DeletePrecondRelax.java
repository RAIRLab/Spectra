package org.rairlab.planner.heuristics;

import org.rairlab.planner.State;
import org.rairlab.planner.Action;
import org.rairlab.planner.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * IN PROGRESS

 * Heuristic that returns the number of actions
 * needed to perform to satisfy a goal where
 * preconditions and deletes of actions are not
 * taken into account.
 *
 * The difficult part here is how do we deal with effect computation?
 * Since normally free variables are instantiated.
 * I think in this case, we keep it as free variables.
 * (Gotta make sure it's fresh and distinct wrt to the other formulae)
 *
 * Then for the goal condition check we see if the the
 * state one-side matches with the goal.
 */
public class DeletePrecondRelax {

    private List<Action> actions;
    private State goal;
    private Map<State, Integer> cache;
    private Optional<Integer> bound = Optional.empty();

    public DeletePrecondRelax(List<Action> actions, State goal) {
        this.actions = actions;
        this.goal = goal;
        this.cache = new HashMap<State, Integer>();
    }

    public int h(State s) {
        if (cache.containsKey(s)) {
            return cache.get(s);
        }
        int ch = compute_h(s);
        this.cache.put(s, ch);
        return ch;
    }

    // TODO: Fill in...
    public int compute_h(State s) {
        return 0;
    }

    public Optional<Integer> getBound() {
        return bound;
    }

    public void setBound(int b) {
        bound = Optional.of(b);
    }

    public void clearBound() {
        bound = Optional.empty();
    }
}
