package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.MDPSolver;
import burlap.behavior.singleagent.planning.Planner;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import com.google.common.collect.BiMap;

import static MultipleAgents.Constants.ACTION_REPAIR;
import static MultipleAgents.Constants.ACTION_STAY;
import static MultipleAgents.DataMulesDomain.generateDomain;
import static MultipleAgents.DataMulesState.createInitialState;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;
   /* Map<domainIntPair, Integer> sensorsTosDom;
    Map<domainIntPair,Integer> agentsTosDom;*/
   BiMap< Integer,domainIntPair> sensorsTosDom;
    BiMap< Integer,domainIntPair> agentsTosDom;
    BiMap<domainIntPair, Integer> inverseAgents;
    BiMap<domainIntPair, Integer> inversSensors;
    //DataMulesState[] dmsArr;
    int numOfDmains = 2;
    Policy[] policyArr;
   // Map<OOSADomain, Policy> plicyMap;



    public HybridPlanner(Planner p) {
        plannerOriginal = p;
        planner =p;

    }

    public void resetSolver() {
        plannerOriginal.resetSolver();
    }


    public DataMulesState[] extractSmallerStates(State s)
    {
        DataMulesState [] dmsArrRes = new DataMulesState[numOfDmains];
        DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        ///createSmallerState
        Integer[][] lastRepair = new Integer[numOfDmains][2]; //////change
        Integer[][] agentLocs = new Integer[numOfDmains][2];//////change


        int domainNum;
        //set agentLocs
        for(int i = 0; i < dmState.agentsLoc.length; i++)
        {
            domainNum =  agentsTosDom.get(i).domainNum;
            ////check it
            agentLocs[domainNum][agentsTosDom.get(i).numVal] = sensorsTosDom.get(dmState.agentsLoc[i]).numVal ;
        }
        for(int i = 0; i < dmState.timeFromLastRepair.length; i++)
        {
            //check if it the same domain like up
            domainNum =  sensorsTosDom.get(i).domainNum;
            lastRepair[domainNum][sensorsTosDom.get(i).numVal] = agentsTosDom.get(dmState.timeFromLastRepair[i]).numVal ;
        }

        //creathe the states of each small domain
        for(int i = 0; i < numOfDmains; i++) {

            dmsArrRes[i] = new DataMulesState(agentLocs[i], lastRepair[i]);
        }
        return dmsArrRes;
    }


    public Policy planFromState(State initialState) {

        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = null;
        agentsTosDom = null;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[numOfDmains];

        int senCounter;
        int agCounter;

        //List<OOSADomain> domains = null;
        OOSADomain[] domains = new OOSADomain[numOfDmains];
        policyArr = new Policy[numOfDmains];

        for (int i = 0; i < 2; i++) {
            OOSADomain dom = generateDomain(2, 2);
            domains[i] = dom;
            planner.setDomain(dom);
            Policy pol = planner.planFromState(createInitialState(2, 2));
            policyArr[i] = pol;
        }
          /*  sensorsTosDom.put(new domainIntPair(domains.get(0),0),0);
            sensorsTosDom.put(new domainIntPair(domains.get(0),1),1);
            sensorsTosDom.put(new domainIntPair(domains.get(1),0),2);
            sensorsTosDom.put(new domainIntPair(domains.get(1),1),3);

            agentsTosDom.put(new domainIntPair(domains.get(0),0),0);
            agentsTosDom.put(new domainIntPair(domains.get(0),1),1);
            agentsTosDom.put(new domainIntPair(domains.get(1),0),2);
            agentsTosDom.put(new domainIntPair(domains.get(1),1),3);*/

        sensorsTosDom.put(0, new domainIntPair(0, 0));
        sensorsTosDom.put(1, new domainIntPair(0, 1));
        sensorsTosDom.put(2, new domainIntPair(1, 0));
        sensorsTosDom.put(3, new domainIntPair(1, 1));

        agentsTosDom.put(0, new domainIntPair(0, 0));
        agentsTosDom.put(1, new domainIntPair(0, 1));
        agentsTosDom.put(2, new domainIntPair(1, 0));
        agentsTosDom.put(3, new domainIntPair(1, 1));

        inverseAgents = agentsTosDom.inverse();
       sensorsTosDom.inverse();

        return new Policy() {
            public Action action(State s) {
                return actionHybrid(s);
            }

            public double actionProb(State s, Action a) {
                return actionProbHybrid(s,a);
            }

            public boolean definedFor(State s) {
                return definedForHybrid(s);
            }
        };
    }




       /*  DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
         ///createSmallerState
         Integer[][] lastRepair = new Integer[numOfDmains][2]; //////change
         Integer[][] agentLocs = new Integer[numOfDmains][2];//////change
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[numOfDmains];


         int domainNum;
         //set agentLocs
         for(int i = 0; i < dmState.agentsLoc.length; i++)
         {
             domainNum =  agentsTosDom.get(i).domainNum;
             ////check it
             agentLocs[domainNum][agentsTosDom.get(i).numVal] = sensorsTosDom.get(dmState.agentsLoc[i]).numVal ;
         }
         for(int i = 0; i < dmState.timeFromLastRepair.length; i++)
         {
             //check if it the same domain like up
             domainNum =  sensorsTosDom.get(i).domainNum;
             lastRepair[domainNum][sensorsTosDom.get(i).numVal] = agentsTosDom.get(dmState.timeFromLastRepair[i]).numVal ;
         }
         String[] actionsFinal = new String[dmState.agentsLoc.length];
         domainIntPair dPair = null;
         BiMap<domainIntPair, Integer> inverseAgents = agentsTosDom.inverse();
         BiMap<domainIntPair, Integer> inversSensors = sensorsTosDom.inverse();
         //creathe the states of each small domain
         for(int i = 0; i < numOfDmains; i++) {

             dmsArr[i] = new DataMulesState(agentLocs[i], lastRepair[i]);
         }*/
       public Action actionHybrid(State s) {
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[numOfDmains];
       DataMulesState [] dmsArr = extractSmallerStates(s);
         String[] actionsFinal = new String[dmsArr[0].agentsLoc.length]; ///if all sizes are same
         domainIntPair dPair = null;
         for(int i = 0; i < numOfDmains; i++) {
             actionsSmaller[i] = new MuleSimpleAction(policyArr[i].action(dmsArr[i]).actionName());
             //if the action is stay or repair

             for(int j = 0; j <  actionsSmaller[i].actions.length; j++)
             {
                //if the action is repair of stay no need to translate sensor number
                 if (actionsSmaller[i].actions[j].equals(ACTION_REPAIR) || actionsSmaller[i].actions[j].equals(ACTION_STAY)) {
                     dPair = new domainIntPair(i, j);
                     actionsFinal[inverseAgents.get(dPair)] = actionsSmaller[i].actions[j];
                 }
                 //action is moving
                 else
                 {
                      //j is the small agent num
                     int dest = actionsSmaller[i].actionDestinations[j];
                     dPair = new domainIntPair(i, dest);
                     int finalDest = inversSensors.get(dPair);
                     actionsFinal[inverseAgents.get(j)]= "" +finalDest;
                 }

             }
         }
         return new MuleSimpleAction(actionsFinal.toString());

     }

     public double actionProbHybrid(State s, Action a) {

            double ans =1;
            // double[] allProb = new double[numOfDmains];
             DataMulesState [] dmsArr = extractSmallerStates(s);
             MuleSimpleAction[] actionsSmaller = extractSmallerActions(a);

            for(int i = 0; i < numOfDmains; i ++)
            {
                ans *= policyArr[i].actionProb(dmsArr[i],actionsSmaller[i]);
            }
            return ans;
            ///
         //continue
         ////

     }

    private MuleSimpleAction[] extractSmallerActions(Action a) {
        MuleSimpleAction action = new MuleSimpleAction(a.actionName());
        int numOfAgents = action.actions.length;
        String[][] strArr = new String[numOfDmains][numOfAgents];
        MuleSimpleAction[] actionArrRes = new MuleSimpleAction[numOfDmains];
        for(int i =0; i <numOfAgents;i++)
        {
            //if the action is repair of stay no need to translate sensor number
            if (action.actions[i].equals(ACTION_REPAIR) ||action.actions[i].equals(ACTION_STAY)) {
                strArr[agentsTosDom.get(i).domainNum][agentsTosDom.get(i).numVal] = action.actions[i];
                // actionsFinal[inverseAgents.get(dPair)] = actionsSmaller[i].actions[j];
            }
            //action is moving
            else
            {
                //j is the small agent num
                int dest = action.actionDestinations[i];
                strArr[agentsTosDom.get(i).domainNum][agentsTosDom.get(i).numVal] = ""+ sensorsTosDom.get(dest).numVal;
            }

        }
        for(int i =0; i < numOfDmains; i++ ) {
            actionArrRes[i] = new MuleSimpleAction(strArr[i].toString());
        }
        return actionArrRes;

    }


    public boolean definedForHybrid(State s) {
         return true;
     }

}

