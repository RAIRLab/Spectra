package org.rairlab.planner.utils;

import org.rairlab.planner.Action;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Variable;

import java.util.List;
import java.util.Set;

public class IndefiniteAction extends Action {

    private IndefiniteAction(String name, Set<Formula> preconditions, Set<Formula> additions, Set<Formula> deletions, List<Variable> freeVariables) {
        super(name, preconditions, additions, deletions, 1, freeVariables, freeVariables);
    }
}
