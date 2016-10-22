import java.lang.invoke.*;

public class Test {
    private int i;

    public static void main(String... args) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Test.main");
        MethodHandles.lookup().findVarHandle(Test.class, "i", int.class);
    }
}
