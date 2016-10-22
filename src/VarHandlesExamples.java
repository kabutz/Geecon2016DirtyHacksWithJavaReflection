import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class VarHandlesExamples {
    public static void main(String... args) {
        ConcurrentHashMap d; // nope
        AtomicReference dd; // yip
        AtomicBoolean vv; // yip
        AtomicInteger di; // nope
        LongAdder ld;
    }
}
