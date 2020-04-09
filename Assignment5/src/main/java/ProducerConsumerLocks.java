import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class ProducerConsumerLocks {
    public static void main(String args[]) {

        testAll();
    }

    public static void testAll(){
        long before = 0;
        long after = 0;

//        System.err.println("Atomic Queue Times:");
//        BlockingQueue AtomicQueue = new AtomicBlockingQueue(10);
//        before = System.currentTimeMillis();
//        generateThreads(AtomicQueue, 5, 5);
//        after = System.currentTimeMillis();
//        System.err.println("5 Producers and 5 Consumers: " + (after - before));
//        AtomicQueue = new AtomicBlockingQueue(10);
//        before = System.currentTimeMillis();
//        generateThreads(AtomicQueue, 2, 5);
//        after = System.currentTimeMillis();
//        System.err.println("2 Producers and 5 Consumers: " + (after - before) + "\n\n");


        System.err.println("Locked Queue Times:");
        BlockingQueue lockedQueue = new LockedBlockingQueue(10);
        before = System.currentTimeMillis();
        generateThreads(lockedQueue, 5, 5);
        after = System.currentTimeMillis();





        System.err.println("5 Producers and 5 Consumers: " + (after - before));
//        lockedQueue = new AtomicBlockingQueue(10);
//        before = System.currentTimeMillis();
//        //generateThreads(lockedQueue, 2, 5);
//        after = System.currentTimeMillis();
//        System.err.println("2 Producers and 5 Consumers: " + (after - before) + "\n\n");

//        System.err.println("Synchronized Queue Times:");
//        before = System.currentTimeMillis();
//        SynchronizedBlockingQueue.testSynchronized(5, 5);
//        after = System.currentTimeMillis();
//        System.err.println("5 Producers and 5 Consumers: " + (after - before));
//        before = System.currentTimeMillis();
//        SynchronizedBlockingQueue.testSynchronized(2, 5);
//        after = System.currentTimeMillis();
//        System.err.println("2 Producers and 5 Consumers: " + (after - before) + "\n\n");
    }

    public static void generateThreads(BlockingQueue<Integer> queue, int numProducers, int numConsumers) {
        ArrayList<Thread> consumers = new ArrayList<>();
        ArrayList<Thread> producers = new ArrayList<>();
        for (int i = 0; i < numProducers; i++) {
            Thread newThread = new Thread(new Producer(queue, i));
            producers.add(newThread);
        }

        for (int i = 0; i < numConsumers; i++) {
            Thread newThread = new Thread(new Consumer(queue, i));
            consumers.add(newThread);
        }

        for(Thread i : producers){
            i.start();
        }
        for(Thread i : consumers){
            i.start();
        }


    }
}

class Producer implements Runnable {

    private final BlockingQueue<Integer> sharedQueue;
    private int threadNo;
    private volatile boolean isRunning = true;

    public Producer(BlockingQueue<Integer> sharedQueue,int threadNo) {
        this.threadNo = threadNo;
        this.sharedQueue = sharedQueue;
    }

    @Override
    public void run() {
        for(int i=1; i<= 100; i++){
            try {
                int number = i+(10*threadNo);

                //System.out.println("Produced:" + number + ":by thread:"+ threadNo);
                sharedQueue.put(number);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable{

    private final BlockingQueue<Integer> sharedQueue;
    private int threadNo;
    private volatile boolean isRunning = true;
    public Consumer (BlockingQueue<Integer> queue,int thread) {
        sharedQueue = queue;
        threadNo = thread;
    }

    @Override
    public void run() {
        while(true){
            try {

                System.err.println("Thread " + Integer.toString(threadNo) + " still running(Consumer)");
                int num = sharedQueue.get();
                //System.err.println("Consumed: "+ num + ":by thread:"+threadNo);
                //wait(1000);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }


}

