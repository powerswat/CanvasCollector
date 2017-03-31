package util;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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

    // Find the maximum number in the given 1D array
    public T findMax(T[] arr){
        T maxVal = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (maxVal.compareTo(arr[i]) > 0)
                maxVal = arr[i];
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

    // Return unique set of elements
    public LinkedHashSet<T> getUniqueSet() {
        return uniqueSet;
    }
}
