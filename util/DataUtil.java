package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.apache.commons.lang3.math.*;

/**
 * Created by powerswat on 3/29/17.
 */
public class DataUtil <T extends Comparable<T>>{
    private LinkedHashSet<T> uniqueSet;

    // Find the minimum number in the given 1D array
    public T findMin(T[] arr){
        T minVal = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (minVal.compareTo(arr[i]) < 0)
                minVal = arr[i];
        return minVal;
    }
    public T findMin(ArrayList<T> arr){
        T minVal = arr.get(0);
        for (int i = 1; i < arr.size(); i++)
            if (minVal.compareTo(arr.get(i)) < 0)
                minVal = arr.get(i);
        return minVal;
    }

    // Find the maximum number in the given 1D array
    public T findMax(T[] arr){
        T maxVal = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (maxVal.compareTo(arr[i]) > 0)
                maxVal = arr[i];
        return maxVal;
    }
    public T findMax(ArrayList<T> arr){
        T maxVal = arr.get(0);
        for (int i = 1; i < arr.size(); i++)
            if (maxVal.compareTo(arr.get(i)) > 0)
                maxVal = arr.get(i);
        return maxVal;
    }

    // Count the number of unique elements in the given 1D array
    public int countElem(T[] arr){
        uniqueSet = new LinkedHashSet<T>();
        int cnt = 0;
        for (int i = 0; i < arr.length; i++)
            if (!uniqueSet.contains(arr[i])) {
                uniqueSet.add(arr[i]);
                cnt++;
            }
        return uniqueSet.size();
    }

    // Find missing indices
    public ArrayList<Integer> findMissingIndices(ArrayList<ArrayList<T>> input) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ArrayList<T> row = input.get(i);
            for (T element: row)
                if (element == null && (res.isEmpty() || res.get(res.size()-1) != i))
                    res.add(i);
        }
        return res;
    }

    // Find maximum index from the given list
    public int findMaxIndex(ArrayList<ArrayList<T>> input){
        int maxIdx = -1;
        int maxLen = 0;
        for (int i = 0; i < input.size(); i++) {
            int curLen = input.get(i).size();
            if (maxLen < curLen) {
                maxLen = curLen;
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    // Make a sample set to compare
    public ArrayList<T> takeSample(ArrayList<ArrayList<T>> input){
        int maxIdx = findMaxIndex(input);
        return input.get(maxIdx);
    }

    // Fill null value with some valid default data
    public ArrayList<ArrayList<T>> fillNull(ArrayList<ArrayList<T>> input,
                                            ArrayList<Integer> indices){
        ArrayList<ArrayList<T>> res = new ArrayList<>();

        // Make a sample set to compare
        ArrayList<T> sample = takeSample(input);

        // Make a hashset of missing indices
        HashSet<Integer> indexSet = new HashSet<>(indices);

        for (int i = 0; i < input.size(); i++){
            ArrayList<T> resRow = new ArrayList<>();
            ArrayList<T> row = input.get(i);
            if (indexSet.contains(i)) {
                for (int j = 0; j < row.size(); j++) {
                    T element = row.get(j);
                    if (element == null) {
                        if (sample.get(j) instanceof String
                                && NumberUtils.isDigits((String) sample.get(j)))
                            resRow.add((T) "0");
                        else
                            resRow.add((T) "");
                    } else
                        resRow.add(element);
                }
            } else
                resRow = row;
            res.add(resRow);
        }
        return res;
    }

    // Return unique set of elements
    public LinkedHashSet<T> getUniqueSet() {
        return uniqueSet;
    }
}
