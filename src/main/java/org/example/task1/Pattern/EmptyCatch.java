package org.example.task1.Pattern;

import org.example.task1.Util.ModelUtil;
import spoon.reflect.code.CtCatch;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Detects catch clauses with an empty body, e.g. {@code catch (Exception e) { }}.
 * AST node used: {@link CtCatch}.
 */
public class EmptyCatch extends AbstractPattern {

    public EmptyCatch(ModelUtil model) {
        super(model, "Empty Catch Block");
    }

    @Override
    public void process() {
        for (CtCatch catchClause : model.getElements(new TypeFilter<>(CtCatch.class))) {
            if (catchClause.getBody() != null && catchClause.getBody().getStatements().isEmpty()) {
                report(catchClause);
            }
        }
    }
}
