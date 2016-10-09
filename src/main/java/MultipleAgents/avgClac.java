package MultipleAgents;

import java.io.*;

import static MultipleAgents.Constants.OUTPUT_PATH;

/**
 * Created by noa on 14-Sep-16.
 */
public class avgClac {



    public static void main(String[] args) {

        //calcAVG(OUTPUT_PATH + "allSumUCTNew 5Horizon.csv",10);
        UCTcalcAVG(OUTPUT_PATH + "results/allSum.csv", 10);
       // VIcalcAVG(OUTPUT_PATH + "allSumVI .csv",3);
    }

    public static void UCTcalcAVG(String input, double sumOf) {
        String line = "";
        BufferedReader br = null;
        double rew = 0;
        long timeT = 0;
        long timeP = 0;
        int totNotFixed = 0;
        try {
            PrintWriter writer = new PrintWriter(OUTPUT_PATH + "results/avg9.10.csv");
            StringBuilder sb = new StringBuilder();
            sb.append("Algorithm name");
            sb.append(',');
            sb.append("# Iterations");
            sb.append(',');
            sb.append("Horizon");
            sb.append(',');
            sb.append("Instance");
            sb.append(',');
            sb.append("AVG Accumulated reward ");
            sb.append(',');
            sb.append("# rollouts");
            sb.append(',');
            sb.append(" AVG Planning runtime");
            sb.append(',');
            sb.append("AVG total runtime");
            sb.append(',');
            sb.append("# Sensors");
            sb.append(',');
            sb.append("# Robots");
            sb.append(',');
            sb.append("p ");
            sb.append(',');
            sb.append("B (Max New Faults)");
            sb.append(',');
            sb.append("AVG notFixed");
            sb.append('\n');
            writer.write(sb.toString());
            writer.flush();

            br = new BufferedReader(new FileReader(input));
            line = br.readLine();
            while (line != null) {
                String[] strArr = line.split(",");
                rew = 0;
                timeP = 0;
                timeT = 0;
                totNotFixed = 0;
                for (int i = 0; i < sumOf && line != null; i++) {
                    // use comma as separator
                    line = br.readLine();
                    if (line != null) {
                        strArr = line.split(",");
                        rew += Float.parseFloat(strArr[4]);
                        timeP += Integer.parseInt(strArr[6]);
                        timeT += Integer.parseInt(strArr[7]);
                        totNotFixed += Integer.parseInt(strArr[12]);
                    }
                }
                if (line != null) {
                    sb = new StringBuilder();
                    sb.append(strArr[0]);
                    sb.append(',');
                    sb.append(strArr[1]);
                    sb.append(',');
                    sb.append(strArr[2]);
                    sb.append(',');
                    sb.append(strArr[3]);
                    sb.append(',');
                    sb.append(rew / sumOf);
                    sb.append(',');
                    sb.append(strArr[5]);
                    sb.append(',');
                    sb.append(timeP / sumOf);
                    sb.append(',');
                    sb.append(timeT / sumOf);
                    sb.append(',');
                    sb.append(strArr[8]);
                    sb.append(',');
                    sb.append(strArr[9]);
                    sb.append(',');
                    sb.append(strArr[10]);
                    sb.append(',');
                    sb.append(strArr[11]);
                    sb.append(',');
                    sb.append(totNotFixed/ sumOf);
                    sb.append('\n');
                    writer.write(sb.toString());
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
            sbVI.append(',');
            sbVI.append("AVGNotFixed");
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
