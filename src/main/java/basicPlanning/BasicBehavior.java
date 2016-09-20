package basicPlanning;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.deterministic.uninformed.dfs.DFS;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static simpleDataMulePlan.Constants.OUTPUT_PATH;


/**
 * Created by noa on 18-Aug-16.
 */
public class BasicBehavior {

    GridWorldDomain gwdg;
    OOSADomain domain;
    RewardFunction rf;

    TerminalFunction tf;
    StateConditionTest goalCondition;
    State initialState;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;


    public BasicBehavior(){

        gwdg = new GridWorldDomain(11, 11);
        gwdg.setMapToFourRooms();
        tf = new GridWorldTerminalFunction(10, 10);
        gwdg.setTf(tf);
        goalCondition = new TFGoalCondition(tf);
        domain = gwdg.generateDomain();

        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);

        VisualActionObserver observer = new VisualActionObserver(domain,GridWorldVisualizer.getVisualizer(gwdg.getMap()));
        observer.initGUI();
        env.addObservers(observer);

    }

    //save result to file
    public void visualize(String outputpath){
        Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
        new EpisodeSequenceVisualizer(v, domain, outputpath);
    }

    public void BFSExample(String outputPath){

        DeterministicPlanner planner = new BFS(domain, goalCondition, hashingFactory);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "bfs");
    }

    public void DFSExample(String outputPath){

        DeterministicPlanner planner = new DFS(domain, goalCondition, hashingFactory);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "dfs");

    }

    public void AStarExample(String outputPath) {

        Heuristic mdistHeuristic = new Heuristic() {

            public double h(State s) {
                GridAgent a = ((GridWorldState) s).agent;
                double mdist = Math.abs(a.x - 10) + Math.abs(a.y - 10);

                return -mdist;
            }
        };

        DeterministicPlanner planner = new AStar(domain, goalCondition, hashingFactory,
                mdistHeuristic);

        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "astar");
    }


    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(initialState,
                domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
                allStates, 11, 11, valueFunction, p);
        gui.initGUI();

    }



    //discount factot:0.99
    //Stop when value change less then 0.001 or 100 iterations.
    public void valueIterationExample(String outputPath){
        Planner planner = new ValueIteration(domain,0.99,hashingFactory,0.001, 100);
        Policy p = planner.planFromState(initialState);
     //   PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath+"vi");

        Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel());
        ep.write(outputPath+"vi");
        ///new part
        manualValueFunctionVis((ValueFunction)planner, p);
        ValueIteration v = (ValueIteration) planner;
        System.out.println( "value::::: " +v.value(initialState));
     //   v.writeValueTable(OUTPUT_PATH + "valueTable.txt");

     /*   Episode ep = PolicyUtils.rollout(p, initialState, domain.getModel(), TOTAL_TIME_STEPS);
        ep.write(OUTPUT_PATH + "viSingle");*/

        List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);

        double totalReward = 0;
        //  List<Double> rewardList =  PolicyUtils.rollout(p, initialState, domain.getModel(),TOTAL_TIME_STEPS).rewardSequence;
        List<Double> rewardList = ep.rewardSequence;
        for (double d : rewardList)
        {
            totalReward += d;
        }
        try {
            printPolicy(p,initialState,allStates,OUTPUT_PATH + "PolicyTest.txt",totalReward);
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






    public void QLearningExample(String outputPath){

        LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);

        //run learning for 50 episodes
        for(int i = 0; i < 50; i++){
            Episode e = agent.runLearningEpisode(env);

            e.write(outputPath + "ql_" + i);
            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }

    }

    public void SarsaLearningExample(String outputPath){

        LearningAgent agent = new SarsaLam(domain, 0.99, hashingFactory, 0., 0.5, 0.3);

        //run learning for 50 episodes
        for(int i = 0; i < 50; i++){
            Episode e = agent.runLearningEpisode(env);

            e.write(outputPath + "sarsa_" + i);
            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }

    }

public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){
    List<State> allStates = StateReachability.getReachableStates(initialState,domain,hashingFactory);

    //define color function
    LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
    rb.addNextLandMark(0.,Color.RED);
    rb.addNextLandMark(1.,Color.BLUE);

    //define a 2D painter of state values, specifying
    //which variables correspond to the x and y coordinates of the canvas
    StateValuePainter2D svp = new StateValuePainter2D(rb) ;
    svp.setXYKeys("agent:x","agent:y",new VariableDomain(0,11),new VariableDomain(0,11),1,1);

    //create our ValueFunctionVisualizer that paints for all states
    //using the ValueFunction source and the state value painter we defined
    ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp,valueFunction);

    //define a policy painter that uses arrow glyphs for each of the grid world actions
    PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
    spp.setXYKeys("agent:x","agent:y",new VariableDomain(0,11), new VariableDomain(0,11),1,1);

    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
    spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);

    //add the policy renderer to it
    gui.setSpp(spp);
    gui.setPolicy(p);

    //set the background color for places where states are not rendered to grey
    gui.setBgColor(Color.GRAY);

    //start the gui
    gui.initGUI();


}


    public static void main(String[] args) {
        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/"; //directory to record results

        //run example
        //example.BFSExample(outputPath);
        example.valueIterationExample(outputPath);

        //run the visualizer
       //example.visualize(outputPath);

        //run the visualizer
     //   example.QLearningExample(outputPath);

    }






}
