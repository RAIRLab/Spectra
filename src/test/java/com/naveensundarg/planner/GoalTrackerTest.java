package org.rairlab.planner;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import org.rairlab.planner.utils.GoalTrackingProblem;
import org.rairlab.shadow.prover.utils.Reader;

import java.util.List;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class GoalTrackerTest {


    public static void main(String[] args) throws Reader.ParsingException {

        List<GoalTrackingProblem> goalTrackingProblemList = (GoalTrackingProblem.readFromFile(Planner.class.getResourceAsStream("goal_management_6.clj")));


        GoalTrackingProblem goalTrackingProblem = goalTrackingProblemList.get(0);

        GoalTracker goalTracker = new GoalTracker(goalTrackingProblem.getPlanningProblem(), goalTrackingProblem.getPlanningProblem().getBackground(),
                goalTrackingProblem.getPlanningProblem().getStart(),
                goalTrackingProblem.getPlanningProblem().getActions());


        ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G1");
        cp.clear();


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


        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G3");
        cp.clear();
        goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G4"));

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.println("Adding goal G3");
        cp.clear();
        goalTracker.adoptGoal(goalTrackingProblem.getGoalNamed("G4"));



    }
}