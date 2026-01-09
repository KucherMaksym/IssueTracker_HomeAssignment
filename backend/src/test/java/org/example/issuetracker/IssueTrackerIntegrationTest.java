package org.example.issuetracker;

import org.example.issuetracker.IssueService;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class IssueTrackerIntegrationTest {

    @Autowired
    private IssueService issueService;

    @Test
    @DisplayName("Full workflow: Create -> List -> Update -> Verify")
    void fullWorkflowTest() throws IOException {
        // Create a new issue
        Issue created = issueService.createIssue("Integration Test Issue", null);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(IssueStatus.OPEN);

        // List issues: Should find the created one
        List<Issue> openIssues = issueService.listByStatus(IssueStatus.OPEN);
        assertThat(openIssues)
                .extracting(Issue::getId)
                .contains(created.getId());

        // Update status to CLOSED
        issueService.updateStatus(created.getId(), IssueStatus.CLOSED);

        // Verify updates: Should NOT be in an OPEN list anymore
        List<Issue> openIssuesAfterUpdate = issueService.listByStatus(IssueStatus.OPEN);
        assertThat(openIssuesAfterUpdate)
                .extracting(Issue::getId)
                .doesNotContain(created.getId());

        // Should be in CLOSED list
        List<Issue> closedIssues = issueService.listByStatus(IssueStatus.CLOSED);
        assertThat(closedIssues)
                .extracting(Issue::getId)
                .contains(created.getId());
    }
}