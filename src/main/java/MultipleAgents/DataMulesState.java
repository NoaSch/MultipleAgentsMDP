package MultipleAgents;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static MultipleAgents.Constants.*;

/**
 * Created by noa on 22-Aug-16.
 */
public class DataMulesState implements ObjectInstance, MutableState {

   // public int agentLoc;
  //  public Set<Integer> brokenSensors;

    //public Integer[] timeFromLastRepair;
    Map<Integer,Integer> timeFromLastRepair;

    public Integer[] agentsLoc;



    private final static List<Object> keys = Arrays.<Object>asList(VAR_AGENTS_LOC,VAR_TIME_FROM_LAST_REPAIR);
    //constructor
   // DataMulesState(Integer[] agentLoc, Set<Integer> brokenSensors, Integer[] timeFromLastRepair){//}, String name ) {
        DataMulesState(Integer[] agentLoc, Map<Integer,Integer> timeFromLastRepair){//}, String name ) {
        this.agentsLoc = agentLoc;
       // this.brokenSensors = brokenSensors;
        this.timeFromLastRepair= timeFromLastRepair;
       // this.name = name;
    }

    public String className() {
        return CLASS_STATE;
    }

    public String name() {
        return CLASS_STATE;
    }

    //copy this state to same state with different name
    public ObjectInstance copyWithName(String newName) {
       // return new DataMulesState(agentsLoc, brokenSensors,timeFromLastRepair);//,newName );

        return new DataMulesState(agentsLoc,timeFromLastRepair);//,newName );
    }

    //set data from keys
    public MutableState set(Object variableKey, Object value) {
        if(variableKey.equals(VAR_AGENTS_LOC)){
            this.agentsLoc = (Integer[])value;
        }
        else if(variableKey.equals(VAR_TIME_FROM_LAST_REPAIR)){
            this.timeFromLastRepair = (Map<Integer,Integer>) value;
        }
        else{
            throw new UnknownKeyException(variableKey);
        }
        return this;
    }


    public List<Object> variableKeys() {
        return keys;
    }

    public Object get(Object variableKey) {
  /*  if(variableKey.equals(VAR_BROKEN_SENSORS))
            return brokenSensors;*/

        /*else*/ if(variableKey.equals(VAR_AGENTS_LOC))
            return agentsLoc;

        else if(variableKey.equals(VAR_TIME_FROM_LAST_REPAIR))
            return timeFromLastRepair;
        throw new UnknownKeyException(variableKey);
    }


    /*public State copy() {
        return new DataMulesState(agentsLoc, brokenSensors,timeFromLastRepair);//, name );
    }*/


    public State copy() {
        return new DataMulesState(agentsLoc,timeFromLastRepair);//, name );
    }

    public static DataMulesState createInitialState() {

        //all sensors will work at beginning
        //Set<Integer> brokenSensors = new HashSet<Integer>();
        //brokenSensors.add(0);
        Map<Integer,Integer> timeFromLastRepair = new HashMap<Integer, Integer>();
        for(int i = 0; i < NUM_OF_SENSORS; i++) {
            timeFromLastRepair.put(i,(int)GUARANTEED_REMAIN_OK);
        }
        Integer[] agentsLoc = new Integer[NUM_OF_AGENTS];

        //the agents are on each of the n first sensors
        for(int i = 0; i < agentsLoc.length; i++) {
            agentsLoc[i] = i;
        }
        //int agentLoc = 0;

        return new DataMulesState(agentsLoc,timeFromLastRepair);
    }

    public static DataMulesState createInitialState(int nSensors, int nAgents) {

        //all sensors will work at beginning
        //Set<Integer> brokenSensors = new HashSet<Integer>();
        //brokenSensors.add(0);
        Map<Integer,Integer> timeFromLastRepair = new HashMap<Integer, Integer>();
        for(int i = 0; i < nSensors; i++) {
            timeFromLastRepair.put(i,(int)GUARANTEED_REMAIN_OK);
        }
        Integer[] agentsLoc = new Integer[nAgents];
        //the agents are on each of the n first sensors
        for(int i = 0; i < agentsLoc.length; i++) {
            agentsLoc[i] = i;
        }
        //int agentLoc = 0;

        return new DataMulesState(agentsLoc,timeFromLastRepair);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataMulesState other = (DataMulesState) o;

        return Arrays.deepEquals(agentsLoc,other.agentsLoc) &&
               (timeFromLastRepair.equals(other.timeFromLastRepair));//&&
               // brokenSensors.equals(other.brokenSensors);

    }
    //@Override
   /* public String toString() {
        return StateUtilities.stateToString(this);
    }*/
    @Override
   public String toString() {
        {
            String lRepair = "";
            for (Integer i:timeFromLastRepair.keySet()) {
                lRepair = lRepair + " " + i+":" + timeFromLastRepair.get(i);
            }
            String agLocs = "";
            for (int i = 0; i < agentsLoc.length; i++) {
                agLocs = agLocs + " " + agentsLoc[i];
            }
            //String s =  "agentsLoc: {" + agLocs + "}, broken:" + brokenSensors.toString() + " , lastRepair: {" + lRepair + "}";
            String s =  "agentsLoc " + agLocs + " , lastRepair " + lRepair ;
            return s;
        }
    }

    public int getNumberOfBroken() {
        int ans = 0;
        for (Integer val:timeFromLastRepair.values()) {
            if(val == -1)
                ans++;
        }
        return ans;
    }

    public int getWontBroken() {
        int ans = 0;
        for (Integer val:timeFromLastRepair.values()) {
            if(val != GUARANTEED_REMAIN_OK)
                ans++;
        }
        return ans;
    }

}

