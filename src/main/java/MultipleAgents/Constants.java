package MultipleAgents;

/**
 * Created by noa on 21-Aug-16.
 */
public class Constants {

    //simulation parameters
    public static int TOTAL_TIME_STEPS = 50;
    public static int NUM_OF_AGENTS = 3;
    public static int NUM_OF_SENSORS = 3;
    public static double PROB_SENSOR_BREAK = 0.3;
    public static double GUARANTEED_REMAIN_OK = 3;
    public static int MAX_BROKEN = 1;


    public static String OUTPUT_PATH = "output/";
    public static double DISCOUNT = 0.99;
    public static int MOVING_COST = 1;
    public static double FIXING_COST = 0;

  //  public static final String ACTION_STAY = "stay";
 //   public static final String ACTION_REPAIR = "repair";
      public static final String ACTION_STAY = "s";
       public static final String ACTION_REPAIR = "r";

    //constants for state parameters
    public static final String VAR_AGENTS_LOC = "agentsLoc";
    //public static final String VAR_BROKEN_SENSORS = "brokenSensors";
    public static final String VAR_TIME_FROM_LAST_REPAIR = "timeFromLastRepair";



    // Constant for state
    public static final String CLASS_STATE = "state";

    // Constant for identifying state (propositional function)
   // public static final String PF_AT_STATE = "at_State";

}
