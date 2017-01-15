package edu.rpi.rair.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.naveensundarg.shadow.prover.utils.Reader;
import edu.rpi.rair.GoalTracker;
import edu.rpi.rair.Planner;

import java.util.List;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class RunDemo {

        public static void main(String[] args) throws Reader.ParsingException {

        List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Planner.class.getResourceAsStream("goal_tracking_tests.clj")));


        GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

        GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem().getBackground(),
                goalTrackingProblem.getPlanningProblem().getStart(),
                goalTrackingProblem.getPlanningProblem().getActions());


        ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G1");
        cp.clear();

        boolean res1  = goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G1"));

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G2");
        cp.clear();

        goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G2"));

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G3");
        cp.clear();
        goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G3"));



    }

}
