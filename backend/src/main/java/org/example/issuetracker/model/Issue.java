package org.example.issuetracker.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Issue {

    private String id;
    private String description;
    private String parentId;
    private IssueStatus status;
    private Instant createdAt;
    private Instant updatedAt;

}
