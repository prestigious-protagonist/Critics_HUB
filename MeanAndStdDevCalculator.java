import java.io.*;
import java.util.*;

public class MeanAndStdDevCalculator {

    public static void calculateMeanAndStdDev(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
        String headerLine = reader.readLine(); // Read the header line
        String[] headers = headerLine.split(","); // Split headers
        
        // Ignore the first column
        List<String> relevantHeaders = new ArrayList<>(Arrays.asList(headers).subList(1, headers.length));

        List<List<Double>> columnData = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) { // Create a list for each column (ignoring the first)
            columnData.add(new ArrayList<>());
        }

        String line;
        // Read data rows
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            for (int i = 1; i < values.length; i++) { // Start from index 1 to ignore the first column
                columnData.get(i - 1).add(Double.parseDouble(values[i]));
            }
        }
        reader.close();

        // Calculate mean and standard deviation for each column
        System.out.println("Column-wise Mean and Standard Deviation:");
        for (int i = 0; i < columnData.size(); i++) {
            double mean = calculateMean(columnData.get(i));
            double stdDev = calculateStandardDeviation(columnData.get(i), mean);
            System.out.printf("%s -> Mean: %.4f, Std Dev: %.4f%n", relevantHeaders.get(i), mean, stdDev);
        }
    }

    // Method to calculate the mean of a list of numbers
    private static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    // Method to calculate the standard deviation of a list of numbers
    private static double calculateStandardDeviation(List<Double> data, double mean) {
        double variance = 0.0;
        for (double value : data) {
            variance += Math.pow(value - mean, 2);
        }
        variance /= data.size(); // Use (data.size() - 1) for sample standard deviation
        return Math.sqrt(variance);
    }

    public static void main(String[] args) {
        try {
            String filePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/preprocessed_Output1.csv"; // Path to your CSV file
            calculateMeanAndStdDev(filePath);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}
