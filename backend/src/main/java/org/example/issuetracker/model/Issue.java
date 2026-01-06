package org.example.issuetracker.model;

import java.time.Instant;

public record Issue (String id, String description, String parentId, IssueStatus status, Instant createdAt, Instant updatedAt) {}

