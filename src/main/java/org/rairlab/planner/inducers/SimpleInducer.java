package org.rairlab.planner.inducers;

import org.rairlab.planner.*;
import org.rairlab.planner.utils.Commons;
import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 12/19/17.
 */

public class SimpleInducer implements Inducer {

    @Override
    public PlanMethod induce(PlanningProblem planningProblem, State start, Goal goal, Plan plan) {


        List<Action> actionList = plan.getActions();
        List<Compound> actionCompounds = actionList.stream().map(Action::getShorthand).collect(Collectors.toList());


        Set<Formula> backgroundFormula = planningProblem.getBackground().stream().collect(Collectors.toSet());
        Set<Formula> initFormula = start.getFormulae().stream().collect(Collectors.toSet());
        Set<Formula> goalFormula = goal.getGoalState().getFormulae().stream().collect(Collectors.toSet());

        Set<Value> values = actionCompounds.stream().
                map(Compound::getArguments).map(Arrays::stream).map(x->x.collect(Collectors.toSet()))
                                .reduce(Sets.newSet(), Sets::union)
                                .stream().filter(Value::isConstant).collect(Collectors.toSet());


        Map<Value, Variable> valueVariableMap = Commons.makeVariables(values);

        Function<Set<Formula>,Set<Formula>> getRelevantFormula = formulae -> {

         return formulae.stream().
                 filter(formula -> Sets.difference(formula.valuesPresent().stream().filter(Value::isConstant).collect(Collectors.toSet()),
                                                    values).isEmpty() &&
                         !Sets.intersection(values, formula.valuesPresent()).isEmpty()).
                 collect(Collectors.toSet());
        };

        Set<Formula> relevantBackgroundFormula  = getRelevantFormula.apply(backgroundFormula);
        Set<Formula> relevantInitFormula = getRelevantFormula.apply(initFormula);
        Set<Formula> relevantGoalFormula = getRelevantFormula.apply(goalFormula);


        Commons.generalize(valueVariableMap, relevantBackgroundFormula);
        Commons.generalize(valueVariableMap, relevantInitFormula);
        Commons.generalize(valueVariableMap, relevantGoalFormula);

        System.out.println(relevantBackgroundFormula);
        System.out.println(relevantInitFormula);
        System.out.println(relevantGoalFormula);

        //PlanMethod(Set<Formula> goalPreconditions, Set<Formula> backGroundStatePreconditions, List<Variable> freeVariables, List<Compound> actionCompounds


       return new PlanMethod(Commons.generalize(valueVariableMap, relevantGoalFormula),
                             Sets.union(Commons.generalize(valueVariableMap, relevantBackgroundFormula),
                                        Commons.generalize(valueVariableMap, relevantInitFormula)),
                             new ArrayList<>(valueVariableMap.values()), actionCompounds.stream().map(x-> (Compound) x.generalize(valueVariableMap)).
                                                                            collect(Collectors.toList()));

     }
}
