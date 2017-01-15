package edu.rpi.rair.utils;

import clojure.lang.Obj;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Reader;
import edu.rpi.rair.Action;
import edu.rpi.rair.State;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Token;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class PlanningProblem {

    public Set<Formula> background;
    public Set<Action> actions;
    public State start;
    public State goal;
    public String name;

    private static final Keyword BACKGROUND = Keyword.newKeyword("background");
    private static final Keyword START = Keyword.newKeyword("start");
    private static final Keyword GOAL = Keyword.newKeyword("goal");
    private static final Keyword NAME = Keyword.newKeyword("name");
    private static final Keyword ACTION = Keyword.newKeyword("actions");

    private static final Keyword PRECONDITIONS = Keyword.newKeyword("preconditions");
    private static final Keyword ADDITIONS = Keyword.newKeyword("additions");
    private static final Keyword DELETIONS = Keyword.newKeyword("deletions");

    private static final Symbol ACTION_DEFINER = Symbol.newSymbol("define-action");


    public PlanningProblem(String name, Set<Formula> background, State start, State goal, Set<Action> actions) {

        this.background = background;
        this.start = start;
        this.actions = actions;
        this.goal = goal;
        this.name = name;
    }

    public static List<PlanningProblem> readFromFile(InputStream inputStream) throws Reader.ParsingException {

        Parseable parseable = Parsers.newParseable(new InputStreamReader(inputStream));
        Parser parser = Parsers.newParser(Parsers.defaultConfiguration());

        List<PlanningProblem> planningProblems = CollectionUtils.newEmptyList();

        Object nextValue = parser.nextValue(parseable);

        while (!nextValue.equals(Token.END_OF_INPUT)) {

            Map<?, ?> planningProblemSpec = (Map<?, ?>) nextValue;

            Set<Formula> background = readFrom((List<?>) planningProblemSpec.get(BACKGROUND));
            Set<Formula> start = readFrom((List<?>) planningProblemSpec.get(START));
            Set<Formula> goal = readFrom((List<?>) planningProblemSpec.get(GOAL));

            List<?> actionDefinitions = (List<?>) planningProblemSpec.get(ACTION);

            String name = planningProblemSpec.get(NAME).toString();
            Set<Action> actions = readActionsFrom(actionDefinitions);

            planningProblems.add(new PlanningProblem(name, background, State.initializeWith(start),
                    State.initializeWith(goal), actions));

            nextValue = parser.nextValue(parseable);
        }

        return planningProblems;

    }

    private static Set<Action> readActionsFrom(List<?> actionSpecs) throws Reader.ParsingException {

        Set<Action> actions = actionSpecs.stream().map(spec -> {
            List<?> specBody = (List<?>) spec;
            if(!specBody.get(0).equals(ACTION_DEFINER)){

               return null;
            }
            String name = specBody.get(1).toString();
            List<Variable> vars = ((List<?>)specBody.get(2)).stream().map(x -> {
                try {
                    return Reader.readLogicValue(x);
                } catch (Reader.ParsingException e) {
                    e.printStackTrace();
                    return null;
                }
            }).map(x->(Variable)x).collect(Collectors.toList());

            if(vars.stream().anyMatch(Objects::isNull)){
                return null;
            }
            Map<?, ?> actionSpec = (Map<?, ?>) specBody.get(3);
            try {


                Set<Formula> preconditions = readFrom((List<?>) actionSpec.get(PRECONDITIONS));
                Set<Formula> additions = readFrom((List<?>) actionSpec.get(ADDITIONS));
                Set<Formula> deletions = readFrom((List<?>) actionSpec.get(DELETIONS));

                return Action.buildActionFrom(name, preconditions, additions, deletions, vars);


            } catch (Reader.ParsingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());

        if (actions.stream().anyMatch(Objects::isNull)) {

            throw new Reader.ParsingException("Couldn't read actions: " + actionSpecs);
        }

        return actions;

    }

    private static Set<Formula> readFrom(List<?> objects) throws Reader.ParsingException {

        Set<Formula> formulae = objects.stream().map(x -> {
            try {
                return Reader.readFormula(x);
            } catch (Reader.ParsingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());


        if (formulae.stream().anyMatch(Objects::isNull)) {

            throw new Reader.ParsingException("Couldn't read formulae: " + objects);
        }

        return formulae;


    }

    @Override
    public String toString() {
        return "PlanningProblem{" +
                "name='" + name + '\'' +
                ", background=" + background +
                ", actions=" + actions +
                ", start=" + start +
                ", goal=" + goal +
                '}';
    }
}
