package app;

import javax.swing.Timer;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Saves the student's work to a file in their user folder, automatically. */
final class Store {
    private final Path file;
    private final Properties data = new Properties();
    private final Timer saveTimer;

    private Store(Path file) {
        this.file = file;
        this.saveTimer = new Timer(800, e -> saveNow());
        this.saveTimer.setRepeats(false);
    }

    static Store load() {
        String appData = System.getenv("APPDATA");
        Path dir = appData != null ? Path.of(appData, "Unit1App") : Path.of(System.getProperty("user.home"), ".unit1app");
        if (System.getProperty("unit1.datadir") != null) dir = Path.of(System.getProperty("unit1.datadir"));
        Store store = new Store(dir.resolve("homework.properties"));
        if (Files.exists(store.file)) {
            try (Reader r = Files.newBufferedReader(store.file, StandardCharsets.UTF_8)) {
                store.data.load(r);
            } catch (IOException ignored) {
            }
        }
        return store;
    }

    String get(String key, String fallback) {
        return data.getProperty(key, fallback);
    }

    boolean has(String key) {
        return data.containsKey(key);
    }

    void put(String key, String value) {
        data.setProperty(key, value);
        saveTimer.restart();
    }

    void saveNow() {
        saveTimer.stop();
        try {
            Files.createDirectories(file.getParent());
            try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                data.store(w, "Unit 1 homework - saved automatically");
            }
        } catch (IOException ignored) {
        }
    }
}
