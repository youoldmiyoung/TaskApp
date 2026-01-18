package com.mia.taskapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutineStore {

    private static final List<Routine> routines = new ArrayList<>();
    private static boolean initialized = false;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final Path FILE_PATH = Paths.get(
            System.getProperty("user.home"),
            ".taskapp",
            "routines.json"
    );

    private RoutineStore() {}

    /* =========================
       Public API
       ========================= */

    public static List<Routine> getRoutines() {
        initIfNeeded();
        return Collections.unmodifiableList(routines);
    }

    public static void addRoutine(Routine routine) {
        initIfNeeded();
        routines.add(routine);
        save();
    }

    /* =========================
       Initialization
       ========================= */

    public static void initIfNeeded() {
        if (initialized) return;
        initialized = true;

        load();

        // First run OR corrupted/empty file → preload examples
        if (routines.isEmpty()) {
            preloadExamples();
            save();
        }
    }

    /* =========================
       Persistence
       ========================= */

    public static void load() {
        try {
            if (!Files.exists(FILE_PATH)) {
                Files.createDirectories(FILE_PATH.getParent());
                return;
            }

            byte[] json = Files.readAllBytes(FILE_PATH);
            List<Routine> loaded =
                    MAPPER.readValue(json, new TypeReference<List<Routine>>() {});
            routines.clear();
            routines.addAll(loaded);

        } catch (IOException e) {
            // Don't crash app if file is bad
            e.printStackTrace();
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            MAPPER.writeValue(FILE_PATH.toFile(), routines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       Preloaded Examples
       ========================= */

    private static void preloadExamples() {
        routines.add(new Routine("Morning Routine Example", List.of(
                new TaskItem("Drink water", 60),
                new TaskItem("Stretch", 5 * 60),
                new TaskItem("Shower", 10 * 60),
                new TaskItem("Breakfast", 15 * 60)
        )));

        routines.add(new Routine("Bedtime Routine Example", List.of(
                new TaskItem("Brush teeth", 2 * 60),
                new TaskItem("Skincare", 5 * 60),
                new TaskItem("Read", 15 * 60)
        )));
    }
}
