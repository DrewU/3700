import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockedBlockingQueue<T> extends BlockingQueue<T> {
    Condition isFullCondition;
    Condition isEmptyCondition;
    Lock lock;
    int limit;
    Queue<T> q;

    public LockedBlockingQueue(int limit) {
        this.limit = limit;
        lock = new ReentrantLock();
        isFullCondition = lock.newCondition();
        isEmptyCondition = lock.newCondition();
        q = new LinkedList<>();
    }

    public boolean isEmpty(){
        return q.isEmpty();
    }
    public void put (T t) {
        lock.lock();
        try {
            while (this.isFull()) {
                try {
                    isFullCondition.await();
                } catch (InterruptedException ex) {}
            }
            q.add(t);
            isEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() {
        T t = null;
        lock.lock();
        try {
            while (q.isEmpty()) {
                try {
                    isEmptyCondition.await();
                } catch (InterruptedException ex) {}
            }
            t = q.poll();
            isFullCondition.signalAll();
        } finally {
            lock.unlock();
        }
        return t;
    }

    public boolean isFull(){
        return (q.size() >= limit);
    }
}
