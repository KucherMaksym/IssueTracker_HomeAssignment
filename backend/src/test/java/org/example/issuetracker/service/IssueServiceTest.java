package org.example.issuetracker.service;


import org.example.issuetracker.IssueService;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository repository;

    @InjectMocks
    private IssueService service;

    // Create
    @Test
    @DisplayName("Create should save issue with OPEN status and generated timestamps")
    void createIssue_Success() {
        when(repository.save(any(Issue.class))).thenAnswer(inv -> {
            Issue arg = inv.getArgument(0);
            arg.setId("AD-1");
            return arg;
        });

        Issue created = service.createIssue("Customer 360 Job", null);

        assertThat(created.getId()).isEqualTo("AD-1");
        assertThat(created.getStatus()).isEqualTo(IssueStatus.OPEN);
        assertThat(created.getDescription()).isEqualTo("Customer 360 Job");
        verify(repository).save(any(Issue.class));
    }

    // Update
    @Test
    @DisplayName("Update should throw exception if issue ID does not exist")
    void updateStatus_NotFound() throws IOException {
        when(repository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus("NON-EXISTENT", IssueStatus.CLOSED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(repository, never()).updateStatus(any(), any());
    }

    // List
    @Test
    @DisplayName("List by status should return filtered results")
    void listByStatus_Success() {
        List<Issue> mockList = List.of(
                Issue.builder().id("1").status(IssueStatus.OPEN).build(),
                Issue.builder().id("2").status(IssueStatus.OPEN).build()
        );
        when(repository.findAllByStatus(IssueStatus.OPEN)).thenReturn(mockList);

        List<Issue> result = service.listByStatus(IssueStatus.OPEN);

        assertThat(result).hasSize(2);
        verify(repository).findAllByStatus(IssueStatus.OPEN);
    }
}