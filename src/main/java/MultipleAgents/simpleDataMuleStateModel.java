package MultipleAgents;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.generic.GenericOOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import java.util.*;

import static MultipleAgents.Constants.*;


/**
 * Created by noa on 22-Aug-16.
 */
public class simpleDataMuleStateModel implements FullStateModel {

    Set <Integer> canBeBroken = new HashSet<Integer>();
    public State sample(State state, Action action) {

        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));
        Random ran = new Random();
        int randNum = ran.nextInt(MAX_BROKEN+1);
        int newNum = randNum - currentState.getWontBroken();
        Set<Integer> repaired = new HashSet<Integer>();
        //  Set<Integer> moved = new HashSet<Integer>();
        //int numOfBroken = currentState.getNumberOfBroken();
        String[] actionsArr = ((MuleSimpleAction)(action)).actions;
        for(int i = 0; i < actionsArr.length; i++)
        {
            if(actionsArr[i].equals(ACTION_REPAIR)) {
                //newBrokens.remove(currentState.agentsLoc[i]);
                repaired.add(currentState.agentsLoc[i]);

            }
        }
        canBeBroken.clear();
        canBeBroken = getCanBeBroken(currentState, action, repaired);
        Set newBrokens = randomSubSet(newNum,canBeBroken);

        Integer[] newLastRepair = addOne(currentState.timeFromLastRepair, repaired,newBrokens);
        DataMulesState dms = new DataMulesState(getLocsAfter(currentState,actionsArr,((MuleSimpleAction) (action))), newLastRepair);
        return new GenericOOState(dms);

    }

    private  Set<Integer> randomSubSet(int outputSize, Set<Integer> canBeBroken) {
        Random rand = new Random();
        List<Integer> copyList = new ArrayList<Integer>(canBeBroken);
        //System.out.println("size" + copySet.size());
        Set<Integer> res = new HashSet<Integer>();
        int randInt;
        int randFrom = outputSize;
        for(int i = 0; i < outputSize; i ++)
        {
            if(copyList.size() == 1) {
                res.add(copyList.get(0));
                copyList.remove(copyList.get(0));
                randFrom--;
            }
            else
            {
                randInt = rand.nextInt(randFrom + 1);
                res.add(copyList.get(randInt));
                copyList.remove(copyList.get(randInt));
                randFrom--;
            }
        }
        return res;
    }

    //Choose one state to move
    /*public State sample(State state, Action action) {
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

    }*/

    //Transition function
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        List<StateTransitionProb> result = new ArrayList<StateTransitionProb>();

        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));

        Set<Integer> repaired = new HashSet<Integer>();

        String[] actionsArr = ((MuleSimpleAction)(action)).actions;

        for(int i = 0; i < actionsArr.length; i++)
        {
            if(actionsArr[i].equals(ACTION_REPAIR)) {
                //newBrokens.remove(currentState.agentsLoc[i]);
                repaired.add(currentState.agentsLoc[i]);
            }
        }

        //find the potentially broken at the next time step
        canBeBroken.clear();
       canBeBroken = getCanBeBroken(currentState, action, repaired);

        Set<Set<Integer>> powSet = powerSet(canBeBroken,currentState.getNumberOfBroken()-repaired.size());
        if(powSet.isEmpty())
        {
            Integer[] newLastRepair = addOne(currentState.timeFromLastRepair, repaired);

            StateTransitionProb stp = new StateTransitionProb(new GenericOOState(new DataMulesState(getLocsAfter(currentState,actionsArr,((MuleSimpleAction) (action))), newLastRepair)), 1);
            result.add(stp);
        }

        for (Set<Integer> newBrokens : powSet)
        {
            //calculate the probability of getting there
           // int canBeNum  = MAX_BROKEN - currentState.getNumberOfBroken() + repaired.size();
            int canBeNum  = canBeBroken.size() - currentState.getNumberOfBroken() + repaired.size();
            double prob = calcProb(canBeNum,canBeBroken,newBrokens);

            //add the known broken to the new broken set
            for(int i = 0; i < NUM_OF_SENSORS;  i++)
            {
                if(currentState.timeFromLastRepair[i] == -1 && !newBrokens.contains(i) && !repaired.contains(i))
                    newBrokens.add(i);
            }

            Integer[] newLastRepair = addOne(currentState.timeFromLastRepair, repaired,newBrokens);
            StateTransitionProb stp = new StateTransitionProb(new GenericOOState(new DataMulesState(getLocsAfter(currentState,actionsArr,((MuleSimpleAction) (action))), newLastRepair)), prob);
            result.add(stp);

        }
        return result;

    }

    private Integer[] addOne(Integer[] current, Set<Integer> repaired) {
        Integer[] result = new Integer[current.length];

        for (int i = 0; i < current.length; i++)
        {
            result[i] = new Integer(current[i]);
            if(repaired.contains(i))
            {
                result[i] = 1;
            }

            else if(current[i] != -1 && current[i] != 0 && current[i] < GUARANTEED_REMAIN_OK)
                result[i] +=1;
        }
        return result;
    }


    private Integer[] getLocsAfter(DataMulesState state, String[] actionsArr, MuleSimpleAction action) {
     //  Integer[] result = state.agentsLoc;
        Integer[] result = new Integer[NUM_OF_AGENTS];
        for(int i = 0; i < actionsArr.length;i++)
        {
            if(!(actionsArr[i].equals(ACTION_REPAIR) ) && !(actionsArr[i].equals(ACTION_STAY)))
            {
                result[i] = action.actionDestinations[i];
            }
            else
                result[i] = state.agentsLoc[i];
        }
        return result;

    }

    //return the number of sensor to move
  /*  public static int getMoveLocation(Action action) {
        String sLoc = action.actionName().substring(6);
        return Integer.parseInt(sLoc);
    }*/
  /*  public static int getMoveLocation(String action) {
        String act = action;
        String sLoc = act.substring(6);
        if(sLoc.length() == 0)
        {
            System.out.println("sLoc:  "+ act);
        }
        return Integer.parseInt(sLoc);
    }*/

    //calculate the probability of getting to a state
    //p^number of new broken * (1-p)^number of the sensors that could be broken but still working
/*    private double calcProb(Set <Integer> canBeBroken, Set<Integer> newBrokens) {
        double countOfP = newBrokens.size();
        double stillWorking = canBeBroken.size() - countOfP;
        double result = Math.pow(PROB_SENSOR_BREAK,countOfP)*Math.pow(1-PROB_SENSOR_BREAK,stillWorking);
        return result;
    }*/

    private double calcProb(int canBeSize, Set<Integer> canBeBroken, Set<Integer> newBrokens) {
        int brokenSize = newBrokens.size();
       // int canBeSize = canBeBroken.size() - currentState.getNumberOfBroken();
        double p1;
        double p2;
        int cooseeRes = choose(canBeSize,brokenSize);
        p2 = 1/(double)cooseeRes;

        p1 = p(brokenSize,canBeSize);
        return p1* p2;
    }

    private double p(int x, int canBeSize){
        if(x == 0)
        {
            double sum = 0;
            for(int i = 1; i <= MAX_BROKEN; i++)
           // for(int i = 1; i <= canBeSize; i++)
            {
                sum += p(i,canBeSize);
            }
            return 1-sum;
        }
        else
            return Math.pow(PROB_SENSOR_BREAK,x)* choose(canBeSize,x);
    }

    public int  choose(int n, int k) {
        if (k == 0)
            return 1;
        return (n * choose(n - 1, k - 1)) / k;

    }
    //Gets a set of sensors that can be broken at the next time steps
    private Set<Integer> getCanBeBroken(DataMulesState currentState, Action action, Set<Integer> repaired) {
        //get the working sensor and get all the power set of them (they can get broken next time)
        Set<Integer> result = new HashSet<Integer>();

        Set <Integer> working = findWorking(currentState.timeFromLastRepair);

        for (Integer i:working)
        {
            if(currentState.timeFromLastRepair[i]>=GUARANTEED_REMAIN_OK) {
                result.add(i);
            }
        }
        //if a sensor was fix it cant be broken
       // if (action.actionName().equals(ACTION_REPAIR))
        String[] sArr = action.actionName().split(", ");
 /*       for(int i = 0; i < NUM_OF_AGENTS; i++)
        {
            //if an agent fix the sensor it can't be broken
            if(sArr[i].equals(ACTION_REPAIR))
                result.remove(currentState.agentsLoc[i]);
        }*/
 for (Integer i : repaired)
 {
     result.remove(i);
 }
        return result;
    }

    //find the sensors that are working
   /* private Set<Integer> findWorking(Set<Integer> brokenSensors) {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < NUM_OF_SENSORS; i++)
            if (!brokenSensors.contains(i))
                result.add(i);

        return result;
    }*/

    private Set<Integer> findWorking(Integer[] lastRepair) {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < NUM_OF_SENSORS; i++)
            if (lastRepair[i] != -1)
                result.add(i);
        return result;
    }

    //add onte time step to an array
    private Integer[] addOne(Integer[] current, Set<Integer> except, Set<Integer> broken)
    {
        Integer[] result = new Integer[current.length];


        for (int i = 0; i < current.length; i++)
        {
            result[i] = new Integer(current[i]);
            if(except.contains(i))
            {
                result[i] = 1;
            }
            else if(broken.contains(i)) {
                result[i] = -1;
            }
             else if(current[i] != -1 && current[i] != 0 && current[i] < GUARANTEED_REMAIN_OK)
                  result[i] +=1;
            }
        return result;
    }

    //return the power set of a given set
    private <T> Set<Set<T>> powerSet(Set<T> originalSet, int numberOfBroken) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest, numberOfBroken)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            if(newSet.size() <= MAX_BROKEN - numberOfBroken) {
                sets.add(newSet);
            }
                sets.add(set);
            }
        return sets;
    }


     /*  private <T> Set<Set<T>> powerSet(Set<T> originalSet, int numBroken) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest,numBroken)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
*/
}

