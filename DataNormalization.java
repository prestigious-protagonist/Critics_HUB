import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataNormalization {

    public static void main(String[] args) {
        String inputFile = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/preprocessed_Output1.csv"; // Replace with your input file path
        String outputFile = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/preprocessed_Output2.csv"; // Replace with your output file path

        List<double[]> dataList = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        boolean isHeader = true;

        // Read data from input CSV
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isHeader) { // Skip the header row
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                labels.add(Integer.parseInt(values[0])); // First column is the label
                double[] features = new double[values.length - 1];
                for (int i = 1; i < values.length; i++) {
                    features[i - 1] = Double.parseDouble(values[i]);
                }
                dataList.add(features);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Convert to array for processing
        double[][] data = dataList.toArray(new double[0][]);

        // Standardize the data
        double[][] standardizedData = standardizeData(data);

        // Save the standardized data into output CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            // Write header
            bw.write("real,pausality,lexical_density,readability,adjective_count");
            bw.newLine();

            // Write data
            for (int i = 0; i < standardizedData.length; i++) {
                bw.write(labels.get(i) + ","); // Add label
                for (int j = 0; j < standardizedData[i].length; j++) {
                    bw.write(String.format("%.4f", standardizedData[i][j])); // Limit to 4 decimal places
                    if (j < standardizedData[i].length - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Data standardization and normalization complete. Saved to: " + outputFile);
    }

    // Method to standardize data
    public static double[][] standardizeData(double[][] data) {
        int numFeatures = data[0].length;
        double[][] standardizedData = new double[data.length][numFeatures];

        for (int j = 0; j < numFeatures; j++) {
            // Calculate mean
            double mean = 0;
            for (double[] datum : data) {
                mean += datum[j];
            }
            mean /= data.length;

            // Calculate standard deviation
            double variance = 0;
            for (double[] datum : data) {
                variance += Math.pow(datum[j] - mean, 2);
            }
            variance /= data.length;
            double stddev = Math.sqrt(variance);
           
            // Standardize feature column
            for (int i = 0; i < data.length; i++) {
                standardizedData[i][j] = (data[i][j] - mean) / stddev;
                
                
            }
            System.out.println(mean);
                System.out.println(stddev) ;
        }
        
        return standardizedData;
    }
}
