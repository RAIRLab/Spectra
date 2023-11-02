package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.planner.heuristics.ConstantHeuristic;
import org.rairlab.planner.search.AStarPlanner;

import org.rairlab.shadow.prover.utils.Reader;
import py4j.GatewayServer;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public final class Py4JServer {

    private AStarPlanner astarplanner;


    public Py4JServer(){
        astarplanner = new AStarPlanner();
    }


    public AStarPlanner getPlanner(){
        return astarplanner;
    }

    public static void main(String[] args) throws UnknownHostException {

        System.out.println("--------------- Starting GatewayServer --------------- ");
        System.out.println("--------------- Started Py4J Gateway   --------------- ");

        InetAddress addr;
        System.setProperty("java.net.preferIPv4Stack", "true");
        addr = Inet4Address.getByName("0.0.0.0");
        GatewayServer server = new GatewayServer(new Py4JServer(),25333, 25334, addr,addr, 0, 0, null);
        System.out.println("--------------- Started Py4J Gateway   --------------- ");

        server.start();

    }

    public String proveFromDescription(String fileString){
        try {

            List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(new ByteArrayInputStream(fileString.getBytes())));

            AStarPlanner astarplanner = new AStarPlanner();

            PlanningProblem planningProblem = planningProblemList.get(0);


            Set<Plan> plans = astarplanner.plan(
                    planningProblem.getBackground(),
                    planningProblem.getActions(),
                    planningProblem.getStart(),
                    planningProblem.getGoal(),
                    ConstantHeuristic::h
            );

            if(plans.size() > 0) {
                return plans.toString();
            }
            else {
                return "FAILED";
            }

        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return null;
        }
    }

}