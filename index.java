import java.io.*;
import java.util.*;

public class index {

    // Load stopwords from a file
    private static Set<String> loadStopwords(String filePath) {
        Set<String> stopwords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error reading stopwords file: " + e.getMessage());
        }
        return stopwords;
    }

    // PAUSALITY: Calculate pauses based on stopwords
    public static int calculatePausality(String review, Set<String> stopwords) {
        int pauseCount = 0;
        String[] words = review.toLowerCase().split("\\W+"); // Split on non-word characters

        for (String word : words) {
            if (!word.isEmpty() && !stopwords.contains(word)) {
                pauseCount++;  // Count non-stopword words as "pauses"
            }
        }

        return pauseCount;
    }

    // CONTENT DIVERSITY
    public static double calculateTypeTokenRatio(String review) {
        String[] words = review.toLowerCase().split("\\W+"); // Split on non-word characters
        Set<String> uniqueWords = new HashSet<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                uniqueWords.add(word);
            }
        }
        return (double) uniqueWords.size() / words.length;
    }

    public static double calculateLexicalDensity(String review) {
        //System.out.println(review);
        String[] words = review.toLowerCase().split("\\W+"); // Split on non-word characters
        int lexicalWordCount = 0;

        // Define lexical word patterns (nouns, verbs, adjectives, and adverbs)
        for (String word : words) {
            if (word.matches(".*[a-z].*")) { // Simple check for valid lexical words
                lexicalWordCount++;
            }
        }
        //System.out.println((double) lexicalWordCount / words.length);
        return (double) lexicalWordCount / words.length;
    }

    // READABILITY USING FRE
    public static double calculateFleschReadingEase(String text) {
        int totalSentences = countSentences(text);
        int totalWords = countWords(text);
        int totalSyllables = countSyllables(text);

        // Flesch Reading Ease formula
        return 206.835 - 1.015 * ((double) totalWords / totalSentences)
                        - 84.6 * ((double) totalSyllables / totalWords);
    }

    private static int countSentences(String text) {
        return text.split("[.!?]").length;
    }

    private static int countWords(String text) {
        return text.split("\\s+").length;
    }

    private static int countSyllables(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int syllableCount = 0;

        for (String word : words) {
            syllableCount += countSyllablesInWord(word);
        }
        return syllableCount;
    }

    private static int countSyllablesInWord(String word) {
        word = word.toLowerCase().replaceAll("[^a-z]", ""); // Remove non-alphabetic characters
        String[] vowelGroups = word.split("[^aeiouy]+"); // Split on non-vowel characters
        int count = 0;

        for (String group : vowelGroups) {
            if (!group.isEmpty()) {
                count++;
            }
        }

        // Ensure at least one syllable for each word
        return Math.max(count, 1);
    }

    private static Set<String> loadAdjectives(String filePath) {
        Set<String> adjectives = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Add each adjective to the set (convert to lowercase for case-insensitive comparison)
                adjectives.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error reading adjectives file: " + e.getMessage());
        }
        return adjectives;
    }

    private static List<String> findAdjectivesInReview(String review, Set<String> adjectives) {
        List<String> foundAdjectives = new ArrayList<>();
        String[] words = review.toLowerCase().split("\\W+");

        // Check each word against the adjectives set
        for (String word : words) {
            if (adjectives.contains(word)) {
                foundAdjectives.add(word);
            }
        }
        return foundAdjectives;
    }

    // Function to process the dataset
    public static void processDataset(String inputFilePath, String outputFilePath, Set<String> adjectives, Set<String> stopwords) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            // Write headers to the output file
            writer.write("real,pausality,lexical_density,readability,adjective_count\n");

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip header line if present
                if (line.startsWith("Review")) {
                    continue;
                }

                // Split by comma (assuming CSV format)
                String[] parts = line.split(",", 2);
                String review = parts[0].trim();
                String label = parts[1].trim();

                // Process the review to extract features
                int pausality = calculatePausality(review, stopwords);
                double typeTokenRatio = calculateTypeTokenRatio(review);
                double lexicalDensity = calculateLexicalDensity(review);
                double readability = calculateFleschReadingEase(review);
                List<String> foundAdjectives = findAdjectivesInReview(review, adjectives);

                // Prepare the output line with features
                String outputLine = String.format("%d,%.2f,%.2f,%.2f,%d\n",
                        Integer.parseInt(label),            // 'real' column value (0 or 1)
                        pausality / 6.7,                    // Pausality (scaled float)
                        lexicalDensity,                     // Lexical density (float)
                        readability / 6.7,                  // Readability (scaled float)
                        foundAdjectives.size()              // Number of adjectives (integer)
                );

                // Write to the output file
                writer.write(outputLine);
            }
        } catch (IOException e) {
            System.err.println("Error processing dataset: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/OG_Dataset.csv"; // Input CSV file
        String outputFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/preprocessed_Output1.csv"; // Output CSV file
        String adjectivesFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/english-adjectives.txt"; // Adjectives file
        String stopwordsFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/stopwords.txt"; // Stopwords file

        // Load adjectives and stopwords
        Set<String> adjectives = loadAdjectives(adjectivesFilePath);
        Set<String> stopwords = loadStopwords(stopwordsFilePath);

        // Process the dataset
        processDataset(inputFilePath, outputFilePath, adjectives, stopwords);

        System.out.println("Dataset processing completed. Check output file: " + outputFilePath);
    }
}
