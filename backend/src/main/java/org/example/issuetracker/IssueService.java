package org.example.issuetracker;

import lombok.RequiredArgsConstructor;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public Issue createIssue(String description, String parentId) {

        return Issue.builder()
                .description(description)
                .parentId(parentId)
                .createdAt(Instant.now())
                .build();
    }

    public void updateStatus(String id, IssueStatus newStatus) {
        issueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Issue not found: " + id));
        issueRepository.updateStatus(id, newStatus);
    }

    public List<Issue> listByStatus(IssueStatus status) {
        return issueRepository .findAllByStatus(status);
    }

}
