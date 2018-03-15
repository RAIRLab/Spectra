package com.naveensundarg.planner;

import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Reader;

import java.util.Set;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class State {

    final Set<Formula> formulae;
    private static final Prover prover = SnarkWrapper.getInstance();
    static Formula FALSE;

    static{

        try {
            FALSE = Reader.readFormulaFromString("(and P (not P))");
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            FALSE  = null;
        }

    }

    private State(Set<Formula> formulae){

        this.formulae = formulae;
    }

    public static State initializeWith(Set<Formula> formulae){

        State newState = new State(formulae);
        return newState;
    }


    public void add(Set<Formula> formulae){

        this.formulae.addAll(formulae);
    }

    public void remove(Set<Formula> formulae){

        this.formulae.removeAll(formulae);

    }

    public Set<Formula> getFormulae() {
        return CollectionUtils.setFrom(formulae);
    }


    @Override
    public String toString() {
        return "State{" +
                "formulae=" + formulae +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return formulae.equals(state.formulae);
    }

    @Override
    public int hashCode() {
        return formulae.hashCode();
    }
}
