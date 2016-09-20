package withSet;

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

import static withSet.Constants.*;

/**
 * Created by noa on 24-Aug-16.
 */
public class main {

    public static void main(String[] args) {

       MultipleAgentsVI(2, 2);
     /*   for(int se = 2 ;se <=4; se ++)

         for(int ag = 1;ag <=se ; ag ++)
       {
           System.out.println(  );
           System.out.println(  );
           System.out.println("sensors " + se + ", Agents:" + ag);
           System.out.println(  );
           MultipleAgentsVI(se, ag);
       }*/


    }

    private static void MultipleAgentsVI(int nSensors, int nAgents) {
        NUM_OF_AGENTS = nAgents ;
        NUM_OF_SENSORS = nSensors;
        DataMulesDomain me = new DataMulesDomain();
        OOSADomain domain = me.generateDomain();

        // Create the initial state
        State initialState = new GenericOOState(DataMulesState.createInitialState());

        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);
        Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
        // ValueIteration v = (ValueIteration) planner;

        //System.out.println( "value::::: " +v.value(initialState));
        Policy p = planner.planFromState(initialState);
        Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS);
        ep.write(OUTPUT_PATH +nSensors +" Sensors ," + nAgents + " Agents viMult");
        //PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).write(OUTPUT_PATH + "viMult");


        double totalReward = 0;
        List<Double> rewardList = ep.rewardSequence;
        // List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
        for (double d : rewardList)
        {
            totalReward += d;
        }

        try {
            printPolicy(p,initialState,allStates,OUTPUT_PATH +nSensors +" Sensors ," + nAgents + " Agents PolicyMultAgents.txt",totalReward);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("Total Reward" + totalReward);
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

     /*Driver function to check for above function*/


}
