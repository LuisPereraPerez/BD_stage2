package com.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WordSearch {

    // Method to get the file path for the book
    private static String getFilePath(String bookName) {
        // Here, bookName is already the correct path as described in the .tsv file
        // No need to add "datalake\books" or anything else.
        return bookName + ".txt";  // We assume the file name in the .tsv contains only the relative part (e.g., "1")
    }

    // Method to get the text of a specific paragraph
    private static String getParagraphText(String filePath, int paragraphIndex) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentParagraph = 0;
            StringBuilder paragraphContent = new StringBuilder();
            boolean paragraphFound = false;

            // Read through the file line by line
            while ((line = reader.readLine()) != null) {
                // Detect paragraph breaks (empty lines)
                if (line.trim().isEmpty()) {
                    currentParagraph++;
                } else if (currentParagraph == paragraphIndex) {
                    // Append content if the current paragraph matches the requested index
                    paragraphContent.append(line).append(" ");
                    paragraphFound = true;
                } else if (paragraphFound) {
                    break; // End of the found paragraph
                }
            }

            if (paragraphFound) {
                return paragraphContent.toString().trim();
            } else {
                return "Paragraph not found";
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + filePath);
            e.printStackTrace();
            return "Paragraph not found";
        }
    }

    // Method to search for a word in the reverse index and count occurrences per paragraph
    private static void searchWordInReverseIndex(String wordToSearch) {
        // Get the file path that contains the occurrences of the word
        String reverseIndexFilePath = "./index/" + wordToSearch.charAt(0) + "/" + wordToSearch + ".tsv";

        Path indexPath = Paths.get(reverseIndexFilePath);

        // Check if the file exists
        if (!Files.exists(indexPath)) {
            System.out.println("File not found: " + reverseIndexFilePath);
            return;
        }

        int totalOccurrences = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(indexPath.toFile()))) {
            String line;
            Map<String, Integer> bookParagraphOccurrences = new HashMap<>();

            // Read the TSV file containing the occurrences
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                if (parts.length < 3) continue; // If the line doesn't contain the correct data, skip it

                String bookName = parts[0];
                int paragraphIndex = Integer.parseInt(parts[1]);
                int occurrences = Integer.parseInt(parts[2]);

                // Calculate total occurrences
                totalOccurrences += occurrences;

                // Add occurrences to the map for each book and paragraph
                bookParagraphOccurrences.put(bookName + "_" + paragraphIndex, occurrences);
            }

            // For each book and paragraph, retrieve the content
            for (Map.Entry<String, Integer> entry : bookParagraphOccurrences.entrySet()) {
                String[] bookAndParagraph = entry.getKey().split("_");
                String bookName = bookAndParagraph[0];
                int paragraphIndex = Integer.parseInt(bookAndParagraph[1]);
                int count = entry.getValue();

                // Get the file path of the book
                String filePath = getFilePath(bookName);

                // Get the text of the paragraph
                String paragraphContent = getParagraphText(filePath, paragraphIndex);

                System.out.println("Book: " + bookName);
                System.out.println(" - Paragraph: " + paragraphIndex + ", Occurrences: " + count);
                System.out.println("   Content: " + paragraphContent);
            }

            System.out.println("Total occurrences of the word '" + wordToSearch + "': " + totalOccurrences);

        } catch (IOException e) {
            System.err.println("Error reading the index: " + reverseIndexFilePath);
            e.printStackTrace();
        }
    }

    // Main method to execute the word search
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the word to search: ");
        String wordToSearch = scanner.nextLine().trim();

        // Start the search in the index folder
        searchWordInReverseIndex(wordToSearch);

        scanner.close();
    }
}
