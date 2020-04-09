import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBlockingQueue<T> extends BlockingQueue<T> {
    int limit;
    Queue<T> q;
    AtomicBoolean isInUse;

    public boolean isEmpty(){
        return q.isEmpty();
    }
    public AtomicBlockingQueue(int lim){
        limit = lim;
        q = new LinkedList<>();
    }

    public void put (T t) {
        if(!isInUse.get()){
        }
    }

    public T get() {
        if(!isInUse.get()){

        }
        return q.poll();
    }

    public boolean isFull(){
        return (q.size() >= limit);
    }
}
