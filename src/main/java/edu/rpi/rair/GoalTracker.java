package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by naveensundarg on 1/14/17.
 */
public class GoalTracker {





    private final Set<Formula> background;
    private State currentState;
    private final Set<Goal> currentGoals;
    private final Planner planner;
    private final Set<Action> actions;

    public GoalTracker(Set<Formula> background, State startState, Set<Action> actions) {
        this.background = background;
        this.currentState = startState;
        this.currentGoals = CollectionUtils.newEmptySet();
        this.planner = new DepthFirstPlanner();
        this.actions = actions;
    }


    public Optional<Plan> adoptGoal(Goal goal) {



        Optional<Set<Plan>> possiblePlans = planner.plan(background, actions, currentState, goal.getGoalState());

        if (!possiblePlans.isPresent()) {

            return Optional.empty();

        } else if (possiblePlans.get().isEmpty()) {

            throw new AssertionError("Unexpected condition: possible plans is empty");

        } else {

            Set<Plan> plans = possiblePlans.get();

            Optional<Plan> noConflictPlan = plans.stream().filter(plan -> plan.noConflicts(currentGoals)).findAny();


            if (noConflictPlan.isPresent()) {

              /*
               * If there is any plan without any goal conflicts, then adopt the goal.
               */
                currentGoals.add(goal);
                return noConflictPlan;

            } else {

              /*
               *  Find goals to drop.
               *  For each plan, find the sum of the priorities of the goals that conflict.
               *  If any plan exists, where sum of priorities of existing goals is less than the new goal,
               *  add the new goal and remove the conflict goals.
               *  Otherwise return false and don't adopt the new goal.
               */

                boolean feasiblePlanExists = false;
                double bestPriorityGap = 0;
                Set<Goal> bestRemovalCandidates = null;
                Plan feasiblePlan = null;
                for (Plan plan : plans) {

                    Set<Goal> conflictingGoals = plan.getConflictingGoals(currentGoals);
                    double conflictSum = conflictingGoals.stream().mapToDouble(Goal::getPriority).sum();
                    double gap = goal.getPriority() - conflictSum;

                    if(gap > 0 && gap > bestPriorityGap ){

                        feasiblePlanExists = true;
                        bestPriorityGap = gap;
                        feasiblePlan = plan;
                        bestRemovalCandidates= conflictingGoals;
                    }
                }

                if(feasiblePlan!=null){

                    currentGoals.removeAll(bestRemovalCandidates);
                    currentGoals.add(goal);

                    return Optional.of(feasiblePlan);
                }
                else {

                    return Optional.empty();
                }


            }


        }


    }

    public Set<Goal> getCurrentGoals() {
        return currentGoals;
    }
}
