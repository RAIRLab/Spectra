package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.And;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Compound;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Reader;
import com.naveensundarg.shadow.prover.utils.Sets;
import edu.rpi.rair.utils.PlanningProblem;
 import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 12/22/17.
 */
public class PlanMethod {

    private final Set<Formula> backGroundStatePreconditions;
    private final Set<Formula> goalPreconditions;
    private final List<Variable> freeVariables;
    private final List<Compound> actionCompounds;



    public PlanMethod(Set<Formula> goalPreconditions, Set<Formula> backGroundStatePreconditions, List<Variable> freeVariables, List<Compound> actionCompounds) {
        this.goalPreconditions = goalPreconditions;
        this.backGroundStatePreconditions = backGroundStatePreconditions;
        this.freeVariables = freeVariables;
        this.actionCompounds = actionCompounds;

    }


    public PlanMethod(Set<Formula> goalPreconditions, List<Variable> freeVariables, List<Compound> actionCompounds) {
        this.goalPreconditions = goalPreconditions;
        this.backGroundStatePreconditions = Sets.newSet();
        this.freeVariables = freeVariables;
        this.actionCompounds = actionCompounds;

    }


    public Optional<List<PlanSketch>> apply(Set<Formula> background, Set<Formula> start,  Set<Formula> goal, Set<Action> actionSpecs) {


        Optional<Set<Map<Variable, Value>>> mappingsOpt = Operations.proveAndGetMultipleBindings(goal, new And(new ArrayList<>(goalPreconditions)), freeVariables);

        if (mappingsOpt.isPresent()) {

            Set<Map<Variable, Value>> mappings = mappingsOpt.get();

            List<PlanSketch> planSketches = CollectionUtils.newEmptyList();


            mappings.forEach(mapping ->{

                Formula whileCondition = (new And(new ArrayList<>(backGroundStatePreconditions))).apply(mapping);

                boolean whileHolds = Operations.proveCached(Sets.union(background, start), whileCondition).isPresent();


                if(whileHolds){

                    List<Compound> instantiatedActionCompounds = actionCompounds.stream().map(compound -> (Compound) compound.apply(mapping)).collect(Collectors.toList());


                    List<Action> actions = CollectionUtils.newEmptyList();

                    for (Compound compound : instantiatedActionCompounds) {


                        try {

                            actions.add(PlanningProblem.readInstantiatedAction(actionSpecs, compound.toString()));

                        } catch (Reader.ParsingException e) {

                            e.printStackTrace();

                        }
                    }

                    planSketches.add(new PlanSketch(actions, background));



                }


            });



            return Optional.of(planSketches);

        } else {

            return Optional.empty();
        }


    }

    @Override
    public String toString() {
        return "PlanMethod{" +
                "backGroundStatePreconditions=" + backGroundStatePreconditions +
                ", goalPreconditions=" + goalPreconditions +
                ", freeVariables=" + freeVariables +
                ", actionCompounds=" + actionCompounds +
                '}';
    }
}
