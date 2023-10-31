package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;

/**
 * Created by naveensundarg on 12/19/17.
 */
public interface Inducer {

     PlanMethod induce(PlanningProblem planningProblem, State start, Goal goal, Plan plan);

}
