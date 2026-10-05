package org.example.task2;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.example.Util.CalculateGitDiff;
import org.example.Util.CommitPair;
import org.example.Util.ModelUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws GitAPIException, IOException {
        // -------- 1. get diff --------

        // Define the path to the Java file to be analyzed
        Path path = Paths.get(System.getProperty("user.dir"),"..", "spring-petclinic");
        // Initialize the ModelUtil instance with the specified Java file
        ModelUtil model = new ModelUtil(path);

        // Use CalculateGitDiff in non-destructive (diff-only) mode so we don't modify the working tree
        CalculateGitDiff diff = new CalculateGitDiff(path.toString(), true);
        CommitPair pair = diff.findMostRecentJavaChangePair();

        // print the diff file
        List<DiffEntry> diffEntry = diff.getDiffBetweenCommits(pair.getParentCommit(), pair.getCurrentCommit());
        System.out.println("File changed for -> "+ pair.getCurrentCommit());
        diffEntry.stream().forEach(entry -> {
            System.out.println(entry.getNewPath());
        });

        // 2. Get Call Graph -> convert to -> Test-to-source hierarchy
        //TODO

        // 3. Select test by intersecting a) test-to-source mapping with b) code changes
        // TODO

        // 4. Run the test using `TestExecutor` -  this is coded already, but you need to call the API.
        // TODO

        // 5. Parse Jacoco - aligns with final project as well -> output total files covered, line coverage and branch coverage
        // TODO

        // 6. Output precision

    }
}