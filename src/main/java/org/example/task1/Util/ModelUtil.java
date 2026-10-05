package org.example.task1.Util;

import org.example.Pattern.AbstractPattern;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Utility class for managing the analysis of Java source code using the Spoon library.
 * Provides methods to parse a Java file and apply custom static analysis rules (processors) to the parsed Abstract Syntax Tree (AST).
 */
public class ModelUtil{

    // Initialize the Spoon Launcher instance for parsing Java source code
    private static final Launcher launcher = new Launcher();
    final Factory factory = launcher.getFactory();
    private CtModel model; // The parsed Abstract Syntax Tree (AST) of the Java source code
    ArrayList<AbstractPattern> rules = new ArrayList<>(); // List to hold custom analysis rules (processors)

    /**
     * Constructs a ModelUtil instance and parses the specified Java file into an AST.
     *
     * @param path The path to the Java source file to be analyzed.
     */
    public ModelUtil(Path path) {
        // Add the Java file to be parsed by Spoon
        launcher.addInputResource(path.toString());

        // Configure the environment settings for the launcher
        configureLauncherEnvironment();

        // Run the launcher to parse the Java file and generate the AST
        model = launcher.buildModel();
    }

    public CtModel getModel(){
        return model; // Returns the parsed AST model
    }

    /**
     * Configures the environment settings for the Spoon launcher, such as enabling or disabling comments,
     * ignoring duplicate declarations, and handling syntax errors.
     */
    private void configureLauncherEnvironment() {
        // Disable comments in the AST
        launcher.getEnvironment().setCommentEnabled(false);

        // Ignore duplicate declarations in the source code
        launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);

        // Prevent copying external resources during parsing
        launcher.getEnvironment().setCopyResources(false);

        // Ignore syntax errors during parsing
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);
    }

    /**
     * Adds a custom static analysis rule (processor) to be applied to the parsed Java code.
     * The rule will be applied to all classes in the parsed Abstract Syntax Tree (AST).
     *
     * @param rule The custom processor (rule) that implements the static analysis logic.
     */
    public void addRuleToAnalyze(AbstractPattern rule) {
        rules.add(rule); // Adds the provided static analysis rule (processor) to the list of rules
    }

    public void runAnalysis(){
        // Process all rules (static analysis processors) added to the ModelUtil instance
        for (AbstractPattern rule : rules) {
            rule.process(); // Apply the custom rule to the AST
        }
    }

    public void print() {
        // Print the results of the analysis for each rule
        for (AbstractPattern rule : rules) {
            rule.print(); // Output the results of each rule's analysis
        }
    }
}
