package org.example.task2.Util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Reads commit history and diffs from a local Git repository using JGit.
 *
 * In diff-only mode (the default) the repository's working tree is never touched;
 * {@link #gitCheckout} is only allowed when diff-only mode is turned off.
 */
public class CalculateGitDiff {
    private final Repository repo;
    private final Git git;
    private final boolean diffOnly;

    public CalculateGitDiff(String repoPath) throws IOException {
        this(repoPath, true);
    }

    public CalculateGitDiff(String repoPath, boolean diffOnly) throws IOException {
        this.diffOnly = diffOnly;
        this.repo = new FileRepositoryBuilder()
                .setGitDir(new File(repoPath, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
        this.git = new Git(repo);
    }

    /**
     * Walks history from HEAD backwards and returns the newest commit (with its parent)
     * that touched at least one .java file, or {@code null} if there is none.
     */
    public CommitPair findMostRecentJavaChangePair() throws GitAPIException, IOException {
        for (RevCommit commit : git.log().call()) {
            if (commit.getParentCount() == 0) {
                continue; // the initial commit has nothing to diff against
            }
            RevCommit parent = commit.getParent(0);
            boolean touchesJava = getDiffBetweenCommits(parent, commit).stream().anyMatch(entry ->
                    entry.getNewPath().endsWith(".java") || entry.getOldPath().endsWith(".java"));
            if (touchesJava) {
                return new CommitPair(parent, commit);
            }
        }
        return null;
    }

    /** Lists every file that differs between the two commits. */
    public List<DiffEntry> getDiffBetweenCommits(RevCommit oldCommit, RevCommit newCommit)
            throws IOException, GitAPIException {
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, oldCommit.getTree());

            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, newCommit.getTree());

            return git.diff()
                    .setOldTree(oldTree)
                    .setNewTree(newTree)
                    .call();
        }
    }

    /** Checks out {@code commit} in the working tree. Fails in diff-only mode. */
    public void gitCheckout(RevCommit commit) {
        if (diffOnly) {
            throw new UnsupportedOperationException("gitCheckout is disabled in diffOnly mode");
        }
        if (commit == null) {
            throw new IllegalArgumentException("commit must not be null for checkout");
        }
        try {
            git.checkout().setName(commit.getName()).call();

            ObjectId headId = repo.resolve("HEAD");
            if (headId == null || !headId.equals(commit.getId())) {
                throw new RuntimeException("[ERROR] Did not checkout properly. Expected: "
                        + commit.getName() + " but HEAD is: " + headId);
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
