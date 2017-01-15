package edu.rpi.rair;

import com.naveensundarg.shadow.prover.utils.Reader;
import edu.rpi.rair.utils.PlanningProblem;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.testng.Assert.*;

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

        PlanningProblem planningProblem = planningProblemList.get(2);
        System.out.println(depthFirstPlanner.plan(planningProblem.background, planningProblem.actions, planningProblem.start, planningProblem.goal));
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
                planningProblem.background,
                planningProblem.actions,
                planningProblem.start,
                planningProblem.goal);

        Assert.assertTrue(possiblePlans.isPresent());

    }
}