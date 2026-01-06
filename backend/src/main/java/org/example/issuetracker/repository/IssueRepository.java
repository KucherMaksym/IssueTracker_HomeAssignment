package org.example.issuetracker.repository;

import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;

import java.util.List;

public interface IssueRepository {

    // Create
    Issue save(Issue issue);

    // Update
    void updateStatus(String id, IssueStatus newStatus);

    // List (by status)
    List<Issue> findAllByStatus(IssueStatus status);

}
