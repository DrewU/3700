import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Huffman {
    static HuffmanNode rootNode;
    static float compressedSize = 0;
    static float uncompressedSize= 0;
    private static Map<Character, String> charPrefixHashMap = new HashMap<>();

    public static void main(String[] args) {
        long before = 0;
        long after = 0;
        String tmp = FileToString("constitution.txt");
        uncompressedSize = tmp.getBytes().length * 8;
        System.err.println("Size of uncompressed String: " + uncompressedSize);

//        decode(Encode(tmp));
//        System.err.println("Size of compressed String: " + compressedSize);
//        System.err.println("Compressed by: " + (compressedSize/uncompressedSize)*100 + "%");
//        charPrefixHashMap = new HashMap<>();

//        decode(EncodeThreadedTask(tmp, 10));
//        charPrefixHashMap = new HashMap<>();
//        System.err.println("Size of compressed String: " + compressedSize);
//        System.err.println("Compressed by: " + (compressedSize/uncompressedSize)*100 + "%");

//        decode(EncodeFixedExecutorTask(FileToString("constitution.txt")));
//        charPrefixHashMap = new HashMap<>();
//        System.err.println("Size of compressed String: " + compressedSize);
//        System.err.println("Compressed by: " + (compressedSize/uncompressedSize)*100 + "%");

        decode(EncodeCachedExecutorTask(FileToString("constitution.txt")));
        System.err.println("Size of compressed String: " + compressedSize);
        System.err.println("Compressed by: " + (compressedSize/uncompressedSize)*100 + "%");
    }

    public static String toBinaryString(String s) {

        char[] cArray=s.toCharArray();

        StringBuilder sb=new StringBuilder();

        for(char c:cArray)
        {
            String cBinaryString=Integer.toBinaryString((int)c);
            sb.append(cBinaryString);
        }

        return sb.toString();
    }

    public static String FileToString(String path) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileNew = contentBuilder.toString();
        return fileNew.replace("\n", " ").replaceAll(" +", " ");
    }

    private static void decode(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        HuffmanNode temp = rootNode;
        compressedSize = s.length();
        System.err.println("Encoded: " + s);
        for (int i = 0; i < s.length(); i++) {
            int j = Integer.parseInt(String.valueOf(s.charAt(i)));

            if (j == 0) {
                temp = temp.left;
                if (temp.left == null && temp.right == null) {
                    stringBuilder.append(temp.value);
                    temp = rootNode;
                }
            }
            if (j == 1) {
                temp = temp.right;
                if (temp.left == null && temp.right == null) {
                    stringBuilder.append(temp.value);
                    temp = rootNode;
                }
            }
        }
        System.err.println("Decoded string is " + stringBuilder.toString());
    }

    private static void setPrefixCodes(HuffmanNode node, StringBuilder prefix) {

        if (node != null) {
            if (node.left == null && node.right == null) {
                charPrefixHashMap.put(node.value, prefix.toString());

            } else {
                prefix.append('0');
                setPrefixCodes(node.left, prefix);
                prefix.deleteCharAt(prefix.length() - 1);

                prefix.append('1');
                setPrefixCodes(node.right, prefix);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }

    }

    public static String Encode(String message) {
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        HashMap<Character, Integer> huffmanMap = new HashMap<>();
        for (int i = 0; i < message.length(); i++) {
            if (!huffmanMap.containsKey(message.charAt(i))) {
                huffmanMap.put(message.charAt(i), 1);
            } else {
                huffmanMap.put(message.charAt(i), huffmanMap.get(message.charAt(i)) + 1);
            }
        }
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after - before));
        HuffmanNode head = makeTree(huffmanMap);
        setPrefixCodes(head, new StringBuilder());
        System.err.println("Character Prefix Map = " + charPrefixHashMap);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            s.append(charPrefixHashMap.get(c));
        }

        return s.toString();
    }

    public static String EncodeThreadedTask(String message, int numThreads) {
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        HashMap<Character, Integer> huffmanMap = new HashMap<>();
        ArrayList<EncodeThread> results = new ArrayList();
        int batchSize = message.length() / numThreads;
        ArrayList<Thread> threads = new ArrayList<>();
        int coreNum = 0;
        for (int i = 0; i < message.length(); i += batchSize) {
            EncodeThread newThread;
            if(coreNum == numThreads){
                newThread = new EncodeThread(message.substring(i));
            } else{
                newThread = new EncodeThread(message.substring(i, (i + batchSize)));
            }
            results.add(newThread);
            Thread t = new Thread(newThread);
            threads.add(t);
            t.start();
            coreNum++;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
            }
        }

        for(EncodeThread curThread : results){
            for(Map.Entry<Character, Integer> curEntry : curThread.getResults().entrySet()){
                if (!huffmanMap.containsKey(curEntry.getKey())) {
                    huffmanMap.put(curEntry.getKey(), 1);
                } else {
                    huffmanMap.put(curEntry.getKey(), huffmanMap.get(curEntry.getKey()) + curEntry.getValue());
                }
            }
        }
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after - before));
        HuffmanNode head = makeTree(huffmanMap);
        setPrefixCodes(head, new StringBuilder());
        System.err.println("Character Prefix Map = " + charPrefixHashMap);
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            s.append(charPrefixHashMap.get(c));
        }

        return s.toString();
    }

    public static String EncodeFixedExecutorTask(String message){
        int numThreads = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        HashMap<Character, Integer> huffmanMap = new HashMap<>();
        ArrayList<EncodeThread> results = new ArrayList();
        int batchSize = message.length() / numThreads;
        ArrayList<Thread> threads = new ArrayList<>();
        int coreNum = 0;
        for (int i = 0; i < message.length(); i += batchSize) {
            EncodeThread newThread;
            if(coreNum == numThreads){
                newThread = new EncodeThread(message.substring(i));
            } else{
                newThread = new EncodeThread(message.substring(i, (i + batchSize)));
            }

            results.add(newThread);
            Thread t = new Thread(newThread);
            executor.execute(newThread);
            coreNum++;
        }
        awaitTerminationAfterShutdown(executor);
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
            }
        }

        for(EncodeThread curThread : results){
            for(Map.Entry<Character, Integer> curEntry : curThread.getResults().entrySet()){
                if (!huffmanMap.containsKey(curEntry.getKey())) {
                    huffmanMap.put(curEntry.getKey(), 1);
                } else {
                    huffmanMap.put(curEntry.getKey(), huffmanMap.get(curEntry.getKey()) + curEntry.getValue());
                }
            }
        }
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after - before));
        HuffmanNode head = makeTree(huffmanMap);
        setPrefixCodes(head, new StringBuilder());
        System.err.println("Character Prefix Map = " + charPrefixHashMap);
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            s.append(charPrefixHashMap.get(c));
        }

        return s.toString();
    }

    public static String EncodeCachedExecutorTask(String message){
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newCachedThreadPool();
        HashMap<Character, Integer> huffmanMap = new HashMap<>();
        ArrayList<EncodeThread> results = new ArrayList();
        int batchSize = message.length() / numThreads;
        ArrayList<Thread> threads = new ArrayList<>();
        int coreNum = 0;
        for (int i = 0; i < message.length(); i += batchSize) {
            EncodeThread newThread;
            if(coreNum == numThreads){
                newThread = new EncodeThread(message.substring(i));
            } else{
                newThread = new EncodeThread(message.substring(i, (i + batchSize)));
            }

            results.add(newThread);
            Thread t = new Thread(newThread);
            executor.execute(newThread);
            coreNum++;
        }
        awaitTerminationAfterShutdown(executor);
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
            }
        }

        for(EncodeThread curThread : results){
            for(Map.Entry<Character, Integer> curEntry : curThread.getResults().entrySet()){
                if (!huffmanMap.containsKey(curEntry.getKey())) {
                    huffmanMap.put(curEntry.getKey(), 1);
                } else {
                    huffmanMap.put(curEntry.getKey(), huffmanMap.get(curEntry.getKey()) + curEntry.getValue());
                }
            }
        }
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after - before));
        HuffmanNode head = makeTree(huffmanMap);
        setPrefixCodes(head, new StringBuilder());
        System.err.println("Character Prefix Map = " + charPrefixHashMap);
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            s.append(charPrefixHashMap.get(c));
        }

        return s.toString();
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public static HuffmanNode makeTree(HashMap<Character, Integer> huffmanMap) {
        PriorityQueue<HuffmanNode> huffmanTree = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : huffmanMap.entrySet()) {
            huffmanTree.offer(new HuffmanNode(entry.getValue(), entry.getKey()));
        }
        while (huffmanTree.size() > 1) {
            HuffmanNode x = huffmanTree.peek();
            huffmanTree.poll();
            HuffmanNode y = huffmanTree.peek();
            huffmanTree.poll();
            HuffmanNode sum = new HuffmanNode();
            sum.freq = x.freq + y.freq;
            sum.value = '-';
            sum.left = x;
            sum.right = y;
            rootNode = sum;
            huffmanTree.offer(sum);
        }
        return huffmanTree.poll();
    }
}

class HuffmanNode implements Comparable<HuffmanNode> {
    int freq;
    char value;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode() {

    }

    public HuffmanNode(int freqVal, char dataVal) {
        freq = freqVal;
        value = dataVal;
        left = null;
        right = null;
    }

    public int compareTo(HuffmanNode compNode) {
        return freq = compNode.freq;
    }
}

class EncodeThread implements Runnable {
    String message;
    HashMap<Character, Integer> charCount = new HashMap<>();

    public EncodeThread(String mes) {
        message = mes;
    }

    public void run() {
        for (int i = 0; i < message.length(); i++) {
            if (!charCount.containsKey(message.charAt(i))) {
                charCount.put(message.charAt(i), 1);
            } else {
                charCount.put(message.charAt(i), charCount.get(message.charAt(i)) + 1);
            }
        }
    }

    public HashMap<Character, Integer> getResults() {
        return charCount;
    }
}
