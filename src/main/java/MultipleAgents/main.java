package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.core.oo.state.generic.GenericOOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import static MultipleAgents.Constants.*;

/**
 * Created by noa on 24-Aug-16.
 */
public class main {

    public static PrintWriter writerAllVI;
    public static PrintWriter writerAllUCT;

    public static void main2(String[] args) {
       /* String [] strArr = {"s","0","1","r","s","0","1","r"};
        printCombination(strArr,8,2);
        System.out.println(vec);
        System.out.println(vec.size());
        removeDuplicates(vec);
        System.out.println(vec);
        System.out.println(vec.size());*/
        Random ran = new Random();
        int x;
        for(int i = 0; i < 10; i++) {
            x = ran.nextInt(14 + 1);
            System.out.println(x);
        }
    }


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        //writerAllVI = new PrintWriter(OUTPUT_PATH + "allSum"+numUCT+".txt", "UTF-8");
        writerAllVI = new PrintWriter(OUTPUT_PATH + "vi" + ".csv");
        writerAllUCT = new PrintWriter(OUTPUT_PATH + "allSumUCTsundaytest" + ".csv");
        StringBuilder sbUCT = new StringBuilder();
        sbUCT.append("number of Sensors");
        sbUCT.append(',');
        sbUCT.append("number of Agents");
        sbUCT.append(',');
        sbUCT.append("horizon");
        sbUCT.append(',');
        sbUCT.append("number of rollouts");
        sbUCT.append(',');
        sbUCT.append("test number");
        sbUCT.append(',');
        sbUCT.append("total reward");
        sbUCT.append(',');
        sbUCT.append("num of visits");
        sbUCT.append(',');
        sbUCT.append("runTime(ms)");
        sbUCT.append('\n');

     /*   writerAllVI = new PrintWriter(OUTPUT_PATH + "allSumVINew " + ".csv");
        StringBuilder sbVI = new StringBuilder();
        sbVI.append("number of Sensors");
        sbVI.append(',');
        sbVI.append("number of Agents");
        sbVI.append(',');
        sbVI.append("test number");
        sbVI.append(',');
        sbVI.append("total reward");
        sbVI.append(',');
        sbVI.append("runTime(ms)");
        sbVI.append('\n');
         //writerAllVI.write(sb.toString());
        // writerAllVI.flush();
       writerAllVI.write(sbVI.toString());
        writerAllVI.flush();
       // MultipleAgentsVI(4, 4, 1);*/
        writerAllUCT.write(sbUCT.toString());
        writerAllUCT.flush();

        MultipleAgentsVI(3, 2, 1);


       MultipleAgentsUCT(3, 2, 1, 500, 50);
    }
 //   }
   // }
       // writerAllVI.close();

       /* for(int i = 1; i <= 10; i++) {
                pw.write(sb.toString());
                 MultipleAgentsVI(4,4,i);
                pw.close();    MultipleAgentsUCT(4,4,i);*/
    /*  for (int horizon = 5; horizon < 25; horizon = horizon + 5)
            for (int numUCT = 50; numUCT < 500; numUCT = numUCT + 400)
                for (int se = 1; se <= 4; se++)

                    for (int ag = 1; ag <= se; ag++) {
                        for (int i = 1; i <= 5; i++) {
                            //MultipleAgentsUCT(6, 6, i, 50, 5);
                           MultipleAgentsUCT(se, ag, i, numUCT, horizon);*/

                   //     }}




       /* for (int horizon = 5; horizon < 25; horizon = horizon + 5)
            for (int numUCT = 50; numUCT < 500; numUCT = numUCT + 400)
                    for (int ag = 1; ag <= 5; ag++) {
                        for (int i = 1; i <= 5; i++) {
                            //MultipleAgentsUCT(6, 6, i, 50, 5);
                            MultipleAgentsUCT(5, ag, i, numUCT, horizon);

                        }}


    }

                     //   }
                 //  }


      /*  for (int se = 1; se <= 5; se++)

            for (int ag = 1; ag <= se; ag++) {
                for (int i = 1; i <= 5; i++) {

                    MultipleAgentsVI(se, ag, i);
                }
                }


                writerAllVI.close();
                 // writerAllUCT.close();

            }
   // }
*/

    private static void MultipleAgentsVI(int nSensors, int nAgents, int testNum) {

        NUM_OF_AGENTS = nAgents;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);

        long startTime = System.currentTimeMillis();

        ValueIteration planner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 100);
        Policy p = planner.planFromState(initialState);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        //  System.out.println(totalTime/);

        // ValueIteration v = (ValueIteration) planner;

        //System.out.println( "value::::: " +v.value(initialState));


        Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
        ep.write(OUTPUT_PATH+ nSensors + " Sensors ," + nAgents + " Agents viMult");


       // oPolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "viMult");


        double totalReward = 0;
        List<Double> rewardList = ep.rewardSequence;
        // List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
        for (double d : rewardList) {
            totalReward += d;
        }

        try {
            printPolicy(writerAllVI, p, initialState,allStates, OUTPUT_PATH+"policy/" + nSensors + " Sensors ," + nAgents + " Agents PolicyMultAgents test" + testNum + ".txt", totalReward, totalTime, testNum);
             //printPolicy(writerAllVI, p, initialState,OUTPUT_PATH + nSensors + "," + nAgents + "," +testNum + ".txt", totalReward, totalTime, testNum);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
  //      System.out.println("Total Reward" + totalReward);
    //    System.out.println("VI runTime: " + totalTime + " miliseconds");///1000);
    }

    public static void printPolicy( PrintWriter writerAll, Policy p, State initialState, List<State> allStates, String output, double totReward, long totalTime, int testNum) throws FileNotFoundException, UnsupportedEncodingException {
   // public static void printPolicy( PrintWriter writerAll, Policy p, State initialState, String output, double totReward, long totalTime, int testNum) throws FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writer = new PrintWriter(output);
        StringBuilder sb = new StringBuilder();
        sb.append(NUM_OF_SENSORS);
        sb.append(',');
        sb.append(NUM_OF_AGENTS);
        sb.append(',');
        sb.append(testNum);
        sb.append(',');
        sb.append(totReward);
        sb.append(',');
        sb.append(totalTime);
        sb.append('\n');
        writerAll.write(sb.toString());
        writerAll.flush();

        for (State s : allStates) {
            writer.println("State: " + s);
            writer.println("Action: " + p.action(s));
            writer.println();
        }
        writer.println();
        writer.println("Total Reward: " + totReward);
        writer.println("runTime: " + totalTime + " miliseconds");//1000);*/
        writer.flush();
        writer.close();
        /*writerAllVI.println();
        writerAllVI.println(NUM_OF_SENSORS + " sensors");
        writerAllVI.println(NUM_OF_AGENTS + " Agents");
        writerAllVI.println("test: "+testNum);
        writerAllVI.println("Total Reward: " + totReward);
        writerAllVI.println("runTime: "+totalTime +" miliseconds");
                writerAllVI.flush();   */


       // writer.close();

}

/*
    public class HybridPolicy implements Policy
    {
        private Policy p1;
        private Policy p2;

        public Action action(State state) {
            // Partition to smaller states
            State s1 = extractSmallerState(state)
            State s2;

            Action a1 = p1.action(s1);
            Action a2= p2.action(s2);

            Action myAction = n
        }

        public double actionProb(State state, Action action) {
            return 0;
        }

        public boolean definedFor(State state) {
            return false;
        }
    }
*/


    //1000);

    public static void printAVG( PrintWriter writerAVG, String output, double avgReward, long avgTime) throws FileNotFoundException, UnsupportedEncodingException {

        // PrintWriter writer = new PrintWriter(output, "UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append(NUM_OF_SENSORS);
        sb.append(',');
        sb.append(NUM_OF_AGENTS);
        sb.append(',');
        sb.append(avgReward);
        sb.append(',');
        sb.append(avgTime);
        sb.append('\n');
        writerAVG.write(sb.toString());
        writerAVG.flush();
    }



    public static void printPolicyUCT( PrintWriter writerAll, Policy p, State initialState,List<State> allStates, String output, double totReward, long totalTime, int testNum ,int horizon, int numUCT, int numOfVisits) throws FileNotFoundException, UnsupportedEncodingException {

        // public static void printPolicyUCT( PrintWriter writerAll, Policy p, State initialState, String output, double totReward, long totalTime, int testNum ,int horizon, int numUCT, int numOfVisits) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(output);
        StringBuilder sb = new StringBuilder();
        sb.append(NUM_OF_SENSORS);
        sb.append(',');
        sb.append(NUM_OF_AGENTS);
        sb.append(',');
        sb.append(horizon);
        sb.append(',');
        sb.append(numUCT);
        sb.append(',');
        sb.append(testNum);
        sb.append(',');
        sb.append(totReward);
        sb.append(',');
        sb.append(numOfVisits);
        sb.append(',');
        sb.append(totalTime);
        sb.append('\n');
        writerAll.write(sb.toString());
        writerAll.flush();

        for (State s : allStates) {
            writer.println("State: " + s);
            writer.println("Action: " + p.action(s));
            writer.println();
        }
        writer.println();
        writer.println("Total Reward: " + totReward);
        writer.println("runTime: " + totalTime + " miliseconds");//1000);*/
        writer.flush();
        writer.close();

        /*writerAllVI.println();
        writerAllVI.println(NUM_OF_SENSORS + " sensors");
        writerAllVI.println(NUM_OF_AGENTS + " Agents");
        writerAllVI.println("test: "+testNum);
        writerAllVI.println("Total Reward: " + totReward);
        writerAllVI.println("runTime: "+totalTime +" miliseconds");
                writerAllVI.flush();   */


       // writer.close();

    }


     /*Driver function to check for above function*/


    private static void MultipleAgentsUCT(int nSensors, int nAgents, int testNum, int numUCT, int horizon) {
        NUM_OF_AGENTS = nAgents;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        //List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);

        /*
        	public UCT(SADomain domain, double gamma, HashableStateFactory hashingFactory, int horizon, int nRollouts, int explorationBias){

         */

       List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
        long startTime = System.currentTimeMillis();
        myUCT uct = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numUCT, 2);

        Policy p = uct.planFromState(initialState);

        //System.out.println("nummmmmm " + uct.getNumOfVisited());

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        int visited = uct.getNumOfVisited();
        System.out.println("visited: " + visited);
         //Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel());
        Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
       // ep.write(OUTPUT_PATH + "policy/1");
        ep.write(OUTPUT_PATH + "episodes/"+ nSensors+ " Sensors ," + nAgents + " Agents test " +testNum +  ","+ +numUCT+" Rolls "+horizon + "hor");
        double totalReward = 0;
        List<Double> rewardList = ep.rewardSequence;
         //List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
       for (double d : rewardList) {
           totalReward += d;
       }

        try {
           printPolicyUCT(writerAllUCT,p, initialState,allStates,OUTPUT_PATH+"policy/" + nSensors+ "," + nAgents + ","+ numUCT+"," +testNum + " ," +horizon + ".txt", totalReward,totalTime, testNum,horizon,numUCT,visited);
           // printPolicyUCT(writerAllUCT,p, initialState, OUTPUT_PATH + nSensors + " S ," + nAgents + " Ag UCT"+ numUCT+"rolls" +testNum + " horizon: " +horizon + ".txt", totalReward,totalTime, testNum,horizon,numUCT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println("Total Reward" + totalReward);
        //System.out.println("UCT runTime: " + totalTime + " miliseconds");//1000);

    }




}
