package org.example.task2;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.example.task2.Util.CalculateGitDiff;
import org.example.task2.Util.CommitPair;
import org.example.task2.Util.ModelUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Static regression test selection pipeline:
 * diff -> call graph -> selected tests -> run with JaCoCo -> precision.
 */
public class Main {
    public static void main(String[] args) throws GitAPIException, IOException {
        // Repository under analysis (expected next to this project).
        Path repoPath = Paths.get(System.getProperty("user.dir"), "..", "spring-petclinic");

        // 1. Find the files changed by the most recent commit that touched Java code.
        CalculateGitDiff gitDiff = new CalculateGitDiff(repoPath.toString(), true);
        CommitPair pair = gitDiff.findMostRecentJavaChangePair();
        if (pair == null) {
            System.out.println("No commit with Java changes found in " + repoPath);
            return;
        }

        List<DiffEntry> changes = gitDiff.getDiffBetweenCommits(pair.getParentCommit(), pair.getCurrentCommit());
        System.out.println("Files changed in " + pair.getCurrentCommit().getName() + ":");
        for (DiffEntry change : changes) {
            System.out.println(change.getNewPath());
        }

        // 2. Build the call graph and turn it into a test-to-source mapping.
        ModelUtil model = new ModelUtil(repoPath);
        // TODO

        // 3. Select tests by intersecting the test-to-source mapping with the changed files.
        // TODO

        // 4. Run the selected tests with TestExecutor.runTestsInRepo(...).
        // TODO

        // 5. Parse jacoco.xml: total files covered, line coverage, branch coverage.
        // TODO

        // 6. Output precision = |Changed ∩ Covered| / |Covered|.
        // TODO
    }
}
