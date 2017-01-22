package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.And;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Compound;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Logic;
import com.naveensundarg.shadow.prover.utils.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class Action {

    private final Set<Formula> preconditions;
    private final Set<Formula> additions;
    private final Set<Formula> deletions;
    private final List<Variable> freeVariables;

    private final String name;
    private final Formula precondition;

    private int weight;
    private final boolean  trivial;

    private final Compound shorthand;

    private Action(String name, Set<Formula> preconditions, Set<Formula> additions, Set<Formula> deletions, List<Variable> freeVariables) {
        this.name = name;
        this.preconditions = preconditions;

        this.additions = additions;
        this.deletions = deletions;
        List<Variable> computedFreeVariables = preconditions.
                stream().
                map(x -> Sets.difference(x.variablesPresent(), x.boundVariablesPresent())).
                reduce(Sets.newSet(), Sets::union).
                stream().sorted().collect(Collectors.toList());

        this.freeVariables = freeVariables;

        this.precondition = new And(preconditions.stream().collect(Collectors.toList()));

        this.weight = preconditions.stream().mapToInt(Formula::getWeight).sum() +
                additions.stream().mapToInt(Formula::getWeight).sum() +
                deletions.stream().mapToInt(Formula::getWeight).sum();

        List<Value> valuesList = freeVariables.stream().collect(Collectors.toList());;
        this.shorthand = new Compound(name, valuesList);

        this.trivial = computeTrivialOrNot();
    }

    private Action(String name, Set<Formula> preconditions, Set<Formula> additions,
                   Set<Formula> deletions, List<Variable> freeVariables,
                   Compound shorthand
    ) {
        this.name = name;
        this.preconditions = preconditions;

        this.additions = additions;
        this.deletions = deletions;
        List<Variable> computedFreeVariables = preconditions.
                stream().
                map(x -> Sets.difference(x.variablesPresent(), x.boundVariablesPresent())).
                reduce(Sets.newSet(), Sets::union).
                stream().sorted().collect(Collectors.toList());

        this.freeVariables = freeVariables;

        this.precondition = new And(preconditions.stream().collect(Collectors.toList()));

        this.weight = preconditions.stream().mapToInt(Formula::getWeight).sum() +
                additions.stream().mapToInt(Formula::getWeight).sum() +
                deletions.stream().mapToInt(Formula::getWeight).sum();

        this.shorthand = shorthand;
        this.trivial = computeTrivialOrNot();

    }


    public static Action buildActionFrom(String name,
                                         Set<Formula> preconditions,
                                         Set<Formula> additions,
                                         Set<Formula> deletions,
                                         List<Variable> freeVariables) {

        return new Action(name, preconditions, additions, deletions, freeVariables);

    }

    public int getWeight() {
        return weight;
    }

    public Formula getPrecondition() {
        return precondition;
    }

    public List<Variable> openVars() {

        return freeVariables;

    }

    public Set<Formula> instantiateAdditions(Map<Variable, Value> mapping) {

        return additions.stream().map(x -> x.apply(mapping)).collect(Collectors.toSet());
    }

    public Set<Formula> instantiateDeletions(Map<Variable, Value> mapping) {

        return deletions.stream().map(x -> x.apply(mapping)).collect(Collectors.toSet());
    }


    public Action instantiate(Map<Variable, Value> binding){

        Set<Formula> newPreconditions = preconditions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());
        Set<Formula> newAdditions = additions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());
        Set<Formula> newDeletions = deletions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());

        List<Variable> newFreeVraibles = CollectionUtils.newEmptyList();
        for(Variable var: freeVariables){

            if(!binding.keySet().contains(var)){
                newFreeVraibles.add(var);
            }
        }

        List<Value> valuesList = freeVariables.stream().collect(Collectors.toList());;
        Compound shorthand = (Compound)(new Compound(name, valuesList)).apply(binding);
        return new Action(name, newPreconditions, newAdditions, newDeletions, newFreeVraibles, shorthand);
    }

    public String getName() {
        return name;
    }

    public boolean isNonTrivial() {
        return !trivial;
    }

    public boolean computeTrivialOrNot(){

        boolean case1Trivial =  Sets.subset(additions, preconditions) && deletions.isEmpty();

        boolean case2Trivial =  additions.isEmpty() && deletions.stream().allMatch(x->preconditions.stream().anyMatch(y->y.equals(Logic.negated(x))));

        boolean trivial = case1Trivial || case2Trivial;

        return trivial;
    }
    @Override
    public String toString() {
        return shorthand.getArguments().length == 0?  name: shorthand.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (!preconditions.equals(action.preconditions)) return false;
        if (!additions.equals(action.additions)) return false;
        if (!deletions.equals(action.deletions)) return false;
        return name.equals(action.name);
    }

    @Override
    public int hashCode() {
        int result = preconditions.hashCode();
        result = 31 * result + additions.hashCode();
        result = 31 * result + deletions.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

}
