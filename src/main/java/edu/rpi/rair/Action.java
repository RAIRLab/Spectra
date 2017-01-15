package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.And;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
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
        return new Action(name, newPreconditions, newAdditions, newDeletions, newFreeVraibles);
    }
    @Override
    public String toString() {
        return "Action{" +
                "preconditions=" + preconditions +
                ", additions=" + additions +
                ", deletions=" + deletions +
                ", name='" + name + '\'' +
                '}';
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
