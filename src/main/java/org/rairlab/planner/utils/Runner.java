package org.rairlab.planner.utils;

import org.rairlab.planner.search.AStarPlanner;
import org.rairlab.planner.Plan;
import org.rairlab.planner.heuristics.ConstantHeuristic;
import org.rairlab.shadow.prover.utils.Reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;



public final class Runner {

    public static void main(String[] args) {

        System.out.println("--------------- Starting Spectra --------------- ");


        // Grab filename from argument list
        if (args.length < 1) {
            System.out.println("Need to include filename with planning problem and description.");
            return;
        }
        String fileName = args[0];


        // Read File
        FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Parse File
        List<PlanningProblem> planningProblemList;
        try {
            planningProblemList = PlanningProblem.readFromFile(fileStream);

        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return;
        }

        AStarPlanner astarplanner = new AStarPlanner();
        astarplanner.setK(1);

        for (PlanningProblem planningProblem : planningProblemList) {

            long start = System.currentTimeMillis();
            Set<Plan> plans = astarplanner.plan(
                planningProblem.getBackground(),
                planningProblem.getActions(),
                planningProblem.getStart(),
                planningProblem.getGoal(),
                ConstantHeuristic::h
            );
            long end = System.currentTimeMillis();

            if(plans.size() > 0) {
                System.out.println(plans.toString());
                System.out.println("Time (ms): " + (end - start));
            }
            else {
                System.out.println("FAILED");
            }
        }

    }
}