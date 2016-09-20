package simpleDataMulePlan;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;

import static simpleDataMulePlan.Constants.*;

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

        domain.addActionTypes(
                new RepairAction(),
                new UniversalActionType(ACTION_STAY));
                for(int i = 0; i < NUM_OF_SENSORS; i++)
                    domain.addActionType(new MoveToAction(i));




       // OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

        simpleDataMuleStateModel smodel = new simpleDataMuleStateModel();

        domain.setModel(new FactoredModel(smodel, rf, tf));

        return domain;
    }



   /* public List<PropositionalFunction> generatePfs(){
        return Arrays.<PropositionalFunction>asList(new AtState());*/
    }

