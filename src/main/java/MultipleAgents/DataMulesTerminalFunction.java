package MultipleAgents;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

/**
 * Created by noa on 21-Aug-16.
 */
public class DataMulesTerminalFunction implements TerminalFunction{

    public DataMulesTerminalFunction(){}

    //check if current state is a terminal state
    public boolean isTerminal(State s){
        return false;
    }
}
