import sun.misc.*;

import java.lang.reflect.*;

public class StackConcurrentUnsafe<E> implements Stack<E> {
    private volatile Node<E> top = null;

    public void push(E e) {
        Node<E> current, next;
        do {
            current = top;
            next = new Node<>(e, current);
        }
        while (!UNSAFE.compareAndSwapObject(this, TOP_OFFSET, current, next));
    }

    public E pop() {
        Node<E> current, next;
        do {
            current = top;
            if (current == null) return null;
            next = current.getNext();
        }
        while (!UNSAFE.compareAndSwapObject(this, TOP_OFFSET, current, next));
        return current.getItem();
    }

    private static final Unsafe UNSAFE;

    private static final long TOP_OFFSET;

    static {
        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafeField.get(null);
            TOP_OFFSET = UNSAFE.objectFieldOffset(
                StackConcurrentUnsafe.class.getDeclaredField("top")
            );
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
