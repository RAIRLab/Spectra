package edu.rpi.rair.utils;
import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.naveensundarg.shadow.prover.Sandbox;
import com.naveensundarg.shadow.prover.core.Problem;
import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.utils.ProblemReader;
import com.naveensundarg.shadow.prover.utils.Reader;
import edu.rpi.rair.Goal;
import edu.rpi.rair.GoalTracker;
import edu.rpi.rair.Plan;
import edu.rpi.rair.Planner;
import edu.rpi.rair.utils.GoalTrackingProblem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class RunDemo {

    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


    static {

        Prover prover = new SnarkWrapper();
        try {
            List<Problem> problems = ProblemReader.readFrom(Sandbox.class.getResourceAsStream("firstorder-completness-tests.clj"));

            problems.forEach(problem -> {
                for (int i = 0; i < 100; i++) {
                    prover.prove(problem.getAssumptions(), problem.getGoal());

                }
            });

           planningProblemWarmUp();
            System.out.println("\nWARM UP DONE");
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Reader.ParsingException {



            System.out.println();

            List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Planner.class.getResourceAsStream("goal_management_3.clj")));


            GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

            GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem().getBackground(),
                    goalTrackingProblem.getPlanningProblem().getStart(),
                    goalTrackingProblem.getPlanningProblem().getActions());

            long start = System.currentTimeMillis();

            Goal g1 = goalTrackingProblem.getGoalNamed("G1");
            Goal g2 = goalTrackingProblem.getGoalNamed("G2");
            Goal g3 = goalTrackingProblem.getGoalNamed("G3");
            Goal g4 = goalTrackingProblem.getGoalNamed("G4");


            tryAndAddGoal(g1, goalTracker);

            tryAndAddGoal(g2, goalTracker);

            tryAndAddGoal(g3, goalTracker);

            tryAndAddGoal(g4, goalTracker);


            long end = System.currentTimeMillis();

            cp.println("--------------------------");
            cp.setForegroundColor(Ansi.FColor.CYAN);

            cp.print("Time Taken:");
            cp.clear();
            cp.print(" ");
            cp.setAttribute(Ansi.Attribute.BOLD);
            cp.print((end - start) / 1000.0 + "s");



    }

    public static void planningProblemWarmUp() throws Reader.ParsingException {


        for (int i = 0; i < 10; i++) {


            List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Planner.class.getResourceAsStream("goal_management_1.clj")));


            GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

            GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem().getBackground(),
                    goalTrackingProblem.getPlanningProblem().getStart(),
                    goalTrackingProblem.getPlanningProblem().getActions());

            long start = System.currentTimeMillis();

            Goal g1 = goalTrackingProblem.getGoalNamed("G1");
            Goal g2 = goalTrackingProblem.getGoalNamed("G2");
            Goal g3 = goalTrackingProblem.getGoalNamed("G3");
            Goal g4 = goalTrackingProblem.getGoalNamed("G4");
            Goal g5 = goalTrackingProblem.getGoalNamed("G5");


            goalTracker.adoptGoal(g1);

            goalTracker.adoptGoal(g2);


            goalTracker.adoptGoal(g3);


            goalTracker.adoptGoal(g4);


            goalTracker.adoptGoal(g5);

            System.out.print(".");


        }


    }

    static void tryAndAddGoal(Goal g, GoalTracker goalTracker) {

        System.out.println("========================");
        printInfo("Trying to Add Goal:", g.getName());

        Optional<Plan> possibleGoalPlan = goalTracker.adoptGoal(g);
        if (possibleGoalPlan.isPresent()) {

            printSuccess("Successfully added:", g.getName());
            printDebug1("Current Goals:", goalTracker.getCurrentGoals().stream().map(Goal::getName).collect(Collectors.toSet()).toString());
            Plan plan = possibleGoalPlan.get();
            printDebug2("Plan:", plan.getActions().isEmpty() ? "No plan needed. Already satisfied." : plan.getActions().toString());

        } else {

            printFailure("Could not add " + g.getName());
            printDebug1("Current Goals: ", goalTracker.getCurrentGoals().stream().map(Goal::getName).collect(Collectors.toSet()).toString());

        }

    }

    static void printInfo(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

    static void printSuccess(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.GREEN);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }


    static void printDebug1(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.YELLOW);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

    static void printDebug2(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.MAGENTA);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

    static void printFailure(String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.RED);   //setting format
        cp.print(message);
        cp.clear();
        cp.println("");
        cp.clear();
    }
}
