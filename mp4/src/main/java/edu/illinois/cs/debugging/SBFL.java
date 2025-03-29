package edu.illinois.cs.debugging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * Implementation for Spectrum-based Fault Localization (SBFL).
 *
 */
public class SBFL {

    /**
     * Use Jsoup to parse the coverage file in the XML format.
     * 
     * @param file
     * @return a map from each test to the set of lines that it covers
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected static Map<String, Set<String>> readXMLCov(File file) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        Map<String, Set<String>> res = new HashMap<String, Set<String>>();
        Document document = Jsoup.parse(fileInputStream, "UTF-8", "",
                Parser.xmlParser());

        Elements tests = document.select("test");
        for (Element test : tests) {
            Element name = test.child(0);
            Element covLines = test.child(1);

            Set<String> lines = new HashSet<String>();
            String[] items = covLines.text().split(", ");
            Collections.addAll(lines, items);
            res.put(name.text(), lines);
        }
        return res;
    }

    /**
     * Compute the suspiciousness values for all covered statements based on
     * Ochiai
     * 
     * @param cov
     * @param failedTests
     * @return a map from each line to its suspiciousness value
     */
    public static Map<String, Double> Ochiai(Map<String, Set<String>> cov, Set<String> failedTests) {
        // using LinkedHashMap so that the statement list can be ranked
        Map<String, Double> susp = new LinkedHashMap<String, Double>();

        // Total number of failed tests.
        int totalFails = failedTests.size();

        // Map to store which tests cover each line: Map<line, Set<tests>>
        Map<String, Set<String>> lineToTests = new HashMap<>();

        // Build the line-to-tests mapping.
        for (Map.Entry<String, Set<String>> entry : cov.entrySet()) {
            String test = entry.getKey();
            Set<String> coveredLines = entry.getValue();

            for (String line : coveredLines) {
                lineToTests.computeIfAbsent(line, k -> new HashSet<>()).add(test);
            }
        }

        // Compute the Ochiai value for each covered statement (line).
        for (Map.Entry<String, Set<String>> entry : lineToTests.entrySet()) {
            String line = entry.getKey();
            Set<String> tests = entry.getValue();

            // Count the number of failed tests that cover this line.
            long failedTestCount = tests.stream().filter(failedTests::contains).count();

            // Avoid division by zero: if no tests cover this line, skip it.
            if (tests.isEmpty() || totalFails == 0) {
                susp.put(line, 0.0);
                continue;
            }

            // Calculate the Ochiai value.
            double ochiaiVal = failedTestCount / Math.sqrt(totalFails * tests.size());

            // Store the suspiciousness value for the current line.
            susp.put(line, ochiaiVal);
        }

        return susp;
    }

    /**
     * Get the suspiciousness value for the buggy line from the suspicious
     * statement list
     * 
     * @param susp
     * @param buggyLine
     * @return the suspiciousness value for the buggy line
     */
    protected static double getSusp(Map<String, Double> susp,
            String buggyLine) {
        return susp.get(buggyLine);
    }

    /**
     * Rank all statements based on the descending order of their suspiciousness
     * values. Get the rank (print the lowest rank for the tied cases) for the
     * buggy line from the suspicious statement list
     * 
     * @param susp
     * @param buggyLine
     * @return the rank of the buggy line
     */
    protected static int getRank(Map<String, Double> susp, String buggyLine) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
                susp.entrySet());
        // Sort the list based on the suspiciousness values in descending order
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Find the suspiciousness value for the buggy line
        double buggy = getSusp(susp, buggyLine);

        // Calculate the rank of the buggy line
        int rank = 0; // Initialize rank to 0

        // Iterate through the sorted list to find the rank
        for (Map.Entry<String, Double> entry : list) {
            if (entry.getValue() < buggy) {
                break; // Stop if we find a value less than the buggy line's value
            }
            rank++;
        }

        return rank; // Return the rank of the buggy line
    }
}
