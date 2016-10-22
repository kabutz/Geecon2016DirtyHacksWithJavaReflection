import java.util.concurrent.atomic.*;

public class StackConcurrentARFU<E> implements Stack<E> {
    private volatile Node<E> top = null;

    public void push(E e) {
        Node<E> current, next;
        do {
            current = top;
            next = new Node<>(e, current);
        } while (!topUpdater.compareAndSet(this, current, next));
    }

    public E pop() {
        Node<E> current, next;
        do {
            current = top;
            if (current == null) return null;
            next = current.getNext();
        } while (!topUpdater.compareAndSet(this, current, next));
        return current.getItem();
    }

    private static final AtomicReferenceFieldUpdater<
        StackConcurrentARFU, Node> topUpdater =
        AtomicReferenceFieldUpdater.newUpdater(
            StackConcurrentARFU.class,
            Node.class,
            "top");
}
