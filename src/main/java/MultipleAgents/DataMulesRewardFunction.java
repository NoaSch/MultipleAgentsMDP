package MultipleAgents;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;

import static MultipleAgents.Constants.*;

/**
 * Created by noa on 22-Aug-16.
 */
public class DataMulesRewardFunction implements RewardFunction {

    public  double reward(State source, Action action, State dest) {
        /*if(dest == null )
        {
            System.out.println("dest is null");
        }*/
        DataMulesState currState = (DataMulesState) (((OOState) dest).object(Constants.CLASS_STATE));
    int numOfBroken = currState.getNumberOfBroken();
        double reward = -1 * numOfBroken;

        String [ ] actArr = action.actionName().split(", ");

        for(int i = 0; i < actArr.length; i++) {

            if ((!actArr[i].equals(ACTION_REPAIR)) && (!actArr[i].equals(ACTION_STAY)))// && getMoveLocation(action) == currState.agentLoc)
                reward += -1 * MOVING_COST;
            if (actArr[i].equals(ACTION_REPAIR))
                reward += -1 * FIXING_COST;
        }
        return reward;
    }
}
