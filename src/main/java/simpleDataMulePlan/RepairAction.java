package simpleDataMulePlan;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static simpleDataMulePlan.Constants.GUARANTEED_REMAIN_OK;

/**
 * Created by noa on 28-Aug-16.
 */
public class RepairAction  extends UniversalActionType {
    public RepairAction() {
        super("repair");
    }

    @Override
    public List<Action> allApplicableActions(State s) {
        List<Action> myList = new ArrayList<Action>();
        DataMulesState currState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        if(currState.timeFromLastRepair[currState.agentLoc] >= GUARANTEED_REMAIN_OK && currState.brokenSensors.contains(currState.agentLoc))
            myList = Arrays.asList(this.action);
        return myList;

    }
}
