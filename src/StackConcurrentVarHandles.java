import java.lang.invoke.*;

public class StackConcurrentVarHandles<E> implements Stack<E> {
    private volatile Node<E> top = null;

    public void push(E e) {
        Node<E> current, next;
        do {
            current = top;
            next = new Node<>(e, current);
        } while (!topHandle.compareAndSet(this, current, next));
    }

    public E pop() {
        Node<E> current, next, swapResult = top;
        do {
            current = swapResult;
            if (current == null) return null;
            next = current.getNext();
        } while ((swapResult = (Node<E>)
            topHandle.compareAndExchange(this, current, next))
            != current);
        return current.getItem();
    }

    private final static VarHandle topHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            topHandle = lookup.findVarHandle(
                StackConcurrentVarHandles.class, "top",
                Node.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
