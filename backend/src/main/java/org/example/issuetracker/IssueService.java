package org.example.issuetracker;

import lombok.RequiredArgsConstructor;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public Issue createIssue(String description, String parentId) throws IOException {

        issueRepository.findById(parentId).orElseThrow(() -> new IllegalArgumentException("Parent issue not found"));
        Issue newIssue = Issue.builder()
                .description(description)
                .parentId(parentId)
                .status(IssueStatus.OPEN)
                .createdAt(Instant.now())
                .build();

        return issueRepository.save(newIssue);
    }

    public void updateStatus(String id, IssueStatus newStatus) throws IOException {
        issueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Issue not found: " + id));
        issueRepository.updateStatus(id, newStatus);
    }

    public List<Issue> listByStatus(IssueStatus status) {
        return issueRepository.findAllByStatus(status);
    }

}
