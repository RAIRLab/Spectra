package org.rairlab.planner.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.google.common.collect.Sets;
import org.rairlab.planner.Goal;
import org.rairlab.planner.GoalTracker;
import org.rairlab.planner.Inducer;
import org.rairlab.planner.Plan;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.utils.Reader;
 import org.rairlab.planner.inducers.SimpleInducer;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class RunDemo {

    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


    static List<Triple<BiConsumer<String, String>, String, String>> printQueue = new ArrayList<>();

    static {

        Prover prover = SnarkWrapper.getInstance();
         /*  try {
         List<Problem> problems = ProblemReader.readFrom(Sandbox.class.getResourceAsStream("../firstorder-completness-tests.clj"));

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

*/
    }

    public static void main(String[] args) throws Reader.ParsingException, InterruptedException {
       planningProblemWarmUp();

        System.out.println();

        Visualizer.setShouldVisualize(false);

       runProblem("../problems/heroism/setup.clj");
     //  runProblem("seriated_challenge_2.clj");


    }

    private static void runProblem(String name) throws Reader.ParsingException {
        List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Sandbox.class.getResourceAsStream(name)));


        GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

        GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem(), goalTrackingProblem.getPlanningProblem().getBackground(),
                goalTrackingProblem.getPlanningProblem().getStart(),
                goalTrackingProblem.getPlanningProblem().getActions());

        long start = System.currentTimeMillis();

        Goal g1 = goalTrackingProblem.getGoalNamed("G1");
        Goal g2 = goalTrackingProblem.getGoalNamed("G2");


       tryAndAddGoal(g1, goalTracker);
        tryAndAddGoal(g2, goalTracker);


        long end = System.currentTimeMillis();


        System.out.println("***************************");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.println("AVAILABLE GOALS AND CONSTRAINTS");
        cp.clear();
        System.out.println("------------------------------");

        goalTrackingProblem.getGoals().forEach(goal->{
             System.out.println(goal);

        });

        System.out.println("***************************");

        Visualizer.unspool(200);
        for (int i = 0; i < printQueue.size(); i++) {

            Triple<BiConsumer<String, String>, String, String> task = printQueue.get(i);

            task.getLeft().accept(task.getMiddle(), task.getRight());

        }

        cp.println("--------------------------");
        cp.setForegroundColor(Ansi.FColor.CYAN);

        cp.print("Time Taken:");
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print((end - start) / 1000.0 + "s");
        cp.println(" ");

        cp.println("--------------------------");
        cp.println(" ");
        cp.println(" ");
        cp.println(" ");
    }

    public static void planningProblemWarmUp() throws Reader.ParsingException {


        for (int i = 0; i < 1; i++) {


            List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Sandbox.class.getResourceAsStream("../problems/prisoner/goal_management_1.clj")));


            GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

            GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem(), goalTrackingProblem.getPlanningProblem().getBackground(),
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

        Inducer simpleInducer = new SimpleInducer();

        System.out.println("========================");
        printInfoLater("Trying to Add Goal or Constraint:", "");
        printInfoLater("  ", g.toString());

        Set<Goal> oldGoals = goalTracker.getCurrentGoals().stream().collect(Collectors.toSet());
        Optional<Plan> possibleGoalPlan = goalTracker.adoptGoal(g);
        if (possibleGoalPlan.isPresent()) {

            System.out.println(simpleInducer.induce(goalTracker.getProblem(), goalTracker.getProblem().getStart(), g, possibleGoalPlan.get()));

            printSuccessLater("Successfully added:", g.getName());
            printDebug1Later("Current Goals and Constraint:", "\n" + goalTracker.getCurrentGoals().stream().collect(Collectors.toSet()).toString());

            Set<Goal> newGoals = goalTracker.getCurrentGoals().stream().collect(Collectors.toSet());

            if (!Sets.difference(oldGoals, newGoals).isEmpty()) {
                printDroppedLater("", "Dropped Goals and Contraints:" + Sets.difference(oldGoals, newGoals));

            }
            Plan plan = possibleGoalPlan.get();
            printDebug2Later("Plan:", plan.getActions().isEmpty() ? "No plan needed. Already satisfied." : "\n" + plan.toString());

        } else {

            printFailureLater("", "Could not add " + g);

            printDebug1Later("Current Goals and Contraints: ", goalTracker.getCurrentGoals().stream().map(Goal::getName).collect(Collectors.toSet()).toString());

        }

    }

    static void printInfoLater(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printInfo(x, y), header, message));

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

    static void printSuccessLater(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printSuccess(x, y), header, message));

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


    static void printDebug1Later(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printDebug1(x, y), header, message));

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

    static void printDebug2Later(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printDebug2(x, y), header, message));

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

    static void printFailureLater(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printFailure(x, y), header, message));

    }

    static void printFailure(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.RED);   //setting format
        cp.print(message);
        cp.clear();
        cp.println("");
        cp.clear();
    }


    static void printDroppedLater(String header, String message) {


        printQueue.add(Triple.of((x, y) -> RunDemo.printDropped(x, y), header, message));

    }


    static void printDropped(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.RED);   //setting format
        cp.print("Dropped Goals:");
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }
}
