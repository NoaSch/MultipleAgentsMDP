package withSet;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static withSet.Constants.VAR_AGENTS_LOC;
import static withSet.Constants.*;

/**
 * Created by noa on 22-Aug-16.
 */
public class DataMulesState implements ObjectInstance, MutableState {

   // public int agentLoc;
    public Set<Integer> brokenSensors;

    public Integer[] timeFromLastRepair;

    public Integer[] agentsLoc;



    private final static List<Object> keys = Arrays.<Object>asList(VAR_AGENTS_LOC, VAR_BROKEN_SENSORS,VAR_TIME_FROM_LAST_REPAIR);
    //constructor
    //DataMulesState(int agentLoc, Set<Integer> brokenSensors, int[] timeFromLastRepair){//}, String name ) {
    DataMulesState(Integer[] agentLoc, Set<Integer> brokenSensors, Integer[] timeFromLastRepair){//}, String name ) {
        this.agentsLoc = agentLoc;
        this.brokenSensors = brokenSensors;
        this.timeFromLastRepair = timeFromLastRepair;
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
        return new DataMulesState(agentsLoc, brokenSensors,timeFromLastRepair);//,newName );
    }

    //set data from keys
    public MutableState set(Object variableKey, Object value) {
       /* if(variableKey.equals(VAR_AGENT_LOC)){
            this.agentLoc = StateUtilities.stringOrNumber(value).intValue();
        }
        else*/ if(variableKey.equals(VAR_BROKEN_SENSORS)){
            this.brokenSensors = (Set<Integer>) value;
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
    if(variableKey.equals(VAR_BROKEN_SENSORS))
            return brokenSensors;

        else if(variableKey.equals(VAR_AGENTS_LOC))
            return agentsLoc;

        else if(variableKey.equals(VAR_TIME_FROM_LAST_REPAIR))
            return timeFromLastRepair;
        throw new UnknownKeyException(variableKey);
    }


    public State copy() {
        return new DataMulesState(agentsLoc, brokenSensors,timeFromLastRepair);//, name );
    }


    public static DataMulesState createInitialState() {

        //all sensors will work at beginning
        Set<Integer> brokenSensors = new HashSet<Integer>();
        //brokenSensors.add(0);
        Integer[] timeFromLastRepair = new Integer[NUM_OF_SENSORS];
        for(int i = 0; i < timeFromLastRepair.length; i++) {
            timeFromLastRepair[i] = (int) GUARANTEED_REMAIN_OK;
        }
        Integer[] agentsLoc = new Integer[NUM_OF_AGENTS];
        //the agents are on each of the n first sensors
        for(int i = 0; i < agentsLoc.length; i++) {
            agentsLoc[i] = i;
        }
        //int agentLoc = 0;

        return new DataMulesState(agentsLoc,brokenSensors,timeFromLastRepair);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataMulesState other = (DataMulesState) o;

        return Arrays.deepEquals(agentsLoc,other.agentsLoc) &&
               Arrays.deepEquals(timeFromLastRepair,other.timeFromLastRepair)&&
                brokenSensors.equals(other.brokenSensors);

    }
    //@Override
   /* public String toString() {
        return StateUtilities.stateToString(this);
    }*/
    @Override
   public String toString() {
        {
            String lRepair = "";
            for (int i = 0; i < timeFromLastRepair.length; i++) {
                lRepair = lRepair + " " + timeFromLastRepair[i];
            }
            String agLocs = "";
            for (int i = 0; i < agentsLoc.length; i++) {
                agLocs = agLocs + " " + agentsLoc[i];
            }
            String s =  "agentsLoc: {" + agLocs + "}, broken:" + brokenSensors.toString() + " , lastRepair: {" + lRepair + "}";
            return s;
        }
    }
}
