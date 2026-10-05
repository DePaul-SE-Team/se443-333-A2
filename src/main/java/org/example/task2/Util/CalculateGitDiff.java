package org.example.task2.Util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CalculateGitDiff {
    private FileRepositoryBuilder builder;
    private   Repository repo;
    private Git git;
    private final boolean diffOnly;

    // Default constructor: safe diff-only mode
    public CalculateGitDiff(String repoString) throws IOException, GitAPIException {
        this(repoString, true);
    }

    // Allow caller to decide whether checkouts are permitted
    public CalculateGitDiff(String repoString, boolean diffOnly) throws IOException, GitAPIException {
        this.diffOnly = diffOnly;
        builder = new FileRepositoryBuilder();
        repo = builder.setGitDir(new File(repoString+"/.git"))
                .readEnvironment() // scan environment GIT variables
                .findGitDir() // scan up the file system tree
                .build();
    }
    /**
     * Traverses commits (from most recent backward) and returns
     * the first commit that modified at least one .java file.
     *
     * @return RevCommit with Java modifications, or null if none found.
     */
    public CommitPair findMostRecentJavaChangePair() throws GitAPIException, IOException {
        this.git = new Git(repo);

        for (RevCommit commit : git.log().call()) {
            if (commit.getParentCount() == 0) continue; // skip initial commit

            RevCommit parent = commit.getParent(0);
            List<DiffEntry> diffs = getDiffBetweenCommits(parent, commit);

            boolean hasJavaChange = diffs.stream().anyMatch(entry ->
                    entry.getNewPath().endsWith(".java") ||
                            entry.getOldPath().endsWith(".java"));

            if (hasJavaChange) {
                return new CommitPair(parent, commit); // return both
            }
        }
        return null; // no commit with Java changes found
    }

    /**
     * Helper: compute diffs between two commits.
     */
    public List<DiffEntry> getDiffBetweenCommits(RevCommit oldCommit, RevCommit newCommit)
            throws IOException, GitAPIException {

        RevTree oldTree = oldCommit.getTree();
        RevTree newTree = newCommit.getTree();

        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldTree);

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newTree);

            return this.git.diff()
                    .setOldTree(oldTreeIter)
                    .setNewTree(newTreeIter)
                    .call();
        } catch (IncorrectObjectTypeException e) {
            throw new RuntimeException("Invalid commit object for diff.", e);
        }
    }

    /**
     * checkout a specific commit
     * @param commit
     */
    public void gitCheckout(RevCommit commit){
        // Perform checkout
        try {
            if (diffOnly) {
                throw new UnsupportedOperationException("gitCheckout is disabled in diffOnly mode");
            }
            if (git == null) {
                this.git = new Git(repo);
            }
            if (commit == null) {
                throw new IllegalArgumentException("commit must not be null for checkout");
            }
            git.checkout()
                    .setName(commit.getName())
                    .call();

            // Verify HEAD now points to this commit
            Repository repo = git.getRepository();
            ObjectId headId = repo.resolve("HEAD");

            if (headId == null || !headId.equals(commit.getId())) {
                throw new RuntimeException("[ERROR] Did not checkout properly. Expected: " + commit.getName() + " but HEAD is: " + headId);
            }

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        } catch (AmbiguousObjectException e) {
            throw new RuntimeException(e);
        } catch (IncorrectObjectTypeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
