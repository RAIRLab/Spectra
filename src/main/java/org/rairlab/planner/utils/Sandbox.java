package org.rairlab.planner.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import org.rairlab.planner.*;
import org.rairlab.planner.search.DepthFirstPlanner;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by naveensundarg on 12/22/17.
 */
public class Sandbox {

    public static void demoPlanMethods(String[] args) throws org.rairlab.shadow.prover.utils.Reader.ParsingException {

        PlanMethod seriatedPlanMethod = (Reader.readPlanMethodsFrom(Sandbox.class.getResourceAsStream("../problems/learning/dry.clj"))).get(0);


        List<GoalTrackingProblem> goalTrackingProblemList1 = (GoalTrackingProblem.readFromFile(Sandbox.class.getResourceAsStream("../problems/seriated/seriated_challenge_1.clj")));

        List<GoalTrackingProblem> goalTrackingProblemList2 = (GoalTrackingProblem.readFromFile(Sandbox.class.getResourceAsStream("../problems/seriated/seriated_challenge_2.clj")));

        System.out.println(seriatedPlanMethod.apply(goalTrackingProblemList1.get(0).getPlanningProblem().getBackground(),
                            goalTrackingProblemList1.get(0).getPlanningProblem().getStart().getFormulae(),
                    goalTrackingProblemList1.get(0).getGoalNamed("G1").getGoalState().getFormulae(),
                    goalTrackingProblemList1.get(0).getPlanningProblem().getActions()

                ));


        System.out.println(seriatedPlanMethod.apply(goalTrackingProblemList2.get(0).getPlanningProblem().getBackground(),
                goalTrackingProblemList2.get(0).getPlanningProblem().getStart().getFormulae(),
                goalTrackingProblemList2.get(0).getGoalNamed("G1").getGoalState().getFormulae(),
                goalTrackingProblemList2.get(0).getPlanningProblem().getActions()

        ));




    }

    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


    public static void main(String[] args) throws org.rairlab.shadow.prover.utils.Reader.ParsingException {



         List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(Sandbox.class.getResourceAsStream("../problems/ai2thor/FloorPlan28.clj")));

        Planner depthFirstPlanner = new DepthFirstPlanner();

        PlanningProblem planningProblem = planningProblemList.stream().filter(problem -> problem.getName().equals("FloorPlan28")).findFirst().get();


        depthFirstPlanner.plan(planningProblem.getBackground(), planningProblem.getActions(), planningProblem.getStart(), planningProblem.getGoal()).ifPresent(plans-> {

           // System.out.println(plans);

            List<Plan> plansList = plans.stream().sorted(Comparator.comparing(plan -> plan.getActions().size())).collect(Collectors.toList());
                   if(!plansList.isEmpty()) {

                       System.out.println("***************************");
                       cp.setAttribute(Ansi.Attribute.BOLD);
                       cp.println("PLAN FOUND");
                       cp.clear();
                       System.out.println("------------------------------");
                       cp.setForegroundColor(Ansi.FColor.BLACK);
                       cp.setBackgroundColor(Ansi.BColor.GREEN);   //setting format

                       cp.println(plansList.get(0));
                   }
                 });



    }
}
