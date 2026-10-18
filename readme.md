# SE333 · Static Analysis & Test Selection Assignment 2

Build a Spoon-based static analysis tool, then extend it to select regression tests and evaluate coverage.

| Assignment | Topic | Grade weight | Due date |
| :--- | :--- | :---: | :--- |
| Part 1| [Static Analysis](#task-1-static-analysis) | 10% | November 2nd, 2026 · 11:59 PM |
| Part 2 | [Static Test Selection](#task-2-static-test-selection) | 10% | November 2nd, 2026 · 11:59 PM |

## Contents

- [Task 1: Static Analysis](#task-1-static-analysis)
  - [Setup and starter code](#a3-setup)
  - [Required detectors](#a3-detectors)
  - [Part 1: Seeded files](#a3-part1)
  - [Part 2: Real repository](#a3-part2)
  - [Grading and submission](#a3-submission)
- [Task 2: Static Test Selection](#task-2-static-test-selection)
  - [Environment setup](#a4-setup)
  - [Step 0: Configure the repository](#a4-step0)
  - [Step 1: Build the static mapping](#a4-step1)
  - [Step 2: Identify changes](#a4-step2)
  - [Step 3: Select tests](#a4-step3)
  - [Step 4: Measure coverage](#a4-step4)
  - [Grading and submission](#a4-deliverables)


## Task 1: Static Analysis

### Motivation

Static analysis examines source code without executing it. In software delivery pipelines, it typically runs during code review, pre-commit or pre-merge checks, and continuous integration (CI). It automates activities such as spotting risky patterns, identifying design smells, and detecting bug-prone code early. Tools provide fast, repeatable feedback that reduces the likelihood of risky changes reaching later pipeline stages.

In this lab, build a small static analysis tool using **Spoon**, an open-source Java framework for source-code analysis and transformation. Spoon builds a model of a codebase that can be queried programmatically.

For example, an empty-method detector queries the `CtModel` using `getElements` to retrieve all `CtMethod` elements, then checks `m.getBody().getStatements().isEmpty()`. An empty body is recorded as an issue with its source file and line range.

![Empty-method detector example](assets/assignment3-page2-1.png)

Example output:

```text
Detected: Empty Method Block at Line[8:10] in class BuggyFile.java
```

This identifies dead or incomplete code and its location.

<a id="a3-setup"></a>

### Starter Code

Accept the [GitHub Classroom assignment](https://classroom.github.com/a/RPWZOM_h).

| File | Purpose |
| :--- | :--- |
| `BuggyFile.java` | Production code intentionally seeded with common bug patterns and design flaws. |
| `ModelUtil.java` | Constructs the Spoon AST model of the codebase. |
| `EmptyCatch.java` | Fully working example detector. |
| `EmptyMethod.java` | Fully working example detector. |
| `Main.java` | CLI runner to extend when adding detectors. |

### Constraints

- Add new detector implementations only under `src/main/java/org/example/task1/Pattern/`.
- You may modify `task1/Main.java` and `task1/Util/ModelUtil.java`. Extend the supplied `AbstractPattern` base class for new detectors.
- Do **not** modify `BuggyFile.java`; treat it as production code you do not control.

### Prerequisites and Environment

- Java 17 or newer.
- Maven 3.9 or newer.
- Git.
- Basic familiarity with JUnit 5.

<details>
<summary><strong>Maven dependencies</strong></summary>

The checked-in `pom.xml` is authoritative. It currently declares:

| Dependency | Version | Scope |
| :--- | :--- | :--- |
| Spoon (`spoon-core`) | 11.1.0 | compile |
| JUnit Jupiter API (`junit-jupiter-api`) | 5.11.4 | test |
| JGit (`org.eclipse.jgit`) | 7.4.0.202509020913-r | compile |
| SLF4J Simple | 2.0.16 | compile |

JUnit's API is supplied; no test engine or test suite is included. The POM also contains Maven plugin dependencies and uses `maven-plugin` packaging, although the starter contains no Mojo implementation. `mvn package` may therefore fail during descriptor generation; use `mvn compile` to compile the starter.

</details>

**Running the tool:** Open this Maven project in IntelliJ, import its dependencies, and select a JDK compatible with the target repository. Run `org.example.task1.Main` or `org.example.task2.Main` with the working directory set to this assignment’s root. Update the target path in the relevant `Main` first. Task 2’s pipeline must be completed before it can select tests and report coverage; its test executor also requires Maven on PATH.

<a id="a3-detectors"></a>

### Objective and Required Detectors

Use Spoon to analyze `BuggyFile.java` and detect common bug patterns and design issues. Use **eight detectors in total**: retain the supplied `EmptyCatch` and `EmptyMethod`, implement the four specified detectors below, and add two rules of your choice.

> This README counts all eight rules explicitly: two supplied examples, four required implementations, and two additional rules.

#### 1. Always True Condition

Look for `CtIf` nodes whose condition always evaluates to true.


```java
public void method3() {
    if (true) {
    }
    int x = 4;
    if (x < 3) {
        x = x + 1;
    }
}
```

#### 2. Incorrect String Comparison

Identify binary comparisons (`==` or `!=`) between `String` values. Relevant Spoon elements include `CtBinaryOperator`, `CtExpression`, and type references to `java.lang.String`.

```java
public void method5() {
    String s1 = "test";
    String s2 = "test";
    boolean equal = (s1 == s2);
}
```

#### 3. File Not Closed

Detect file resources that are opened but never closed. Inspect `CtTry`, `CtCatch`, constructor calls such as `new FileReader(...)`, missing `close()` calls, and the absence of try-with-resources.


```java
public void method6_1() {
    try {
        FileReader fileReader = new FileReader("file.txt");
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
}
```

> **Hint** - Treat declared as try-with-resources resources as managed. Supporting other resource types, aliases, resources passed to helper methods, and proof of closure on every execution path is optional; document any limitations.

#### 4. Division by Zero

Look for division operations whose divisor is zero. Focus on `CtBinaryOperator`, division operators (`/`), and literal values equal to `0`.


```java
public void method7_1() {
    int x = 10;
    x = x / 0;
}
```

> **Hint** -  Detecting a variable initialized to zero, as in `method7_2()`, requires additional data-flow analysis and is optional.

#### 5. Two New Rules

Develop two additional rules based on real bugs. Identify each rule and cite the documentation it came from:

- [FindBugs](https://findbugs.sourceforge.net/)
- [SpotBugs](https://spotbugs.github.io/)
- “Security-related rules | SonarQube Cloud | Sonar Documentation” (named in the PDF without an extractable URL).

<a id="a3-part1"></a>

### Part 1: Build and Test on Seeded Files

1. **Understand the starter code.** Extend the project by adding detectors; do not rewrite it. The source highlights the intended extension area in red.

   ![Starter-code extension area](assets/assignment3-page7-1.png)

   ![Specific code to extend](assets/assignment3-page7-2.png)

2. **Implement the static analysis tool.** Implement each rule as a separate detector. Call `report(element)` for each finding. The supplied base class stores the file path, line range, and AST-rendered code in a `Detection`; `print()` logs the issue name, line range, and file path. Include relevant code snippets in your README findings review.

3. **Update `Main.java`** to invoke the implemented rules.

   ![Updating the runner](assets/assignment3-page8-1.png)

**Hints:** Start with `EmptyMethod.java`. The detection structure is similar to `EmptyCatch`. Identify the appropriate Spoon AST node types, such as `CtMethod`, `CtCatch`, and `CtIf`.

<a id="a3-part2"></a>

### Part 2: Run on a Real Repository

1. **Select a target repository.** Choose a Java GitHub repository with no more than approximately **50,000 lines of code**.
2. **Execute the analysis.** Clone the repository and run `Main` on its `src` directory. Update the `Path path` assignment in `task1/Main.java` to the cloned repository’s source directory. The default resolves `BuggyFile.java` relative to the working directory, so run from this project’s root.

   ![Repository path configuration](assets/assignment3-page9-1.png)

3. **Sample and inspect results.** For each rule, select two findings, inspect the corresponding source code, and label each as a **True Positive (TP)** or **False Positive (FP)**.

   These intentionally simple rules may produce no findings for some patterns. A repository may already enforce similar checks. Clearly state any absence of findings in the write-up; you will not be penalized for it.

4. **Write the README analysis.** For each rule, include a brief description and two example findings with a code snippet or link to the relevant real-world source file, a TP/FP label, and an explanation.

Treat the lab as a mini static analysis framework: each detector is a rule, and the real-repository run evaluates how rules behave in practice. The goal is to understand how static analysis works, rather than perfect tool correctness. Real projects commonly use third-party tools such as SonarQube or CodeQL; understanding their mechanics helps you use them effectively.

<a id="a3-submission"></a>

### Grading and Submission

Task 1 is graded out of **10 points**:

| Criterion | Points |
| :--- | ---: |
| Detector implementations and runner | 4 |
| Real-repository execution and logs | 3 |
| README report and findings review | 3 |

Partial credit is available for incomplete functionality when limitations are clearly explained.

For both tasks, **push all code, reports, logs, and screenshots to your Classroom50 assignment repository**. Do not submit a ZIP file or anything in D2L. Include:

- **Source code:** All required Spoon detectors under `src/main/java/org/example/task1/Pattern/`, updated `Main.java`, and `ModelUtil.java` if needed. `BuggyFile.java` must remain unchanged.
- **Analysis output on a real repository:** Run `Main` on the cloned Java repository’s `src` directory (approximately 50K LOC or less), and include detection logs in `.txt` format.
- **README report:** Include the sections below.

#### Required README Sections

1. **Name and Student ID:** Include both so the instructor can associate the GitHub repository with you.
2. **Rule Specifications:** For every detector, describe what it detects and which Spoon AST nodes it uses.
3. **Real-World Findings Review:** For every rule, provide two findings, a code snippet or source-file link, a TP/FP classification, and a short justification. Explain when findings are absent.

Your Classroom50 repository must contain source code, the log file, and `README.md`.

---

## Task 2: Static Test Selection

> **Grade weight:** 10%

### Goals

- Understand how static call graphs approximate which tests are affected by a code change.
- Use dynamic coverage (JaCoCo) to measure which tests execute changed code.
- Compare static predictions with dynamic measurements.
- Calculate precision and recall for regression test selection.
- Measure false positives and false negatives using per-test coverage of the full suite, and explain why affected tests were missed.

### Background

Rerunning all tests after every code change can be wasteful. Test selection aims to run tests relevant to modified code.

| Approach | Basis | Strength | Limitation |
| :--- | :--- |---|---|
| Static analysis | Builds a call graph between tests and source code. | Fast; no runtime overhead. | Conservative; may include tests that could execute a change but do not actually do so. |
| Dynamic analysis | Uses runtime coverage instrumentation. | Precise; captures real execution paths. | Slower; requires test execution and data collection. |

Use both approaches on a real codebase and compare their outcomes. Explain why they differ, particularly when coverage reports omit changed files.

<a id="a4-setup"></a>

### Environment Setup

1. Clone the project:

   ```sh
   git clone https://github.com/spring-projects/spring-petclinic
   cd spring-petclinic
   git checkout 142321aa3ed056eeda0662c1987b76db25b3d736
   ```

   Use this fixed commit for analysis and test execution. Compare it with its first parent when identifying changes.

2. Verify that it builds and runs tests:

   ```sh
   mvn clean test
   ```

3. Check the **target repository’s** `pom.xml` for JaCoCo configuration. The analyzer’s POM does not configure JaCoCo, and the supplied executor does not attach the coverage agent itself. Configure JaCoCo in the target project to instrument tests and generate HTML and XML reports before using the executor. The expected HTML location is:

   ```text
   target/site/jacoco/index.html
   ```

<a id="a4-step0"></a>

### Step 0: Repository of Analysis

In `src/main/java/org/example/task2/Main.java`, update `repoPath` to the target repository root, which must contain `.git` and `pom.xml`. The default expects Petclinic beside this assignment directory:

```java
Path repoPath = Paths.get(System.getProperty("user.dir"), "..", "spring-petclinic");
```

Run from this assignment’s root directory. The starter builds a Spoon model from the whole target repository; refine the input to `src/main/java` and `src/test/java` if needed to exclude generated or unrelated sources. `FileLevelCallGraphVisitor` and `JacocoParser` are empty implementation stubs, and steps 2–6 in `task2/Main.java` are TODOs.

<a id="a4-step1"></a>

### Step 1: Static Test Selection

Build a static mapping between test methods and source classes using the Inria Spoon API. This approximates which tests could be affected by changes to classes or methods.

#### Warm-up Question

Given:

```text
ClassA#MethodA -> ClassB#MethodC
TestClassA#TestMethodB -> ClassA#MethodA
TestClassC#TestMethodC -> ClassC#MethodC
```

If the latest commit changes `ClassC`, which tests should rerun?

#### Parse and Build the Call Graph

1. Use the static analysis tool from Task 1 to traverse the repository and collect class-level dependencies.
2. Parse all source files in `src/main/java` and `src/test/java`.
3. For each method invocation (`CtInvocation` in Spoon), record:

   ```text
   <callerClass>#<callerMethod> -> <calleeClass>#<calleeMethod>
   ```

Example graph:

```text
ClassA#methodA -> ClassB#methodC
TestClassA#testMethodB -> ClassA#methodA
TestClassC#testMethodC -> ClassC#methodC
```

#### Derive the Test-to-Source Mapping

Map each test method to the classes it depends on, directly or transitively:

```text
TestClassA#testMethodB, [ClassA, ClassB]
TestClassC#testMethodC, [ClassC]
```

Tips:

- Identify tests using `@Test`.
- Follow invocations recursively.
- Inspect `CtInvocation<?>` nodes and use `invocation.getExecutable().getDeclaringType()` to identify the target class.
- Use a `Map<String, Set<String>>` whose key is a test method and whose value is its set of directly or indirectly referenced source classes.

> **Hint** — (i) Use identifiers such as org.example.Service#find(int) and org.example.Service#find(String) so overloaded methods remain separate. (ii) You do not have to trace constructors, inherited methods or resolve calls made indirectly through interfaces, reflection, or Spring. Because those advanced cases may be skipped, the tool might miss an affected test. Students should describe those limitations in their report

<a id="a4-step2"></a>

### Step 2: Identify Code Changes (Δ)

Automatically collect changed files between two commits using **JGit**, the Java API for Git. These files represent the changes targeted by regression test selection.

#### Warm-up Question

Suppose the diff identifies:

```text
OwnerController.java
WebConfiguration.java
Vet.java
```

Assume the test-to-source mapping is:

```text
OwnerControllerTests#testProcessFindFormSuccess -> [OwnerController]
VetControllerTests#showVetList -> [Vet]
```

Which tests should run? Which changed file remains untested?

#### What Counts as Changed?

A file is changed if it was **modified, added, or renamed** between two commits and is a `.java` file under `src/main/java/`.

Produce a set such as:

```text
Changed = {
  org/springframework/samples/petclinic/owner/OwnerController.java,
  org/springframework/samples/petclinic/model/Person.java,
  ...
}
```

#### JGit Starter Code

The supplied implementation is `task2/Util/CalculateGitDiff.java`, used by `task2/Main.java`:

```java
CalculateGitDiff gitDiff = new CalculateGitDiff(repoPath.toString(), true);
CommitPair pair = gitDiff.findMostRecentJavaChangePair();
if (pair == null) {
    System.out.println("No commit with Java changes found in " + repoPath);
    return;
}
List<DiffEntry> changes = gitDiff.getDiffBetweenCommits(
        pair.getParentCommit(), pair.getCurrentCommit());
```

`findMostRecentJavaChangePair()` walks history from `HEAD` and selects the first commit whose diff against its first parent touches a `.java` path. This can be older than `HEAD`, and the Java change can be outside production sources. Initial commits without parents are skipped.

`getDiffBetweenCommits()` returns **all** diff entries for that pair. The starter prints their new paths without filtering. Implement the production-source filter described above before selecting tests: include additions, modifications, and renames whose new paths end in `.java` under `src/main/java/`; exclude deletions and unrelated files. Rename handling may require enabling rename detection.

Diff-only mode is enabled in this example and does not change the target working tree. Build the mapping and collect coverage from the source revision corresponding to the selected child commit; the starter does not ensure that the working tree matches that revision. Record both commit IDs and the analyzed revision in your report.

<a id="a4-step3"></a>

### Step 3: Select Tests for Code Changes

Using the static test-to-source mapping and JGit’s changed-file set:

1. Load the `Map<String, Set<String>>` mapping.
2. Load the list of changed files.
3. Find intersections to identify tests that should rerun.

Normalize Git paths, Spoon class names, and JaCoCo source paths before comparison. Use source-file identities so multiple classes in one file count once. State whether your execution selects test classes or individual methods: class selection runs every test in a selected class, while method selection must retain the method identity.

<a id="a4-step4"></a>

### Step 4: Dynamic Coverage for Selected Tests

1. Automatically run the following command from Java to collect coverage for only the statically selected tests:

   ```sh
   mvn test -Dtest=<comma-separated-test-list> jacoco:report
   ```

   The supplied `TestExecutor` uses `cmd.exe /c mvn` on Windows and `mvn` on other systems. Maven must be on the process PATH. Its API requires a `Set<String>` and a timeout in minutes. Invoke it using:

   ```java
   TestExecutor.runTestsInRepo(
       "C:/my/repo", Set.of("PathUtilTest", "TestMappingTest"), 10);
   ```

   Import `java.util.Set`. To run individual methods, pass selectors such as `"OwnerControllerTests#testProcessFindFormSuccess"` rather than only class names. Check the returned exit code before using coverage results.

   **Empty selection:** the helper runs the entire suite when passed `null` or an empty set. Handle an empty selection in your pipeline by skipping execution and reporting precision as not applicable.

   **Fresh coverage:** the helper does not clean previous coverage data. Remove or reset old JaCoCo execution data and reports before collecting this run’s results. Its timeout check occurs after output streaming, so it is not a reliable wall-clock limit for a hung process.

2. Parse the coverage report at `target/site/jacoco/jacoco.xml`. Use the JDK XML parser or another XML parser to extract per-source-file coverage. `JacocoParser` is a stub to implement. Count a production source file as covered only if it has at least one covered line; presence in the XML alone does not establish execution. The optional dependency below is not included in the starter POM:

   ```xml
   <dependency>
     <groupId>com.fasterxml.jackson.dataformat</groupId>
     <artifactId>jackson-dataformat-xml</artifactId>
     <version>2.17.1</version>
   </dependency>
   ```

3. Report JaCoCo **line and branch coverage** for the selected tests: `covered / (covered + missed) × 100%` (N/A if the denominator is zero). List changed files executed by these tests: `Changed ∩ Covered`.

   - **Covered:** Number of lines or branches executed by the tests.
   - **Missed:** Number of lines or branches not executed.
   - **Formula:** Divide covered by the total (`covered + missed`), then multiply by 100. Calculate line and branch coverage separately.
   - **Example:** 80 covered lines and 20 missed lines give `80 / (80 + 20) × 100% = 80%` line coverage.
   - **Line coverage:** Percentage of executable lines that ran.
   - **Branch coverage:** Percentage of decision outcomes exercised, such as the true and false paths of an `if`.
   - **`Changed ∩ Covered`:** Files that were both changed and executed. This does not establish that the changed lines ran.
   - These measurements describe code execution, not whether the right tests were selected.

4. Evaluate test selection:

   - Run the full test suite as a separate baseline, collecting fresh coverage for each test on the same source revision. Identify which tests execute at least one changed production source file.
   - **TP:** A selected test executes a changed file.
   - **FP:** A selected test does not execute any changed file.
   - **FN:** An unselected test executes a changed file.
   - **Precision:** `TP / (TP + FP)`.
   - **Recall:** `TP / (TP + FN)`.
   - Report both metrics and TP, FP, and FN counts. Use test methods or test classes consistently; report a metric as N/A only when its denominator is zero.
   - Keep the full-suite baseline data separate from the selected-test coverage report required in step 3.

5. Answer:

   - Report TP, FP, FN, precision, and recall. Explain false positives and false negatives, including limitations of the static analysis.
   - Why might some changed files not appear in the JaCoCo report? Consider configuration classes, unused code, and paths not exercised by tests.

<a id="a4-deliverables"></a>

### Grading and Submission

**Submission:** Push all code, reports, logs, and screenshots to your Classroom50 assignment repository. Do not submit a ZIP file or anything in D2L.

Task 2 is graded out of **10 points**:

| Criterion | Points |
| :--- | ---: |
| Call graph, changed-file detection, and test selection | 4 |
| Selected-test execution and line and branch coverage | 3 |
| Precision, recall, and README evidence including warm-up answers | 3 |

Write sections 1–6 below in **`readme.md` at the root of your codebase**, and push the codebase and all evidence to Classroom50 as item 7.

| Section | Required content | Screenshot requirement |
| :--- | :--- |---|
| 1. Static Call Graph | Show the tool’s mapping between tests and source classes, using console output or a CSV extract. | Call-graph summary or mapping file. |
| 2. Code Changes (Δ) | List files detected as changed between two commits. | JGit terminal output or printed changed-file list. |
| 3. Selected Tests | Show tests automatically selected by intersecting the test-to-source mapping with changed files. | Console or log output showing selected tests. |
| 4. Dynamic Coverage Report | Demonstrate that only selected tests ran and report **both line and branch coverage**. | JaCoCo `index.html` showing both coverage percentages, or not applicable where no corresponding lines or branches exist. |
| 5. Precision and Recall | Report TP, FP, FN, precision, and recall using the full-suite per-test coverage baseline. Explain false positives and false negatives. | Table showing per-test classifications and both computed metrics. |
| 6. Warm-up Questions | Answer all warm-up questions from the assignment. | Brief written reflection; no screenshot needed. |
| 7. Codebase | Push your code and screenshots to Classroom50. | Code must run without issues. |
