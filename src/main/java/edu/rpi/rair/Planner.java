package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by naveensundarg on 1/13/17.
 */
public interface Planner {

    Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal);

}
