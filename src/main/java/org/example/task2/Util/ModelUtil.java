package org.example.task2.Util;

import spoon.Launcher;
import spoon.reflect.CtModel;

import java.nio.file.Path;

/** Parses Java source code (a file or a whole directory) into a Spoon AST. */
public class ModelUtil {

    private final CtModel model;

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
}
