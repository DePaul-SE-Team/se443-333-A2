package org.example.task1.Util;

import org.example.task1.Pattern.AbstractPattern;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses Java source code into a Spoon AST ({@link CtModel}) and runs a list of
 * detectors over it.
 *
 * Typical use: construct with a file or directory, {@link #addRuleToAnalyze} each rule,
 * then call {@link #runAnalysis()} and {@link #print()}.
 */
public class ModelUtil {

    private final CtModel model;
    private final List<AbstractPattern> rules = new ArrayList<>();

    /** @param path a single .java file or a directory of sources to parse */
    public ModelUtil(Path path) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(path.toString());
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);
        launcher.getEnvironment().setCopyResources(false);
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);
        model = launcher.buildModel();
    }

    public CtModel getModel() {
        return model;
    }

    public void addRuleToAnalyze(AbstractPattern rule) {
        rules.add(rule);
    }

    public void runAnalysis() {
        for (AbstractPattern rule : rules) {
            rule.process();
        }
    }

    public void print() {
        for (AbstractPattern rule : rules) {
            rule.print();
        }
    }
}
