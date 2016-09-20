package simpleDataMulePlan;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by noa on 28-Aug-16.
 */

public class MoveToAction extends UniversalActionType {

    private  List<Action> myList;

    public MoveToAction(int i) {
        super("moveTo"+ i);
    }

    @Override
    public List<Action> allApplicableActions(State s) {
        List<Action> myList = new ArrayList<Action>();
        DataMulesState currState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        if(currState.agentLoc != Integer.parseInt(this.action.actionName().substring(6)))
            myList = Arrays.asList(this.action);
        return myList;

    }
}
