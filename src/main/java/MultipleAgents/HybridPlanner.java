package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.MDPSolver;
import burlap.behavior.singleagent.planning.Planner;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.generic.GenericOOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;

import static MultipleAgents.Constants.*;
import static MultipleAgents.DataMulesDomain.generateDomain;
import static MultipleAgents.DataMulesState.createInitialState;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;

   BiMap< Integer,domainIntPair> sensorsTosDom;
    BiMap< Integer,domainIntPair> agentsTosDom;
    BiMap<domainIntPair, Integer> inverseAgents;
    BiMap<domainIntPair, Integer> inversSensors;
    int numOfDmains;
    int origSensorsNum = NUM_OF_SENSORS;
    int origAgentsNum = NUM_OF_AGENTS;
    Policy[] policyArr;



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
        Integer[][] lastRepair = new Integer[numOfDmains][dmState.timeFromLastRepair.length/numOfDmains]; //////change
        Integer[][] agentLocs = new Integer[numOfDmains][dmState.agentsLoc.length/numOfDmains];//////change


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
           // lastRepair[domainNum][sensorsTosDom.get(i).numVal] = agentsTosDom.get(dmState.timeFromLastRepair[i]).numVal ;
            lastRepair[domainNum][sensorsTosDom.get(i).numVal] = dmState.timeFromLastRepair[i];
        }

        //creathe the states of each small domain
        for(int i = 0; i < numOfDmains; i++) {

            dmsArrRes[i] = new DataMulesState(agentLocs[i], lastRepair[i]);
        }
        return dmsArrRes;
    }


    public Policy planFromState(State initialState)
    {
       return planFromState(initialState, 2);
    }
    public Policy planFromState(State initialState, int nOfDomains) {
        numOfDmains = nOfDomains;
        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = HashBiMap.create();

        agentsTosDom = HashBiMap.create();;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[numOfDmains];



        OOSADomain[] domains = new OOSADomain[numOfDmains];
        policyArr = new Policy[numOfDmains];

        int numOfSens = NUM_OF_SENSORS/numOfDmains;
        int numOfAg = NUM_OF_AGENTS/numOfDmains;
        for (int i = 0; i < numOfDmains; i++) {
            OOSADomain dom = generateDomain(numOfSens, numOfAg);
            domains[i] = dom;
            planner.setDomain(dom);
            Policy pol = planner.planFromState(new GenericOOState(createInitialState(numOfSens, numOfAg)));
            policyArr[i] = pol;
        }
        int sNum =0;

        while (sNum != origSensorsNum)
        {
            for(int newNum = 0; newNum < numOfSens; newNum++)
            {
                for(int d = 0; d < numOfDmains; d++)
                {
                    domainIntPair dmp = new domainIntPair(d, newNum);
                    sensorsTosDom.put(sNum, dmp);
                    sNum++;
                }
            }
        }

        int agNum =0;
        while (agNum != origAgentsNum)
        {
            for(int newNum = 0; newNum < numOfAg; newNum++)
            {
                for(int d = 0; d < numOfDmains; d++)
                {
                    domainIntPair dmp = new domainIntPair(d, newNum);
                    agentsTosDom.put(agNum, dmp);
                    agNum++;
                }
            }
        }


     /*  while (sNum != origSensorsNum)
        {
            for(int d = 0; d < numOfDmains; d++)
            {
                for (int newS =0; newS < numOfSens; newS++)
                {
                    domainIntPair dmp = new domainIntPair(d, newS);
                    sensorsTosDom.put(sNum, dmp);
                    sNum++;
                }
            }
        }
        int agNum =0;

        while (agNum != origAgentsNum)
        {
            for(int d = 0; d < numOfDmains; d++)
            {
                for (int newAg =0; newAg < numOfAg; newAg++)
                {
                    agentsTosDom.put(agNum, new domainIntPair(d, newAg));
                    agNum++;
                }
            }
        }*/

        inverseAgents = agentsTosDom.inverse();
       inversSensors = sensorsTosDom.inverse();

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
       public Action actionHybrid(State s) {
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[numOfDmains];
           DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
           boolean b = checkDMstate(dmState);
           if(!b)
           {
               return null;
           }
       DataMulesState [] dmsArr = extractSmallerStates(s);
        // String[] actionsFinal = new String[dmsArr[0].agentsLoc.length]; ///if all sizes are same
           String[] actionsFinal = new String[dmState.agentsLoc.length];

         domainIntPair dPair = null;
         for(int d = 0; d < numOfDmains; d++) {
             Action a = policyArr[d].action(new GenericOOState(dmsArr[d]));;
             actionsSmaller[d] = new MuleSimpleAction(a.actionName());
             //if the action is stay or repair

             for(int ac = 0; ac <  actionsSmaller[d].actions.length; ac++)
             {
                //if the action is repair of stay no need to translate sensor number
                 if (actionsSmaller[d].actions[ac].equals(ACTION_REPAIR) || actionsSmaller[d].actions[ac].equals(ACTION_STAY)) {
                     dPair = new domainIntPair(d, ac);
                     int tstNum =inverseAgents.get(dPair);

                     actionsFinal[tstNum] = actionsSmaller[d].actions[ac];
                 }
                 //action is moving
                 else
                 {
                      //j is the small agent num
                     int dest = actionsSmaller[d].actionDestinations[ac];
                     dPair = new domainIntPair(d, dest);
                     domainIntPair domAg = new domainIntPair(d,ac);
                     int finalDest = inversSensors.get(dPair);
                     int orgAg =inverseAgents.get(domAg);
                     actionsFinal[orgAg]= "" +finalDest;
                 }

             }
         }
           String retStr =Arrays.toString(actionsFinal);
           retStr = retStr.substring(1,retStr.length()-1);

         return new MuleSimpleAction(retStr);

     }

    public boolean checkDMstate(DataMulesState dmState){
        for(int i = 0; i < dmState.agentsLoc.length; i++)
        {
            if(agentsTosDom.get(i).domainNum != sensorsTosDom.get(dmState.agentsLoc[i]).domainNum)
            {
                return false;
            }
        }
        return true;
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

