package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.MDPSolver;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.montecarlo.uct.UCT;
import burlap.behavior.singleagent.planning.stochastic.rtdp.RTDP;
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
import static MultipleAgents.DataMulesDomain.graph;
import static MultipleAgents.main.writerPrints;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;

    //map between domain and the sensors in it
    Map<Integer,List<Integer>> sensorsInDomains;

    //maps between the original agent number to the domain and the agent's number in the domain
    BiMap< Integer, IntIntPair> agentsTosDom;
    BiMap<IntIntPair, Integer> inverseAgents;

    //the current number of domains
    int currNumOfDomains;
    //the original number of domains
    int origNumOfDomains;
    int origSensorsNum = NUM_OF_SENSORS;
    int origAgentsNum = NUM_OF_AGENTS;
    //an array for the policy each to one domain
    Policy[] policyArr;
    //map how many agents in every domain
   Map <Integer,Integer> domainsSize;
    int numRollouts;
    int horizon;
    int maxDepth;
    int maxItr;
    double delta;

    //constructor for vi
    public HybridPlanner(Planner p, int numOfDomains, double delta, int maxItr) {
        plannerOriginal = p;
        planner =p;
        currNumOfDomains = numOfDomains;
        this.delta = delta;
        this.maxItr = maxItr;

    }

    //constructor for RTDP
    public HybridPlanner(Planner p, int numOfDomains, int numRollouts, double delta, int maxDepth) {
        plannerOriginal = p;
        planner =p;
        currNumOfDomains = numOfDomains;
        this.numRollouts = numRollouts;
        this.delta = delta;
        this.maxDepth = maxDepth;
      //  this.originalDom = originalDom;

    }

    //constructor for uct
    public HybridPlanner(Planner p, int numOfDomains, int horizon, int numRollouts) {
        plannerOriginal = p;
        planner =p;
        currNumOfDomains = numOfDomains;
        this.horizon = horizon;
        this.numRollouts = numRollouts;

    }

    public void resetSolver() {
        plannerOriginal.resetSolver();
    }


    //construct the smaller states from the original state
    public DataMulesState[] extractSmallerStates(State s)
    {
        DataMulesState [] dmsArrRes = new DataMulesState[currNumOfDomains];
        DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
        ///separate the actions to domains
        Integer[][] agentLocs = new Integer[currNumOfDomains][];
        //map the last repair to each small domain
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

        //set the number of agents
        for(int i = 0; i < currNumOfDomains; i++)
        {
            agentLocs[i] = new Integer[domainsSize.get(i)];
        }

        int domainNum;
        int sensNum;
        int agNum;
        //set each agent location
        for(int i = 0; i < dmState.agentsLoc.length; i++)
        {
            domainNum =  agentsTosDom.get(i).firstNum;
            agentLocs[domainNum][agentsTosDom.get(i).secondNum] = dmState.agentsLoc[i];
        }

        //creathe the states of each small domain
        for(int i = 0; i < currNumOfDomains; i++) {

            dmsArrRes[i] = new DataMulesState(agentLocs[i], lastRepair.get(i));
        }
        //return the array of small states
        return dmsArrRes;
    }


    public Policy planFromState(State initialState)
    {
       return planFromState(initialState, currNumOfDomains);
    }

    //Create the plan with certain number of domains
    public Policy planFromState(State initialState, int nOfDomains) {
        writerPrints.write("Start PlanfromState");
        writerPrints.flush();
        origNumOfDomains = nOfDomains;
        currNumOfDomains = NUM_OF_AGENTS;
        sensorsInDomains = new HashMap<Integer, List<Integer>>();
        for(int i = 0; i < sensorsInDomains.size(); i++)
            sensorsInDomains.put(i,new ArrayList<Integer>());

        agentsTosDom = HashBiMap.create();;
        domainsSize = new HashMap<Integer, Integer>();


        OOSADomain[] domains = new OOSADomain[currNumOfDomains];
        policyArr = new Policy[currNumOfDomains];
        //divide the sensors and the agents

        //if the number of agents is the number of domains
        if(origNumOfDomains == NUM_OF_AGENTS)
             setNumDomainsiIsNumAgents(initialState);
        else
            setNumDomainsiIsNOTNumAgents(initialState);

        //plan for each domain
        for (int i = 0; i < currNumOfDomains; i++) {
            DataMulesState initSmallState = extractSmallerStates(initialState)[i];
            OOSADomain dom = generateDomain(getSensorsNum(i), getAgentsNum(i), sensorsInDomains.get(i));
            domains[i] = dom;

            Planner newPlanner = null;
            if(plannerOriginal instanceof ValueIteration)
            {
                newPlanner = new ValueIteration(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),delta,maxItr);
            }
            else if(plannerOriginal instanceof UCT)
            {
                newPlanner = new UCT(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(),horizon, numRollouts,2);
            }
            else if(plannerOriginal instanceof RTDP)
            {
                newPlanner = new RTDP(dom,plannerOriginal.getGamma(),plannerOriginal.getHashingFactory(), 0,numRollouts,delta,maxDepth);
            }
            Policy pol = newPlanner.planFromState(new GenericOOState(initSmallState));
            policyArr[i] = pol;


        }


        inverseAgents = agentsTosDom.inverse();

        writerPrints.write("Ens PlanfromState\n");
        writerPrints.flush();
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

    //
    private void SetDomainsWithOutGraph() {
        int numOfSens = NUM_OF_SENSORS/ currNumOfDomains;
        int numOfAg = NUM_OF_AGENTS/ currNumOfDomains;

        int sNum =0;
        int firstExraSensor = NUM_OF_SENSORS - currNumOfDomains *numOfSens;
        int firstExraAgent = NUM_OF_AGENTS - currNumOfDomains *numOfAg;

        //calc how many sensors

      /*  for(int i = firstExraSensor; i < currNumOfDomains; i ++)
        {
            domainsSize[i][0] = numOfSens;
        }*/

        for(int i = firstExraAgent; i < currNumOfDomains; i ++)
        {
            //domainsSize[i][1] = numOfAg;
            domainsSize.put(i,numOfAg);
        }


        int idx;
       /* for(idx = 0; idx < firstExraSensor; idx++)
        {
            domainsSize[idx][0] = numOfSens + 1;
        }*/

        //calc how many agents

        for(idx = 0; idx < firstExraAgent; idx++)
        {
            //domainsSize[idx][1] = numOfAg + 1;
            domainsSize.put(idx,numOfAg + 1);
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
           domainsSize.put(i,1);
       }
       //set the current agents' location to the agent's domain
        for(int i =0; i < NUM_OF_AGENTS; i++)
        {

            List<Integer> li = new ArrayList<Integer>();
            li.add(dmState.agentsLoc[i]);
            sensorsInDomains.put(i,li);
            setSensors++;
        }
        //set the left sensors to domains
        List<Integer> sensorsPool = new ArrayList<Integer>();
        for(int i = NUM_OF_AGENTS; i < NUM_OF_SENSORS; i++)
        {
            sensorsPool.add(i);
        }
        while(setSensors != NUM_OF_SENSORS)
        {
                for(int i =0; i < sensorsInDomains.size() && setSensors != NUM_OF_SENSORS; i++) {
                 if (findSensorToDomain(i,sensorsPool))
                 {
                     setSensors++;
                 }
                }
            }
        }

    private void setNumDomainsiIsNOTNumAgents(State initialState) {
        writerPrints.write("Start set\n");
        writerPrints.flush();
        setNumDomainsiIsNumAgents(initialState);
        writerPrints.write("End set\n");
        writerPrints.flush();
        writerPrints.write("Start merge\n");
        writerPrints.flush();
        mergeDomains();
        writerPrints.write("End merge\n");
        writerPrints.flush();
    }

    private void mergeDomains() {
        List<Integer> tmpExcept = new ArrayList<Integer>();
        List<Integer> tried = new ArrayList<Integer>();
        boolean merged = false;
        while(currNumOfDomains != origNumOfDomains && !merged)
        {
                int minDomain = findMinDomain(tmpExcept,currNumOfDomains - 1);
                tried.add(minDomain);
                tmpExcept.addAll(tried);
                int min2;
                if(!tmpExcept.contains(minDomain))
                    tmpExcept.add(minDomain);
                int size = tmpExcept.size();
                for(int i = 0; i < currNumOfDomains-size && !merged;i++)
                {
                    min2 = findMinDomain(tmpExcept,currNumOfDomains - 1);
                    tmpExcept.add(min2);
                    merged = tryMerge(minDomain, min2);
                }
            tmpExcept.clear();
            }
        System.out.println("enddd merge");
        }

    //check if two domains can be merged
    private boolean tryMerge(int minDomain, int min2) {
        for(Integer se1 : sensorsInDomains.get(minDomain))
            for(Integer se2 : sensorsInDomains.get(min2))
            {
                if(graph.contains(se1,se2)) {
                    merge(minDomain, min2);
                    return true;
                }
            }
        return false;
    }

    private void merge(int to, int from) {
        //add the sensors

        //allways merge to left
        if(to> from) {
            int tmp = from;
            from = to;
            to = tmp;
        }

            for (Integer se2 : sensorsInDomains.get(from)) {
                sensorsInDomains.get(to).add(se2);
                //domainsSize[to][0]++;
            }


        inverseAgents = agentsTosDom.inverse();

        Set <IntIntPair> iipList = inverseAgents.keySet();
        List<Integer> toDelete = new ArrayList<Integer>();
        //get all the agents of the domain that will be deleted
        for(IntIntPair iip : iipList)
        {
            if(iip.firstNum == from)
            {
                toDelete.add(inverseAgents.get(iip));
            }
        }
        for(Integer toMove : toDelete)
        {
            agentsTosDom.remove(toMove);
           //  agentsTosDom.put(toMove,new IntIntPair(to,domainsSize[to][1]));
            //domainsSize[to][1]++;

            //move the agents to the new domain
            agentsTosDom.put(toMove,new IntIntPair(to,domainsSize.get(to)));
            int tmp = domainsSize.get(to)+1;
            domainsSize.put(to,tmp);///check if bigger
        }
        //remove the old domain
        domainsSize.remove(from);
        sensorsInDomains.remove(from);
        ///decrease domain size in 1;
        currNumOfDomains--;
    }

    //find the domain with the mininum sensors
    private int findMinDomain(List<Integer> except, int from) {
        int ans = -1;
        for(int i = from; i >=0 ;i--)
           if(!except.contains(i)) {
               ans = i;
               break;
           }
        for(int i = from   ; i >= 0 ; i--)
        {
            int newSize = getSensorsNum(i);
            int oldSize = getSensorsNum(ans);
            if( (!except.contains(i)) &&  newSize< oldSize)
                ans = i;
        }
        return ans;
    }

    //find a free sensor to a certain domain
    private boolean  findSensorToDomain(int domNum, List<Integer> sensPool) {
        //////should be random?? if yes add all to set
        for(int i : sensPool)
        {
            {
                for(Integer sens : sensorsInDomains.get(domNum))
                {
                    if(graph.contains(i,sens)) {
                        //domainsSize[domNum][0] +=1 ;
                        //IntIntPair newIIP = new IntIntPair(domNum, i);
                        //sensorsTosDom.put(i,newIIP);
                        sensorsInDomains.get(domNum).add(i);
                        sensPool.remove((Object)i);
                        return true;

                    }
                }
            }
        }
        return false;
    }

    //find a free sensor to a certain domain
    private boolean  findSensorToDomainv1(int domNum, int lastSensor) {
        //////should be random?? if yes add all to set
        for(int i = lastSensor; i < NUM_OF_SENSORS; i++)
        {
            {
                for(Integer sens : sensorsInDomains.get(domNum))
                {
                        if(graph.contains(i,sens)) {
                            //domainsSize[domNum][0] +=1 ;
                            //IntIntPair newIIP = new IntIntPair(domNum, i);
                            //sensorsTosDom.put(i,newIIP);
                            sensorsInDomains.get(domNum).add(i);
                            return true;

                        }
                        }
                    }
    }
    return false;
}

    //
    public Action actionHybrid(State s) throws Exception {
         MuleSimpleAction[] actionsSmaller = new  MuleSimpleAction[currNumOfDomains];
           DataMulesState dmState = (DataMulesState) (((OOState) s).object(Constants.CLASS_STATE));
            //Check if the state are legal
           boolean b = checkDMstate(dmState);
           if(!b)
           {
               return null;
           }
           //set the smaller state to each domain in an array
       DataMulesState [] dmsArr = extractSmallerStates(s);
           String[] actionsFinal = new String[dmState.agentsLoc.length];

           Action a = null;
         IntIntPair dPair = null;

        //get the corresponding action from each domain
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
                     //get the desination of the agent
                     int dest = actionsSmaller[d].actionDestinations[ac];
                     dPair = new IntIntPair(d, dest);
                     IntIntPair domAg = new IntIntPair(d,ac);
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
         return sensorsInDomains.get(i).size();
     }

    //get the number of sensors in a certain domain
    private int getAgentsNum(int i)
    {
        //return domainsSize[i][1];
        return domainsSize.get(i);
    }
}

