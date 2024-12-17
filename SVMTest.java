import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SVMTest {

    
    private double[] weights;
    private double bias;

    private double learningRate;
    private double regularizationParam;

    public SVMTest(double learningRate, double regularizationParam) {
        this.learningRate = learningRate;
        this.regularizationParam = regularizationParam;
    }

    // Train the SVM model using gradient descent
    public void train(double[][] X, int[] y, int epochs) {
        int numFeatures = X[0].length;
        weights = new double[numFeatures];
        bias = 0;

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < X.length; i++) {
                double margin = y[i] * (dotProduct(weights, X[i]) + bias);
                if (margin >= 1) {
                    for (int j = 0; j < numFeatures; j++) {
                        weights[j] -= learningRate * (2 * regularizationParam * weights[j]);
                    }
                } else {
                    for (int j = 0; j < numFeatures; j++) {
                        weights[j] -= learningRate * (2 * regularizationParam * weights[j] - y[i] * X[i][j]);
                    }
                    bias -= learningRate * (-y[i]);
                }
            }
        }
    }

    // Make predictions
    public int predict(double[] x) {
        double prediction = dotProduct(weights, x) + bias;
        return prediction >= 0 ? 1 : -1;
    }

    // Calculate accuracy
    public double calculateAccuracy(double[][] X, int[] y) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            if (predict(X[i]) == y[i]) {
                correct++;
            }
        }
        return (double) correct / X.length;
    }

    // Helper method to compute dot product
    private double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    // Main method
    public static void main(String[] args) {
        String csvFile = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/preprocessed_Output2.csv"; // Replace with your dataset file path
        List<double[]> featureList = new ArrayList<>();
        List<Integer> labelList = new ArrayList<>();

        // Read the dataset
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String str;
            boolean isHeader = true;
            while ((str = br.readLine()) != null) {
                if (isHeader) { // Skip header
                    isHeader = false;
                    continue;
                }
                String[] values = str.split(",");
                int label = Integer.parseInt(values[0]);
                double[] features = new double[values.length - 1];
                for (int i = 1; i < values.length; i++) {
                    features[i - 1] = Double.parseDouble(values[i]);
                }
                labelList.add(label == 0 ? -1 : 1); // Convert 0/1 to -1/1
                featureList.add(features);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Convert lists to arrays
        double[][] X = featureList.toArray(new double[0][]);
        int[] y = labelList.stream().mapToInt(i -> i).toArray();

        // Split data into train and test sets (80-20 split) with fixed seed
        int trainSize = (int) (0.8 * X.length);
        Random random = new Random(42); // Fixed seed for reproducibility
        int[] indices = random.ints(0, X.length).distinct().limit(X.length).toArray();

        double[][] X_train = new double[trainSize][X[0].length];
        double[][] X_test = new double[X.length - trainSize][X[0].length];
        int[] y_train = new int[trainSize];
        int[] y_test = new int[X.length - trainSize];

        for (int i = 0; i < trainSize; i++) {
            X_train[i] = X[indices[i]];
            y_train[i] = y[indices[i]];
        }
        for (int i = trainSize; i < X.length; i++) {
            X_test[i - trainSize] = X[indices[i]];
            y_test[i - trainSize] = y[indices[i]];
        }

        // Hyperparameter tuning
        double bestAccuracy = 0;
        double bestLearningRate = 0;
        double bestRegularizationParam = 0;
        for (double lr = 0.0001; lr <= 1; lr *= 10) {  // Log scale for learning rate
            for (double reg = 0.001; reg <= 10; reg *= 10) {  // Log scale for regularization
                // Train the model
                SVMTest svm = new SVMTest(lr, reg);
                svm.train(X_train, y_train, 600);
                double accuracy = svm.calculateAccuracy(X_test, y_test);
                // Update best model if necessary
                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestLearningRate = lr;
                    bestRegularizationParam = reg;
                }
            }
        }
        

        System.out.println("Best Learning Rate: " + bestLearningRate);
        System.out.println("Best Regularization Parameter: " + bestRegularizationParam);
        System.out.println("Best Model Accuracy: " + bestAccuracy * 100 + "%");

        // Train the best model
        SVMTest bestModel = new SVMTest(bestLearningRate, bestRegularizationParam);
        bestModel.train(X_train, y_train, 100);

        // Get user input for prediction
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter " + X[0].length + " features for prediction (space-separated): ");
        double[] userFeatures = new double[X[0].length];
        for (int i = 0; i < X[0].length; i++) {
            userFeatures[i] = scanner.nextDouble();
        }

        // Predict and display result
        int prediction = bestModel.predict(userFeatures);
        String result = (prediction == 1) ? "Real" : "Fake";
        System.out.println("Prediction: " + result);
    }
}