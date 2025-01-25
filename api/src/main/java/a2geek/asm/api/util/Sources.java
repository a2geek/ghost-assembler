package a2geek.asm.api.util;

import java.io.*;
import java.util.function.Supplier;

public class Sources {
    private Sources() {}

    public static Supplier<Reader> get(File file) {
        return () -> {
            try {
                return new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Supplier<Reader> get(String source) {
        return () -> new StringReader(source);
    }

    public static Supplier<Reader> get(Supplier<InputStream> inputStream) {
        return () -> new InputStreamReader(inputStream.get());
    }
}
