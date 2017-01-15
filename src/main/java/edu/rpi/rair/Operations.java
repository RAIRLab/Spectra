package edu.rpi.rair;

import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.representations.formula.And;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.ImmutablePair;
import com.naveensundarg.shadow.prover.utils.Pair;
import com.naveensundarg.shadow.prover.utils.Sets;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.rpi.rair.State.FALSE;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class Operations {

    private static Prover prover;

    static{
        prover = new SnarkWrapper();
    }

    public static synchronized Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> givens, Formula goal, List<Variable> variables){

        return prover.proveAndGetBindings(givens, goal, variables);
    }

    public static Optional<Pair<State,Action>> apply(Set<Formula> background, Action action, State state){

        Prover prover = new SnarkWrapper();


        Set<Formula> givens = Sets.union(background, state.getFormulae());

        Optional<Map<Variable, Value>> bingdingsOpt = proveAndGetBindings(givens, action.getPrecondition(), action.openVars());

        State newState;

        if(!bingdingsOpt.isPresent()){


            return Optional.empty();

        }
        Set<Formula> newFormulae = state.getFormulae();

        newFormulae = Sets.union(newFormulae, action.instantiateAdditions(bingdingsOpt.get()));

        newFormulae = Sets.difference(newFormulae, action.instantiateDeletions(bingdingsOpt.get()));

        newState = State.initializeWith(newFormulae);

        return Optional.of(ImmutablePair.from(newState, action.instantiate(bingdingsOpt.get())));

    }


    public static boolean satisfies(Set<Formula> background, State state, State goal){

        return goal.getFormulae().stream().
                allMatch(x->prover.prove(Sets.union(background, state.getFormulae()), x).isPresent());

    }

    public static boolean conflicts(Set<Formula> background, State state1, State state2){

        return prover.prove(Sets.union(background, Sets.union(state1.getFormulae(), state2.getFormulae())), FALSE ).isPresent();

    }

}
