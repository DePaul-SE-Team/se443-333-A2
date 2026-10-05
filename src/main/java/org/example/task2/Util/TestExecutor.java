package org.example.task2.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Small helper to programmatically run `mvn test` in a given Maven repository directory
 * for a provided list of impacted tests.
 *
 * Usage example:
 *   TestExecutor.runTestsInRepo("C:/my/repo", List.of("PathUtilTest","TestMappingTest"), timeout);
 */
public final class TestExecutor {
    private TestExecutor() {}


    public static int runTestsInRepo(String repoPath, Set<String> testNames, long timeoutMinutes) throws IOException, InterruptedException {
        Objects.requireNonNull(repoPath, "repoPath");
        Path repo = Paths.get(repoPath).toAbsolutePath();
        if (!Files.exists(repo) || !Files.isDirectory(repo)) {
            throw new IllegalArgumentException("repoPath does not exist or is not a directory: " + repo);
        }

        File pom = repo.resolve("pom.xml").toFile();
        if (!pom.exists()) {
            throw new IllegalArgumentException("No pom.xml found under: " + repo);
        }

        String pattern = null;
        if (testNames != null && !testNames.isEmpty()) {
            // Join test names with comma. Example: PathUtilTest,TestMappingTest
            pattern = testNames.stream().collect(Collectors.joining(","));
        }

        // Prefer a repo-local Maven wrapper if present (mvnw.cmd for Windows, mvnw for Unix)
        String mvnExecutable = "mvn";
        ProcessBuilder pb = new ProcessBuilder();
        if (pattern != null && !pattern.isBlank()) {
            pb.command("cmd.exe","/c", mvnExecutable, "test","-Dtest=" + pattern, "jacoco:report");
        } else {
            pb.command(mvnExecutable, "test","jacoco:report");
        }

        pb.directory(repo.toFile());
        pb.redirectErrorStream(true);

        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        boolean finished = p.waitFor(timeoutMinutes, TimeUnit.MINUTES);
        if (!finished) {
            p.destroyForcibly();
            throw new RuntimeException("mvn test did not finish within timeout");
        }
        return p.exitValue();
    }
}
