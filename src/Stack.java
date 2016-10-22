public interface Stack<E> {
    void push(E e);

    E pop();

    class Node<T> {
        private final T item;
        private final Node<T> next;

        public Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }

        public T getItem() {
            return item;
        }

        public Node<T> getNext() {
            return next;
        }
    }
}
