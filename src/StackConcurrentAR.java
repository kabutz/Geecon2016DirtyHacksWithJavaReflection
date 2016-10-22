import java.util.concurrent.atomic.*;

public class StackConcurrentAR<E> implements Stack<E> {
    private final AtomicReference<Node<E>> top = new AtomicReference<>();

    public void push(E e) {
        Node<E> current, next;
        do {
            current = top.get();
            next = new Node<>(e, current);
        } while (!top.compareAndSet(current, next));
    }

    public E pop() {
        Node<E> current, next;
        do {
            current = top.get();
            if (current == null) return null;
            next = current.getNext();
        } while (!top.compareAndSet(current, next));
        return current.getItem();
    }
}
