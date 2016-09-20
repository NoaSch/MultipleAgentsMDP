package withSet;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;

import java.util.*;

import static withSet.Constants.GUARANTEED_REMAIN_OK;

/**
 * Created by noa on 29-Aug-16.
 */
public class MultipleAction extends UniversalActionType {
    public MultipleAction(String typeName) {
        super(typeName);
    }

    @Override
    public List<Action> allApplicableActions(State s) {
        DataMulesState currState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        List<Action> myList = new ArrayList<Action>();

        //Boolean allGood = true;
        String[] actions = this.action.actionName().split(", ");
        for(int i = 0; i < actions.length; i++) {
            //System.out.println("i: " + i + " action: " + actions[i]);
            if (actions[i].equals("repair")) {
                if (currState.brokenSensors.isEmpty())
                    return myList; //return empty List

                if (currState.timeFromLastRepair[currState.agentsLoc[i]] < GUARANTEED_REMAIN_OK ||
                        !currState.brokenSensors.contains(currState.agentsLoc[i])) {
                    return myList; //return empty List
                }
            }
            else if(actions[i].equals("stay"))
            {
                //nothing to do.
            }
            //moving
            else if(currState.agentsLoc[i] == Integer.parseInt(actions[i].substring(6)))
                return myList; //return empty List
            else{
            }

        }
        myList = Arrays.asList(this.action);
        return myList;
    }

}
