package org.rairlab.planner.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import org.rairlab.planner.search.DepthFirstPlanner;
import org.rairlab.planner.GoalTracker;
import org.rairlab.planner.Plan;
import org.rairlab.planner.Planner;
import org.rairlab.shadow.prover.utils.Reader;
 import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class LearningSystem {

    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


    static List<Triple<BiConsumer<String, String>, String, String>> printQueue = new ArrayList<>();


    public static void main(String[] args) throws Reader.ParsingException, InterruptedException {


        System.out.println();

        RunDemo.planningProblemWarmUp();

        Visualizer.setShouldVisualize(true);

        List<GoalTrackingProblem> goalTrackingProblemList1 = (GoalTrackingProblem.readFromFiles(Sandbox.class.getResourceAsStream("../problems/seriated/seriated_challenge_1.clj"),
                Sandbox.class.getResourceAsStream("../problems/seriated/methods.clj")));
        List<GoalTrackingProblem> goalTrackingProblemList2 = (GoalTrackingProblem.readFromFiles(Sandbox.class.getResourceAsStream("../problems/seriated/seriated_challenge_2.clj"),
                Sandbox.class.getResourceAsStream("../problems/seriated/methods.clj")));


        GoalTrackingProblem goalTrackingProblem1 = goalTrackingProblemList1.get(0);
        GoalTrackingProblem goalTrackingProblem2 = goalTrackingProblemList2.get(0);


        PlanningProblem planningProblem1 = goalTrackingProblem1.getPlanningProblem();
        PlanningProblem planningProblem2 = goalTrackingProblem2.getPlanningProblem();

        Planner planner = new DepthFirstPlanner();

        // GoalTracker(PlanningProblem problem, Set<Formula> background, State startState, Set<Action> actions)

        GoalTracker goalTracker1 = new GoalTracker(planningProblem1, planningProblem1.getBackground(), planningProblem1.getStart(), planningProblem1.getActions());
        GoalTracker goalTracker2 = new GoalTracker(planningProblem2, planningProblem2.getBackground(), planningProblem2.getStart(), planningProblem2.getActions());


        long start, end;





       // Plan plan1 =  goalTracker1.adoptGoal(goalTrackingProblem1.getGoalNamed("G1")).get();
        //Plan plan2 = goalTracker2.adoptGoal(goalTrackingProblem2.getGoalNamed("G1")).get();

        BiFunction<GoalTracker, GoalTrackingProblem, Plan> run = (goalTracker, goalTrackingProblem) ->  {
            Optional<Plan> planOptional  = goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G1"));

            if(planOptional.isPresent()){
                return planOptional.get();
            }
             else {

                System.out.println("Could not find a plan");
                return null;
            }
        };

      // Object plan1 = (Commons.runAndTime(run, goalTracker1, goalTrackingProblem1, "Problem 1"));

        Object plan2 = (Commons.runAndTime(run, goalTracker2, goalTrackingProblem2, "Problem 2"));


        Visualizer.unspool(10);

    }

}
