import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jszutkowski
 */
class DataReader {

    static float[][] readArray(String filename) throws Exception {

        ArrayList<float[]> list = getList(filename);

        int listSize = list.size();
        float[][] data = new float[listSize][];

        for (int i = 0; i < listSize; i++) {
            int rowSize = list.get(i).length;
            data[i] = new float[rowSize];
            System.arraycopy(list.get(i), 0, data[i], 0, rowSize);
        }
        return data;
    }

    private static ArrayList<float[]> getList(String filename) throws IOException {
        FileReader fileReader = new FileReader(new File(filename));
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        ArrayList<float[]> list = new ArrayList<>();
        ArrayList<Float> rowList;
        String line;

        while ((line = bufferedReader.readLine()) != null) {

            rowList = new ArrayList<>();

            String[] split = line.split(" ");

            for (String element : split) {
                rowList.add(Float.valueOf(element));
            }

            int rowListSize = rowList.size();
            float[] row = new float[rowListSize];

            for (int i = 0; i < rowListSize; i++) {
                row[i] = rowList.get(i);
            }

            list.add(row);
        }
        return list;
    }
}