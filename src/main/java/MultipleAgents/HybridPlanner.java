package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.MDPSolver;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.montecarlo.uct.UCT;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.generic.GenericOOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static MultipleAgents.Constants.*;
import static MultipleAgents.DataMulesDomain.generateDomain;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;

   BiMap< Integer, IntIntPair> sensorsTosDom;
    BiMap< Integer, IntIntPair> agentsTosDom;
    BiMap<IntIntPair, Integer> inverseAgents;
    BiMap<IntIntPair, Integer> inversSensors;
    int currNumOfDmains;
    int origNumOfDmains;
    int origSensorsNum = NUM_OF_SENSORS;
    int origAgentsNum = NUM_OF_AGENTS;
    Policy[] policyArr;
    int[][] domainsSize;
    int numUCT;
    int horizon;
    int maxItr;
    double delta;
    DataMulesDomain originalDom;



    public HybridPlanner( DataMulesDomain originalDom, Planner p, int numOfDomains, double delta, int maxItr) {
        plannerOriginal = p;
        planner =p;
        currNumOfDmains = numOfDomains;
        this.delta = delta;
        this.maxItr = maxItr;
        this.originalDom = originalDom;

    }
    public HybridPlanner(Planner p, int numOfDomains, int horizon, int numUCT) {
        plannerOriginal = p;
        planner =p;
        currNumOfDmains = numOfDomains;
        this.horizon = horizon;
        this.numUCT = numUCT;

    }

    public void resetSolver() {
        plannerOriginal.resetSolver();
    }


    public DataMulesState[] extractSmallerStates(State s)
    {
        DataMulesState [] dmsArrRes = new DataMulesState[currNumOfDmains];
        DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        ///createSmallerState
        Integer[][] lastRepair = new Integer[currNumOfDmains][]; //////change
        Integer[][] agentLocs = new Integer[currNumOfDmains][];//////change
        //set the number of sensors each domain
        for(int i = 0; i < currNumOfDmains; i++)
        {
            lastRepair[i] = new Integer[domainsSize[i][0]];
        }

        //set the nubmer of agents
        for(int i = 0; i < currNumOfDmains; i++)
        {
            agentLocs[i] = new Integer[domainsSize[i][1]];
        }


        int domainNum;
        int sensNum;
        int agNum;
        //set agentLocs
        for(int i = 0; i < dmState.agentsLoc.length; i++)
        {
            domainNum =  agentsTosDom.get(i).firstNum;

            ////check it
            agentLocs[domainNum][agentsTosDom.get(i).secondNum] = sensorsTosDom.get(dmState.agentsLoc[i]).secondNum;
        }
        for(int i = 0; i < dmState.timeFromLastRepair.length; i++)
        {
            //check if it the same domain like up
            domainNum =  sensorsTosDom.get(i).firstNum;
            int sens = sensorsTosDom.get(i).secondNum;
           // lastRepair[domainNum][sensorsTosDom.get(i).secondNum] = agentsTosDom.get(dmState.timeFromLastRepair[i]).secondNum ;
            lastRepair[domainNum][sens] = dmState.timeFromLastRepair[i];
        }

        //creathe the states of each small domain
        for(int i = 0; i < currNumOfDmains; i++) {

            dmsArrRes[i] = new DataMulesState(agentLocs[i], lastRepair[i]);
        }
        return dmsArrRes;
    }


    public Policy planFromState(State initialState)
    {
       return planFromState(initialState, currNumOfDmains);
    }


    public Policy planFromState2(State initialState, int nOfDomains) {
        currNumOfDmains = nOfDomains;
        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = HashBiMap.create();

        agentsTosDom = HashBiMap.create();;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[currNumOfDmains];
        domainsSize = new int[currNumOfDmains][2];


        OOSADomain[] domains = new OOSADomain[currNumOfDmains];
        policyArr = new Policy[currNumOfDmains];

        SetDomainsWithOutGraph();

        for (int i = 0; i < currNumOfDmains; i++) {
            DataMulesState initSmallState = extractSmallerStates(initialState)[i];
            OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i));
            domains[i] = dom;

            Planner newPlanner = null;
            if(plannerOriginal instanceof ValueIteration)
           {
              newPlanner = new ValueIteration(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),delta,maxItr);
            }
            else if(plannerOriginal instanceof UCT)
            {
                newPlanner = new UCT(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),horizon,numUCT,2);
            }


          // Policy pol = planner.planFromState(new GenericOOState(createInitialState(getSensorsNum(i),  getAgentsNum(i))));
            Policy pol = newPlanner.planFromState(new GenericOOState(initSmallState));
            policyArr[i] = pol;
           // List<State> allStates = StateReachability.getReachableStates(initSmallState, dom,  new SimpleHashableStateFactory());

         //   writePolicy(null,OUTPUT_PATH+ "testttttt-Dom" + i+".txt" ,pol,allStates);
        }


        inverseAgents = agentsTosDom.inverse();
       inversSensors = sensorsTosDom.inverse();

        return new Policy() {
            public Action action(State s) {
                try {
                    return actionHybrid(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public double actionProb(State s, Action a) {
                return actionProbHybrid(s,a);
            }

            public boolean definedFor(State s) {
                return definedForHybrid(s);
            }
        };
    }

    public Policy planFromStateWork(State initialState, int nOfDomains) {
        currNumOfDmains = nOfDomains;
        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = HashBiMap.create();

        agentsTosDom = HashBiMap.create();;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[currNumOfDmains];
        domainsSize = new int[currNumOfDmains][2];


        OOSADomain[] domains = new OOSADomain[currNumOfDmains];
        policyArr = new Policy[currNumOfDmains];
        setNumDomainsiIsNumAgents(initialState);
        //SetDomainsWithOutGraph();

        for (int i = 0; i < currNumOfDmains; i++) {
            DataMulesState initSmallState = extractSmallerStates(initialState)[i];
            OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i));
            domains[i] = dom;

            Planner newPlanner = null;
            if(plannerOriginal instanceof ValueIteration)
            {
                newPlanner = new ValueIteration(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),delta,maxItr);
            }
            else if(plannerOriginal instanceof UCT)
            {
                newPlanner = new UCT(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),horizon,numUCT,2);
            }


            // Policy pol = planner.planFromState(new GenericOOState(createInitialState(getSensorsNum(i),  getAgentsNum(i))));
            Policy pol = newPlanner.planFromState(new GenericOOState(initSmallState));
            policyArr[i] = pol;
            // List<State> allStates = StateReachability.getReachableStates(initSmallState, dom,  new SimpleHashableStateFactory());

            //   writePolicy(null,OUTPUT_PATH+ "testttttt-Dom" + i+".txt" ,pol,allStates);
        }


        inverseAgents = agentsTosDom.inverse();
        inversSensors = sensorsTosDom.inverse();

        return new Policy() {
            public Action action(State s) {
                try {
                    return actionHybrid(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public double actionProb(State s, Action a) {
                return actionProbHybrid(s,a);
            }

            public boolean definedFor(State s) {
                return definedForHybrid(s);
            }
        };
    }


    public Policy planFromState(State initialState, int nOfDomains) {
        currNumOfDmains = nOfDomains;
        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = HashBiMap.create();

        agentsTosDom = HashBiMap.create();;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[currNumOfDmains];
        domainsSize = new int[currNumOfDmains][2];


        OOSADomain[] domains = new OOSADomain[currNumOfDmains];
        policyArr = new Policy[currNumOfDmains];
        setNumDomainsiIsNumAgents(initialState);
        //SetDomainsWithOutGraph();

        for (int i = 0; i < currNumOfDmains; i++) {
            DataMulesState initSmallState = extractSmallerStates(initialState)[i];
            OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i));
            domains[i] = dom;

            Planner newPlanner = null;
            if(plannerOriginal instanceof ValueIteration)
            {
                newPlanner = new ValueIteration(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),delta,maxItr);
            }
            else if(plannerOriginal instanceof UCT)
            {
                newPlanner = new UCT(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),horizon,numUCT,2);
            }


            // Policy pol = planner.planFromState(new GenericOOState(createInitialState(getSensorsNum(i),  getAgentsNum(i))));
            Policy pol = newPlanner.planFromState(new GenericOOState(initSmallState));
            policyArr[i] = pol;
            // List<State> allStates = StateReachability.getReachableStates(initSmallState, dom,  new SimpleHashableStateFactory());

            //   writePolicy(null,OUTPUT_PATH+ "testttttt-Dom" + i+".txt" ,pol,allStates);
        }


        inverseAgents = agentsTosDom.inverse();
        inversSensors = sensorsTosDom.inverse();

        return new Policy() {
            public Action action(State s) {
                try {
                    return actionHybrid(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public double actionProb(State s, Action a) {
                return actionProbHybrid(s,a);
            }

            public boolean definedFor(State s) {
                return definedForHybrid(s);
            }
        };
    }

    private List<Integer> findSensorsInDomain(int i) {
        List<Integer> li = new ArrayList<Integer>();
        inversSensors = sensorsTosDom.inverse();
        for(IntIntPair iip : inversSensors.keySet())
        {
            if(iip.firstNum == i)
                li.add(iip.secondNum);
        }
        return li;
    }

    private void SetDomainsWithOutGraph() {
        int numOfSens = NUM_OF_SENSORS/ currNumOfDmains;
        int numOfAg = NUM_OF_AGENTS/ currNumOfDmains;

        int sNum =0;
        int firstExraSensor = NUM_OF_SENSORS - currNumOfDmains *numOfSens;
        int firstExraAgent = NUM_OF_AGENTS - currNumOfDmains *numOfAg;

        //calc how many sensors

        for(int i = firstExraSensor; i < currNumOfDmains; i ++)
        {
            domainsSize[i][0] = numOfSens;
        }

        for(int i = firstExraAgent; i < currNumOfDmains; i ++)
        {
            domainsSize[i][1] = numOfAg;
        }


        int idx;
        for(idx = 0; idx < firstExraSensor; idx++)
        {
            domainsSize[idx][0] = numOfSens + 1;
        }

        //calc how many agents

        for(idx = 0; idx < firstExraAgent; idx++)
        {
            domainsSize[idx][1] = numOfAg + 1;
        }

        while (sNum != currNumOfDmains *numOfSens)
        {
            for(int newNum = 0; newNum < numOfSens; newNum++)
            {
                for(int d = 0; d < currNumOfDmains &&sNum != origSensorsNum; d++)
                {
                    IntIntPair dmp = new IntIntPair(d, newNum);
                    sensorsTosDom.put(sNum, dmp);
                    sNum++;
                }
            }
        }
        //add the extra sensors
        for(int i = 0; i < origSensorsNum- currNumOfDmains *numOfSens; i++)
        {
            for(int d = 0; d < currNumOfDmains &&sNum != origSensorsNum; d++)
            {
                IntIntPair dmp = new IntIntPair(d, numOfSens);
                sensorsTosDom.put(sNum, dmp);
                sNum++;
            }
        }
        int agNum =0;
        //newNum =0;
        while (agNum != currNumOfDmains *numOfAg)
        {
            for(int newNum = 0; newNum < numOfAg; newNum++)
            {
                for(int d = 0; d < currNumOfDmains; d++)
                {
                    IntIntPair dmp = new IntIntPair(d, newNum);
                    agentsTosDom.put(agNum, dmp);
                    agNum++;
                }
            }
        }
        //add the extra agents
        for(int i = 0; i < origAgentsNum - currNumOfDmains *numOfAg; i++)
        {
            for(int d = 0; d < currNumOfDmains &&agNum != origAgentsNum; d++)
            {
                IntIntPair dmp = new IntIntPair(d, numOfAg);
                agentsTosDom.put(agNum, dmp);
                agNum++;
            }
        }
    }


//if numOfDomains = numOfAgents
    private void setNumDomainsiIsNumAgents(State initialState) {
        DataMulesState dmState = (DataMulesState) (((OOState) initialState).object(Constants.CLASS_STATE));
        //set each agent to seperate domain
        int setSensors =0;
       for(int i =0; i < NUM_OF_AGENTS; i++)
       {
           IntIntPair dmp = new IntIntPair(i, 0);
           agentsTosDom.put(i, dmp);
           domainsSize[i][1]= 1;
       }
       //set the current agents' location to the agent's domain
        for(int i =0; i < NUM_OF_AGENTS; i++)
        {
            IntIntPair dmp = new IntIntPair(i, 0);
            sensorsTosDom.put(dmState.agentsLoc[i], dmp);
            setSensors++;
            domainsSize[dmState.agentsLoc[i]][0] = 1;
        }
        //set the left seneors to domains
        while(setSensors != NUM_OF_SENSORS)
        {
                for(int i =0; i < NUM_OF_AGENTS; i++) {
                 if (findSensorToDomain(i))
                 {
                     setSensors++;
                 }
                }
            }
        }

    private void setNumDomainsiIsNOTNumAgents(State initialState) {
        origNumOfDmains = currNumOfDmains;
        currNumOfDmains = NUM_OF_AGENTS;
        setNumDomainsiIsNumAgents(initialState);


    }
    private boolean  findSensorToDomain(int domNum) {
        //////should be random?? if yes add all to set
        for(int i = 0; i < NUM_OF_SENSORS; i++)
        {
            //DataMulesState dmState = (DataMulesState) (((OOState) initialState).object(Constants.CLASS_STATE));
               //DataMulesDomain dom = (DataMulesDomain)plannerOriginal.getDomain();//if not send the domain to the hybridPlanner
            if(!sensorsTosDom.containsKey(i))
            {
                 inversSensors = sensorsTosDom.inverse();
                for(IntIntPair iip : sensorsTosDom.values())
                {
                    if (iip.firstNum == domNum)
                    {
                        if(originalDom.graph.contains(i,inversSensors.get(iip)))
                        {
                            domainsSize[domNum][0] +=1 ;
                           IntIntPair newIIP = new IntIntPair(domNum, domainsSize[domNum][0] -1);
                            sensorsTosDom.put(i,newIIP);
                            return true;
                        }
                    }
                }

            }
    }
    return false;
}
        /*int numOfSens = NUM_OF_SENSORS/currNumOfDmains;
        int numOfAg = NUM_OF_AGENTS/currNumOfDmains; //1
        int sNum =0;
        //int exraSensors = NUM_OF_SENSORS - currNumOfDmains*numOfSens;
        //int exraAgents = NUM_OF_AGENTS - currNumOfDmains*numOfAg; //0

        //calc how many sensors

        for(int i = exraSensors; i < currNumOfDmains; i ++)
        {
            domainsSize[i][0] = numOfSens;
        }

        for(int i = exraAgents; i < currNumOfDmains; i ++)
        {
            domainsSize[i][1] = numOfAg;
        }


        int idx;
        for(idx = 0; idx < exraSensors; idx++)
        {
            domainsSize[idx][0] = numOfSens + 1;
        }

        //calc how many agents

        for(idx = 0; idx < exraAgents; idx++)
        {
            domainsSize[idx][1] = numOfAg + 1;
        }

        while (sNum != currNumOfDmains*numOfSens)
        {
            for(int newNum = 0; newNum < numOfSens; newNum++)
            {
                for(int d = 0; d < currNumOfDmains &&sNum != origSensorsNum; d++)
                {
                    IntIntPair dmp = new IntIntPair(d, newNum);
                    sensorsTosDom.put(sNum, dmp);
                    sNum++;
                }
            }
        }
        //add the extra sensors
        for(int i = 0; i < origSensorsNum-currNumOfDmains*numOfSens; i++)
        {
            for(int d = 0; d < currNumOfDmains &&sNum != origSensorsNum; d++)
            {
                IntIntPair dmp = new IntIntPair(d, numOfSens);
                sensorsTosDom.put(sNum, dmp);
                sNum++;
            }
        }
        int agNum =0;
        //newNum =0;
        while (agNum != currNumOfDmains*numOfAg)
        {
            for(int newNum = 0; newNum < numOfAg; newNum++)
            {
                for(int d = 0; d < currNumOfDmains; d++)
                {
                    IntIntPair dmp = new IntIntPair(d, newNum);
                    agentsTosDom.put(agNum, dmp);
                    agNum++;
                }

            }
        }
        //add the extra agents
        for(int i = 0; i < origAgentsNum - currNumOfDmains*numOfAg; i++)
        {
            for(int d = 0; d < currNumOfDmains &&agNum != origAgentsNum; d++)
            {
                IntIntPair dmp = new IntIntPair(d, numOfAg);
                agentsTosDom.put(agNum, dmp);
                agNum++;
            }
        }
    }

*/
    public Action actionHybrid(State s) throws Exception {
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[currNumOfDmains];
           DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
           boolean b = checkDMstate(dmState);
           if(!b)
           {
               return null;
           }
       DataMulesState [] dmsArr = extractSmallerStates(s);
        // String[] actionsFinal = new String[dmsArr[0].agentsLoc.length]; ///if all sizes are same
           String[] actionsFinal = new String[dmState.agentsLoc.length];

           Action a = null;
         IntIntPair dPair = null;
         for(int d = 0; d < currNumOfDmains; d++) {
                 GenericOOState gs = new GenericOOState(dmsArr[d]);
                  a = policyArr[d].action(gs);
             actionsSmaller[d] = new MuleSimpleAction(a.actionName());
             //if the action is stay or repair
             for(int ac = 0; ac <  actionsSmaller[d].actions.length; ac++)
             {
                //if the action is repair of stay no need to translate sensor number
                 if (actionsSmaller[d].actions[ac].equals(ACTION_REPAIR) || actionsSmaller[d].actions[ac].equals(ACTION_STAY)) {
                     dPair = new IntIntPair(d, ac);
                     int tstNum =inverseAgents.get(dPair);

                     actionsFinal[tstNum] = actionsSmaller[d].actions[ac];
                 }
                 //action is moving
                 else
                 {
                      //j is the small agent num
                     int dest = actionsSmaller[d].actionDestinations[ac];
                     dPair = new IntIntPair(d, dest);
                     IntIntPair domAg = new IntIntPair(d,ac);
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
            if(agentsTosDom.get(i).firstNum != sensorsTosDom.get(dmState.agentsLoc[i]).firstNum)
            {
                return false;
            }
        }
        return true;
    }

    public double actionProbHybrid(State s, Action a) {

            double ans =1;
            // double[] allProb = new double[currNumOfDmains];
             DataMulesState [] dmsArr = extractSmallerStates(s);
             MuleSimpleAction[] actionsSmaller = extractSmallerActions(a);

            for(int i = 0; i < currNumOfDmains; i ++)
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
        String[][] strArr = new String[currNumOfDmains][numOfAgents];
        MuleSimpleAction[] actionArrRes = new MuleSimpleAction[currNumOfDmains];
        for(int i =0; i <numOfAgents;i++)
        {
            //if the action is repair of stay no need to translate sensor number
            if (action.actions[i].equals(ACTION_REPAIR) ||action.actions[i].equals(ACTION_STAY)) {
                strArr[agentsTosDom.get(i).firstNum][agentsTosDom.get(i).secondNum] = action.actions[i];
                // actionsFinal[inverseAgents.get(dPair)] = actionsSmaller[i].actions[j];
            }
            //action is moving
            else
            {
                //j is the small agent num
                int dest = action.actionDestinations[i];
                strArr[agentsTosDom.get(i).firstNum][agentsTosDom.get(i).secondNum] = ""+ sensorsTosDom.get(dest).secondNum;
            }

        }
        for(int i = 0; i < currNumOfDmains; i++ ) {
            actionArrRes[i] = new MuleSimpleAction(strArr[i].toString());
        }
        return actionArrRes;

    }


    public boolean definedForHybrid(State s) {
         return true;
     }

     //get the number of sensors in a certain domain
     private int getSensorsNum(int i)
     {
         return domainsSize[i][0];
     }

    //get the number of sensors in a certain domain
    private int getAgentsNum(int i)
    {
        return domainsSize[i][1];
    }
}

