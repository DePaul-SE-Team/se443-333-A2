package org.example.task1.Pattern;

import org.example.task1.Util.ModelUtil;
import spoon.reflect.CtModel;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every static analysis rule (detector).
 *
 * A subclass only has to implement {@link #process()}: query {@link #model} for the
 * AST nodes it cares about and call {@link #report(CtElement)} for each offending node.
 */
public abstract class AbstractPattern {

    /** One issue found by a detector. */
    public static class Detection {
        public final String message;   // e.g. "Empty Catch Block"
        public final String path;      // absolute path of the source file
        public final int startLine;
        public final int endLine;
        public final String code;      // source snippet of the offending element

        public Detection(String message, String path, int startLine, int endLine, String code) {
            this.message = message;
            this.path = path;
            this.startLine = startLine;
            this.endLine = endLine;
            this.code = code;
        }
    }

    /** The parsed AST of the code under analysis. */
    protected final CtModel model;

    /** Human-readable name of the issue this rule detects; used in every report. */
    private final String issueName;

    private final List<Detection> detections = new ArrayList<>();

    protected AbstractPattern(ModelUtil modelUtil, String issueName) {
        this.model = modelUtil.getModel();
        this.issueName = issueName;
    }

    /** Scans {@link #model} and calls {@link #report(CtElement)} for every issue found. */
    public abstract void process();

    /** Records {@code element} as an issue, capturing its file and line range. */
    protected void report(CtElement element) {
        SourcePosition position = element.getPosition();
        detections.add(new Detection(
                issueName,
                position.getFile().toPath().toAbsolutePath().toString(),
                position.getLine(),
                position.getEndLine(),
                element.toString()
        ));
    }

    public List<Detection> getDetection() {
        return detections;
    }

    public void print() {
        for (Detection d : detections) {
            System.out.printf("Detected: %s at Line[%d:%d] in class %s%n",
                    d.message, d.startLine, d.endLine, d.path);
        }
    }
}
