package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Sets;
import org.rairlab.planner.search.DepthFirstPlanner;

import java.util.Comparator;
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
    private final PlanningProblem problem;
    public GoalTracker(PlanningProblem problem, Set<Formula> background, State startState, Set<Action> actions) {
        this.background = background;
        this.currentState = startState;
        this.currentGoals = CollectionUtils.newEmptySet();
        this.planner = new DepthFirstPlanner();
        this.actions = actions;

        this.problem = problem;

        Operations.reset();
    }


    public synchronized boolean deleteGoal(Goal goal){

        return currentGoals.remove(goal);

    }

    public synchronized boolean addToBackground(Formula formula){

        return background.add(formula);
    }

     public synchronized boolean addAllToBackground(Set<Formula> formulae){

        return background.addAll(formulae);
    }

    public synchronized boolean deleteFromBackground(Formula formula){

        return background.remove(formula);
    }

     public synchronized boolean removeAllFromBackground(Set<Formula> formulae){

        return background.removeAll(formulae);
    }



    public synchronized Optional<Plan> adoptGoal(Goal goal) {



        Optional<Set<Plan>> possiblePlans = planner.plan(problem, background, actions, currentState, goal.getGoalState());

        if (!possiblePlans.isPresent()) {

            return Optional.empty();

        } else if (possiblePlans.get().isEmpty()) {

            throw new AssertionError("Unexpected condition: possible plans is empty");

        } else {

            Set<Plan> plans = possiblePlans.get();

            Optional<Plan> possibleNoConflictPlan = plans.stream().filter(plan -> plan.noConflicts(currentGoals)).
                    sorted(Comparator.comparing(plan->plan.getActions().size())).findAny();


            if (possibleNoConflictPlan.isPresent()) {

              /*
               * If there is any plan without any goal conflicts, then adopt the goal.
               */
                Plan noConflictPlan = possibleNoConflictPlan.get();
                currentGoals.add(goal);
                currentState = noConflictPlan.getExpectedStates().get(noConflictPlan.getExpectedStates().size()-1);
                return possibleNoConflictPlan;

            } else {

              /*
               *  Find goals to drop.
               *  For each plan, find the sum of the priorities of the goals that conflict.
               *  If any plan exists, where sum of priorities of existing goals is less than the new goal,
               *  add the new goal and remove the conflict goals.
               *  Otherwise return false and don't adopt the new goal.
               */

                boolean feasiblePlanExists = false;
                int bestPlanSize = Integer.MAX_VALUE;
                double bestPriorityGap = 0;
                Set<Goal> bestRemovalCandidates = null;
                Set<Plan> feasiblePlans = Sets.newSet();
                for (Plan plan : plans) {

                    Set<Goal> conflictingGoals = plan.getConflictingGoals(currentGoals);
                    double conflictSum = conflictingGoals.stream().mapToDouble(Goal::getPriority).sum();
                    double gap = goal.getPriority() - conflictSum;

                    if(gap > 0 && gap >= bestPriorityGap && plan.getActions().size() <= bestPlanSize){

                        feasiblePlanExists = true;
                        bestPriorityGap = gap;
                        bestPlanSize = plan.getActions().size();
                        feasiblePlans.add(plan);
                        bestRemovalCandidates= conflictingGoals;
                    }
                }

                if(!feasiblePlans.isEmpty()){

                    Plan bestPlan = feasiblePlans.stream().
                            min(Comparator.comparing(plan->plan.getActions().stream().mapToInt(Action::getWeight).sum())).get();
                    currentGoals.removeAll(bestRemovalCandidates);
                    currentGoals.add(goal);
                    currentState = bestPlan.getExpectedStates().get(bestPlan.getExpectedStates().size()-1);

                    return Optional.of(bestPlan);
                }
                else {

                    return Optional.empty();
                }


            }


        }


    }

    public Set<Formula> getBackground() {
        return background;
    }

    public State getCurrentState() {
        return currentState;
    }

    public PlanningProblem getProblem() {
        return problem;
    }

    public Set<Goal> getCurrentGoals() {
        return currentGoals;
    }
}
