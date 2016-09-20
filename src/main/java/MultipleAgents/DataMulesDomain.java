package MultipleAgents;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;

import static MultipleAgents.Constants.*;
import static MultipleAgents.Permotations.*;

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
        //String arr[] = new String[NUM_OF_AGENTS*(NUM_OF_SENSORS+1)+MAX_BROKEN+1];
           String arr[] = new String[NUM_OF_AGENTS*(NUM_OF_SENSORS+2)];
           int rIndex = 0;
        int idx = 0;
        for(int i = 0; i < NUM_OF_AGENTS; i++) {
            arr[idx] = ACTION_STAY;
            idx++;
            arr[idx] = ACTION_REPAIR;
            idx++;
            for(int k = 0; k < NUM_OF_SENSORS; k++) {
                arr[idx] = ""+ k;
                idx++;
            }

        }
 /*          for(int i = 0; i <= MAX_BROKEN; i++) {
               arr[idx] = "r";
               idx++;
           }*/


        int r = NUM_OF_AGENTS;
        int n = arr.length;
        printCombination(arr, n, r);
              removeDuplicates(vec);
        for (int i = 0; i < vec.size(); i++) {

            String str = vec.get(i);
            //System.out.println(Arrays.toString(sArr));
           // String s = Arrays.toString(sArr);
           String  s = str.substring(1,str.length()-1);
          //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }
 /*   public OOSADomain generateDomain() {

        OOSADomain domain = new OOSADomain();

        domain.addStateClass(CLASS_STATE, DataMulesState.class);

        // String arr[] = {"moveTo0","moveTo0","moveTo0","moveTo1","moveTo1","moveTo1","repair","repair","repair","stay", "stay","stay"};
        vec.clear();
        String arr[] = new String[NUM_OF_AGENTS*(NUM_OF_SENSORS+2)];
        int idx = 0;
        for(int i = 0; i < NUM_OF_AGENTS; i++) {
            arr[idx] = "s";
            idx++;
            arr[idx] = "r";
            idx++;
            for(int k = 0; k < NUM_OF_SENSORS; k++) {
                arr[idx] = ""+ k;
                idx++;
            }
        }
        int r = NUM_OF_AGENTS;
        int n = arr.length;
        printCombination(arr, n, r);
              removeDuplicates(vec);
        for (int i = 0; i < vec.size(); i++) {

            String str = vec.get(i);
            //System.out.println(Arrays.toString(sArr));
           // String s = Arrays.toString(sArr);
           String  s = str.substring(1,str.length()-1);
          //  System.out.println(s);
            domain.addActionType(new MultipleAction(s));

        }*/
      //  System.out.println("hello");








       // OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();

        domain.setModel(new FactoredModel(smodel, rf, tf));

        return domain;
    }



   /* public List<PropositionalFunction> generatePfs(){
        return Arrays.<PropositionalFunction>asList(new AtState());*/
    }

