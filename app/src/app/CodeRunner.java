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
 * The code is placed inside "public class Main { ... }" (and, in MAIN mode, inside main too),
 * compiled with the JDK's built-in compiler, then run in a separate Java process
 * (so an endless loop or System.exit cannot freeze the app).
 * The first header line also imports Scanner, so input works without an import line.
 */
final class CodeRunner {
    private static final String CLASS_HEADER = "import java.util.Scanner; public class Main {\n";
    private static final String MAIN_HEADER = CLASS_HEADER + "    public static void main(String[] args) {\n";
    private static final int TIMEOUT_SECONDS = 5;
    private static final int MAX_OUTPUT = 50_000;

    record Result(boolean compiled, String output, boolean timedOut) {}

    private CodeRunner() {}

    /** Runs the student's code, feeding it the given input as if it were typed on the keyboard. */
    static Result run(String code, Unit.Mode mode, String input) {
        boolean inMain = mode == Unit.Mode.MAIN;
        int headerLines = inMain ? 2 : 1;
        String source = (inMain ? MAIN_HEADER : CLASS_HEADER) + code + (inMain ? "\n    }\n}\n" : "\n}\n");
        Path dir = null;
        try {
            dir = Files.createTempDirectory("unit-run");
            Path main = dir.resolve("Main.java");
            Path runMain = dir.resolve("RunMain.java");
            Path bucket = dir.resolve("Bucket.java");
            Files.writeString(main, source, StandardCharsets.UTF_8);
            Files.writeString(runMain, Theme.resource("runner/RunMain.java.txt"), StandardCharsets.UTF_8);
            Files.writeString(bucket, Theme.resource("runner/Bucket.java.txt"), StandardCharsets.UTF_8);

            String errors = compile(dir, headerLines, main, runMain, bucket);
            if (errors != null) return new Result(false, errors, false);
            return execute(dir, headerLines, input);
        } catch (IOException | InterruptedException e) {
            return new Result(false, "Could not run the program: " + e.getMessage(), false);
        } finally {
            deleteQuietly(dir);
        }
    }

    /** Returns null when compilation succeeds, otherwise friendly error messages. */
    private static String compile(Path dir, int headerLines, Path... sources) throws IOException {
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
            long line = d.getLineNumber() - headerLines;
            String where = line >= 1 ? "Line " + line : "End of your code";
            sb.append("  ").append(where).append(": ").append(d.getMessage(Locale.ENGLISH)).append('\n');
        }
        return sb.toString();
    }

    private static Result execute(Path dir, int headerLines, String input) throws IOException, InterruptedException {
        String exe = System.getProperty("os.name", "").toLowerCase().contains("windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", exe);
        List<String> cmd = new ArrayList<>(List.of(java.toString(),
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-Dstdin.encoding=UTF-8",
                "-Dfile.encoding=UTF-8", "-Xshare:auto", "-cp", dir.toString(), "RunMain"));
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        try (var stdin = p.getOutputStream()) {
            if (input != null && !input.isBlank()) {
                String text = input.replace("\r\n", "\n");
                stdin.write((text.endsWith("\n") ? text : text + "\n").getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ignored) {
            // The program ended before reading its input; that's fine.
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread reader = new Thread(() -> copyLimited(p.getInputStream(), out));
        reader.setDaemon(true);
        reader.start();

        boolean finished = p.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) p.destroyForcibly();
        reader.join(1000);

        String text = out.toString(StandardCharsets.UTF_8);
        if (out.size() >= MAX_OUTPUT) text += "\n... (output too long, cut here)";
        return new Result(true, explain(fixLineNumbers(text, headerLines)), !finished);
    }

    /** Stack traces mention Main.java:N. Translate to the line number the student sees, and hide the starter class. */
    private static String fixLineNumbers(String text, int headerLines) {
        text = text.replaceAll("(?m)^\\s*at RunMain\\.main\\(RunMain\\.java:\\d+\\)\\R?", "");
        Matcher m = Pattern.compile("(?<!Run)Main\\.java:(\\d+)").matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            int line = Integer.parseInt(m.group(1)) - headerLines;
            m.appendReplacement(sb, "your code, line " + line);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** Adds a plain-words hint after the most common input crashes. */
    private static String explain(String text) {
        if (text.contains("java.util.InputMismatchException")) {
            return text + "\nHint: the program expected a number, but the next value in the INPUT box is something else"
                    + "\n(for example a word, or a decimal number where a whole number was expected).";
        }
        if (text.contains("java.util.NoSuchElementException")) {
            return text + "\nHint: the program tried to read more values than you typed in the INPUT box."
                    + "\nType one value for each time your program reads input.";
        }
        return text;
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
