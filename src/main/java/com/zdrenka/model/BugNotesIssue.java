package com.zdrenka.model;

import java.time.LocalDate;

public class BugNotesIssue {

    private String projectName;
    private String projectCreationTime;
    private String bugTitle;
    private String bugNumber;
    private String user;
    private LocalDate updateTime;
    private String priority;
    private String status;
    private String assignedTo;
    private String text;
    private String image;
    private String file;

    public BugNotesIssue(String projectName, String projectCreationTime, String bugTitle, String bugNumber, String user, LocalDate updateTime, String priority, String status, String assignedTo, String text, String image, String file) {
        this.projectName = projectName;
        this.projectCreationTime = projectCreationTime;
        this.bugTitle = bugTitle;
        this.bugNumber = bugNumber;
        this.user = user;
        this.updateTime = updateTime;
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo;
        this.text = text;
        this.image = image;
        this.file = file;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCreationTime() {
        return projectCreationTime;
    }

    public void setProjectCreationTime(String projectCreationTime) {
        this.projectCreationTime = projectCreationTime;
    }

    public String getBugTitle() {
        return bugTitle;
    }

    public void setBugTitle(String bugTitle) {
        this.bugTitle = bugTitle;
    }

    public String getBugNumber() {
        return bugNumber;
    }

    public void setBugNumber(String bugNumber) {
        this.bugNumber = bugNumber;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDate getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDate updateTime) {
        this.updateTime = updateTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
