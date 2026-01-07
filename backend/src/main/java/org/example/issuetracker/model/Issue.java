package org.example.issuetracker.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Issue (String id, String description, String parentId, IssueStatus status, Instant createdAt, Instant updatedAt) {}

