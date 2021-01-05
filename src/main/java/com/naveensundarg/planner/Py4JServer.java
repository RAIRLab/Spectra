package com.naveensundarg.planner;

import com.naveensundarg.planner.utils.PlanningProblem;
import com.naveensundarg.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import com.naveensundarg.shadow.prover.core.proof.Justification;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.Problem;
import com.naveensundarg.shadow.prover.utils.ProblemReader;
import com.naveensundarg.shadow.prover.utils.Reader;
import py4j.GatewayServer;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;


public final class Py4JServer {


    private DepthFirstPlanner depthFirstPlanner;


    public Py4JServer(){

        depthFirstPlanner = new DepthFirstPlanner();

    }


    public Planner getPlanner(){
        return depthFirstPlanner;
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

    public ArrayList newEmptyList(){

        return new ArrayList();
    }

    public String proveFromDescription(String fileString){
        try {

            List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(new ByteArrayInputStream(fileString.getBytes())));

            Planner depthFirstPlanner = new DepthFirstPlanner();

            PlanningProblem planningProblem = planningProblemList.get(0);


            Optional<Set<Plan>> optionalPlans = depthFirstPlanner.plan(
                    planningProblem.getBackground(),
                    planningProblem.getActions(),
                    planningProblem.getStart(),
                    planningProblem.getGoal());

            if(optionalPlans.isPresent()) {
                return optionalPlans.get().toString();
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