package withSet;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.ArrayList;
import java.util.Arrays;

import static withSet.Permotations.*;
import static withSet.Constants.*;

/**
 * Created by noa on 22-Aug-16.
 */
public class DataMulesDomain implements DomainGenerator {

    //
    // Create the full domain
    // Create reward function
    DataMulesRewardFunction rf = new DataMulesRewardFunction();

    // Create terminal function (the function that tells us if Zwe are at a terminal state)
    DataMulesTerminalFunction tf = new DataMulesTerminalFunction();

    public OOSADomain generateDomain() {

        OOSADomain domain = new OOSADomain();

        domain.addStateClass(CLASS_STATE, DataMulesState.class);

        // String arr[] = {"moveTo0","moveTo0","moveTo0","moveTo1","moveTo1","moveTo1","repair","repair","repair","stay", "stay","stay"};
        vec.clear();
        String arr[] = new String[NUM_OF_AGENTS*(NUM_OF_SENSORS+2)];
        int idx = 0;
        for(int i = 0; i < NUM_OF_AGENTS; i++) {
            arr[idx] = "stay";
            idx++;
            arr[idx] = "repair";
            idx++;
            for(int k = 0; k < NUM_OF_SENSORS; k++) {
                arr[idx] = "moveTo" + k;
                idx++;
            }
        }
        int r = NUM_OF_AGENTS;
        int n = arr.length;
        printCombination(arr, n, r);
        ArrayList<String[]> arrAns = removeDuplicates(vec);
        for (int i = 0; i < arrAns.size(); i++) {
            String[] sArr = arrAns.get(i);
            //System.out.println(Arrays.toString(sArr));
            String s = Arrays.toString(sArr);
            s = s.substring(1,s.length()-1);
          //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }








       // OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();

        domain.setModel(new FactoredModel(smodel, rf, tf));

        return domain;
    }



   /* public List<PropositionalFunction> generatePfs(){
        return Arrays.<PropositionalFunction>asList(new AtState());*/
    }

