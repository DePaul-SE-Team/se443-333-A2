package org.example.task2.Util;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitPair {
    public final RevCommit parent;
    public final RevCommit child;

    public CommitPair(RevCommit parent, RevCommit child) {
        this.parent = parent;
        this.child = child;
    }

    public RevCommit getCurrentCommit(){
        return this.child;
    }

    public RevCommit getParentCommit(){
        return this.parent;
    }
}