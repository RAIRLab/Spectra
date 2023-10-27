package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.*;
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

   /* (define-method planMethod [?b ?c ?d]
    {:goal [(In ?b ?c) (In ?c ?d)]
   :while [(In ?b ?d) (Empty ?c)
        (< (size ?c) (size ?d))
        (< (size ?b) (size ?c))]
   :actions [(removeFrom  ?b ?d) (placeInside  ?b ?c) (placeInside  ?c ?d)]})
*/
    @Override
    public String toString() {
        return "(define-method planMethod " +   freeVariables.toString().replace(",", " ")  + "\n" +
                "\t{:goal    " + goalPreconditions.toString().replace(",", " ") + "\n" +
                "\t:while   " + backGroundStatePreconditions.toString().replace(",", " ") + "\n" +
                "\t:actions " + actionCompounds.toString().replace(",", " ") + "})";
    }
}
