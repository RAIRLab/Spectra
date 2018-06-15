package com.naveensundarg.planner.utils;

import com.naveensundarg.planner.DepthFirstPlanner;
import com.naveensundarg.planner.PlanMethod;
import com.naveensundarg.planner.Planner;

import java.util.List;

/**
 * Created by naveensundarg on 12/22/17.
 */
public class Sandbox {

    public static void demoPlanMethods(String[] args) throws com.naveensundarg.shadow.prover.utils.Reader.ParsingException {

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

    public static void main(String[] args) throws com.naveensundarg.shadow.prover.utils.Reader.ParsingException {

        List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(Sandbox.class.getResourceAsStream("../problems/tora/sodacan.clj")));

        Planner depthFirstPlanner = new DepthFirstPlanner();

        PlanningProblem planningProblem = planningProblemList.stream().filter(problem -> problem.getName().equals("soda can challenge")).findFirst().get();


        depthFirstPlanner.plan(planningProblem.getBackground(), planningProblem.getActions(), planningProblem.getStart(), planningProblem.getGoal()).ifPresent(
                System.out::println
        );



    }
}
