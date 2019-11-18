package com.zdrenka.utils;

import com.zdrenka.model.Issue;
import com.zdrenka.model.Comment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Utils {

    private List<Issue> issues = new ArrayList<>();

    public List<Issue> csvToIssue(String csvFile) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withEscape('\\');
        CSVParser csvParser = new CSVParser(reader,csvFileFormat);
        for (CSVRecord csvRecord : csvParser) {
            if(csvRecord.get(0).length() > 1)
                createIssue(csvRecord);
        }
        return getIssues();
    }



    private void createIssue(CSVRecord csvRecord) {

        Optional<Issue> foundIssue = getIssues().stream().filter(key -> key.getSummary().equals(csvRecord.get(2))).findFirst();
        if(foundIssue.isPresent()) {
            if(!csvRecord.get(9).isEmpty()) {
                Comment comment = createComment(csvRecord);
                System.out.println("Created Comment");
                foundIssue.get().getComments().add(comment);
            }
        } else {
            Issue issue = new Issue();
            issue.setSummary(csvRecord.get(2));
            issue.setStartDate(convertDate(csvRecord.get(5)));
            issue.setDescription(csvRecord.get(9));
            System.out.println("Created Issue: "+ issue.getSummary());
            getIssues().add(issue);
        }
    }

    public static Comment createComment(CSVRecord csvRecord) {
        Comment comment = new Comment();
        comment.setComment(csvRecord.get(9));
        comment.setDate(convertDate(csvRecord.get(5)));
        return comment;
    }

    public static String convertDate(String date) {//Thu, 07 Jun 2012 13:36:03
        DateTimeFormatter currentFormat = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss");
        LocalDate localDate = LocalDate.parse(date, currentFormat);
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}

