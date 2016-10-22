import com.sun.javafx.tk.*;

import java.lang.management.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Hackity hack class for monitoring what the QuantumRenderer thread is doing
 * in a JavaFX class.  Will most probably not work in future versions of JavaFX.
 * It is used to check saturation of the QuantumRenderer thread.
 * <p>
 * Example uses:
 * <p>
 * java -classpath PATH_TO_PERFORMANCE_TESTING_FX_LAUNCHER:Modena.jar PerformanceTestingFXLauncher modena.Modena
 * (You can see that the first page with all the graphical controls does almost 100% saturation of the QuantumRenderer thread)
 * <p>
 * java -classpath PATH_TO_PERFORMANCE_TESTING_FX_LAUNCHER:MandelbrotSet.jar PerformanceTestingFXLauncher demo.parallel.Main
 *
 * @author Dr Heinz M. Kabutz
 */
public class PerformanceTestingFXLauncher {
    public static void main(String... args) throws ReflectiveOperationException {
        try {
            setupWatcher();
        } catch (ReflectiveOperationException ex) {
            System.err.println("Could not set up performance probes: " + ex);
        }
        callNextMainMethod(args);
    }

    private static void setupWatcher() throws ReflectiveOperationException {
        ThreadPoolExecutor quantumRendererPool = grabPool();
        Thread quantumRendererThread = grabThread(quantumRendererPool);
        startWatching(quantumRendererPool, quantumRendererThread);
    }

    private static void callNextMainMethod(String[] args) throws ReflectiveOperationException {
        Class<?> runnerClass = Class.forName(args[0]);
        Method main = runnerClass.getMethod("main", String[].class);
        main.invoke(null, (Object) Arrays.copyOfRange(args, 1, args.length));
    }

    private static void startWatching(ThreadPoolExecutor quantumRendererPool,
                                      Thread quantumRendererThread) {
        Timer timer = new Timer("JavaFXPerformanceWatcher", true);
        timer.schedule(new TimerTask() {
            public Thread fxAppThread;

            @Override
            public void run() {
                if (fxAppThread == null) {
                    try {
                        fxAppThread = grabFxAppThread();
                    } catch (ReflectiveOperationException e) {
                        System.err.println("Could not grab JavaFX Application Thread");
                    }
                }
                showStats(quantumRendererPool, quantumRendererThread, fxAppThread);
            }
        }, 5000, 5000);
    }

    private static ThreadPoolExecutor grabPool() throws ReflectiveOperationException {
        Class<? extends ThreadPoolExecutor> qrc =
            Class.forName("com.sun.javafx.tk.quantum.QuantumRenderer", true,
                Thread.currentThread().getContextClassLoader())
                .asSubclass(ThreadPoolExecutor.class);
        Method getInstanceMethod = qrc.getMethod("getInstance");
        getInstanceMethod.setAccessible(true);
        return (ThreadPoolExecutor) getInstanceMethod.invoke(null);
    }

    private static Thread grabThread(ThreadPoolExecutor quantumRendererPool)
        throws ReflectiveOperationException {
        Field workersField = ThreadPoolExecutor.class.getDeclaredField("workers");
        workersField.setAccessible(true);
        Set<?> workers = (Set<?>) workersField.get(quantumRendererPool);
        Object worker = workers.iterator().next();
        Field threadField = worker.getClass().getDeclaredField("thread");
        threadField.setAccessible(true);
        return (Thread) threadField.get(worker);
    }

    private static Thread grabFxAppThread() throws ReflectiveOperationException {
        Method userThreadMethod = Toolkit.class.getDeclaredMethod("getFxUserThread");
        userThreadMethod.setAccessible(true);
        return (Thread) userThreadMethod.invoke(null);
    }

    private static class ThreadDiff {
        private final LongDiff cpuDiff = new LongDiff(0);
        private final LongDiff userDiff = new LongDiff(0);
    }

    private static void showStats(ThreadPoolExecutor tpe, Thread worker, Thread fxAppThread) {
        long cpuTime = quantumRenderer.cpuDiff.diffSinceLast(tmxbean.getThreadCpuTime(worker.getId()));
        long userTime = quantumRenderer.userDiff.diffSinceLast(tmxbean.getThreadUserTime(worker.getId()));
        long cpuTimeApp = fxAppThread == null ? 0 : fxAppThreadDiff.cpuDiff.diffSinceLast(tmxbean.getThreadCpuTime(fxAppThread.getId()));
        long userTimeApp = fxAppThread == null ? 0 : fxAppThreadDiff.userDiff.diffSinceLast(tmxbean.getThreadUserTime(fxAppThread.getId()));
        long elapsedTime = elapsedDiff.diffSinceLast(System.currentTimeMillis());
        long tasksCompleted = tasksDiff.diffSinceLast(tpe.getCompletedTaskCount());

        System.out.println(
            "QuantumRenderer: " + (cpuTime / elapsedTime / 10000) + "%"
        );
//        System.out.println(
//                "QuantumRenderer: " +
//                        "tasks = " + tasksCompleted + ", " +
//                        "cpu = " + TimeUnit.NANOSECONDS.toMillis(cpuTime) + "ms, " +
//                        "user = " + TimeUnit.NANOSECONDS.toMillis(userTime) + "ms, " +
//                        "elapsed = " + elapsedTime + "ms, " +
//                        "cpu saturation = " + (cpuTime / elapsedTime / 10000) + "%, " +
//                        "cpu/task = " + (int) ((double) TimeUnit.NANOSECONDS.toMicros(cpuTime) / tasksCompleted) + "µs"
//        );
        System.out.println(
            "JavaFX Application Thread: " + (cpuTimeApp / elapsedTime / 10000) + "%"
        );
    }

    private static final ThreadMXBean tmxbean = ManagementFactory.getThreadMXBean();
    private static final ThreadDiff fxAppThreadDiff = new ThreadDiff();
    private static final ThreadDiff quantumRenderer = new ThreadDiff();
    private static final LongDiff elapsedDiff = new LongDiff(System.currentTimeMillis());
    private static final LongDiff tasksDiff = new LongDiff(0);

    private static class LongDiff {

        private long lastValue;

        public LongDiff(long lastValue) {
            this.lastValue = lastValue;
        }

        public long diffSinceLast(long value) {
            if (lastValue == 0) {
                lastValue = value;
                return value;
            } else {
                long temp = value;
                value = value - lastValue;
                lastValue = temp;
                return value;
            }
        }

    }
}
