package org.rairlab.planner;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Reader;

import java.util.Set;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class State {

    final Set<Formula> formulae;
    static Formula TRUE;
    static Formula FALSE;

    static{

        try {
            TRUE = Reader.readFormulaFromString("(or P (not P))");
            FALSE = Reader.readFormulaFromString("(and P (not P))");
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            TRUE = null;
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
