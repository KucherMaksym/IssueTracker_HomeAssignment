package org.example.issuetracker.repository;

import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IssueRepository {

    Optional<Issue> findById(String id) throws IOException;

    // Create
    Issue save(Issue issue);

    // Update
    void updateStatus(String id, IssueStatus newStatus);

    // List (by status)
    List<Issue> findAllByStatus(IssueStatus status);

}
