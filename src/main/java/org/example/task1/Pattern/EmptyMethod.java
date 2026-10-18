package org.example.task1.Pattern;

import org.example.task1.Util.ModelUtil;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Detects methods whose body has no statements, e.g. {@code void method0() { }}.
 * AST node used: {@link CtMethod}.
 */
public class EmptyMethod extends AbstractPattern {

    public EmptyMethod(ModelUtil model) {
        super(model, "Empty Method Block");
    }

    @Override
    public void process() {
        for (CtMethod<?> method : model.getElements(new TypeFilter<CtMethod<?>>(CtMethod.class))) {
            // Abstract and interface methods have no body at all; only flag real empty bodies.
            if (method.getBody() != null && method.getBody().getStatements().isEmpty()) {
                report(method);
            }
        }
    }
}
