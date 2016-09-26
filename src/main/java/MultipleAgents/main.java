package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.planning.Planner;
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

    public static PrintWriter writerAll;




    public static void main3(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter avgVI = new PrintWriter(OUTPUT_PATH + "avgVI" + ".csv");
        PrintWriter avgUCT = new PrintWriter(OUTPUT_PATH + "avgUCT" + ".csv");

    }
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
        //writerAllVI = new PrintWriter(OUTPUT_PATH + "vi" + ".csv");
        writerAll = new PrintWriter(OUTPUT_PATH + "results/allSum" + ".csv");
        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm name");
        sb.append(',');
        sb.append("# Iterations");
        sb.append(',');
        sb.append("Horizon");
        sb.append(',');
        sb.append("Instance");
        sb.append(',');
        sb.append("Accumulated reward ");
        sb.append(',');
        sb.append("# rollouts");
        sb.append(',');
        sb.append("Planning runtime");
        sb.append(',');
        sb.append("total runtime");
        sb.append(',');
        sb.append("# Sensors");
        sb.append(',');
        sb.append("# Robots");
        sb.append(',');
        sb.append("p ");
        sb.append(',');
        sb.append("B (Max New Faults)");
        sb.append('\n');
        writerAll.write(sb.toString());
        writerAll.flush();
        runAlgorithm("vi",3,1,1,2,2);
        runAlgorithm("vi",3,2,1,2,2);
runAlgorithm("vi",3,3,1,2,2);


        //  for(int i = 0; i < 5; i ++) {
        //       runAlgorithm("uct", 2, 2, 1, 2, 2000);
        //   }


        //  for (int se = 1; se <= 3; se++)
   /*    for(int numUCT = 3000; numUCT <= 5000; numUCT+= 500)
            for (int ag = 1; ag <= 3; ag++){
                for (int i = 1; i <= 10; i++) {
                    runAlgorithm("uct",3, ag, i, 3, numUCT);
                }}

*/
    /*    int se = 3;
        for (int horizon = 3; horizon <= 4; horizon++)
            for (int numUCT = 4500; numUCT < 8000; numUCT = numUCT + 500) {
                // for (int se = 1; se <= 3; se++)
                for (int ag = 1; ag <= se; ag++)
                    for (int i = 1; i <= 10; i++) {
                        runAlgorithm("uct", se, ag, i, horizon, numUCT);
                    }
            }*/
    }
        /*    for (int se = 1; se <= 4; se++)
                for (int ag = 1; ag <= se; ag++)
                   // for (int i = 1; i <= 10; i++) {
                        runAlgorithm("vi",se, ag, 1, horizon, 0);*/
                    //}


        // MultipleAgentsVI(2, 2, 1);






           // MultipleAgentsVI(2, 1, i);
       // }
  //  }
 //   }
   // }
       // writerAllVI.close();

       /* for(int i = 1; i <= 10; i++) {
                pw.write(sb.toString());
                 MultipleAgentsVI(4,4,i);
                pw.close();    MultipleAgentsUCT(4,4,i);*/


  /*      for (int se = 1; se <= 3; se++)
            for (int ag = 1; ag <= se; ag++)
                for (int i = 1; i <= 3; i++)
                    MultipleAgentsVI(se, ag, 1);

      for (int horizon = 2; horizon < 20; horizon = horizon + 5)
            for (int numUCT = 50; numUCT < 500; numUCT = numUCT + 200)
            for (int se = 1; se <= 4; se++)
                    for (int ag = 1; ag <= se; ag++)
       for (int i = 1; i <= 3; i++) {
            //MultipleAgentsUCT(6, 6, i, 50, 5);
            MultipleAgentsUCT(se, ag, 1, numUCT, horizon);
        }

        for (int ag = 1; ag <= 4; ag++)
            for (int i = 1; i <= 3; i++)
                MultipleAgentsVI(4, ag, 1);


                writerAllVI.close();
                  writerAllUCT.close();

            }*/

    private static void runAlgorithm(String algorithm, int nSensors, int nAgents, int testNum, int horizon, int numUCT) {

        NUM_OF_AGENTS = nAgents;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);

        long startTime = System.currentTimeMillis();

        Planner planner = null;
        if(algorithm.equals("vi")) {
             planner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 10000);
        }
       else  if(algorithm.equals("uct")) {
            planner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numUCT, 2);
        }

        Policy p = planner.planFromState(initialState);
        long endTimePlan = System.currentTimeMillis();
        long totalTimePlan = endTimePlan - startTime;




        Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
        ep.write(OUTPUT_PATH+"episodes/"+ nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm);

        long endTimeTot = System.currentTimeMillis();
        long totalTimeTot = endTimeTot - startTime;


       // oPolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "viMult");


        double totalReward = 0;
        List<Double> rewardList = ep.rewardSequence;
        // List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
        for (double d : rewardList) {
            totalReward += d;
        }

        try {
           //writeResults(writerAllVI, p, initialState,allStates, OUTPUT_PATH+"policy/" + nSensors + " Sensors ," + nAgents + " Agents PolicyMultAgents test" + testNum , totalReward, totalTimePlan, testNum);
             writeResults(algorithm, numUCT, horizon, writerAll, totalReward, totalTimePlan,totalTimeTot, testNum);
            if(algorithm == "vi")
            writePolicy((ValueIteration)planner,OUTPUT_PATH+"policy/"+nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm,p, allStates);
            else
                writePolicy(null,OUTPUT_PATH+"policy/"+nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm,p, allStates);
          //  writePolicy33((ValueIteration)planner,OUTPUT_PATH+"policy/"+nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm,p, allStates);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
  //      System.out.println("Total Reward" + totalReward);
    //    System.out.println("VI runTime: " + totalTime + " miliseconds");///1000);
    }

  //  private static void writePolicy33(ValueIteration vi,String output, Policy p, List<State> allStates) {
      /*  try {
            PrintWriter writer = new PrintWriter(output + ".csv");
            StringBuilder sbPol = new StringBuilder();
            sbPol.append("l0");
            sbPol.append(',');
            sbPol.append("l1");
            sbPol.append(',');
            sbPol.append("l2");
            sbPol.append(',');
            sbPol.append("s0");
            sbPol.append(',');
            sbPol.append("s1");
            sbPol.append(',');
            sbPol.append("s2");
            sbPol.append(',');
            sbPol.append("action");
            sbPol.append(',');
            sbPol.append("value");
            sbPol.append('\n');
            writer.write(sbPol.toString());
            writer.flush();
            for (DataMulesState : allStates) {
                //DataMulesState dms= (DataMulesState)s;
                sbPol = new StringBuilder();
                sbPol.append(dms.agentsLoc[0]);
                sbPol.append(',');
                sbPol.append(dms.agentsLoc[1]);
                sbPol.append(',');
                sbPol.append(dms.agentsLoc[2]);
                sbPol.append(',');
                sbPol.append(dms.timeFromLastRepair[0]);
                sbPol.append(',');
                sbPol.append(dms.timeFromLastRepair[0]);
                sbPol.append(',');
                sbPol.append(dms.timeFromLastRepair[0]);
                sbPol.append(',');
                sbPol.append(p.action(s));
                sbPol.append(',');
                sbPol.append(vi.value(s));
                sbPol.append('\n');
                writer.write(sbPol.toString());
                writer.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/



    //public static void writeResults( PrintWriter writerAll, Policy p, State initialState, List<State> allStates, String output, double totReward, long totalTime, int testNum) throws FileNotFoundException, UnsupportedEncodingException {
        public static void writeResults( String algorithm,int numOfUCT, int horizon, PrintWriter writerAll, double totReward, long totalTimePlan,long totalTimeTot, int testNum) throws FileNotFoundException, UnsupportedEncodingException {

        //PrintWriter writer = new PrintWriter(output + ".csv");
        StringBuilder sb = new StringBuilder();
            sb.append(algorithm);
            sb.append(',');
            if(algorithm.equals("uct")) {
                sb.append(numOfUCT);
                sb.append(',');
                sb.append(horizon);
                sb.append(',');
            }
            else if(algorithm.equals("vi"))
            {
                sb.append("N/A");
                sb.append(',');
                sb.append("N/A");
                sb.append(',');
            }
            sb.append(testNum);
            sb.append(',');
            sb.append(totReward);
            sb.append(',');
            sb.append(TOTAL_TIME_STEPS);
            sb.append(',');
            sb.append(totalTimePlan);
            sb.append(',');
            sb.append(totalTimeTot);
            sb.append(',');
            sb.append(NUM_OF_SENSORS);
            sb.append(',');
            sb.append(NUM_OF_AGENTS);
            sb.append(',');
            sb.append(PROB_SENSOR_BREAK);
            sb.append(',');
            sb.append(MAX_BROKEN);
            sb.append('\n');
            writerAll.write(sb.toString());
            writerAll.flush();

    }

    public static void writePolicy(ValueIteration vi, String output, Policy p, List<State> allStates)
    {
        try {
            PrintWriter writer = new PrintWriter(output + ".csv");
            StringBuilder sbPol = new StringBuilder();
            sbPol.append("Locs");
            sbPol.append(',');
            sbPol.append("Sensors");
            sbPol.append(',');
            sbPol.append("action");
            if(vi != null) {
                sbPol.append(',');
                sbPol.append("value");
            }
            sbPol.append('\n');

            writer.write(sbPol.toString());
            writer.flush();
            for (State s : allStates) {
                sbPol = new StringBuilder();
                sbPol.append(s.toString());
                sbPol.append(',');
                sbPol.append(',');
                sbPol.append(p.action(s));
                if(vi != null) {
                    sbPol.append(',');
                    sbPol.append(vi.value(s));
                }
                sbPol.append('\n');
                writer.write(sbPol.toString());
                writer.flush();
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
        //}
/*
        for (State s : allStates) {
            writer.println("State: " + s);
            writer.println("Action: " + p.action(s));
            writer.println();
        }
        writer.println();
        writer.println("Total Reward: " + totReward);
        writer.println("runTime: " + totalTime + " miliseconds");//1000);*/
          //  writer.flush();
           // writer.close();
      //  }

        /*writerAllVI.println();
        writerAllVI.println(NUM_OF_SENSORS + " sensors");
        writerAllVI.println(NUM_OF_AGENTS + " Agents");
        writerAllVI.println("test: "+testNum);
        writerAllVI.println("Total Reward: " + totReward);
        writerAllVI.println("runTime: "+totalTime +" miliseconds");
                writerAllVI.flush();   */


       // writer.close();

//}

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

   /* public static void printAVG( PrintWriter writerAVG, String output, double avgReward, long avgTime) throws FileNotFoundException, UnsupportedEncodingException {

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
    }*/



  /* public static void printPolicyUCT( PrintWriter writerAll, Policy p, State initialState,List<State> allStates, String output, double totReward, long totalTime, int testNum ,int horizon, int numUCT, int numOfVisits) throws FileNotFoundException, UnsupportedEncodingException {

       // public static void printPolicyUCT( PrintWriter writerAll, Policy p, State initialState, String output, double totReward, long totalTime, int testNum ,int horizon, int numUCT, int numOfVisits) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(output  +".csv");
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

       StringBuilder sbPol = new StringBuilder();
        sbPol.append("State");
        sbPol.append(',');
        sbPol.append("action");
        sbPol.append('\n');
        writer.write(sbPol.toString());
        writer.flush();
        for (State s : allStates) {
            sbPol = new StringBuilder();
            sbPol.append(s);
            sbPol.append(',');
            sbPol.append(p.action(s));
            sbPol.append('\n');
            writer.write(sbPol.toString());
            writer.flush();
        }
       /* writer.println();
        writer.println("Total Reward: " + totReward);
        writer.println("runTime: " + totalTime + " miliseconds");//1000);*/
    //    writer.flush();
    //    writer.close();

        /*writerAllVI.println();
        writerAllVI.println(NUM_OF_SENSORS + " sensors");
        writerAllVI.println(NUM_OF_AGENTS + " Agents");
        writerAllVI.println("test: "+testNum);
        writerAllVI.println("Total Reward: " + totReward);
        writerAllVI.println("runTime: "+totalTime +" miliseconds");
                writerAllVI.flush();   */


       // writer.close();

    //}


     /*Driver function to check for above function*/


   /* private static void MultipleAgentsUCT(int nSensors, int nAgents, int testNum, int numUCT, int horizon) {
        NUM_OF_AGENTS = nAgents;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);

        /*
        	public UCT(SADomain domain, double gamma, HashableStateFactory hashingFactory, int horizon, int nRollouts, int explorationBias){

         */

      //List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
       /* long startTime = System.currentTimeMillis();
        myUCT uct = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numUCT, 2);
        Map<State,List<QValue>> lQ = null;
       /* for(State st: allStates) {
            lQ.put(st, uct.qValues(st));
        }

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
           printPolicyUCT(writerAllUCT,p, initialState,allStates,OUTPUT_PATH+"policy/" + nSensors+ "," + nAgents + ","+ numUCT+"," +testNum + " ," +horizon , totalReward,totalTime, testNum,horizon,numUCT,visited);
          // printPolicyUCT(writerAllUCT,p, initialState, OUTPUT_PATH+ "policy/" + nSensors + " S ," + nAgents + " Ag UCT"+ numUCT+"rolls" +testNum + " horizon: " +horizon + ".txt", totalReward,totalTime, testNum,horizon,numUCT, visited);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }*/




}
