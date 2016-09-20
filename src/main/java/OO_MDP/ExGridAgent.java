package OO_MDP;

/**
 * Created by noa on 21-Aug-16.
 */
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.Arrays;
import java.util.List;



import static OO_MDP.ExampleOOGridWorld.VAR_X;
import static OO_MDP.ExampleOOGridWorld.VAR_Y;
import static OO_MDP.ExampleOOGridWorld.CLASS_AGENT;


@DeepCopyState
public class ExGridAgent implements ObjectInstance, MutableState {

    public int x;
    public int y;

    public String name = "agent";

    private final static List<Object> keys = Arrays.<Object>asList(VAR_X, VAR_Y);

    public ExGridAgent() {
    }

    public ExGridAgent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ExGridAgent(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public String className() {
        return CLASS_AGENT;
    }

    public String name() {
        return name;
    }

    public ObjectInstance copyWithName(String objectName) {
        return new ExGridAgent(x, y, objectName);
    }

    public MutableState set(Object variableKey, Object value) {
        if (variableKey.equals(VAR_X)) {
            this.x = StateUtilities.stringOrNumber(value).intValue();
        }
        else if (variableKey.equals(VAR_Y)) {
            this.y = StateUtilities.stringOrNumber(value).intValue();
        }
        else {
            throw new UnknownKeyException(variableKey);
        }
        return this;
    }

    public List<Object> variableKeys() {
        return keys;
    }

    public Object get(Object variableKey) {
        if(variableKey.equals(VAR_X)){
            return x;
        }
        else if(variableKey.equals(VAR_Y)){
            return y;
        }
        throw new UnknownKeyException(variableKey);
    }

    public ExGridAgent copy() {
        return new ExGridAgent(x, y, name);
    }

    @Override
    public String toString() {
        return StateUtilities.stateToString(this);
    }
}

