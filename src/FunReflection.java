import java.lang.invoke.*;
import java.lang.reflect.*;

public class FunReflection {
    public static void main(String... args) throws ReflectiveOperationException {
        Constructor<MethodHandles.Lookup> constr =
            MethodHandles.Lookup.class.getDeclaredConstructor(
                Class.class, int.class
            );
        constr.setAccessible(true);
        MethodHandles.Lookup su_lookup = constr.newInstance(String.class, -1);
        VarHandle valueHandle = su_lookup.findVarHandle(
            String.class,
            "value",
            byte[].class
        );
        valueHandle.set("hello!", valueHandle.get("cheers"));
        System.out.println("hello!");
    }
}
