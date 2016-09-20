package MultipleAgents;

import burlap.mdp.core.action.SimpleAction;

import static MultipleAgents.Constants.ACTION_REPAIR;
import static MultipleAgents.Constants.ACTION_STAY;

/**
 * Created by user on 15/09/2016.
 */
public class MuleSimpleAction extends SimpleAction {
    public String[] actions;
    public int[] actionDestinations;

    public MuleSimpleAction(String typeName) {
        super(typeName);

        actions = this.actionName().split(", ");
        actionDestinations = new int[actions.length];
        for(int i=0;i<actions.length;i++)
        {
            if(!(actions[i].equals(ACTION_REPAIR) ) && !(actions[i].equals(ACTION_STAY)))
                actionDestinations[i]= Integer.parseInt(actions[i]);
            else
                actionDestinations[i] = -1;

        }
    }
}
