public abstract class BlockingQueue<T> {
    abstract void put (T t);
    abstract T get();
    abstract boolean isFull();
    abstract boolean isEmpty();
}
