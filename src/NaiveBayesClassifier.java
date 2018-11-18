import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author jszutkowski
 */
class NaiveBayesClassifier {

    private float[][] testSet;
    private float[][] trainingSet;

    private ArrayList<Float> possibleDecisions = new ArrayList<>();
    private HashMap<Float, Integer> decisionOccurrences = new HashMap<>();
    private Random rand = new Random();

    private int testSetLength = 0;
    private int correctlyClassified = 0;

    private float globalAccuracy = 0;
    private float balancedAccuracy = 0;

    private float[] decisions;

    public static void main(String[] args) {
        NaiveBayesClassifier nbc = new NaiveBayesClassifier();
        nbc.run();
    }

    void run() {
        readDataSets();
        setPossibleDecisions();
        setDecisionOccurrences();
        calculate();
        setGlobalAccuracy();
        setBalancedAccuracy();
        printResults();
    }

    private void readDataSets() {
        try {
            testSet = DataReader.readArray("australian_TST.txt");
            trainingSet = DataReader.readArray("australian_TRN.txt");

            testSetLength = testSet.length;
            decisions = new float[testSetLength];
        } catch (Exception e) {
            System.out.print("Something went wrong during reading of file");
            System.exit(0);
        }
    }

    private void setPossibleDecisions() {
        for (float[] trainingRow : trainingSet) {
            float trainingDecision = trainingRow[trainingRow.length - 1];
            if (!possibleDecisions.contains(trainingDecision)) {
                possibleDecisions.add(trainingDecision);
            }
        }
    }

    private void setDecisionOccurrences() {
        for (float decision : possibleDecisions) {
            int count = 0;
            for (float[] trainingRow : trainingSet) {
                if (trainingRow[trainingRow.length - 1] == decision) {
                    count++;
                }
            }

            decisionOccurrences.put(decision, count);
        }
    }

    private void calculate() {
        for (int i = 0; i < testSetLength; i++) {

            float rowDecisions[] = getRowDecisions(testSet[i]);

            decisions[i] = possibleDecisions.get(getMaxIndex(rowDecisions));

            if (decisions[i] == testSet[i][testSet[i].length - 1]) {
                correctlyClassified++;
            }
        }
    }

    private float[] getRowDecisions(float[] testRow) {
        int possibleDecisionsCount = possibleDecisions.size();
        float rowDecision[] = new float[possibleDecisionsCount];

        for (int i = 0; i < possibleDecisionsCount; i++) {
            float decisionValue = possibleDecisions.get(i);

            float factor = decisionOccurrences.get(decisionValue) / trainingSet.length;
            float sum = calculateSum(decisionValue, testRow);
            rowDecision[i] = factor * sum;
        }

        return rowDecision;
    }

    private float calculateSum(float decisionValue, float[] testRow)
    {
        int occurrences = decisionOccurrences.get(decisionValue);
        int testRowLength = testRow.length;
        float sum = 0;

        for (int i = 0; i < testRowLength; i++) {
            int unitCount = 0;
            for (float[] trainingRow : trainingSet) {
                if (trainingRow[trainingRow.length - 1] == decisionValue && trainingRow[i] == testRow[i]) {
                    unitCount++;
                }
            }
            sum += unitCount / (float) occurrences;
        }

        return sum;
    }

    private int getMaxIndex(float[] values) {
        int valuesSize = values.length;

        if (areAllValuesEqual(values)) {
            return rand.nextInt(valuesSize);
        }

        int maxIndex = 0;

        for (int i = 1; i < valuesSize; i++) {
            if (values[i] >= values[maxIndex]) {
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    private boolean areAllValuesEqual(float[] values) {
        int size = values.length;

        for (int i = 1; i < size; i++) {
            if (values[i] != values[0]) {
                return false;
            }
        }

        return true;
    }

    private void setGlobalAccuracy() {
        this.globalAccuracy = correctlyClassified / (float) trainingSet.length;
    }

    private void setBalancedAccuracy() {
        float sum = 0;
        for (float decision : possibleDecisions) {
            int classCount = 0;
            int correctCount = 0;
            for (int i = 0; i < testSetLength; i++) {
                if (testSet[i][testSet[i].length - 1] == decision) {
                    classCount++;
                    if (testSet[i][testSet[i].length - 1] == decisions[i]) {
                        correctCount++;
                    }
                }
            }
            sum += correctCount / (float) classCount;
        }

        this.balancedAccuracy = sum / (float) possibleDecisions.size();
    }

    private void printResults() {
        System.out.println("Global Accuracy: " + globalAccuracy);
        System.out.println("Balanced Accuracy: " + balancedAccuracy);

        int decisionsCount = decisions.length;
        for (int i = 0; i < decisionsCount; i++) {
            System.out.println(decisions[i] + " -> " + (int) testSet[i][testSet[i].length - 1]);
        }
    }
}
