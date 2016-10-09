package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.core.oo.state.OOState;
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
        sb.append(',');
        sb.append("totalNotFixed");
        sb.append('\n');
        writerAll.write(sb.toString());
        writerAll.flush();


            for (int se = 2; se <= 4; se++)
                for (int ag = 1; ag <= se; ag++)
           //int se = 5;
                  // int ag = 3;
                    //for(int doms = ag; doms >=2; doms--)
            runAlgorithm(1,"hybridVI", se,ag,2,2,10);
        }
       // runAlgorithm("hybridUct",2,2,1,2,2000);
       // runAlgorithm("uct",2,2,1,2,2000);
       /* runAlgorithm("vi",2,1,1,2,2);
        runAlgorithm("hybridVI",2,1,1,2,2);
        runAlgorithm("uct",2,1,1,2,2000);
        runAlgorithm("hybridUct",2,1,1,2,2000);
        runAlgorithm("vi",4,2,1,2,2);
        runAlgorithm("hybridVI",4,2,1,2,2);
        runAlgorithm("uct",4,2,1,2,2000);
        runAlgorithm("hybridUct",4,2,1,2,2000);
        */


        //runAlgorithm(30,"hybridVI", 3,3,2,2,2);
        //runAlgorithm(2,"vi",4,3,2,2,1);
      /*  runAlgorithm("vi",4,2,1,2,2);
        runAlgorithm("hybridUct",4,2,1,2,2000);
        runAlgorithm("uct",4,2,1,2,2000);
        runAlgorithm("hybridUct",4,2,1,2,4000);
        runAlgorithm("uct",4,2,1,2,4000);
        runAlgorithm("hybridUct",4,2,1,3,2000);
        runAlgorithm("uct",4,2,1,3,2000);
        runAlgorithm("hybridUct",4,2,1,3,4000);
        runAlgorithm("uct",4,2,1,3,4000);*/

  //      runAlgorithm("vi",3,2,1,2,2);
//runAlgorithm("vi",3,3,1,2,2);


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
     /*  int se = 3;
        int horizon=3;
            for (int numUCT = 50; numUCT < 2200; numUCT = numUCT + 200) {
                // for (int se = 1; se <= 3; se++)
                for (int ag = 1; ag <= se; ag++)
                    for (int i = 1; i <= 10; i++) {
                        runAlgorithm("uct", se, ag, i, horizon, numUCT);
                    }
            }*/
    //}
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

    private static void runAlgorithm(int numOfDomains, String algorithm, int nSensors, int nAgents, int horizon, int numUCT, int iterations) {

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
        if (algorithm.equals("vi")) {
            planner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 100000);
        } else if (algorithm.equals("uct")) {
            planner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numUCT, 2);
        } else if (algorithm.equals("hybridUct")) {
            Planner innerPlanner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numUCT, 2);
            planner = new HybridPlanner(innerPlanner, numOfDomains,horizon,numUCT);
        } else if (algorithm.equals("hybridVI")) {
            Planner innerPlanner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 100000);
            planner = new HybridPlanner(innerPlanner,numOfDomains, 0.001, 100000);

        }

        Policy p = planner.planFromState(initialState);
        long endTimePlan = System.currentTimeMillis();
        long totalTimePlan = endTimePlan - startTime;

     /*   try {
            PrintWriter pw = new PrintWriter(OUTPUT_PATH + "tests/policyTest.txt");
            for (State s : allStates) {
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString());
                sb.append(p.action(s));
                pw.write(sb.toString());
                pw.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        for(int testNum = 0; testNum < iterations; testNum++ )
        {
            Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
            ep.write(OUTPUT_PATH + "episodes/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-" + testNum + " " + algorithm);

            long endTimeTot = System.currentTimeMillis();
            long totalTimeTot = endTimeTot - startTime;

            int notFixed = 0;
            // oPolicyUtils.rollout(p, initialState, domainNum.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "viMult");
            DataMulesState prev = (DataMulesState) (((OOState) ep.state(0)).object(Constants.CLASS_STATE));
            for (int t = 1; t < ep.stateSequence.size(); t++)
            {
                DataMulesState curr = (DataMulesState)(((OOState) ep.state(t)).object(Constants.CLASS_STATE));
                notFixed += checkLastRepair( prev.timeFromLastRepair, curr.timeFromLastRepair);
                prev = curr;
            }

            double totalReward = 0;
            List<Double> rewardList = ep.rewardSequence;
            // List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domainNum.getModel(),TOTAL_TIME_STEPS).rewardSequence;
            for (double d : rewardList) {
                totalReward += d;
            }

            try {
                //writeResults(writerAllVI, p, initialState,allStates, OUTPUT_PATH+"policy/" + nSensors + " Sensors ," + nAgents + " Agents PolicyMultAgents test" + testNum , totalReward, totalTimePlan, testNum);
                writeResults(algorithm, numOfDomains,nSensors, nAgents, numUCT, horizon, writerAll, totalReward, totalTimePlan, totalTimeTot, testNum, notFixed);
                if (algorithm == "")
                    writePolicy((ValueIteration) planner, OUTPUT_PATH + "policy/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-" + testNum + " " + algorithm, p, allStates);
                else
                    writePolicy(null, OUTPUT_PATH + "policy/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-" + testNum + " " + algorithm, p, allStates);
                //  writePolicy33((ValueIteration)planner,OUTPUT_PATH+"policy/"+nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm,p, allStates);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //      System.out.println("Total Reward" + totalReward);
            //    System.out.println("VI runTime: " + totalTime + " miliseconds");///1000);
        }
    }

    private static int checkLastRepair(Integer[] prev, Integer[] curr) {
        int ans = 0;
        for(int i = 0; i < prev.length; i++)
        {
            if(prev[i] == -1 && curr[i] == -1)
                ans +=1;

        }
        return ans;
    }



    //public static void writeResults( PrintWriter writerAll, Policy p, State initialState, List<State> allStates, String output, double totReward, long totalTime, int testNum) throws FileNotFoundException, UnsupportedEncodingException {
        public static void writeResults(String algorithm, int numOfDomains, int nSensors, int nAgents, int numOfUCT, int horizon, PrintWriter writerAll, double totReward, long totalTimePlan, long totalTimeTot, int testNum, int notFixed) throws FileNotFoundException, UnsupportedEncodingException {

        //PrintWriter writer = new PrintWriter(output + ".csv");
        StringBuilder sb = new StringBuilder();
            sb.append(algorithm);
            sb.append(numOfDomains);
            sb.append(',');
            if(algorithm.equals("uct")||algorithm.equals("hybridUct") ) {
                sb.append(numOfUCT);
                sb.append(',');
                sb.append(horizon);
                sb.append(',');
            }
          //  else if(algorithm.equals("vi"))
            else
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
            sb.append(nSensors);
            sb.append(',');
            sb.append(nAgents);
            sb.append(',');
            sb.append(PROB_SENSOR_BREAK);
            sb.append(',');
            sb.append(MAX_BROKEN);
            sb.append(',');
            sb.append(notFixed);
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

}
