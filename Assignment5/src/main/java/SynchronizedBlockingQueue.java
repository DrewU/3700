import java.util.ArrayList;
import java.util.LinkedList;

public class SynchronizedBlockingQueue {

    public static void main(String[] args) {

    }

    public static long testSynchronized(int numProducers, int numConsumers){
        long before = System.currentTimeMillis();
        final PC pc = new PC();

        ArrayList<Thread> producers = new ArrayList<>();
        ArrayList<Thread> consumers = new ArrayList<>();

        for (int i = 0; i < numProducers; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pc.produce();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.setName(Integer.toString(i));
            producers.add(t1);
        }

        for (int i = 0; i < numConsumers; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pc.consume();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.setName(Integer.toString(i));
            consumers.add(t1);
        }


        for (Thread t : producers) {
            t.start();
        }
        for (Thread t : consumers) {
            t.start();
        }
        for (Thread t : producers) {

        }
        for (Thread t : consumers) {
            try{
                t.join();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return System.currentTimeMillis() - before;
    }

    public static class PC {
        LinkedList<Integer> list = new LinkedList<>();
        int capacity = 5;

        public void produce() throws InterruptedException {
            int value = 0;
            while (value < 100) {
                synchronized (this) {
                    while (list.size() == capacity)
                        wait();


                    list.add(value++);

                    //System.out.println("Produced: "+ value + ":by thread:"+ Thread.currentThread().getName());

                    notify();
                }
            }

        }

        public void consume() throws InterruptedException {
            while (true) {
                synchronized (this) {

                    while (list.size() == 0)
                        wait();

                    int val = list.removeFirst();

                    //System.out.println("Consumed: "+ val + ":by thread:"+ Thread.currentThread().getName());

                    notify();

                    //Thread.sleep(1000);
                }
            }
        }
    }
}