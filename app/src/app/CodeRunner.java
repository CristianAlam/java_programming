package app;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Compiles and runs a student's program.
 * The code is placed inside "public class Main { public static void main(...) { ... } }",
 * compiled with the JDK's built-in compiler, then run in a separate Java process
 * (so an endless loop or System.exit cannot freeze the app).
 */
final class CodeRunner {
    static final String HEADER = "public class Main {\n    public static void main(String[] args) {\n";
    static final String FOOTER = "\n    }\n}\n";
    private static final int HEADER_LINES = 2;
    private static final int TIMEOUT_SECONDS = 5;
    private static final int MAX_OUTPUT = 50_000;

    record Result(boolean compiled, String output, boolean timedOut) {}

    private CodeRunner() {}

    /** Runs the statements that go inside main. */
    static Result run(String mainBody) {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("unit1-run");
            Path main = dir.resolve("Main.java");
            Path bucket = dir.resolve("Bucket.java");
            Files.writeString(main, HEADER + mainBody + FOOTER, StandardCharsets.UTF_8);
            Files.writeString(bucket, Theme.resource("runner/Bucket.java.txt"), StandardCharsets.UTF_8);

            String errors = compile(dir, main, bucket);
            if (errors != null) return new Result(false, errors, false);
            return execute(dir);
        } catch (IOException | InterruptedException e) {
            return new Result(false, "Could not run the program: " + e.getMessage(), false);
        } finally {
            deleteQuietly(dir);
        }
    }

    /** Returns null when compilation succeeds, otherwise friendly error messages. */
    private static String compile(Path dir, Path... sources) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return "The Java compiler was not found.\n"
                    + "Install the JDK (Temurin JDK 25), not only the Java runtime.\n"
                    + "See the setup steps your teacher gave you.";
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
            List<String> options = List.of("-d", dir.toString(), "-encoding", "UTF-8", "-nowarn");
            boolean ok = compiler.getTask(null, fm, diagnostics, options, null,
                    fm.getJavaFileObjects(sources)).call();
            if (ok) return null;
        }
        StringBuilder sb = new StringBuilder("Your code has errors, so it could not run:\n\n");
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR) continue;
            long line = d.getLineNumber() - HEADER_LINES;
            String where = line >= 1 ? "Line " + line : "End of your code";
            sb.append("  ").append(where).append(": ").append(d.getMessage(Locale.ENGLISH)).append('\n');
        }
        return sb.toString();
    }

    private static Result execute(Path dir) throws IOException, InterruptedException {
        String exe = System.getProperty("os.name", "").toLowerCase().contains("windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", exe);
        List<String> cmd = new ArrayList<>(List.of(java.toString(),
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-Xshare:auto",
                "-cp", dir.toString(), "Main"));
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        p.getOutputStream().close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread reader = new Thread(() -> copyLimited(p.getInputStream(), out));
        reader.setDaemon(true);
        reader.start();

        boolean finished = p.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) p.destroyForcibly();
        reader.join(1000);

        String text = out.toString(StandardCharsets.UTF_8);
        if (out.size() >= MAX_OUTPUT) text += "\n... (output too long, cut here)";
        return new Result(true, fixLineNumbers(text), !finished);
    }

    /** Stack traces mention Main.java:N. Translate to the line number the student sees. */
    private static String fixLineNumbers(String text) {
        Matcher m = Pattern.compile("Main\\.java:(\\d+)").matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            int line = Integer.parseInt(m.group(1)) - HEADER_LINES;
            m.appendReplacement(sb, "your code, line " + line);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void copyLimited(InputStream in, ByteArrayOutputStream out) {
        byte[] buf = new byte[4096];
        try {
            int n;
            while ((n = in.read(buf)) != -1) {
                if (out.size() < MAX_OUTPUT) out.write(buf, 0, Math.min(n, MAX_OUTPUT - out.size()));
            }
        } catch (IOException ignored) {
        }
    }

    private static void deleteQuietly(Path dir) {
        if (dir == null) return;
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
        }
    }
}
