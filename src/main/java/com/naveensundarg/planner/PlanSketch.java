package com.naveensundarg.planner;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;

import java.util.List;
import java.util.Set;

public class PlanSketch extends Plan{
    public PlanSketch(List<Action> actions, Set<Formula> background) {
        super(actions, CollectionUtils.newEmptyList(), background);
    }
}
