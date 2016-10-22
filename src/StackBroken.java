public class StackBroken<E> implements Stack<E> {
    private Node<E> top = null;

    public void push(E e) {
        top = new Node<>(e, top);
    }

    public E pop() {
        Node<E> current = this.top;
        if (current == null) return null;
        this.top = current.getNext();
        return current.getItem();
    }
}
