package org.example.task1;

import org.example.task1.Pattern.EmptyCatch;
import org.example.task1.Pattern.EmptyMethod;
import org.example.task1.Util.ModelUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main class to demonstrate the usage of the ModelUtil utility for static analysis of Java source code.
 * It parses a specific Java file and applies custom analysis rules (processors) to the parsed Abstract Syntax Tree (AST).
 */
public class Main {

    /**
     * The entry point of the application.
     * This method demonstrates how to use the ModelUtil class to analyze a Java file
     * and apply a static analysis rule.
     *
     * @param args Command-line arguments (not used in this example).
     */
    public static void main(String[] args) {

        // Define the path to the Java file to be analyzed
        Path path = Paths.get(System.getProperty("user.dir"), "Assignment3","src", "main", "java","org","example","Resource","BuggyFile.java");
        // Initialize the ModelUtil instance with the specified Java file
        ModelUtil model = new ModelUtil(path);

        // Create a custom static analysis rule (EmptyCatch in this case)
        final EmptyCatch rule1 = new EmptyCatch(model);
        final EmptyMethod rule2 = new EmptyMethod(model);

        // Apply the rule to the parsed Java file
        model.addRuleToAnalyze(rule1);
        model.addRuleToAnalyze(rule2);

//      // Apply analysis
        model.runAnalysis();

        // Print results
        model.print();

        // printout csv
        // detection.csv
        // empty metohd, in this java.file, method(){ }
    }
}

