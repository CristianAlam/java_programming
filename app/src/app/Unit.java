package app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The content of the unit bundled in this jar, read from unit.properties.
 * The Java code is shared by all units; only the files under units/unitN differ.
 */
final class Unit {
    /** Where a piece of student code goes: inside main, or the whole class body (for helper methods). */
    enum Mode { MAIN, CLASS }

    record Section(String title, String page) {
        @Override
        public String toString() { return title; }
    }

    record Example(String title, String file, String description, Mode mode) {
        @Override
        public String toString() { return title; }
    }

    record Exercise(String title, String id, Mode mode) {
        @Override
        public String toString() { return title; }
    }

    record QuizItem(String question, String answer) {}

    static final Unit CURRENT = load();

    final int number;
    final String title;
    final String heading;
    final boolean input;
    final List<Section> tutorial = new ArrayList<>();
    final List<Example> examples = new ArrayList<>();
    final List<Exercise> homework = new ArrayList<>();
    final List<String> checklist = new ArrayList<>();
    final String checklistNext;
    final List<QuizItem> quiz = new ArrayList<>();

    private Unit(Properties p) {
        number = Integer.parseInt(p.getProperty("number", "1").strip());
        title = p.getProperty("title", "");
        heading = p.getProperty("heading", "UNIT " + number);
        input = Boolean.parseBoolean(p.getProperty("input", "false").strip());
        checklistNext = p.getProperty("checklist.next", "");
        for (String[] f : list(p, "tutorial")) {
            // "- Title" marks a sub-item; the side list indents titles that start with spaces.
            String t = f[0].startsWith("- ") ? "    " + f[0].substring(2) : f[0];
            tutorial.add(new Section(t, f[1]));
        }
        for (String[] f : list(p, "examples")) examples.add(new Example(f[0], f[1], f[2], mode(f, 3)));
        for (String[] f : list(p, "homework")) homework.add(new Exercise(f[0], f[1], mode(f, 2)));
        for (String[] f : list(p, "checklist")) checklist.add(f[0]);
        for (String[] f : list(p, "quiz")) quiz.add(new QuizItem(f[0], f[1]));
    }

    /** "Unit 2 - Basic Programming Concepts": the window title (also used to find the window for ScreenGuard). */
    String windowTitle() {
        return "Unit " + number + " - " + title;
    }

    private static Mode mode(String[] fields, int index) {
        return fields.length > index && fields[index].strip().equalsIgnoreCase("class") ? Mode.CLASS : Mode.MAIN;
    }

    /** Reads prefix.1, prefix.2, ... until one is missing; each value split on |. */
    private static List<String[]> list(Properties p, String prefix) {
        List<String[]> out = new ArrayList<>();
        for (int i = 1; p.containsKey(prefix + "." + i); i++) {
            String[] fields = p.getProperty(prefix + "." + i).split("\\|", -1);
            for (int k = 0; k < fields.length; k++) fields[k] = fields[k].strip();
            out.add(fields);
        }
        return out;
    }

    private static Unit load() {
        Properties p = new Properties();
        try (InputStream in = Unit.class.getResourceAsStream("/unit.properties")) {
            if (in != null) p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
        return new Unit(p);
    }
}
