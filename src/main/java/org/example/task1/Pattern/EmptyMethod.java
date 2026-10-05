package org.example.task1.Pattern;

// Importing necessary classes from the Spoon library and other utility classes.
import org.example.task1.Util.ModelUtil;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * A custom static analysis rule that checks for empty method clauses in Java source code.
 * It extends Spoon's AbstractPattern to process CtMethod elements in the Abstract Syntax Tree (AST).
 */
public class EmptyMethod extends AbstractPattern {

    // Log message that will be used for the empty method blocks.
    String logMessage = "Empty Method Block";

    // The CtModel object that contains the entire abstract syntax tree of the parsed Java code.
    CtModel model;

    // Constructor that takes a ModelUtil object and initializes the CtModel field.
    public EmptyMethod(ModelUtil model){
        this.model = model.getModel(); // Retrieves the CtModel from the ModelUtil.
    }

    /**
     * Processes the CtModel to find empty method blocks and add them to the 'elements' list.
     */
    @Override
    public void process() {
        // Get all the method blocks (CtMethod) in the CtModel.
        // You need to write your relevant code here. Moreover, for each pattern, you need to create a new file.
        //TODO
        List<CtMethod> methods = model.getElements(new TypeFilter<>(CtMethod.class));
        for (CtMethod method : methods){
            if (method.getBody().getStatements().isEmpty()){
                elements.add(new elementSchema(
                        logMessage,
                        method.getPosition().getFile().toPath().toAbsolutePath().toString(),
                        method.getPosition().getLine(),
                        method.getPosition().getEndLine(),
                        method.toString()
                ));
            }
        }
    }
}
