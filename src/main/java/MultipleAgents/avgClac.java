package MultipleAgents;

import java.io.*;

import static MultipleAgents.Constants.OUTPUT_PATH;

/**
 * Created by noa on 14-Sep-16.
 */
public class avgClac {


    public static void main(String[] args) {

        //calcAVG(OUTPUT_PATH + "allSumUCTNew 5Horizon.csv",10);
        avgCalc(OUTPUT_PATH + "results/allSum.csv", 10);
        // VIcalcAVG(OUTPUT_PATH + "allSumVI .csv",3);
    }

    public static void avgCalc(String input, double sumOf) {
        String line = "";
        BufferedReader br = null;
        double rew = 0;
        long timeT = 0;
        long timeP = 0;
        int totNotFixed = 0;
        try {
            PrintWriter writer = new PrintWriter(OUTPUT_PATH + "results/18.10Results.csv");
            StringBuilder sb = new StringBuilder();
            sb.append("Algorithm name");
            sb.append(',');
            sb.append("Num of Domains");
            sb.append(',');
            sb.append("# Iterations");
            sb.append(',');
            sb.append("Horizon");
            sb.append(',');
            sb.append("MaxDelta");
            sb.append(',');
            sb.append("maxDepth");
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
                        rew += Float.parseFloat(strArr[7]);
                        timeP += Integer.parseInt(strArr[9]);
                        timeT += Integer.parseInt(strArr[10]);
                        totNotFixed += Integer.parseInt(strArr[15]);
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
                    sb.append(strArr[4]);
                    sb.append(',');
                    sb.append(strArr[5]);
                    sb.append(',');
                    sb.append(strArr[6]);
                    sb.append(',');
                    sb.append(rew / sumOf);
                    sb.append(',');
                    sb.append(strArr[8]);
                    sb.append(',');
                    sb.append(timeP / sumOf);
                    sb.append(',');
                    sb.append(timeT / sumOf);
                    sb.append(',');
                    sb.append(strArr[11]);
                    sb.append(',');
                    sb.append(strArr[12]);
                    sb.append(',');
                    sb.append(strArr[13]);
                    sb.append(',');
                    sb.append(strArr[14]);
                    sb.append(',');
                    sb.append(totNotFixed / sumOf);
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
}

