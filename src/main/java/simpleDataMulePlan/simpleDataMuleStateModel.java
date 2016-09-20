package simpleDataMulePlan;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.generic.GenericOOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static simpleDataMulePlan.Constants.*;


/**
 * Created by noa on 22-Aug-16.
 */
public class simpleDataMuleStateModel implements FullStateModel {

    //Choose one state to move
    public State sample(State state, Action action) {
        int agentLoc;
        //Get all possible states for this source state and action
        List<StateTransitionProb> lResult = stateTransitions(state, action);
        double rand = Math.random();
        double sum = 0;
        //initialize an array due to the probability of reaching each state
        double[] probs = new double[lResult.size()];
        //initialize the array
        for (int i = 0; i < probs.length; i++) {
            sum = sum + lResult.get(i).p;
            probs[i] = sum;
        }
        //Return the proper cell
       for(int j = 0; j < probs.length; j++){
           if(rand <= probs[j])
               return lResult.get(j).s;
        }
        return lResult.get(0).s;

    }

    //Transition function
    public List<StateTransitionProb> stateTransitions(State state, Action action) {
        List<StateTransitionProb> result = new ArrayList<StateTransitionProb>();
       // System.out.println("in trans");
        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));

        //find the potentially broken at the next time step
        Set <Integer> canBeBroken = getCanBeBroken(currentState, action);

        for (Set<Integer> newBrokens : powerSet(canBeBroken))
        {
            //calculate the probability of getting there
            double prob = calcProb(canBeBroken,newBrokens);
            //add the known broken to the new broken set
            newBrokens.addAll(currentState.brokenSensors);
           // System.out.println("new broken before:" +newBrokens );

            //if the action is repar
            Integer[] newLastRepair = new Integer[currentState.timeFromLastRepair.length];
            if (action.actionName().equals(ACTION_REPAIR))
            {
                newBrokens.remove(currentState.agentLoc);
                newLastRepair = addOne(currentState.timeFromLastRepair, currentState.agentLoc);
            }
            //add one time steps to all sensors' last repair
             else
            {
                newLastRepair = addOne(currentState.timeFromLastRepair,0);
            }
            if (action.actionName().equals(ACTION_REPAIR) || action.actionName().equals(ACTION_STAY) ) {
               // System.out.println(action.actionName());
               // System.out.println("newBroken: " + newBrokens + "  newLAst:" + newLastRepair[0] + " " +newLastRepair[1] + " " + newLastRepair[2] ) ;
                result.add(new StateTransitionProb(new GenericOOState(new DataMulesState(currentState.agentLoc, newBrokens, newLastRepair)), prob));
            }

            else
            {

                int loc = getMoveLocation(action);
                //You can't move to yourself
                if(loc != currentState.agentLoc)
                result.add(new StateTransitionProb(new GenericOOState(new DataMulesState(loc, newBrokens, newLastRepair)),prob));
                else
                {
                    result.add(new StateTransitionProb(new GenericOOState(new DataMulesState(currentState.agentLoc, newBrokens, newLastRepair)), prob));

                }
            }

        }
        return result;

    }

    //return the number of sensor to move
    public static int getMoveLocation(Action action) {
        String sLoc = action.actionName().substring(6);
        return Integer.parseInt(sLoc);
    }

    //calculate the probability of getting to a state
    //p^number of new broken * (1-p)^number of the sensors that could be broken but still working
    private double calcProb(Set <Integer> canBeBroken, Set<Integer> newBrokens) {
        double countOfP = newBrokens.size();
        double stillWorking = canBeBroken.size() - countOfP;
        double result = Math.pow(PROB_SENSOR_BREAK,countOfP)*Math.pow(1-PROB_SENSOR_BREAK,stillWorking);
        return result;
    }


    //Gets a set of sensors that can be broken at the next time steps
    private Set<Integer> getCanBeBroken(DataMulesState currentState, Action action) {
        //get the working sensor and get all the power set of them (they can get broken next time)
        Set<Integer> result = new HashSet<Integer>();
        Set <Integer> working = findWorking(currentState.brokenSensors);

        for (Integer i:working)
        {
            if(currentState.timeFromLastRepair[i]!=0 &&  currentState.timeFromLastRepair[i]>=GUARANTEED_REMAIN_OK) {
                result.add(i);
            }
        }
        //if a sensor was fix it can be broken
        if (action.actionName().equals(ACTION_REPAIR))
        {
            result.remove(currentState.agentLoc);
        }
        return result;
    }

    //find the sensors that are working
    private Set<Integer> findWorking(Set<Integer> brokenSensors) {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < NUM_OF_SENSORS; i++)
            if (!brokenSensors.contains(i))
                result.add(i);

        return result;
    }

    //add onte time step to an array
    private Integer[] addOne(Integer[] current, int except)
    {
        Integer[] result = new Integer[current.length];


        for (int i = 0; i < current.length; i++)
        {
            result[i] = new Integer(current[i]);
            if(i == except)
            {
                result[i] = 1;
            }
            else
            {
              if(current[i] != -1 && current[i] < GUARANTEED_REMAIN_OK)
                  result[i] +=1;
            }

        }
        return result;
    }

    //return the power set of a given set
    private <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
}

