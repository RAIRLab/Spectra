package org.rairlab.planner;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;

import java.util.List;
import java.util.Set;

public class PlanSketch extends Plan{
    public PlanSketch(List<Action> actions, Set<Formula> background) {
        super(actions, CollectionUtils.newEmptyList(), background);
    }
}
