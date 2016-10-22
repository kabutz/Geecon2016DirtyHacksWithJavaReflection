public class StackSynchronized<E> implements Stack<E> {
    private Node<E> top = null;

    public synchronized void push(E e) {
        top = new Node<>(e, top);
    }

    public synchronized E pop() {
        Node<E> current = this.top;
        if (current == null) return null;
        this.top = current.getNext();
        return current.getItem();
    }
}
