package MultipleAgents;

import java.io.*;

import static MultipleAgents.Constants.OUTPUT_PATH;

/**
 * Created by noa on 14-Sep-16.
 */
public class avgClac {



    public static void main(String[] args) {

        //calcAVG(OUTPUT_PATH + "allSumUCTNew 5Horizon.csv",10);
        //UCTcalcAVG(OUTPUT_PATH + "allSumUCTNewFrom20 - Copy.csv",5);
        VIcalcAVG(OUTPUT_PATH + "allSumUCT16.9.csv",10);
    }

    public static void UCTcalcAVG(String input, double sumOf) {
        String line = "";
        BufferedReader br = null;
        double rew = 0;
        long time = 0;
        try {
            PrintWriter writer = new PrintWriter(OUTPUT_PATH + "AvgUCT-p30-Horizon20.csv");
            StringBuilder sbUCT = new StringBuilder();
            sbUCT.append("number of Sensors");
            sbUCT.append(',');
            sbUCT.append("number of Agents");
            sbUCT.append(',');
            sbUCT.append("horizon");
            sbUCT.append(',');
            sbUCT.append("number of rollouts");
            sbUCT.append(',');
            sbUCT.append("AVG reward");
            sbUCT.append(',');
            sbUCT.append("num of visits");
            sbUCT.append(',');
            sbUCT.append("AVGrunTime(ms)");
            sbUCT.append('\n');
            writer.write(sbUCT.toString());
            writer.flush();

            br = new BufferedReader(new FileReader(input));
            line = br.readLine();
            while (line != null) {
                String[] strArr = line.split(",");
                rew = 0;
                time = 0;
                for (int i = 0; i < sumOf && line != null; i++) {
                    // use comma as separator
                    line = br.readLine();
                    if (line != null) {
                        strArr = line.split(",");
                        rew += Float.parseFloat(strArr[5]);
                        time += Integer.parseInt(strArr[7]);
                    }
                }
                if (line != null) {
                    sbUCT = new StringBuilder();
                    sbUCT.append(strArr[0]);
                    sbUCT.append(',');
                    sbUCT.append(strArr[1]);
                    sbUCT.append(',');
                    sbUCT.append(strArr[2]);
                    sbUCT.append(',');
                    sbUCT.append(strArr[3]);
                    sbUCT.append(',');
                    sbUCT.append(rew / sumOf);
                    sbUCT.append(',');
                    sbUCT.append(strArr[6]);
                    sbUCT.append(',');
                    sbUCT.append(time / sumOf);
                    sbUCT.append('\n');
                    writer.write(sbUCT.toString());
                    writer.flush();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public static void VIcalcAVG(String input, double sumOf) {
        String line = "";
        BufferedReader br = null;
        double rew = 0;
        long time = 0;
        try {
            PrintWriter writer = new PrintWriter(OUTPUT_PATH + "AvgVI.csv");
            StringBuilder sbVI = new StringBuilder();
            sbVI.append("number of Sensors");
            sbVI.append(',');
            sbVI.append("number of Agents");
            sbVI.append(',');
            sbVI.append("AVG reward");
            sbVI.append(',');
            sbVI.append("AVGrunTime(ms)");
            sbVI.append('\n');
            writer.write(sbVI.toString());
            writer.flush();

            br = new BufferedReader(new FileReader(input));
            line = br.readLine();
            while (line != null) {
                String[] strArr = line.split(",");
                rew = 0;
                time = 0;
                for (int i = 0; i < sumOf && line != null; i++) {
                    // use comma as separator
                    line = br.readLine();
                    if (line != null) {
                        strArr = line.split(",");
                        rew += Float.parseFloat(strArr[3]);
                        time += Integer.parseInt(strArr[4]);
                    }
                }
                if (line != null) {
                    sbVI = new StringBuilder();
                    sbVI.append(strArr[0]);
                    sbVI.append(',');
                    sbVI.append(strArr[1]);
                    sbVI.append(',');
                    sbVI.append(rew / sumOf);
                    sbVI.append(',');
                    sbVI.append(time / sumOf);
                    sbVI.append('\n');
                    writer.write(sbVI.toString());
                    writer.flush();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
