package org.example.task2.Util;

/**
 * TODO: parse a JaCoCo XML report ({@code target/site/jacoco/jacoco.xml}) and return
 * per-source-file coverage (covered files, line coverage, branch coverage).
 *
 * The JDK's {@code javax.xml.parsers.DocumentBuilder} is enough; the readme also suggests
 * {@code jackson-dataformat-xml}. JaCoCo uses paths like {@code org/foo/Bar.java}, which
 * need normalizing before comparing with Spoon class names or Git paths.
 */
public class JacocoParser {
}
