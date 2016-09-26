package MultipleAgents;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.MDPSolver;
import burlap.behavior.singleagent.planning.Planner;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.List;
import java.util.Map;

import static MultipleAgents.DataMulesDomain.generateDomain;
import static MultipleAgents.DataMulesState.createInitialState;

/**
 * Created by noa on 25-Sep-16.
 */
public class HybridPlanner extends MDPSolver implements Planner {
    Planner plannerOriginal;
    Planner planner;
   /* Map<domainIntPair, Integer> sensorsTosDom;
    Map<domainIntPair,Integer> AgentsTosDom;*/
   Map< Integer,domainIntPair> sensorsTosDom;
    Map< Integer,domainIntPair> AgentsTosDom;
    Map<OOSADomain, Policy> plicyMap;


    public HybridPlanner(Planner p) {
        plannerOriginal = p;
        planner =p;

    }

    public void resetSolver() {
        plannerOriginal.resetSolver();
    }

    public Policy planFromState(State initialState) {

        //partition the domain;
        //Map<Integer, Map<Domain,Integer>> sensorsTosDom;
        //Domain d = plannerOriginal.getDomain();
        sensorsTosDom = null;
        AgentsTosDom = null;
        plicyMap = null;

        int numOfDmains = 2;

        int senCounter;
        int agCounter;

        List<OOSADomain> domains = null;

        for(int i = 0; i < 2; i++) {
            OOSADomain dom = generateDomain(2, 2);
            domains.add(dom);
            planner.setDomain(dom);
            Policy pol = planner.planFromState(createInitialState(2,2));
            plicyMap.put(dom, pol);

          /*  sensorsTosDom.put(new domainIntPair(domains.get(0),0),0);
            sensorsTosDom.put(new domainIntPair(domains.get(0),1),1);
            sensorsTosDom.put(new domainIntPair(domains.get(1),0),2);
            sensorsTosDom.put(new domainIntPair(domains.get(1),1),3);

            AgentsTosDom.put(new domainIntPair(domains.get(0),0),0);
            AgentsTosDom.put(new domainIntPair(domains.get(0),1),1);
            AgentsTosDom.put(new domainIntPair(domains.get(1),0),2);
            AgentsTosDom.put(new domainIntPair(domains.get(1),1),3);*/

             sensorsTosDom.put(0,new domainIntPair(domains.get(0),0));
            sensorsTosDom.put(1,new domainIntPair(domains.get(0),1));
            sensorsTosDom.put(2,new domainIntPair(domains.get(1),0));
            sensorsTosDom.put(3,new domainIntPair(domains.get(1),1));

            AgentsTosDom.put(0,new domainIntPair(domains.get(0),0));
            AgentsTosDom.put(1,new domainIntPair(domains.get(0),1));
            AgentsTosDom.put(2,new domainIntPair(domains.get(1),0));
            AgentsTosDom.put(3,new domainIntPair(domains.get(1),1));

            ///set the big policy


        }

        Policy p = new Policy() {
            public Action action(State s) {
                return null;
            }

            public double actionProb(State s, Action a) {
                return 0;
            }

            public boolean definedFor(State s) {
                return false;
            }
        }


        //choose which sensors and agent;

       // private setSensorsAndAgents(int sensEach, int )
        //allocate domains to the sensors
       // for(int i = 0; i <)
      //
                //plan each domain
     //   for(Domain dom : Domains)
        //    planner.setDomain(generateDomain(int numOfSensors, int numOfAgents));

                //merge the policy


                return null;
                }
    }
*/
