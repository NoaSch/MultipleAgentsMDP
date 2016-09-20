package MultipleAgents;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static MultipleAgents.Constants.ACTION_REPAIR;
import static MultipleAgents.Constants.ACTION_STAY;

/**
 * Created by noa on 29-Aug-16.
 */
public class MultipleAction implements ActionType {
    public String typeName;
    public MuleSimpleAction action;
    protected List<Action> allActions;

    private List<Action> myList;
    private List<Action> emptyList;


    public MultipleAction(String typeName) {

        this.typeName = typeName;
        this.action = new MuleSimpleAction(typeName);
        this.allActions = Arrays.asList(new Action[]{this.action});


        myList = Arrays.asList((Action)(this.action));
        emptyList = new ArrayList<Action>();


    }

    public String typeName() {
        return this.typeName;
    }


    public Action associatedAction(String strRep) {
        return this.action;
    }


    public List<Action> allApplicableActions(State s) {
        DataMulesState currState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));

        //Boolean allGood = true;
        String[] actions = this.action.actions;
        for(int i = 0; i < actions.length; i++) {
            //System.out.println("i: " + i + " action: " + actions[i]);
            if (actions[i].equals(ACTION_REPAIR)) {
             /*   if (currState.brokenSensors.isEmpty())
                    return myList; //return empty List*/
                if (currState.getNumberOfBroken() == 0)
                    //return myList; //return empty List
                        return emptyList;


              /*  if (currState.timeFromLastRepair[currState.agentsLoc[i]] < GUARANTEED_REMAIN_OK ||
                        !currState.brokenSensors.contains(currState.agentsLoc[i])) {
                    return myList; //return empty List*/
                if (currState.timeFromLastRepair[currState.agentsLoc[i]] != -1)
                    // return myList;
                    return emptyList;

            }

            else if(actions[i].equals(ACTION_STAY))
            {
                //nothing to do.
            }
            //moving
            else if(currState.agentsLoc[i] == action.actionDestinations[i])
//                    actions[i].substring(6)))
                //return myList; //return empty List
                return emptyList;
            else{
            }

        }
        return myList;
    }

}
