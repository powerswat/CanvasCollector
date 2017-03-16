package util;

import java.io.*;

/**
 * Created by powerswat on 1/27/17.
 */
public class FileProcessor {
    public void writeFile(String filePath, String data){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
