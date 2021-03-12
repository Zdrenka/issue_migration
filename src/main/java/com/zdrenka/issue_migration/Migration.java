package com.zdrenka.issue_migration;

import com.nulabinc.backlog4j.Attachment;
import com.nulabinc.backlog4j.AttachmentData;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.BacklogClientFactory;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.IssueComment;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.api.option.AddIssueCommentParams;
import com.nulabinc.backlog4j.api.option.CreateIssueParams;
import com.nulabinc.backlog4j.conf.BacklogComConfigure;
import com.nulabinc.backlog4j.conf.BacklogConfigure;
import com.nulabinc.backlog4j.internal.json.IssueCommentJSONImpl;
import com.zdrenka.model.BugNotesIssue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.zdrenka.Headers.ASSIGNED_TO;
import static com.zdrenka.Headers.BUG_NUMBER;
import static com.zdrenka.Headers.BUG_TITLE;
import static com.zdrenka.Headers.FILE;
import static com.zdrenka.Headers.IMAGE;
import static com.zdrenka.Headers.PRIORITY;
import static com.zdrenka.Headers.PROJECT_CREATION_TIME;
import static com.zdrenka.Headers.PROJECT_NAME;
import static com.zdrenka.Headers.STATUS;
import static com.zdrenka.Headers.TEXT;
import static com.zdrenka.Headers.UPDATE_TIME;
import static com.zdrenka.Headers.USER;


public class Migration {

    private BacklogConfigure configure;
    private BacklogClient backlog;
    private Project project;

    public static final String BUG = "114907";
    public static final String TASK = "114908";

    public static void main(String args[]) throws Exception {

        Migration migration = new Migration();
        migration.init(args);
        migration.run(args[0]);

    }

    public void init(String[] args) throws IOException {
        configure = new BacklogComConfigure("zdrenka").apiKey("");
        backlog = new BacklogClientFactory(configure).newClient();
        project = backlog.getProject("VRS");

    }

    public void run(String csvFile) throws IOException {
        Map<String, Long> created = new TreeMap<>();
        Map<String, List<BugNotesIssue>> issues = getIssues(csvFile);

        issues.forEach((id, bugs) -> {
            bugs.sort(Comparator.comparing(BugNotesIssue::getUpdateTime));
            bugs.forEach(bug -> {
                List<Long> ids = attachments(bug, backlog);
                if(!created.containsKey(bug.getBugNumber())) {
                    Issue issue = createIssue(bug, ids);
                    created.put(bug.getBugNumber(), issue.getId());//add to completed list
                } else {
                    createComment(created, bug, ids);
                }
                System.out.println(bug.getBugNumber() + " - " + bug.getUpdateTime());
            });
        });
    }

    private void createComment(Map<String, Long> created, BugNotesIssue bug, List<Long> ids) {
        if(bug.getText().length() > 0) {
            AddIssueCommentParams commentParams = new AddIssueCommentParams(created.get(bug.getBugNumber()), bug.getUser() + " said: " + bug.getText());
            if (ids.size() > 0) {
                commentParams.attachmentIds(ids);
            }
            backlog.addIssueComment(commentParams);
        }
    }

    private Issue createIssue(BugNotesIssue bug, List<Long> ids) {
        CreateIssueParams issueParams = new CreateIssueParams(
                project.getId(),
                "["+ bug.getBugNumber() +"] - " + bug.getBugTitle(),
                (bug.getBugTitle().startsWith("BUG:") ? BUG : TASK),//task
                Issue.PriorityType.Normal);
        if(ids.size() > 0){
            issueParams.attachmentIds(ids);
        }
        issueParams.description(bug.getUser() + " said: " + bug.getText());
        issueParams.startDate(bug.getUpdateTime().toString());

        return backlog.createIssue(issueParams);
    }

    public LocalDate convertDate(String date) {//Thu, 07 Jun 2012 13:36:03
        DateTimeFormatter from = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss");
        DateTimeFormatter to = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, from);
        String ld = localDate.format(to);
        return LocalDate.parse(ld, to);
    }

    private static CSVParser readCSV(String arg) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(arg));
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withEscape('\\');
        return new CSVParser(reader, csvFileFormat);
    }


    public Map<String, List<BugNotesIssue>> getIssues(String csvFile) throws IOException {
        Map<String, List<BugNotesIssue>> issues = new TreeMap<>();
        CSVParser parser = readCSV(csvFile);
        for (CSVRecord csvRecord : parser) {
            if (csvRecord.get(0).length() > 1) {
                BugNotesIssue issue = populateBugNotesIssue(csvRecord);
                if(issues.containsKey(issue.getBugNumber())) {
                    issues.get(issue.getBugNumber()).add(issue);
                } else {
                    List<BugNotesIssue> bugs = new ArrayList<>();
                    bugs.add(issue);
                    issues.put(issue.getBugNumber(),bugs);
                }
            }
        }
        return issues;
    }

    private List<Long> attachments(BugNotesIssue issue, BacklogClient backlog) {
        List<Long> attachmentIds = new ArrayList<>();
        if (!issue.getFile().equals("No file")) {
            Attachment attachment = createAttachment(backlog, issue.getBugTitle(), issue.getFile());
            if(attachment != null) {
                attachmentIds.add(attachment.getId());
            }
        }
        if (!issue.getImage().equals("No image")) {
            Attachment attachment = createAttachment(backlog, issue.getBugTitle(), issue.getImage());
            if(attachment != null) {
                attachmentIds.add(attachment.getId());

            }
        }
        return attachmentIds;
    }

    private BugNotesIssue populateBugNotesIssue(CSVRecord csvRecord) {
        return new BugNotesIssue(csvRecord.get(PROJECT_NAME.getValue())
                , csvRecord.get(PROJECT_CREATION_TIME.getValue())
                , csvRecord.get(BUG_TITLE.getValue())
                , csvRecord.get(BUG_NUMBER.getValue())
                , csvRecord.get(USER.getValue())
                , convertDate(csvRecord.get(UPDATE_TIME.getValue()))
                , csvRecord.get(PRIORITY.getValue())
                , csvRecord.get(STATUS.getValue())
                , csvRecord.get(ASSIGNED_TO.getValue())
                , csvRecord.get(TEXT.getValue())
                , csvRecord.get(IMAGE.getValue())
                , csvRecord.get(FILE.getValue()));
    }

    private static Attachment createAttachment(BacklogClient backlog, String name, String url) {

        AttachmentData data = new AttachmentData() {
            @Override
            public String getFilename() {
                return name;
            }

            @Override
            public InputStream getContent() {
                try {
                    return new URL(url).openStream();
                } catch (FileNotFoundException | MalformedURLException e) {
                    System.out.println("cannot find attachment for:" + name + " - url : " + url);
                } catch (IOException e) {

                }
                return null;
            }
        };

        if(data.getContent() != null) {
            return backlog.postAttachment(data);
        }
        return null;
    }
}
