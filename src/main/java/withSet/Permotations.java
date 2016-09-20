package withSet;

import java.util.ArrayList;

/**
 * Created by noa on 29-Aug-16.
 */
public class Permotations {
        static ArrayList<String[]> vec = new ArrayList<String[]>();
        /* arr[] ---> Input Array
        data[] ---> Temporary array to store current combination
        start & end ---> Staring and Ending indexes in arr[]
        index ---> Current index in data[]
        r ---> Size of a combination to be printed */
        static void combinationUtil(String arr[], String data[], int start,
                                    int end, int index, int r)
        {
            String [] arrStr = new String[r];
            // Current combination is ready to be printed, print it
            if (index == r)
            {
                for (int j=0; j<r; j++) {
                    //System.out.print(data[j] + " ");
                    arrStr[j] = (data[j]);

                }
               // System.out.println("");
                vec.add(arrStr);

                return;
            }

            // replace index with all possible elements. The condition
            // "end-i+1 >= r-index" makes sure that including one element
            // at index will make a combination with remaining elements
            // at remaining positions
            for (int i=start; i<=end && end-i+1 >= r-index; i++)
            {
                data[index] = arr[i];
                combinationUtil(arr, data, i+1, end, index+1, r);
            }
        }

        // The main function that prints all combinations of size r
        // in arr[] of size n. This function mainly uses combinationUtil()
        static void printCombination(String arr[], int n, int r)
        {
            // A temporary array to store all combination one by one
            String data[]=new String[r];

            // Print all combination using temprary array 'data[]'
            combinationUtil(arr, data, 0, n-1, 0, r);
        }

    public static ArrayList<String[]>  removeDuplicates(ArrayList<String[]> v) {
        ArrayList<String[]> ans = new ArrayList<String[]>();
        for (int i = 0; i < v.size(); i++) {
            ans.add(v.get(i));
            for (int j = 0; j < v.size(); j++) {
                if (i != j) {
                    boolean notAddJ = true;
                    for (int k = 0; k < v.get(i).length && notAddJ; k++) {
                        if (!(v.get(i)[k].equals(v.get(j)[k]))) {
                            notAddJ = false;
                            // l.add(j);
                        }
                    }
                    //if (b)
                      //  v.remove(j);

                    if(!notAddJ) {
                        ans.add(v.get(j));
                    }
                }
            }
        }
        //return v;
        return ans;
    }

        /*Driver function to check for above function*/
        /*public static void main (String[] args) {
        String arr[] = {"moveTo0","moveTo0","moveTo0","moveTo1","moveTo1","moveTo1","repair","repair","repair","stay", "stay","stay"};
        int r = 3;
        int n = arr.length;
        printCombination(arr, n, r);
            ArrayList<String[]> vecAns = removeDuplicates(vec);
            for(int i = 0; i < vecAns.size(); i++) {
                String[] sArr = vecAns.get(i);
                for (int j = 0; j < sArr.length; j++)
                    System.out.print(sArr[j] + " ");
                System.out.println("");
            }




        }*/
        }

