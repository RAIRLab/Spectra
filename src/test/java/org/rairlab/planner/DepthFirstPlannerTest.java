package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.utils.Reader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class DepthFirstPlannerTest {

    DepthFirstPlanner depthFirstPlanner;

    @BeforeMethod
    public void setUp() throws Exception {

        depthFirstPlanner = new DepthFirstPlanner();
    }

    public static void main(String[] args) throws Reader.ParsingException {

        List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(Planner.class.getResourceAsStream("completeness_problems.clj")));

        Planner depthFirstPlanner = new DepthFirstPlanner();

        PlanningProblem planningProblem = planningProblemList.stream().filter(problem -> problem.getName().equals("reasoning 3")).findFirst().get();


        depthFirstPlanner.plan(planningProblem.getBackground(), planningProblem.getActions(), planningProblem.getStart(), planningProblem.getGoal()).get().forEach(System.out::println);
    }

    @DataProvider
    public static Object[][] testCompletenessDataProvider() throws Reader.ParsingException {

        List<PlanningProblem> planningProblemList = (PlanningProblem.readFromFile(Planner.class.getResourceAsStream("completeness_problems.clj")));

        Object[][] cases = new Object[planningProblemList.size()][1];

        for (int i = 0; i < planningProblemList.size(); i++) {

            cases[i][0] = planningProblemList.get(i);

        }

        return cases;


    }



    @Test(dataProvider = "testCompletenessDataProvider")
    public void testCompletness(PlanningProblem planningProblem) throws Exception {

        Optional<Set<Plan>> possiblePlans = depthFirstPlanner.plan(
                planningProblem.getBackground(),
                planningProblem.getActions(),
                planningProblem.getStart(),
                planningProblem.getGoal());

        Assert.assertTrue(possiblePlans.isPresent());

        Set<Plan> plans = possiblePlans.get();

        if(planningProblem.getExpectedActionSequencesOpt().isPresent()){

            Set<List<Action>> actionSequences = plans.stream().map(Plan::getActions).collect(Collectors.toSet());
            Set<List<Action>> expectedActionSequences = planningProblem.getExpectedActionSequencesOpt().get();



            Assert.assertFalse(actionSequences.isEmpty());
         //   Assert.assertEquals(actionSequences, expectedActionSequences);
        }


    }
}