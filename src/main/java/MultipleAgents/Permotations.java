package MultipleAgents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by noa on 29-Aug-16.
 */
public class Permotations {
   //     static ArrayList<String[]> vec = new ArrayList<String[]>();
         static ArrayList<String> vec = new ArrayList<String>();
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
            //    vec.add(arrStr);
                vec.add(Arrays.toString(arrStr)
                );

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
            //combinationUtil(arr, data, 0, n, 0, r);
        }

    //public static void  removeDuplicates(ArrayList<String[]> arrList) {
        public static void  removeDuplicates(ArrayList<String> arrList) {
        //List<String[]> al = new ArrayList<String[]>();
        // add elements to al, including duplicates
        Set<String> hs = new HashSet<String>();
        hs.addAll(arrList);
        arrList.clear();
        arrList.addAll(hs);
    }
   /* public static ArrayList<String[]>  removeDuplicates(ArrayList<String[]> v) {
        ArrayList<String[]> ans = new ArrayList<String[]>();

    /*    for (int i = 0; i < v.size(); i++) {
            String[] vi = v.get(i);
            if(!ans.contains(vi))
            {
                ans.add(vi);
            }
        }
        return ans;*/
    /*
         for (int i = 0; i < v.size(); i++) {
            ans.add(v.get(i));
            for (int j = 0; j < v.size(); j++) {
                if (i != j) {
                    boolean notAddJ = true;
                    for (int k = 0; k < v.get(i).length && notAddJ; k++) {
                        String[] vi = v.get(i);
                        String [] vj = v.get(j);
                        if (!(vi[k].equals(vj[k]))) {
                            notAddJ = false;
                            // l.add(j);
                        }
                    }
                    //if (b)
                      //  v.remove(j);
                    if(!notAddJ && !ans.contains(v.get(j))) {
                        ans.add(v.get(j));
                    }
                }
            }
        }
        //return v;
        return ans;
    }*
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

