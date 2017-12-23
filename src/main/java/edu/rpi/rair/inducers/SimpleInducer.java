package edu.rpi.rair.inducers;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Compound;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.Pair;
import com.naveensundarg.shadow.prover.utils.Sets;
import edu.rpi.rair.*;
import edu.rpi.rair.utils.Commons;
import edu.rpi.rair.utils.PlanningProblem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 12/19/17.
 */
public class SimpleInducer implements Inducer{
    @Override
    public Plan induce(PlanningProblem planningProblem, State start, Goal goal, Plan plan) {


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

        Function<Set<Formula>,Set<Formula>> getRelevantFormula = formulae -> formulae.stream().
                filter(formula -> !Sets.intersection(values, formula.valuesPresent()).isEmpty()).
                collect(Collectors.toSet());

        Set<Formula> relevantBackgroundFormula  = getRelevantFormula.apply(backgroundFormula);
        Set<Formula> relevantInitFormula = getRelevantFormula.apply(initFormula);
        Set<Formula> relevantGoalFormula = getRelevantFormula.apply(goalFormula);


        Commons.generalize(valueVariableMap, relevantBackgroundFormula);
        Commons.generalize(valueVariableMap, relevantInitFormula);
        Commons.generalize(valueVariableMap, relevantGoalFormula);

        System.out.println(relevantBackgroundFormula);
        System.out.println(relevantInitFormula);
        System.out.println(relevantGoalFormula);

        return null;
    }
}
