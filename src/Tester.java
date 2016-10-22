import java.util.stream.*;

public class Tester {
    public static void main(String... args) {
        // this is only a correctness test, not a performance test.
        Stream.of(
            new StackBroken<Integer>(),
            new StackSynchronized<Integer>(),
            new StackConcurrentAR<Integer>(),
            new StackConcurrentARFU<Integer>(),
            new StackConcurrentUnsafe<Integer>(),
            new StackConcurrentVarHandles<Integer>()
        ).forEach(ConcurrentTester::test);
    }
}
