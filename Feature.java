import java.io.*;
import java.util.*;

public class Feature {

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
        review = "\"" + review + "\""; 
        System.out.println(review);
        String[] words = review.toLowerCase().split("\\W+"); // Split on non-word characters
        int lexicalWordCount = 0;

        // Define lexical word patterns (nouns, verbs, adjectives, and adverbs)
        for (String word : words) {
            if (word.matches(".*[a-z].*")) { // Simple check for valid lexical words
                lexicalWordCount++;
            }
        }
        System.out.println((double) lexicalWordCount / words.length);
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
    public static void processDataset(Set<String> adjectives, Set<String> stopwords) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a string: ");
        
        // Read user input as a single line
        String line = scanner.nextLine();
        // Split by comma (assuming CSV format)
        
        String review = line;

        // Process the review to extract features
        double pausality = calculatePausality(review, stopwords)/6.7;
       
        double lexicalDensity = calculateLexicalDensity(review);
        double readability = calculateFleschReadingEase(review)/6.7;
        List<String> foundAdjectives = findAdjectivesInReview(review, adjectives);
        
        // Prepare data for standardization
        double[] features = {pausality / 6.7, lexicalDensity, readability / 6.7, foundAdjectives.size()};
        double[][] data = {features};

        // Standardize the data
       
        double pau_mean = 5.083571428571428;
        
        double lexical_density_mean =0.9773333333333334;
        double readability_mean = 9.802476190476186;
        double adjCount_mean =8.623809523809523;

        double pau_std = 2.712;
        double ld_std = 0.0163;
        double readability_std = 1.489;
        double adj_std = 5.2315;

        // Print original and standardized values
        System.out.println("Original values:");
        System.out.println("Pausality: " + pausality);
        System.out.println("Lexical Density: " + lexicalDensity);
        System.out.println("Readability (Flesch Reading Ease): " + readability);
        System.out.println("Adjectives found: " + foundAdjectives.size());
        
        System.out.println("\nStandardized values:");

        double pau_stan = (pausality-pau_mean)/pau_std;
        double ld_stan = (lexicalDensity-lexical_density_mean)/ld_std;
        double red_stan = (readability-readability_mean)/readability_std;
        double adj_stan = (foundAdjectives.size()-adjCount_mean)/adj_std;
        
       System.out.println(pau_stan);
       System.out.println(ld_stan);
       System.out.println(red_stan);
       System.out.println(adj_stan);
       scanner.close();
       
    }

    public static void main(String[] args) {
        String adjectivesFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/english-adjectives.txt"; // Adjectives file
        String stopwordsFilePath = "C:/Users/jaska/OneDrive/Desktop/Minor/rev/stopwords.txt"; // Stopwords file

        // Load adjectives and stopwords
        Set<String> adjectives = loadAdjectives(adjectivesFilePath);
        Set<String> stopwords = loadStopwords(stopwordsFilePath);

        // Process the dataset
        processDataset(adjectives, stopwords);

        System.out.println("Done");
    }
}