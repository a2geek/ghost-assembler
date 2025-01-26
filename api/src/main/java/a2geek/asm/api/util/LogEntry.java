package a2geek.asm.api.util;

public record LogEntry(int lineNumber, LogType type, String message) {
    public LogEntry(int lineNumber, LogType level, String fmt, Object... args) {
        this(lineNumber, level, String.format(fmt, args));
    }

    @Override
    public String toString() {
        return String.format("%s in line %d: %s", type, lineNumber, message);
    }

    public enum LogType {
        SOURCE, ERROR
    }
}
