package com.naveensundarg.planner.utils;

import com.naveensundarg.planner.BreadthFirstPlanner;
import com.naveensundarg.planner.Plan;
import com.naveensundarg.planner.Planner;
import com.naveensundarg.shadow.prover.utils.Reader;

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
        
        Planner breadthFirstPlanner = new BreadthFirstPlanner();

        for (PlanningProblem planningProblem : planningProblemList) {
            Optional<Set<Plan>> optionalPlans = breadthFirstPlanner.plan(
                planningProblem.getBackground(),
                planningProblem.getActions(),
                planningProblem.getStart(),
                planningProblem.getGoal());

            if(optionalPlans.isPresent()) {
                System.out.println(optionalPlans.get().toString());
            }
            else {
                System.out.println("FAILED");
            }
        }

    }
}