package org.example.issuetracker.repository.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.example.issuetracker.IssueService;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GoogleSheetsRepository implements IssueRepository {

    private final Sheets sheetsService;

    @Value("${app.sheets.spreadsheet-id}")
    private String spreadsheetId;

    private static final String RANGE = "Sheet1!A:Z";

    @Override
    public Issue save(Issue issue) {
        try {

            // Generate ID. might be another strategy
            int nextId = getNextId();
            String newId = "AD-" + nextId;
            issue.setId(newId);

            // Compose row data. Null is not acceptable, pass "" instead
            List<Object> row = Arrays.asList(
                    issue.getId(),
                    issue.getDescription(),
                    issue.getParentId() != null ? issue.getParentId() : "",
                    issue.getStatus() != null ? issue.getStatus().name() : "",
                    issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : "",
                    issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : ""
            );

            ValueRange body = new ValueRange().setValues(Collections.singletonList(row));

            // Append, Save
            sheetsService.spreadsheets().values().append(spreadsheetId, "Sheet1", body)
                    .setValueInputOption("RAW")
                    .execute();

            return issue;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save to Google Sheets", e);
        }
    }

    @Override
    public void updateStatus(String id, IssueStatus status) {
        try {
            List<List<Object>> values = fetchAllRows();
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (!row.isEmpty() && row.get(0).toString().equals(id)) {

                    // Update only cells from Status to UpdatedAt
                    String updateRange = "Sheet1!D" + (i + 1) + ":F" + (i + 1); // D=Status, F=UpdatedAt

                    List<Object> updateData = Arrays.asList(
                            status.name(),
                            row.get(4),
                            Instant.now().toString()
                    );

                    ValueRange body = new ValueRange().setValues(Collections.singletonList(updateData));
                    sheetsService.spreadsheets().values()
                            .update(spreadsheetId, updateRange, body)
                            .setValueInputOption("RAW")
                            .execute();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update status", e);
        }
    }

    @Override
    public List<Issue> findAllByStatus(IssueStatus status) {
        return findAll().stream()
                .filter(i -> i.getStatus() == status)
                .toList();
    }

    @Override
    public Optional<Issue> findById(String id) throws IOException {
        List<Issue> issues = findAll();
        return issues.stream().filter(is -> is.getId().equals(id)).findFirst();
    }

    // === Helper Methods ===

    private List<List<Object>> fetchAllRows() throws IOException {
        ValueRange range = sheetsService.spreadsheets().values()
                .get(spreadsheetId, RANGE)
                .execute();

        return range.getValues();
    }

    private int getNextId() throws IOException {
        List<List<Object>> values = fetchAllRows();
        if (values == null || values.isEmpty() || values.size() == 1) return 1;
        return values.size();
    }

    private List<Issue> findAll() {
        try {
            List<List<Object>> fetched_data = fetchAllRows();
            return parseIssues(fetched_data);

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch data", e);
        }
    }

    private List<Issue> parseIssues(List<List<Object>> rawData) {
        List<Issue> issues = new ArrayList<>();
        if (rawData == null || rawData.isEmpty()) return issues;

        try {
            boolean headerSkipped = false;
            for (List<Object> row : rawData) {
                // If the header was not skipped and the first cell is "id" -> skip this row
                if (!headerSkipped || row.getFirst().toString().equalsIgnoreCase("id")) {
                    headerSkipped = true;
                    continue;
                }

                if (row.size() >= 5) {
                    Issue issue = Issue.builder()
                            .id(row.get(0).toString())
                            .description(row.get(1).toString())
                            .parentId(row.get(2).toString())
                            .status(IssueStatus.valueOf(row.get(3).toString()))
                            .createdAt(Instant.parse(row.get(4).toString()))
                            .updatedAt(row.size() >= 6 ? Instant.parse(row.get(5).toString()) : null)
                            .build();
                    issues.add(issue);
                }
            }

            return issues;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse issues", e);
        }

    }
}
