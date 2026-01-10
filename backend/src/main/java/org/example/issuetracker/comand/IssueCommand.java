package org.example.issuetracker.comand;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.example.issuetracker.IssueService;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Command(command = "sheet")
@RequiredArgsConstructor
public class IssueCommand {

    // Sheets service with authentication from GoogleSheetsConfig
    private final Sheets sheetsService;
    private final IssueService issueService;

    @Value("${app.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Command(command = "test", description = "Test integration with Google Sheets")
    public List<List<Object>> testSheetsConnection() throws IOException {
        String range = "Sheet1!A:Z";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        // List[0] - Header row, List[1...X] - data
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        return values;
    }

    // create --desc "Some Issue" --parent "AD-1"
    @Command(command = "create", description = "Create a new issue: --desc <text> --parent (optional) <id>")
    public String createIssue(
            @Option(longNames = "desc", description = "Description of the issue", required = true) String desc,
            @Option(longNames = "parent", description = "Parent Issue ID") String parent
    ) {
        Issue issue = issueService.createIssue(desc, parent);
        return "Created issue with ID: " + issue.getId();
    }

    // update --id "AD-2" --status CLOSED
    @Command(command = "update", description = "Update issue status: --id <id> --status <status>")
    public String updateStatus(
            @Option(longNames = "id", description = "Issue ID", required = true) String id,
            @Option(longNames = "status", description = "New Status (OPEN, IN_PROGRESS, CLOSED)", required = true) String statusString
    ) throws IOException {
        IssueStatus status = IssueStatus.valueOf(statusString.toUpperCase());
        issueService.updateStatus(id, status);
        return "Status updated successfully.";
    }

    // list --status OPEN
    @Command(command = "list", description = "List issues by status: --status <status>")
    public void listIssues(
            @Option(longNames = "status", description = "Status to filter by", required = true) String statusString
    ) {
        IssueStatus status = IssueStatus.valueOf(statusString.toUpperCase());
        List<Issue> issues = issueService.listByStatus(status);

        String formatedIssues = formatIssuesToTable(issues);

        System.out.println(formatedIssues);
    }

    private String formatIssuesToTable(List<Issue> issues) {
        String[][] data = new String[issues.size() + 1][6];
        data[0] = new String[]{"ID", "Description", "Parent ID", "Status", "Created At", "Updated At"};

        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            data[i + 1] = new String[]{
                    issue.getId(),
                    issue.getDescription(),
                    issue.getParentId() == null ? "" : issue.getParentId(),
                    issue.getStatus().name(),
                    issue.getCreatedAt().toString(),
                    issue.getUpdatedAt() == null ? "" : issue.getUpdatedAt().toString()
            };
        }

        TableBuilder tableBuilder = new TableBuilder(new ArrayTableModel(data));
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);

        return tableBuilder.build().render(120);
    }

}
