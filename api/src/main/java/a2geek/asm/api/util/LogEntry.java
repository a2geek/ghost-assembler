package a2geek.asm.api.util;

public record LogEntry(int lineNumber, LogLevel level, String message) {
    public LogEntry(int lineNumber, LogLevel level, String fmt, Object... args) {
        this(lineNumber, level, String.format(fmt, args));
    }

    @Override
    public String toString() {
        return String.format("%s in line %d: %s", level, lineNumber, message);
    }

    public enum LogLevel {
        TRACE, INFO, DEBUG, ERROR
    }
}
