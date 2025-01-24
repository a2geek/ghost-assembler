package a2geek.asm.api.util;

import java.io.*;
import java.util.function.Supplier;

public class Source {
    private Supplier<Reader> supplier;

    public Source(final String sourceCode) {
        this.supplier = () -> new StringReader(sourceCode);
    }
    public Source(final File file) {
        this.supplier = () -> {
            try {
                return new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }
    public Source(final Supplier<Reader> supplier) {
        this.supplier = supplier;
    }

    public Reader newReader() {
        return supplier.get();
    }
}
