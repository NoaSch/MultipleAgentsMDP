package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.rtdp.RTDP;
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
import java.util.Map;

import static MultipleAgents.Constants.*;

/**
 * Created by noa on 24-Aug-16.
 */
public class main {

    public static PrintWriter writerAll;

    public static PrintWriter writerPrints;


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        //writerAllVI = new PrintWriter(OUTPUT_PATH + "allSum"+numUCT+".txt", "UTF-8");
        //writerAllVI = new PrintWriter(OUTPUT_PATH + "vi" + ".csv");
        writerAll = new PrintWriter(OUTPUT_PATH + "results/allSum" + ".csv");
        writerPrints = new PrintWriter(OUTPUT_PATH + "results/prints.txt");

        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm name");
        sb.append(',');
        sb.append("# Iterations");
        sb.append(',');
        sb.append("Horizon");
        sb.append(',');
        sb.append("MaxDelta");
        sb.append(',');
        sb.append("maxDepth");
        sb.append(',');
        sb.append("Instance");
        sb.append(',');
        sb.append("Accumulated reward ");
        sb.append(',');
        sb.append("# rollouts");
        sb.append(',');
        sb.append("Planning runtime");
        sb.append(',');
        sb.append("rollout runtime");
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

        //runAlgorithm(1, "vi", 5, 1, 2, 0, 0.001, 100, 5);
        runAlgorithm(2,"hybridVI",4,2,1,0,0.001,0,3);

        //  runAlgorithm(1, "vi", 10, 1, 2, 0, 0.001, 100, 5);
       /* for(int se = 8; se <=20;se++)
        {
            runAlgorithm(1, "vi", se, 1, 2, 0, 0.001, 100, 5);
            runAlgorithm(1, "vi", se, 2, 2, 0, 0.001, 100, 5);
        }*/
        /*    for (int se = 1; se <= 3; se++)
                for (int ag = 1; ag <= se; ag++) {
                    //int se = 5;
                    // int ag = 3;
                    //for(int doms = ag; doms >=2; doms--)
                    runAlgorithm(1, "vi", se, ag, 2, 0, 0.001, 100, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 2000, 0.001, 150, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 2000, 0.001, 100, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 2000, 0.001, 50, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 1000, 0.001, 150, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 1000, 0.001, 100, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 1000, 0.001, 50, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 500, 0.001, 150, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 500, 0.001, 100, 10);
                    runAlgorithm(1, "rtdp", se, ag, 2, 500, 0.001, 50, 10);

                }*/
  /*          for (int ag = 1; ag <= 2; ag++) {
                //int se = 5;
                // int ag = 3;
                //for(int doms = ag; doms >=2; doms--)
                runAlgorithm(1, "vi", 4, ag, 2, 2000,0.001,100,10);
                runAlgorithm(1, "rtdp", 4, ag, 2, 2000, 0.001,100,10);
                runAlgorithm(1, "uct", 4, ag, 2, 2000, 0.001,100,10);
            }*/

        }
       // runAlgorithm("hybridUct",2,2,1,2,2000);


    private static void runAlgorithm(int numOfDomains, String algorithm, int nSensors, int nAgents, int horizon, int numRollouts,double maxDelta,int maxLength,  int iterations) {

        NUM_OF_AGENTS = nAgents;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
       // System.out.println(allStates.size());


        long startTime = System.currentTimeMillis();

        Planner planner = null;
        if (algorithm.equals("vi")) {
            planner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 100000);
        } else if (algorithm.equals("uct")) {
            planner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numRollouts, 2);
        } else if (algorithm.equals("hybridUct")) {
            Planner innerPlanner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numRollouts, 2);
            planner = new HybridPlanner(innerPlanner, numOfDomains,horizon,numRollouts);
        } else if (algorithm.equals("hybridVI")) {
            Planner innerPlanner = new ValueIteration(domain, DISCOUNT, hashingFactory, maxDelta, 100000);
            planner = new HybridPlanner(me,innerPlanner,numOfDomains, 0.001, 100000);
        }
//	public RTDP(SADomain domain, double gamma, HashableStateFactory hashingFactory, double vInit, int numRollouts, double maxDelta, int maxDepth){

        else if (algorithm.equals("rtdp")) {
            planner = new RTDP(domain,DISCOUNT,hashingFactory,0,numRollouts,maxDelta,maxLength);
        }


        writerPrints.write("START Planning\n");
        writerPrints.flush();
        Policy p = planner.planFromState(initialState);
        long endTimePlan = System.currentTimeMillis();
        long totalTimePlan = endTimePlan - startTime;
        writerPrints.write("END Planning\n");
        writerPrints.flush();
     /*  try {
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

        for(int testNum = 0; testNum < iterations; testNum++ ) {
            writerPrints.write("START Rollout");
            writerPrints.flush();
            startTime = System.currentTimeMillis();
            Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
            writerPrints.write("end Rollout");
            writerPrints.flush();
            ep.write(OUTPUT_PATH + "episodes/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-" + testNum + " " + algorithm);
            writerPrints.write("END Rollout write");
            writerPrints.flush();
            long endTimeRoll = System.currentTimeMillis();
            long totalTimeRoll = endTimeRoll - startTime;

            int notFixed = 0;
            // oPolicyUtils.rollout(p, initialState, domainNum.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "viMult");
            DataMulesState prev = (DataMulesState) (((OOState) ep.state(0)).object(Constants.CLASS_STATE));
            for (int t = 1; t < ep.stateSequence.size(); t++) {
                DataMulesState curr = (DataMulesState) (((OOState) ep.state(t)).object(Constants.CLASS_STATE));
                notFixed += checkLastRepair(prev.timeFromLastRepair, curr.timeFromLastRepair);
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
                writeResults(algorithm, numOfDomains, nSensors, nAgents, numRollouts, horizon, maxDelta, maxLength, writerAll, totalReward, totalTimePlan, totalTimeRoll, testNum, notFixed);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

                if (algorithm == "vi")
                    writePolicy((ValueIteration) planner, OUTPUT_PATH + "policy/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-"  + " " + algorithm, p, allStates);
                else
                    writePolicy(null, OUTPUT_PATH + "policy/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-"  + " " + algorithm, p, allStates);
                //  writePolicy33((ValueIteration)planner,OUTPUT_PATH+"policy/"+nSensors + " Sensors ," + nAgents + " Agents " +"test-"+ testNum + " " +algorithm,p, allStates);



            //      System.out.println("Total Reward" + totalReward);
            //    System.out.println("VI runTime: " + totalTime + " miliseconds");///1000);

    }

    private static int checkLastRepair(Map<Integer,Integer> prev, Map<Integer,Integer> curr) {
        int ans = 0;
       // for(int i = 0; i < prev.si; i++)
        for(Integer i: prev.keySet())
        {
            if(prev.get(i) == -1 && curr.get(i) == -1)
                ans +=1;

        }
        return ans;
    }



    //public static void writeResults( PrintWriter writerAll, Policy p, State initialState, List<State> allStates, String output, double totReward, long totalTime, int testNum) throws FileNotFoundException, UnsupportedEncodingException {
        public static void writeResults(String algorithm, int numOfDomains, int nSensors, int nAgents, int numOfinnerRollouts, int horizon, double maxDelta,int maxDepth,PrintWriter writerAll, double totReward, long totalTimePlan, long totalTimeTot, int testNum, int notFixed) throws FileNotFoundException, UnsupportedEncodingException {

        //PrintWriter writer = new PrintWriter(output + ".csv");
        StringBuilder sb = new StringBuilder();
            sb.append(algorithm);
            sb.append(numOfDomains);
            sb.append(',');
            if(algorithm.equals("uct")||algorithm.equals("hybridUct") ) {
                sb.append(numOfinnerRollouts);
                sb.append(',');
                sb.append(horizon);
                sb.append(',');
                sb.append("N/A");
                sb.append(',');
                sb.append("N/A");
                sb.append(',');
            }
          //  else if(algorithm.equals("vi"))
            else if(algorithm.equals("rtdp")) {
                sb.append(numOfinnerRollouts);
                sb.append(',');
                sb.append("N/A");
                sb.append(',');
                sb.append(maxDelta);
                sb.append(',');
                sb.append(maxDepth);
                sb.append(',');
            }
                else
                {
                    sb.append("N/A");
                    sb.append(',');
                    sb.append("N/A");
                    sb.append(',');
                    sb.append(maxDelta);
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
