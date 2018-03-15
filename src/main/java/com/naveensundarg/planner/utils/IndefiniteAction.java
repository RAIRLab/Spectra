package com.naveensundarg.planner.utils;

import com.naveensundarg.planner.Action;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Variable;

import java.util.List;
import java.util.Set;

public class IndefiniteAction extends Action {

    private IndefiniteAction(String name, Set<Formula> preconditions, Set<Formula> additions, Set<Formula> deletions, List<Variable> freeVariables) {
        super(name, preconditions, additions, deletions, freeVariables);
    }
}
