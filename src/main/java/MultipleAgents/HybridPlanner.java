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

import java.util.*;

import static MultipleAgents.Constants.*;
import static MultipleAgents.DataMulesDomain.generateDomain;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;

   //BiMap< Integer, IntIntPair> sensorsTosDom;
    Map<Integer,List<Integer>> sensorsInDomains;
    BiMap< Integer, IntIntPair> agentsTosDom;
    BiMap<IntIntPair, Integer> inverseAgents;
   // BiMap<IntIntPair, Integer> inversSensors;
    int currNumOfDomains;
    int origNumOfDomains;
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
        currNumOfDomains = numOfDomains;
        this.delta = delta;
        this.maxItr = maxItr;
        this.originalDom = originalDom;

    }
    public HybridPlanner(Planner p, int numOfDomains, int horizon, int numUCT) {
        plannerOriginal = p;
        planner =p;
        currNumOfDomains = numOfDomains;
        this.horizon = horizon;
        this.numUCT = numUCT;

    }

    public void resetSolver() {
        plannerOriginal.resetSolver();
    }


    public DataMulesState[] extractSmallerStates(State s)
    {
        DataMulesState [] dmsArrRes = new DataMulesState[currNumOfDomains];
        DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        ///createSmallerState
        //Integer[][] lastRepair = new Integer[currNumOfDomains][]; //////change
        Integer[][] agentLocs = new Integer[currNumOfDomains][];//////change
       Map<Integer,Map<Integer,Integer>> lastRepair = new HashMap<Integer, Map<Integer, Integer>>();

        for(int i = 0; i < currNumOfDomains; i++)
        {
            lastRepair.put(i,new HashMap<Integer, Integer>());
        }
        //set the LastRepair
        Map domLRepair;
        for(Integer domNum : sensorsInDomains.keySet())
        {
            domLRepair = new HashMap();
            for(Integer sensNum : sensorsInDomains.get(domNum))
            {
                domLRepair.put(sensNum,dmState.timeFromLastRepair.get(sensNum));
            }
            lastRepair.put(domNum,domLRepair);
        }

        //set the nubmer of agents
        for(int i = 0; i < currNumOfDomains; i++)
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
            //agentLocs[domainNum][agentsTosDom.get(i).secondNum] = sensorsTosDom.get(dmState.agentsLoc[i]).secondNum;
            agentLocs[domainNum][agentsTosDom.get(i).secondNum] = dmState.agentsLoc[i];
        }
       // for(int i = 0; i < dmState.timeFromLastRepair.size(); i++)
    /*    for(int i : dmState.timeFromLastRepair.keySet())
        {
            //check if it the same domain like up
            domainNum =  sensorsTosDom.get(i).firstNum;
            int sens = sensorsTosDom.get(i).secondNum;
           // lastRepair[domainNum][sensorsTosDom.get(i).secondNum] = agentsTosDom.get(dmState.timeFromLastRepair[i]).secondNum ;
            Map<Integer,Integer> currMap =  lastRepair.get(domainNum);
            currMap.put(sens, dmState.timeFromLastRepair.get(i));
            lastRepair.put(domainNum,currMap);
        }*/

        //creathe the states of each small domain
        for(int i = 0; i < currNumOfDomains; i++) {

            dmsArrRes[i] = new DataMulesState(agentLocs[i], lastRepair.get(i));
        }
        return dmsArrRes;
    }


    public Policy planFromState(State initialState)
    {
       return planFromState(initialState, currNumOfDomains);
    }


    public Policy planFromState(State initialState, int nOfDomains) {
        origNumOfDomains = nOfDomains;
        currNumOfDomains = NUM_OF_AGENTS;
        //partition the domainNum;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        //sensorsTosDom = HashBiMap.create();
        sensorsInDomains = new HashMap<Integer, List<Integer>>();
        for(int i = 0; i < sensorsInDomains.size(); i++)
            sensorsInDomains.put(i,new ArrayList<Integer>());

        agentsTosDom = HashBiMap.create();;
        //   plicyMap = null;

        //dmsArr = new DataMulesState[currNumOfDomains];
        domainsSize = new int[currNumOfDomains][2];


        OOSADomain[] domains = new OOSADomain[currNumOfDomains];
        policyArr = new Policy[currNumOfDomains];
        if(origNumOfDomains == NUM_OF_AGENTS)
             setNumDomainsiIsNumAgents(initialState);
        else
            setNumDomainsiIsNOTNumAgents(initialState);
        //SetDomainsWithOutGraph();

        for (int i = 0; i < currNumOfDomains; i++) {
            DataMulesState initSmallState = extractSmallerStates(initialState)[i];
           // OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i), findSensorsInDomain(i));
            OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i), sensorsInDomains.get(i));
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
        //inversSensors = sensorsTosDom.inverse();

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

    private void SetDomainsWithOutGraph() {
        int numOfSens = NUM_OF_SENSORS/ currNumOfDomains;
        int numOfAg = NUM_OF_AGENTS/ currNumOfDomains;

        int sNum =0;
        int firstExraSensor = NUM_OF_SENSORS - currNumOfDomains *numOfSens;
        int firstExraAgent = NUM_OF_AGENTS - currNumOfDomains *numOfAg;

        //calc how many sensors

        for(int i = firstExraSensor; i < currNumOfDomains; i ++)
        {
            domainsSize[i][0] = numOfSens;
        }

        for(int i = firstExraAgent; i < currNumOfDomains; i ++)
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

        while (sNum != currNumOfDomains *numOfSens)
        {
          //
            //{
                for(int d = 0; d < currNumOfDomains &&sNum != origSensorsNum; d++)
                {
                    //IntIntPair dmp = new IntIntPair(d, newNum);
                   // sensorsTosDom.put(sNum, dmp);
                    if(!sensorsInDomains.containsKey(d))
                    {
                        List<Integer> li = new ArrayList<Integer>();
                        li.add(sNum);
                        sensorsInDomains.put(d,li);
                    }

                    sensorsInDomains.get(d).add(sNum);
                    sNum++;
                }
         //   }
        }
        //add the extra sensors
        for(int i = 0; i < origSensorsNum- currNumOfDomains *numOfSens; i++)
        {
            for(int d = 0; d < currNumOfDomains &&sNum != origSensorsNum; d++)
            {
                //IntIntPair dmp = new IntIntPair(d, numOfSens);
                //sensorsTosDom.put(sNum, dmp);
                sensorsInDomains.get(d).add(sNum);
                sNum++;
            }
        }
        int agNum =0;
        //newNum =0;
        while (agNum != currNumOfDomains *numOfAg)
        {
            for(int newNum = 0; newNum < numOfAg; newNum++)
            {
                for(int d = 0; d < currNumOfDomains; d++)
                {
                    IntIntPair dmp = new IntIntPair(d, newNum);
                    agentsTosDom.put(agNum, dmp);
                    agNum++;
                }
            }
        }
        //add the extra agents
        for(int i = 0; i < origAgentsNum - currNumOfDomains *numOfAg; i++)
        {
            for(int d = 0; d < currNumOfDomains &&agNum != origAgentsNum; d++)
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
            //IntIntPair dmp = new IntIntPair(i, dmState.agentsLoc[i]);
            //sensorsTosDom.put(dmState.agentsLoc[i], dmp);
            List<Integer> li = new ArrayList<Integer>();
            li.add(dmState.agentsLoc[i]);
            sensorsInDomains.put(i,li); //////////////////works only if the agents in start 0,1,2....?
            setSensors++;
            domainsSize[dmState.agentsLoc[i]][0] = 1;
        }
        //set the left seneors to domains
        while(setSensors != NUM_OF_SENSORS)
        {
                for(int i =0; i < NUM_OF_AGENTS; i++) {
                 if (findSensorToDomain(i,setSensors))
                 {
                     setSensors++;
                 }
                }
            }
        }

    private void setNumDomainsiIsNOTNumAgents(State initialState) {
        setNumDomainsiIsNumAgents(initialState);
        System.out.println("blabla");
        mergeDomains();
    }

    private void mergeDomains() {
        List<Integer> except = new ArrayList<Integer>();
        while(currNumOfDomains != origNumOfDomains)
        {

            boolean merged = false;
            while(!merged)
            {
                int minDomain = findMinDomain(except,currNumOfDomains - 2);
                int min2;
                except.add(minDomain);
                int size = except.size();
                for(int i = 0; i < currNumOfDomains-size && !merged;i++)
                {
                    min2 = findMinDomain(except,currNumOfDomains - 2);
                    merged = tryMerge(minDomain, min2);
                }
            }


        }
    }

    private boolean tryMerge(int minDomain, int min2) {
        return false;
    }

    private int findMinDomain(List<Integer> except, int from) {
        int ans;
            ans = from;
        //find the domain with the minimum sensors
        for(int i = from -1  ; i >= 0 && !except.contains(i) ; i--)
        {
            if(domainsSize[i][0] < domainsSize[ans][0])
                ans =i;
        }
        return ans;
    }

    private boolean  findSensorToDomain(int domNum, int lastSensor) {
        //////should be random?? if yes add all to set
        for(int i = lastSensor; i < NUM_OF_SENSORS; i++)
        {
                for(Integer sens : sensorsInDomains.get(domNum))
                {
                        if(originalDom.graph.contains(i,sens))
                        {
                            domainsSize[domNum][0] +=1 ;
                           //IntIntPair newIIP = new IntIntPair(domNum, i);
                            //sensorsTosDom.put(i,newIIP);
                            sensorsInDomains.get(domNum).add(i);
                            return true;
                        }
                    }
    }
    return false;
}

    public Action actionHybrid(State s) throws Exception {
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[currNumOfDomains];
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
         for(int d = 0; d < currNumOfDomains; d++) {
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
                    // int finalDest = inversSensors.get(dPair);
                     int orgAg =inverseAgents.get(domAg);
                     actionsFinal[orgAg]= "" +dest;
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
            //if(agentsTosDom.get(i).firstNum != sensorsTosDom.get(dmState.agentsLoc[i]).firstNum)
            if(!(sensorsInDomains.get(agentsTosDom.get(i).firstNum).contains(dmState.agentsLoc[i])))
            {
                return false;
            }
        }
        return true;
    }

    public double actionProbHybrid(State s, Action a) {

            double ans =1;
            // double[] allProb = new double[currNumOfDomains];
             DataMulesState [] dmsArr = extractSmallerStates(s);
             MuleSimpleAction[] actionsSmaller = extractSmallerActions(a);

            for(int i = 0; i < currNumOfDomains; i ++)
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
        String[][] strArr = new String[currNumOfDomains][numOfAgents];
        MuleSimpleAction[] actionArrRes = new MuleSimpleAction[currNumOfDomains];
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
                strArr[agentsTosDom.get(i).firstNum][agentsTosDom.get(i).secondNum] = ""+ dest;//sensorsTosDom.get(dest).secondNum;
            }

        }
        for(int i = 0; i < currNumOfDomains; i++ ) {
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

