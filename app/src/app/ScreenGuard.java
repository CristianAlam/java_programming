package app;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

/**
 * Asks Windows to hide a window from screenshots and screen recordings
 * (SetWindowDisplayAffinity). Does nothing on other operating systems.
 * Start the app with -Dunitapp.noguard=true to switch it off (for the teacher).
 */
final class ScreenGuard {
    private static final int WDA_MONITOR = 0x01;            // window shows as black in captures
    private static final int WDA_EXCLUDEFROMCAPTURE = 0x11; // window is left out of captures (Win 10 2004+)

    private ScreenGuard() {}

    static boolean protect(String windowTitle) {
        if (!System.getProperty("os.name", "").toLowerCase().contains("windows")) return false;
        if (Boolean.getBoolean("unitapp.noguard")) return false;
        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup user32 = SymbolLookup.libraryLookup("user32", Arena.global());
            MethodHandle findWindow = linker.downcallHandle(
                    user32.find("FindWindowW").orElseThrow(),
                    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
            MethodHandle setAffinity = linker.downcallHandle(
                    user32.find("SetWindowDisplayAffinity").orElseThrow(),
                    FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT));

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment title = arena.allocateFrom(windowTitle, StandardCharsets.UTF_16LE);
                MemorySegment hwnd = (MemorySegment) findWindow.invokeExact(MemorySegment.NULL, title);
                if (hwnd.address() == 0) return false;
                int ok = (int) setAffinity.invokeExact(hwnd, WDA_EXCLUDEFROMCAPTURE);
                if (ok == 0) ok = (int) setAffinity.invokeExact(hwnd, WDA_MONITOR);
                return ok != 0;
            }
        } catch (Throwable t) {
            return false;
        }
    }
}
