package edu.rpi.rair.utils;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Reader;
import edu.rpi.rair.Goal;
import edu.rpi.rair.State;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Token;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class GoalTrackingProblem {

    private final PlanningProblem planningProblem;
    private final Set<Goal> goals;
    private final Map<String, Goal> goalNameMap;

    private static final Keyword DEFINITIONS = Keyword.newKeyword("definitions");
    private static final Keyword GOALS = Keyword.newKeyword("goals");

    private static final Keyword PRIORITY = Keyword.newKeyword("priority");
    private static final Keyword STATE = Keyword.newKeyword("state");

    public GoalTrackingProblem(PlanningProblem planningProblem, Set<Goal> goals) {
        this.planningProblem = planningProblem;
        this.goals = goals;
        this.goalNameMap = CollectionUtils.newMap();

        for(Goal g: goals){
            goalNameMap.put(g.getName(), g);
        }


    }

    public static List<GoalTrackingProblem> readFromFile(InputStream inputStream) throws Reader.ParsingException {

        Parseable parseable = Parsers.newParseable(new InputStreamReader(inputStream));
        Parser parser = Parsers.newParser(Parsers.defaultConfiguration());

        List<GoalTrackingProblem> goalTrackingProblems = CollectionUtils.newEmptyList();

        Object nextValue = parser.nextValue(parseable);

        Set<Goal> goals = CollectionUtils.newEmptySet();
        while (!nextValue.equals(Token.END_OF_INPUT)) {
            Map<?, ?> goalTrackingProblemSpec = (Map<?, ?>) nextValue;

             PlanningProblem planningProblem = PlanningProblem.readFromObject(goalTrackingProblemSpec.get(DEFINITIONS));


            Map<?, ?> goalSpecs = (Map<?,?>) goalTrackingProblemSpec.get(GOALS);

            for(Map.Entry<?,?> entry : goalSpecs.entrySet()){

                String name = entry.getKey().toString();
                Map<?, ?> goalSpec = (Map<?,?>)entry.getValue();

                double priority = ((Double) goalSpec.get(PRIORITY));
                Set<Formula> stateFormulae = PlanningProblem.readFrom((List<?>) goalSpec.get(STATE));

                goals.add(Goal.makeGoal(State.initializeWith(stateFormulae), priority, name));

            }

            goalTrackingProblems.add(new GoalTrackingProblem(planningProblem, goals));
            nextValue = parser.nextValue(parseable);


        }

        return goalTrackingProblems;
    }

    public PlanningProblem getPlanningProblem() {
        return planningProblem;
    }

    public Set<Goal> getGoals() {
        return goals;
    }

    public  Goal getGoalNamed(String goalName) {
        return goalNameMap.get(goalName);
    }
}
