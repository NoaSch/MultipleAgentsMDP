package withSet;

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

import static withSet.Constants.*;


/**
 * Created by noa on 22-Aug-16.
 */
public class simpleDataMuleStateModel implements FullStateModel {

    //Choose one state to move
    public State sample(State state, Action action) {
        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));

        //    int agentLoc;
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
      //  State st = state.copy();
        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));
       // DataMulesState currentState = (DataMulesState) origState.copy();
        //find the potentially broken at the next time step
        Set <Integer> canBeBroken = getCanBeBroken(currentState, action);

        String[] actionsArr = action.actionName().split(", ");

        for (Set<Integer> newBrokens : powerSet(canBeBroken))
        {
            //calculate the probability of getting there
            double prob = calcProb(canBeBroken,newBrokens);
            //add the known broken to the new broken set
            for(Integer in :currentState.brokenSensors )
            {
                if(!newBrokens.contains(in))
                    newBrokens.add(in);
            }

            //if the action is repar
            Integer[] newLastRepair = new Integer[currentState.timeFromLastRepair.length];

          Set<Integer> repaired = new HashSet<Integer>();
            Set<Integer> moved = new HashSet<Integer>();
          for(int i = 0; i < actionsArr.length; i++)
          {
              if(actionsArr[i].equals(ACTION_REPAIR)) {
                  newBrokens.remove(currentState.agentsLoc[i]);
                  repaired.add(currentState.agentsLoc[i]);
                  //newLastRepair = addOne(currentState.timeFromLastRepair, currentState.agentsLoc);
              }
          }

            newLastRepair = addOne(currentState.timeFromLastRepair, repaired);

            //result.add(new StateTransitionProb(new GenericOOState(new DataMulesState(currentState.agentsLoc, newBrokens, newLastRepair)), prob));
           // StateTransitionProb stp = new StateTransitionProb(new GenericOOState(new DataMulesState(getLocsAfter(currentState,actionsArr), newBrokens, newLastRepair)), prob);

            result.add(new StateTransitionProb(new GenericOOState(new DataMulesState(getLocsAfter(currentState,actionsArr), newBrokens, newLastRepair)), prob));

        }
        return result;

    }

    private Integer[] getLocsAfter(DataMulesState state, String[] actionsArr) {
     //  Integer[] result = state.agentsLoc;
        Integer[] result = new Integer[NUM_OF_AGENTS];
        for(int i = 0; i < actionsArr.length;i++)
        {
            if(!(actionsArr[i].equals(ACTION_REPAIR) ) && !(actionsArr[i].equals(ACTION_STAY)))
            {
                result[i] = getMoveLocation(actionsArr[i]);
            }
            else
                result[i] = state.agentsLoc[i];
        }
        return result;

    }

    //return the number of sensor to move
    public static int getMoveLocation(Action action) {
        String sLoc = action.actionName().substring(6);
        return Integer.parseInt(sLoc);
    }
    public static int getMoveLocation(String action) {
        String act = action;
        String sLoc = act.substring(6);
        if(sLoc.length() == 0)
        {
            System.out.println("sLoc:  "+ act);
        }
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
       // if (action.actionName().equals(ACTION_REPAIR))
        String[] sArr = action.actionName().split(", ");
        for(int i = 0; i < NUM_OF_AGENTS; i++)
        {
            //if an agent fix the sensor it can't be broken
            if(sArr[i].equals("repair"))
                result.remove(currentState.agentsLoc[i]);
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
    private Integer[] addOne(Integer[] current, Set<Integer> except)
    {
        Integer[] result = new Integer[current.length];


        for (int i = 0; i < current.length; i++)
        {
            result[i] = new Integer(current[i]);
            if(except.contains(i))
            {
                result[i] = 1;
            }
            else
            {
              if(current[i] != 0 && current[i] < GUARANTEED_REMAIN_OK)
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

