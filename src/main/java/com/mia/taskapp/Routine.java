package com.mia.taskapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Routine {
    private final String title;
    private final List<TaskItem> tasks;

    public Routine(String title, List<TaskItem> tasks) {
        this.title = title;
        this.tasks = new ArrayList<>(tasks);
    }

    public String getTitle() {
        return title;
    }

    public List<TaskItem> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
