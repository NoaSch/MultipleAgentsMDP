package MultipleAgents;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.List;
import java.util.Set;

import static MultipleAgents.Constants.*;
import static MultipleAgents.Permotations.*;

/**
 * Created by noa on 22-Aug-16.
 */
public class DataMulesDomain implements DomainGenerator {

    static Graph graph;
    // Create reward function
    private static RewardFunction rf = new DataMulesRewardFunction();

    // Create terminal function (the function that tells us if Zwe are at a terminal state)
    private static DataMulesTerminalFunction tf = new DataMulesTerminalFunction();
    private static int numOfSensors;
    private static int numOfAgents;
    private static Set<Integer> sensors;

    public DataMulesDomain(int deg) {
        graph = new Graph(NUM_OF_SENSORS);
        graph.createGraphByDeg(deg);
    }
    //
    // Create the full domainNum

    public OOSADomain generateDomain() {

        OOSADomain domain = new OOSADomain();
        domain.addStateClass(CLASS_STATE, DataMulesState.class);
        vec.clear();
        String arr[] = new String[NUM_OF_AGENTS * (NUM_OF_SENSORS + 2)];
        int rIndex = 0;
        int idx = 0;
        for (int i = 0; i < NUM_OF_AGENTS; i++) {
            arr[idx] = ACTION_STAY;
            idx++;
            arr[idx] = ACTION_REPAIR;
            idx++;
            for (int k = 0; k < NUM_OF_SENSORS; k++) {
                arr[idx] = "" + k;
                idx++;
            }
        }
        int r = NUM_OF_AGENTS;
        int n = arr.length;
        printCombination(arr, n, r);
        removeDuplicates(vec);
        for (int i = 0; i < vec.size(); i++) {

            String str = vec.get(i);
            String s = str.substring(1, str.length() - 1);
            //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }


        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();
        domain.setModel(new FactoredModel(smodel, rf, tf));
      //  writerPrints.write("END generateDomain\n");
     //   writerPrints.flush();
        return domain;
    }


    public static OOSADomain generateDomain(int numOfSensors, int numOfAgents) {

        OOSADomain domain = new OOSADomain();

        domain.addStateClass(CLASS_STATE, DataMulesState.class);

        vec.clear();
        String arr[] = new String[numOfAgents * (numOfSensors + 2)];
        int rIndex = 0;
        int idx = 0;
        for (int i = 0; i < numOfAgents; i++) {
            arr[idx] = ACTION_STAY;
            idx++;
            arr[idx] = ACTION_REPAIR;
            idx++;
            for (int k = 0; k < numOfSensors; k++) {
                arr[idx] = "" + k;
                idx++;
            }

        }

        int r = numOfAgents;
        int n = arr.length;
        printCombination(arr, n, r);
        removeDuplicates(vec);
        for (int i = 0; i < vec.size(); i++) {

            String str = vec.get(i);
            String s = str.substring(1, str.length() - 1);
            //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }


        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();

        domain.setModel(new FactoredModel(smodel,rf , tf));

        return domain;
    }

    public static OOSADomain generateDomain(int numOfSensors, int numOfAgents, List<Integer> sensors) {

        OOSADomain domain = new OOSADomain();

        domain.addStateClass(CLASS_STATE, DataMulesState.class);
        vec.clear();
        String arr[] = new String[numOfAgents * (numOfSensors + 2)];
        int rIndex = 0;
        int idx = 0;
        for (int i = 0; i < numOfAgents; i++) {
            arr[idx] = ACTION_STAY;
            idx++;
            arr[idx] = ACTION_REPAIR;
            idx++;
            for (Integer k : sensors) {
                arr[idx] = "" + k;
                idx++;
            }

        }

        int r = numOfAgents;
        int n = arr.length;
        printCombination(arr, n, r);
        removeDuplicates(vec);
        for (int i = 0; i < vec.size(); i++) {

            String str = vec.get(i);
            String s = str.substring(1, str.length() - 1);
            //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }

        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();

        domain.setModel(new FactoredModel(smodel,rf , tf));

        return domain;
    }
}




