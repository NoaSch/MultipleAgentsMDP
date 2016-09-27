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

    Random ran = new Random(1);
    Set<Integer> canBeBroken = new HashSet<Integer>();

    public State sample(State state, Action action) {
        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));
        Set<Integer> repaired = new HashSet<Integer>();

        String[] actionsArr = ((MuleSimpleAction) (action)).actions;
        for (int i = 0; i < actionsArr.length; i++) {
            if (actionsArr[i].equals(ACTION_REPAIR)) {
                repaired.add(currentState.agentsLoc[i]);
            }
        }
        canBeBroken.clear();
        canBeBroken = getCanBeBroken(currentState);


        double[] probs = calcProbArr(Math.min(canBeBroken.size(),MAX_BROKEN));
       // double[] probs = calcProbArr(canBeBroken.size());
        int newNum = chooseSubSetSize(probs);

        Set<Integer> newBrokens = randomSubSet(newNum,canBeBroken);
        Integer[] newLastRepair = addOne(currentState.timeFromLastRepair, repaired,newBrokens);
        DataMulesState dms = new DataMulesState(getLocsAfter(currentState,actionsArr,((MuleSimpleAction) (action))), newLastRepair);
        return new GenericOOState(dms);
    }


    private int chooseSubSetSize(double[] probs) {
        //double rand = Math.random();
        double rand = ran.nextDouble();
        //Return the proper cell
        for(int j = 0; j < probs.length; j++) {
            if (rand <= probs[j])
                return j;
        }
        return 0;
    }

    private double[] calcProbArr(int PossibleBrokensSize) {
        double[] result = new double[PossibleBrokensSize+1];
        double sum = 0;


        for(int i = 0; i <= PossibleBrokensSize; i++) {
            sum += p(i);
            result[i] = sum;
        }
        return result;
    }

    private  Set<Integer> randomSubSet(int outputSize, Set<Integer> canBeBroken) {
        List<Integer> copyList = new ArrayList<Integer>(canBeBroken);
        //System.out.println("size" + copySet.size());
        Set<Integer> res = new HashSet<Integer>();
        int randInt;
        int optionsNum = copyList.size();
        for(int i = 0; i < outputSize; i ++)
        {
            if(copyList.size() == 0) {
            }

            if(copyList.size() == 1) {
                res.add(copyList.get(0));
                copyList.remove(copyList.get(0));
                optionsNum--;

            }
            else
            {
                randInt = ran.nextInt(optionsNum);
                res.add(copyList.get(randInt));
                copyList.remove(copyList.get(randInt));
                optionsNum--;
            }
        }
        return res;
    }

    //Transition function
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        List<StateTransitionProb> result = new ArrayList<StateTransitionProb>();
        DataMulesState currentState;
        if(state instanceof DataMulesState)
            currentState = (DataMulesState) state;
        else
            currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));

        Set<Integer> repaired = new HashSet<Integer>();

        String[] actionsArr = ((MuleSimpleAction)(action)).actions;


        for(int i = 0; i < actionsArr.length; i++)
        {
            if(actionsArr[i].equals(ACTION_REPAIR)) {
                //newBrokens.remove(currentState.agentsLoc[i]);
                repaired.add(currentState.agentsLoc[i]);
            }
        }

        if(currentState.timeFromLastRepair.equals(new int[]{-1, -1})&& currentState.agentsLoc.equals(new int[]{1, 1}))
        System.out.println(" ");

        //find the potentially broken at the next time step
        canBeBroken.clear();
       canBeBroken = getCanBeBroken(currentState);


       // Set<Set<Integer>> powSet = powerSet(canBeBroken,currentState.getNumberOfBroken()-repaired.size());
        Set<Set<Integer>> powSet = powerSet(canBeBroken);
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
            //int canBeNum  = canBeBroken.size() - currentState.getNumberOfBroken() + repaired.size();
            double prob = calcProb(newBrokens.size());

            //add the known broken to the new broken set
            //for(int i = 0; i < NUM_OF_SENSORS;  i++)
            for (int i = 0; i < currentState.timeFromLastRepair.length; i++)
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
      //  Integer[] result = new Integer[NUM_OF_AGENTS];
        Integer[] result = new Integer[state.agentsLoc.length];
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


       private double calcProb(int xNewBrokens) {
        //int brokenSize = newBrokens.size();
       // int canBeSize = canBeBroken.size() - currentState.getNumberOfBroken();
        double p1;
        double p2;
        int cooseRes = choose(canBeBroken.size(),xNewBrokens);
        p2 = 1/(double)cooseRes;

        p1 = p(xNewBrokens);
        double res = p1* p2;
        return res;
    }

    private double p(int x){
        int canBeNum = canBeBroken.size();
        if(x == 0)
        {
            double sum = 0;

            int minBroken = Math.min(MAX_BROKEN,canBeBroken.size());
            for(int i = 1; i <= minBroken; i++)
            {
                sum += p(i);
            }
            return 1-sum;
        }
        else {
            int ch = choose(canBeNum, x);
            double powP = Math.pow(PROB_SENSOR_BREAK, x);
            double pow1minP = Math.pow(1-PROB_SENSOR_BREAK,canBeNum - x);
            double ans =  powP*pow1minP * ch;
            return  ans ;
        }
    }


   /* private double calcProb(int xNewBrokens,int canBeSize) {
        //int brokenSize = newBrokens.size();
       // int canBeSize = canBeBroken.size() - currentState.getNumberOfBroken();
        double p1;
        double p2;
        int cooseRes = choose(canBeSize,xNewBrokens);
        p2 = 1/(double)cooseRes;

        p1 = p(xNewBrokens,canBeSize, canBeBroken.size());
        double res = p1* p2;
        return res;
    }

    private double p(int x, int maxBrokenSize, int canBeBrokenNum){
        if(x == 0)
        {
            double sum = 0;
            for(int i = 1; i <= MAX_BROKEN; i++)
           // for(int i = 1; i <= canBeSize; i++)
            {
                sum += p(i,maxBrokenSize);
            }
            return 1-sum;
        }
        else {
            int ch = choose(maxBrokenSize, x);
            double powP = Math.pow(PROB_SENSOR_BREAK, x);
            double pow1minP = Math.pow(1-PROB_SENSOR_BREAK,maxBrokenSize - x);
            double ans =  powP*pow1minP * ch;
            return  ans ;
        }
    }*/

    public int  choose(int n, int k) {
        if (k == 0)
            return 1;
        return (n * choose(n - 1, k - 1)) / k;

    }
    //Gets a set of sensors that can be broken at the next time steps
    private Set<Integer> getCanBeBroken(DataMulesState currentState) {
        //get the working sensor and get all the power set of them (they can get broken next time)
        Set<Integer> result = new HashSet<Integer>();

        Set<Integer> working = findWorking(currentState.timeFromLastRepair);

        for (Integer i : working) {
            if (currentState.timeFromLastRepair[i] >= GUARANTEED_REMAIN_OK) {
                result.add(i);
            }
        }
        return result;
    }


    private Set<Integer> findWorking(Integer[] lastRepair) {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < lastRepair.length; i++)
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
            if(newSet.size() <= MAX_BROKEN) {
                sets.add(newSet);
            }
                sets.add(set);
            }
        return sets;
    }
}

