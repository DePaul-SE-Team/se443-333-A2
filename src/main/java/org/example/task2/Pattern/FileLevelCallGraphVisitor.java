package org.example.task2.Pattern;

/**
 * TODO: build the call graph and the test-to-source mapping.
 *
 * Suggested approach (see readme, Task 2 Step 1):
 *  - For every {@code CtInvocation}, record {@code callerClass#callerMethod -> calleeClass#calleeMethod}
 *    (callee class via {@code invocation.getExecutable().getDeclaringType()}).
 *  - For every method annotated with {@code @Test}, follow the graph transitively and collect
 *    the source classes it reaches into a {@code Map<String, Set<String>>}.
 */
public class FileLevelCallGraphVisitor {
}
