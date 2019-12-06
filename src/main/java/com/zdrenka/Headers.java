package com.zdrenka;

public enum Headers {

    PROJECT_NAME(0),
    PROJECT_CREATION_TIME(1),
    BUG_TITLE(2),
    BUG_NUMBER(3),
    USER(4),
    UPDATE_TIME(5),
    PRIORITY(6),
    STATUS(7),
    ASSIGNED_TO(8),
    TEXT(9),
    IMAGE(10),
    FILE(11);

    private final int value;

    private Headers(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
