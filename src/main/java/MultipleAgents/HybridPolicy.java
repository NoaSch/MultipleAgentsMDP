package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.QValue;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;

import java.util.List;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPolicy implements Policy{
  //  private Policy p1;
  //  private Policy p2;

    private Policy[] polArr;
    int number;

    public HybridPolicy(Policy[] polArr) {
        this.polArr = polArr;
    }



    public Action action(State state) {
        DataMulesState currentState = (DataMulesState) (((OOState) state).object(Constants.CLASS_STATE));

        // Partition to smaller states
        State[] stArr = extractSmallerState(currentState);

        Action[] actArr = new Action[stArr.length];
        for (int i = 0; i < actArr.length; i++) {
            actArr[i] = polArr[i].action(stArr[i]);

        }
        return mergeActions(actArr);
    }

    private DataMulesState[]  extractSmallerState(State state) {
        return null;
    }

    private Action mergeActions(Action[] actArr) {
        return null;
    }

    public double actionProb(State state, Action action) {
        //return PolicyUtils.actionProbFromEnum(state, action);
        return 0;
    }

    public boolean definedFor(State state) {
        return true;
    }

    public void resetSolver() {

    }

    public Policy planFromState(State initialState) {
        return null;
    }

    public List<QValue> qValues(State s) {
        return null;
    }

    public double qValue(State s, Action a) {
        return 0;
    }

    public double value(State s) {
        return 0;
    }
}

