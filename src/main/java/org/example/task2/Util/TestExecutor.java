package org.example.task2.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Runs {@code mvn test [-Dtest=...] jacoco:report} inside another Maven project.
 *
 * Usage:
 *   TestExecutor.runTestsInRepo("C:/my/repo", Set.of("PathUtilTest", "TestMappingTest"), 10);
 */
public final class TestExecutor {
    private TestExecutor() {}

    /**
     * @param repoPath       root of the Maven project (must contain pom.xml)
     * @param testNames      tests to run; null or empty runs the whole suite
     * @param timeoutMinutes how long to wait before killing Maven
     * @return Maven's exit code (0 means all selected tests passed)
     */
    public static int runTestsInRepo(String repoPath, Set<String> testNames, long timeoutMinutes)
            throws IOException, InterruptedException {
        Objects.requireNonNull(repoPath, "repoPath");
        Path repo = Paths.get(repoPath).toAbsolutePath();
        if (!Files.isDirectory(repo)) {
            throw new IllegalArgumentException("repoPath does not exist or is not a directory: " + repo);
        }
        if (!Files.exists(repo.resolve("pom.xml"))) {
            throw new IllegalArgumentException("No pom.xml found under: " + repo);
        }

        Process process = new ProcessBuilder(buildMavenCommand(testNames))
                .directory(repo.toFile())
                .redirectErrorStream(true)
                .start();

        // Echo Maven's output as it runs.
        try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = output.readLine()) != null) {
                System.out.println(line);
            }
        }

        if (!process.waitFor(timeoutMinutes, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new RuntimeException("mvn test did not finish within timeout");
        }
        return process.exitValue();
    }

    private static List<String> buildMavenCommand(Set<String> testNames) {
        List<String> command = new ArrayList<>();
        // On Windows, mvn is a .cmd script and has to be launched through cmd.exe.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command.add("cmd.exe");
            command.add("/c");
        }
        command.add("mvn");
        command.add("test");
        if (testNames != null && !testNames.isEmpty()) {
            command.add("-Dtest=" + String.join(",", testNames));
        }
        command.add("jacoco:report");
        return command;
    }
}
