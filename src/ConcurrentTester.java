import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ConcurrentTester {
    private static final int THREADS = 8;
    private static final int UPTO = 1_000_000;

    public static void test(Stack<Integer> stack) {
        System.out.print("Testing " + stack.getClass().getSimpleName());
        AtomicBoolean failed = new AtomicBoolean();
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < UPTO; j++) {
                        stack.push(j);
                        if (stack.pop() == null) {
                            failed.set(true);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    failed.set(true);
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new CancellationException();
            }
        }
        if (failed.get() || stack.pop() != null) {
            System.out.println(" - FAILED!!!");
        } else {
            System.out.println(" - passed");
        }
    }
}
