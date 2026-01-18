package com.mia.taskapp;

public class TaskItem {
    private final String name;
    private final int durationSeconds;

    public TaskItem(String name, int durationSeconds) {
        this.name = name;
        this.durationSeconds = durationSeconds;
    }

    public String getName() {
        return name;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}
