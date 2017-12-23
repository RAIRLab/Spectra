package edu.rpi.rair;

import edu.rpi.rair.Plan;
import edu.rpi.rair.utils.PlanningProblem;

/**
 * Created by naveensundarg on 12/19/17.
 */
public interface Inducer {

     Plan induce(PlanningProblem planningProblem, State start, Goal goal, Plan plan);

}
