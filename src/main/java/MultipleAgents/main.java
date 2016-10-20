package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.rtdp.RTDP;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.core.action.Action;
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
import java.util.concurrent.*;

import static MultipleAgents.Constants.*;
import static MultipleAgents.DataMulesDomain.graph;
import static cern.clhep.Units.deg;

/**
 * Created by noa on 24-Aug-16.
 */
public class main {

    private static OOSADomain domain;
    private static DataMulesDomain me;
    public static PrintWriter writerAll;
   //static Policy p;
    public static PrintWriter writerPrints;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, TimeoutException {
        setResultsHeaders();
       try {
        int iterations = 10;
       for(int se = 2; se <= 20; se ++)
        {
            for(int ag = 1; ag < se; ag ++)
            {
                for(int deg = 1; deg <= Math.min(se-1,3); deg++)
                {
                    for(int i =1; i <= iterations; i++)
                    {
                        NUM_OF_SENSORS = se;
                        NUM_OF_AGENTS = ag;
                        me = new DataMulesDomain(deg);
                        domain = me.generateDomain();
                        //start print the graph in graphs folder
                        PrintWriter pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "sensors, " + NUM_OF_AGENTS + "agents, deg-" + deg +" iteraion"+i+ ".txt");
                        pw.write(graph.toString());
                        pw.flush();
                        //end
                        runAlgorithm(domain, 1, "vi", se, ag, 2, 0, 0.001, 100, i,deg);
                        runAlgorithm(domain, 1, "rtdp", se, ag, 2, 500, 0.001, 50, i,deg);
                        runAlgorithm(domain, 1, "rtdp", se, ag, 2, 500, 0.001, 150, i,deg);
                        runAlgorithm(domain, 1, "rtdp", se, ag, 2, 2000, 0.001, 50, i,deg);
                        runAlgorithm(domain, 1, "rtdp", se, ag, 2, 2000, 0.001, 150, i,deg);

                        for(int nOfDomains = ag; nOfDomains >=1;nOfDomains-- )
                        {
                            runAlgorithm(domain, nOfDomains, "hybridVI", se, ag, 2, 0, 0.001, 0, i,deg);
                        }
                    }
                }
            }
        }
    }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("done!");
    }

        //Set the result's cav file headers
    private static void setResultsHeaders() throws FileNotFoundException {
        writerAll = new PrintWriter(OUTPUT_PATH + "results/allSum" + ".csv");
        writerPrints = new PrintWriter(OUTPUT_PATH + "results/prints.txt");

        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm name");
        sb.append(',');
        sb.append("Number Of Domains");
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
        sb.append("graph deg");
        sb.append(',');
        sb.append("p ");
        sb.append(',');
        sb.append("B (Max New Faults)");
        sb.append(',');
        sb.append("totalNotFixed");
        sb.append('\n');
        writerAll.write(sb.toString());
        writerAll.flush();
    }


    /**
     * plan and rollout ont iteration.
     * @param domain the domain in which to plan
     * @param numOfDomains the number of wanted domains - only for hybrid
     * @param algorithm the name of the planning algorithm
     * @param nSensors the number of sensors.
     * @param nAgents the number of agents
     * @param horizon the horizon - only for uct
     * @param numRollouts the number of rollouts - only for uct and RTDP
     * @param maxDelta when the maximum change in the value function is smaller than this value, VI will terminate.
     * @param maxLength the maximum depth - onlt for RTDP
     * @param testNum the number of iteration
     * @param deg the graph degree
     */
    private static void runAlgorithm(OOSADomain domain,int numOfDomains, String algorithm, int nSensors, int nAgents, int horizon, int numRollouts,double maxDelta,int maxLength,  int testNum, int deg) throws InterruptedException, ExecutionException, TimeoutException {


        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();

        //get the planning start time
        long startTime = System.currentTimeMillis();

        //create the planner
        Planner planner = null;
        if (algorithm.equals("vi")) {
            planner = new ValueIteration(domain, DISCOUNT, hashingFactory, 0.001, 100000);
        } else if (algorithm.equals("uct")) {
            planner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numRollouts, 2);
        } else if (algorithm.equals("hybridUct")) {
            Planner innerPlanner = new myUCT(domain, DISCOUNT, hashingFactory, horizon, numRollouts, 2);
            planner = new HybridPlanner(innerPlanner, numOfDomains, horizon, numRollouts);
        } else if (algorithm.equals("hybridVI")) {
            Planner innerPlanner = new ValueIteration(domain, DISCOUNT, hashingFactory, maxDelta, 100000);
            planner = new HybridPlanner(innerPlanner, numOfDomains, 0.001, 100000);
        } else if (algorithm.equals("rtdp")) {
            planner = new RTDP(domain, DISCOUNT, hashingFactory, 0, numRollouts, maxDelta, maxLength);
        } else if (algorithm.equals("hybridRTDP")) {
            Planner innerPlanner = new RTDP(domain, DISCOUNT, hashingFactory, 0, numRollouts, maxDelta, maxLength);
            planner = new HybridPlanner(innerPlanner, numOfDomains, numRollouts, maxDelta, maxLength);
        }

        //run the planning in a separate thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Policy p = null;
        try {
            writerPrints.write("Start Planning\n");
            writerPrints.flush();
            p = executor.submit(new Planning(planner, initialState)).get(1, TimeUnit.MINUTES);
        }

        catch(TimeoutException e)
        {
            long endTimePlan = System.currentTimeMillis();
            long totalTimePlan = endTimePlan - startTime;
            writerPrints.write("END Planning\n");
            writerPrints.flush();
            try {
                //write the results to allSum file
                writeResults(algorithm, numOfDomains, nSensors, nAgents, numRollouts, horizon, maxDelta, maxLength, writerAll, 1, totalTimePlan, -1, testNum, -1, deg);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            executor.shutdown();
        }

       if(p != null) {
           long endTimePlan = System.currentTimeMillis();
           long totalTimePlan = endTimePlan - startTime;
           writerPrints.write("END Planning\n");
           writerPrints.flush();
            executor.shutdown();
          /*  writerPrints.write("Start allStates\n");
            writerPrints.flush();
            List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
            writerPrints.write("End allStates\n");
            writerPrints.flush();*/
            writerPrints.write("Start rollout\n");
            writerPrints.flush();
            //rollout the policy
                long startTimeR = System.currentTimeMillis();
                Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
                ep.write(OUTPUT_PATH + "episodes/" + nSensors + " Sensors ," + nAgents + " Agents " + "test-" + testNum + " " + algorithm);
                long endTimeRoll = System.currentTimeMillis();
                long totalTimeRoll = endTimeRoll - startTimeR;

                //cont how many sensors was broken 2 consecutive time steps
                int notFixed = 0;
                DataMulesState prev = (DataMulesState) (((OOState) ep.state(0)).object(Constants.CLASS_STATE));
                for (int t = 1; t < ep.stateSequence.size(); t++) {
                    DataMulesState curr = (DataMulesState) (((OOState) ep.state(t)).object(Constants.CLASS_STATE));
                    notFixed += checkLastRepair(prev.timeFromLastRepair, curr.timeFromLastRepair);
                    prev = curr;
                }

                //calculate the total reward
                double totalReward = 0;
                List<Double> rewardList = ep.rewardSequence;
                for (double d : rewardList) {
                    totalReward += d;
                }

                //write the result in a csv file
                try {
                    writeResults(algorithm, numOfDomains, nSensors, nAgents, numRollouts, horizon, maxDelta, maxLength, writerAll, totalReward, totalTimePlan, totalTimeRoll, testNum, notFixed, deg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            writerPrints.write("END rollout\n");
            writerPrints.flush();
               /*if (algorithm == "vi")

                    writePolicy((ValueIteration) planner, OUTPUT_PATH + "policy/" + nSensors + " S ," + nAgents + " A "  + " " + algorithm+","+numOfDomains+" de-"+deg,p, allStates);
                else
                    writePolicy(null, OUTPUT_PATH + "policy/" + nSensors + " S ," + nAgents + " A " + algorithm+","+numOfDomains+" de-"+deg, p, allStates);
*/
        }

    //Count how many sensors was broken and not fixed at the time step after
    private static int checkLastRepair(Map<Integer,Integer> prev, Map<Integer,Integer> curr) {
        int ans = 0;
        for(Integer i: prev.keySet())
        {
            if(prev.get(i) == -1 && curr.get(i) == -1)
                ans +=1;

        }
        return ans;
    }

    //write the result in a csv file
    public static void writeResults(String algorithm, int numOfDomains, int nSensors, int nAgents, int numOfinnerRollouts, int horizon, double maxDelta,int maxDepth,PrintWriter writerAll, double totReward, long totalTimePlan, long totalTimeTot, int testNum, int notFixed, int deg) throws FileNotFoundException, UnsupportedEncodingException {
            StringBuilder sb = new StringBuilder();
            sb.append(algorithm);
        sb.append(',');
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
        if(totReward!= 1)
            sb.append(totReward);
        else
            sb.append("null");
            sb.append(',');
            sb.append(TOTAL_TIME_STEPS);
            sb.append(',');
            sb.append(totalTimePlan);
            sb.append(',');
        if(totReward!= 1)
            sb.append(totalTimeTot);
        else
            sb.append("null");
            sb.append(',');
            sb.append(nSensors);
            sb.append(',');
            sb.append(nAgents);
            sb.append(',');
        sb.append(deg);
        sb.append(',');
            sb.append(PROB_SENSOR_BREAK);
            sb.append(',');
            sb.append(MAX_BROKEN);
            sb.append(',');
        if(totReward!= 1)
            sb.append(notFixed);
        else
            sb.append("null");
            sb.append('\n');
            writerAll.write(sb.toString());
            writerAll.flush();

    }

    //write the policy in a csv file
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
                Action act = p.action(s);
                if (act == null){}
                else{
                    sbPol = new StringBuilder();
                    sbPol.append(s.toString());
                    sbPol.append(',');
                    sbPol.append(',');
                    sbPol.append(act);
                    if (vi != null) {
                        sbPol.append(',');
                        sbPol.append(vi.value(s));
                    }
                    sbPol.append('\n');
                    writer.write(sbPol.toString());
                    writer.flush();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class Planning implements Callable<Policy>{//},Runnable {
       // private static class Planning implements Runnable {

        Planner planner;
        State initialState;

        public Planning(Planner planner, State initialState) {
            this.planner = planner;
            this.initialState = initialState;
        }

        public Policy call() throws Exception {
           Policy  p = planner.planFromState(initialState);
            return p;
        }
        /*public void run() {
            p = planner.planFromState(initialState);
        }*/
    }

    public void oldThreadTests()
    {
        // }// Timeout of 10 minutes.

        //   writerPrints.write("START Planning\n");
        //  writerPrints.flush();
    /*    Thread thread = new Thread(new Planning(planner,initialState));
        thread.start();
        thread.join(1000*60);
        if (thread.isAlive())
            thread.interrupt();*/
    }
    public void oldTests(){
          /*  for(int i =3; i <16;i++)
        {
            for(int deg =1; deg < i; deg++)
            {
                System.out.println(i + ",deg:"+ deg);
                Graph gr = new Graph(i);
                gr.createGraphByDeg(deg);
                PrintWriter pw = new PrintWriter(OUTPUT_PATH + "graphs/" + i +"," +deg + ".txt");
                pw.write(gr.toString());
                pw.flush();
            }
        }*/
        //setResultsHeaders();

        //runAlgorithm(1, "rtdp", 5, 2, 2, 2000, 0.001, 100, 10);

        //runAlgorithm(1,"vi",4,3,1,0,0.001,0,1);

        //runAlgorithm(4,"hybridVI",6,4,1,0,0.001,0,10);
        //  runAlgorithm(3,"hybridVI",6,4,1,0,0.001,0,10);
        //  //runAlgorithm(2,"hybridVI",6,4,1,0,0.001,0,10);

     /*  for (int se = 3 ; se <= 6; se++)
        {
            runAlgorithm(2, "vi", se, 2, 2, 2000, 0.001, 100, 10);
        }*/



            /*for (int se = 2 ; se <= 15; se++)
            for (int ag = 1; ag < se; ag++) {
                runAlgorithm(1, "rtdp", se, ag, 2, 2000, 0.001, 100, 10);
            }




        //  runAlgorithm(1, "vi", 10, 1, 2, 0, 0.001, 100, 5);
        for(int se = 8; se <=20;se++)
        {
            runAlgorithm(1, "vi", se, 1, 2, 0, 0.001, 100, 5);
            runAlgorithm(1, "vi", se, 2, 2, 0, 0.001, 100, 5);
        }*/
    /*    DataMulesDomain me;
        OOSADomain domain;
        for (int se = 1; se <= 3; se++) {
            NUM_OF_AGENTS = 1;
            NUM_OF_SENSORS = se;
            for (int deg = 1; deg <= NUM_OF_SENSORS - 1; deg++) {
                me = new DataMulesDomain(deg);
                domain = me.generateDomain();
                PrintWriter pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "," + NUM_OF_AGENTS +","+deg+ ".txt");
                pw.write(graph.toString());
                pw.flush();
                //int se = 5;
                // int ag = 3;
                //for(int doms = ag; doms >=2; doms--)

               /* runAlgorithm(domain, 1, "vi", se, 1, 2, 0, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridVI", se, 1, 2, 0, 0.001, 100, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 2000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 2000, 0.001, 150, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 2000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 2000, 0.001, 100, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 2000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 2000, 0.001, 50, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 1000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 1000, 0.001, 150, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 1000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 1000, 0.001, 100, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 1000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 1000, 0.001, 50, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 500, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 500, 0.001, 150, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 500, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 500, 0.001, 100, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, 1, 2, 500, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, 1, 2, 500, 0.001, 50, 10,deg);*/

         /*       NUM_OF_AGENTS = 2;
                NUM_OF_SENSORS = se;
                me = new DataMulesDomain(deg);
                domain = me.generateDomain();

                runAlgorithm(domain, 1, "vi", se, NUM_OF_AGENTS, 2, 0, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridVI", se, NUM_OF_AGENTS, 2, 0, 0.001, 100, 10,deg);
                runAlgorithm(domain, 2, "hybridVI", se, NUM_OF_AGENTS, 2, 0, 0.001, 100, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 2000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 150, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 2000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 100, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 2000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 50, 10,deg);

                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 1000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 1000, 0.001, 150, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 2000, 0.001, 50, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 1000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 1000, 0.001, 100, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 1000, 0.001, 100, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 1000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 1000, 0.001, 50, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 1000, 0.001, 50, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 500, 0.001, 150, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 150, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 150, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 500, 0.001, 100, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 100, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 100, 10,deg);


                runAlgorithm(domain, 1, "rtdp", se, NUM_OF_AGENTS, 2, 500, 0.001, 50, 10,deg);
                runAlgorithm(domain, 1, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 50, 10,deg);
                runAlgorithm(domain, 2, "hybridRTDP", se, NUM_OF_AGENTS, 2, 500, 0.001, 50, 10,deg);


            }*/
  /*          for (int ag = 1; ag <= 2; ag++) {
                //int se = 5;
                // int ag = 3;
                //for(int doms = ag; doms >=2; doms--)
                runAlgorithm(1, "vi", 4, ag, 2, 2000,0.001,100,10);
                runAlgorithm(1, "rtdp", 4, ag, 2, 2000, 0.001,100,10);
                runAlgorithm(1, "uct", 4, ag, 2, 2000, 0.001,100,10);
            }*/
        //   }

        //  }

    }
    public static void mainOLd(String[] args) throws FileNotFoundException, UnsupportedEncodingException, TimeoutException {

        setResultsHeaders();
      /*  for(int deg =1; deg <= 3; deg++)
        {
            for(int se = 2; se <30; se++)
            {
               for (int ag = 1; ag <= 2; ag++)*/
        try {
            NUM_OF_SENSORS = 6;
            NUM_OF_AGENTS = 5;
            me = new DataMulesDomain(2);
            domain = me.generateDomain();
            PrintWriter pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "," + NUM_OF_AGENTS + "," + deg + ".txt");
            pw.write(graph.toString());
            pw.flush();
            runAlgorithm(domain, 1, "vi", 6, 5, 2, 0, 0.001, 100, 10,2);
            NUM_OF_SENSORS = 2;
            NUM_OF_AGENTS = 1;
            me = new DataMulesDomain(1);
            domain = me.generateDomain();
            pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "," + NUM_OF_AGENTS + "," + deg + ".txt");
            pw.write(graph.toString());
            pw.flush();
            runAlgorithm(domain, 1, "vi", 2, 1, 2, 0, 0.001, 100, 10,2);
            NUM_OF_SENSORS = 6;
            NUM_OF_AGENTS = 5;
            me = new DataMulesDomain(2);
            domain = me.generateDomain();
            pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "," + NUM_OF_AGENTS + "," + deg + ".txt");
            pw.write(graph.toString());
            pw.flush();
            runAlgorithm(domain, 1, "vi", 6, 5, 2, 0, 0.001, 100, 10,2);
            NUM_OF_SENSORS = 3;
            NUM_OF_AGENTS = 2;
            me = new DataMulesDomain(1);
            domain = me.generateDomain();
            pw = new PrintWriter(OUTPUT_PATH + "graphs/" + NUM_OF_SENSORS + "," + NUM_OF_AGENTS + "," + deg + ".txt");
            pw.write(graph.toString());
            pw.flush();
            runAlgorithm(domain, 1, "vi", 3, 2, 2, 0, 0.001, 100, 10,2);


                       /*runAlgorithm(domain, 1, "rtdp", se, ag, 2, 500, 0.001, 50, 10,deg);
                                   runAlgorithm(domain, 1, "rtdp", se, ag, 2, 500, 0.001, 100, 10,deg);
           runAlgorithm(domain, 1, "rtdp", se, ag, 2, 1000, 0.001, 50, 10,deg);
                       runAlgorithm(domain, 1, "rtdp", se, ag, 2, 1000, 0.001, 50, 10,deg);*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("done!");
    }
    //  }
    public void testTreads()
    {
     /*   int iterations = 10;
        try {
            NUM_OF_SENSORS = 6;
            NUM_OF_AGENTS = 5;
            me = new DataMulesDomain(2);
            domain = me.generateDomain();
            runAlgorithm(domain, 1, "vi", 6, 5, 2, 0, 0.001, 100, 1,2);
            NUM_OF_SENSORS = 2;
            NUM_OF_AGENTS = 1;
            me = new DataMulesDomain(1);
            domain = me.generateDomain();
            runAlgorithm(domain, 1, "vi", 2, 1, 2, 0, 0.001, 100, 1,2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }
}
