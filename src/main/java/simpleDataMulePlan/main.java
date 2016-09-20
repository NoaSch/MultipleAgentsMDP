package simpleDataMulePlan;

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

import static simpleDataMulePlan.Constants.OUTPUT_PATH;

/**
 * Created by noa on 24-Aug-16.
 */
public class main {

   public static void main(String[] args) {
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());



        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);
        Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
        Policy p = planner.planFromState(initialState);


        //   PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "vi");
       /* for(int i = 10; i <= 150; i = i +10 ){
            rolloutAndgetPolicy(domain, initialState, allStates, p, i, 5);
        }*/

       rolloutAndgetPolicy(domain, initialState, allStates, p, 10, 1);


    }

    private static void rolloutAndgetPolicy(OOSADomain domain, State initialState, List<State> allStates, Policy p, int numOfTime, int avgof) {
        int sum =0;
        for (int i = 0; i < avgof; i++) {
            Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), numOfTime);
            ep.write(OUTPUT_PATH + "viSingle");

            double totalReward = 0;
            //  List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
            List<Double> rewardList = ep.rewardSequence;
            for (double d : rewardList) {
                totalReward += d;
            }
            try {
                printPolicy(p, initialState, allStates, OUTPUT_PATH + "Policy.txt", totalReward);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sum += totalReward;
            System.out.println("Total Reward" + totalReward);

        }
        double avg = sum/avgof;
        System.out.println();
        System.out.println("numOfTimes: " + numOfTime + ", Avg total reward: " + avg);
        System.out.println();

    }
    public static void printPolicy(Policy p, State initialState, List<State> allStates, String output, double totReward) throws FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writer = new PrintWriter(output, "UTF-8");

        for(State s: allStates)
        {
            writer.println("State: " + s);
            writer.println("Action: " + p.action(s));
            writer.println();
        }
        writer.println();
        writer.println("Total Reward: " + totReward);
        writer.close();
    }

}
