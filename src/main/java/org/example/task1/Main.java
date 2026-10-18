package org.example.task1;

import org.example.task1.Pattern.EmptyCatch;
import org.example.task1.Pattern.EmptyMethod;
import org.example.task1.Util.ModelUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Runs every static analysis rule against a target file or source directory and
 * prints the detections.
 */
public class Main {

    public static void main(String[] args) {
        // Target to analyze. For Part 2, point this at the cloned repository's src directory.
        Path path = Paths.get(System.getProperty("user.dir"),
                "src", "main", "java", "org", "example", "task1", "Resource", "BuggyFile.java");

        ModelUtil model = new ModelUtil(path);

        // Register each rule here.
        model.addRuleToAnalyze(new EmptyCatch(model));
        model.addRuleToAnalyze(new EmptyMethod(model));

        model.runAnalysis();
        model.print();
    }
}
