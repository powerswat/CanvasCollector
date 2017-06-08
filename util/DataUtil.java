package util;

import java.util.*;

import org.apache.commons.lang3.math.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    // Find a set of columns that have at least one valid value in each of them. (JSONArray data)
    public ArrayList<T> extractNonEmptyColumns(JSONArray data, ArrayList<T> cols){
        ArrayList<T> nonEmptyCols = new ArrayList<T>();
        Hashtable<T, Integer> emptyMap = new Hashtable<T, Integer>();

        // Collect a list of columns that have at least one null value
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            for (int j = 0; j < cols.size(); j++)
                if (jsonObject.get(cols.get(j)) == null)
                    if (emptyMap.containsKey(cols.get(j)))
                        emptyMap.put(cols.get(j), emptyMap.get(cols.get(j)) + 1);
                    else
                        emptyMap.put(cols.get(j), 1);
        }

        // List a set of columns that contain at least one meaningful value in it
        for (int i = 0; i < cols.size(); i++)
            if (!emptyMap.containsKey(cols.get(i)) || (emptyMap.get(cols.get(i)) < data.size()))
                nonEmptyCols.add(cols.get(i));

        return nonEmptyCols;
    }

    // Find a set of columns that have at least one valid value in each of them. (ArrayList data)
    public ArrayList<T> extractNonEmptyColumns(ArrayList<ArrayList<T>> data, List<T> cols){
        ArrayList<T> nonEmptyCols = new ArrayList<T>();
        Hashtable<T, Integer> emptyMap = new Hashtable<T, Integer>();

        // Collect a list of columns that have at least one null value
        for (int i = 0; i < data.size(); i++) {
            ArrayList<T> element = data.get(i);
            for (int j = 0; j < element.size(); j++) {
                if (element.get(j) == null)
                    System.out.println();
            }
        }

        // List a set of columns that contain at least one meaningful value in it
        for (int i = 0; i < cols.size(); i++)
            if (!emptyMap.containsKey(cols.get(i)) || (emptyMap.get(cols.get(i)) < data.size()))
                nonEmptyCols.add(cols.get(i));

        return nonEmptyCols;
    }

    // Get the type of given columns in the given table
    public Hashtable<String, String> retrieveColTypes(ArrayList<ArrayList<String>> tableCols,
                                     String tableName, String[] columns){
        Hashtable<String, String> colTypeTbl = new Hashtable<>();
        for (int i = 0; i < tableCols.size(); i++) {
            for (String column : columns) {
                String signature = "";
                if (column.indexOf(".") >= 0)
                    signature = column.substring(column.indexOf(".") + 1);
                else
                    signature = column;

                if (signature.equals(tableCols.get(i).get(0)))
                    colTypeTbl.put(column, tableCols.get(i).get(1));
            }
        }
        return colTypeTbl;
    }

    // Return unique set of elements
    public LinkedHashSet<T> getUniqueSet() {
        return uniqueSet;
    }
}
