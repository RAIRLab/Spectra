package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.Optional;
import java.util.Set;

/**
 * Created by naveensundarg on 1/13/17.
 */
public interface Planner {

    Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal);
    Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal);




}
