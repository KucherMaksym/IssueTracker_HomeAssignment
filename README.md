# Issue Tracker CLI

A Java Spring Boot CLI application for tracking issues in Google Sheets, accessible via Docker.

## Prerequisites

1.  **Credentials**: Place your Google Cloud Service Account key as `credentials.json` in the project root (IssueTracker, not backend!).
2.  **Access**: Share your Google Sheet with the `client_email` found in `credentials.json` (Editor role).

## Build

Build the Docker image:

```bash
docker build -t issue-tracker .
```

## Run

Run the container in interactive mode (`-it`) and mount your credentials file.

**Default Run:**
```bash
docker run -it --rm \
  -v "$(pwd)/credentials.json":/app/credentials.json \
  issue-tracker
```

**Run with Custom Spreadsheet ID:**
```bash
docker run -it --rm \
  -v "$(pwd)/credentials.json":/app/credentials.json \
  -e APP_SHEETS_SPREADSHEET_ID="YOUR_SPREADSHEET_ID" \
  issue-tracker
```

## Usage

Once inside the shell (`sheet:>`), use the following commands:

| Action | Command                                                                                      |
| :--- |:---------------------------------------------------------------------------------------------|
| **Create Issue** | `sheet create --desc "Fix bug"` <br> `sheet create --desc "Subtask" --parent "AD-1"`         |
| **Update Status** | `sheet update --id "AD-1" --status IN_PROGRESS` <br> *(Statuses: OPEN, IN_PROGRESS, CLOSED)* |
| **List Issues** | `sheet list --status OPEN`                                                                   |
| **Exit** | `exit`                                                                                       |