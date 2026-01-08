package org.example.issuetracker.comand;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Command(command = "sheet")
@RequiredArgsConstructor
public class ShellCommand {

    // Sheets service with authentication from GoogleSheetsConfig
    private final Sheets sheetsService;

    @Value("${app.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Command(command = "test")
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

}
