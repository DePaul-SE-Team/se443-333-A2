package org.example.task2.Util;

import org.eclipse.jgit.revwalk.RevCommit;

/** A commit together with its (first) parent, i.e. the two sides of a diff. */
public class CommitPair {
    private final RevCommit parent;
    private final RevCommit child;

    public CommitPair(RevCommit parent, RevCommit child) {
        this.parent = parent;
        this.child = child;
    }

    public RevCommit getParentCommit() {
        return parent;
    }

    public RevCommit getCurrentCommit() {
        return child;
    }
}
