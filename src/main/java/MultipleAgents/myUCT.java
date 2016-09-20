package MultipleAgents;

import burlap.behavior.singleagent.planning.stochastic.montecarlo.uct.UCT;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;

/**
 * Created by noa on 12-Sep-16.
 */
public class myUCT  extends UCT {
    /**
     * Initializes UCT
     *
     * @param domain          the domain in which to plan
     * @param gamma           the discount factor
     * @param hashingFactory  the state hashing factory
     * @param horizon         the planning horizon
     * @param nRollouts       the number of rollouts to perform
     * @param explorationBias the exploration bias constant (suggested &gt;2)
     */
    public myUCT(SADomain domain, double gamma, HashableStateFactory hashingFactory, int horizon, int nRollouts, int explorationBias) {
        super(domain, gamma, hashingFactory, horizon, nRollouts, explorationBias);
    }

    public int getNumOfVisited() {
        return this.numVisits;
    }
}


