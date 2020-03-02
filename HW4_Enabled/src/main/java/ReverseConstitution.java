import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.TranslateOptions;

public class ReverseConstitution {
    public static void main(String[] args) {
        cleanFile("independence.txt", "outTest.txt");
        System.out.println("Original: " + FileToString("outTest.txt"));
        System.out.println(reverseFileSingleThreaded(FileToString("outTest.txt")));
        System.out.println(reverseFileMultiThreaded(FileToString("outTest.txt"), 5));
        testGoogleSingleTimes();
        testGoogleMultiTimes(10);
    }

    public static void testThreadTimes(){
        ArrayList<Long> time = new ArrayList<>();
        HashMap<Long, String> names = new HashMap<>();
        String cleanedString = FileToString("outTest.txt");
        long timeBefore = 0;
        long timeAfter = 0;

        for (int i = 1; i < 16; i++) {
            timeBefore = System.nanoTime();
            reverseFileMultiThreaded(cleanedString, i);
            timeAfter = System.nanoTime();
            time.add((timeAfter - timeBefore));
            names.put((timeAfter - timeBefore), ("Total time for " + i + " threads: "));
        }

        timeBefore = System.nanoTime();
        reverseFileSingleThreaded(cleanedString);
        timeAfter = System.nanoTime();
        time.add((timeAfter - timeBefore));
        names.put((timeAfter - timeBefore), "Single Thread: ");

        Collections.sort(time);

        for (Long cur : time)
            System.out.println(names.get(cur) + cur);
    }

    public static void testGoogleMultiTimes(int numCores){
        ArrayList<Long> time = new ArrayList<>();
        HashMap<Long, String> names = new HashMap<>();
        String cleanedString = FileToString("outTest.txt");
        long timeBefore = 0;
        long timeAfter = 0;

        timeBefore = System.nanoTime();
        System.out.println(TranslateTextMultiThread(cleanedString, numCores));
        timeAfter = System.nanoTime();
        time.add((timeAfter - timeBefore));
        names.put((timeAfter - timeBefore), ("Total time for " + numCores + " threads: "));
        Collections.sort(time);

        for (Long cur : time)
            System.out.println(names.get(cur) + cur);
    }

    public static void testGoogleSingleTimes(){
        ArrayList<Long> time = new ArrayList<>();
        HashMap<Long, String> names = new HashMap<>();
        String cleanedString = FileToString("outTest.txt");
        long timeBefore = 0;
        long timeAfter = 0;

        timeBefore = System.nanoTime();
        TranslateTextSingleThread(cleanedString);
        timeAfter = System.nanoTime();
        time.add((timeAfter - timeBefore));
        names.put((timeAfter - timeBefore), "Single Thread: ");

        Collections.sort(time);

        for (Long cur : time)
            System.out.println(names.get(cur) + cur);
    }


    public static void TranslateTextSingleThread(String cleanedString) {
        try {
            Translate translate = TranslateOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\dh961_000\\Certs\\Creds.json"))).build().getService();

            Translation translation = translate.translate(cleanedString, Translate.TranslateOption.sourceLanguage("en"), Translate.TranslateOption.targetLanguage("ru"));
            System.out.println(translation.getTranslatedText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String TranslateTextMultiThread(String cleanedString, int numThreads) {
        TreeMap<Integer, TranslateThread> results = new TreeMap();
        ArrayList<Thread> threads = new ArrayList<>();
        int perCoreBatchSize = cleanedString.length() / numThreads;
        int coreNum = 0;

        for (int i = 0; i < cleanedString.length() - 1; i += perCoreBatchSize) {
            TranslateThread translationThread = null;
            if (coreNum == numThreads) {
                translationThread = new TranslateThread(cleanedString.substring(i, cleanedString.length()));
            } else {
                translationThread = new TranslateThread(cleanedString.substring(i, i + perCoreBatchSize));
            }
            results.put(i, translationThread);
            Thread t = new Thread(translationThread);
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

        String res = "";
        for (Map.Entry<Integer, TranslateThread> entry : results.entrySet()) {
            res =  res + entry.getValue().getResult();
        }

        return res;
    }


    public static void cleanFile(String inFileName, String outFileName) {
        try {
            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines(Paths.get(inFileName), StandardCharsets.UTF_8)) {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String toClean = contentBuilder.toString();
            toClean = toClean.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("( )+", " ");
            try (PrintWriter out = new PrintWriter(outFileName)) {
                out.println(toClean);
            }
        } catch (Exception e) {
            System.out.println("File not found");
        }
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

    public static String reverseFileSingleThreaded(String contents) {
        String[] parts = contents.split(" ");
        String out = "";
        long before = System.currentTimeMillis();

        for (int i = parts.length - 1; i > 0; i--) {
            out += parts[i] + " ";
        }
        long after = System.currentTimeMillis();
        System.out.println("Time for single thread: " + (after-before));
        return out + parts[0];
    }

    public static String reverseFileMultiThreaded(String contents, int numThreads) {
        TreeMap<Integer, ReverseThread> results = new TreeMap();
        ArrayList<Thread> threads = new ArrayList<>();
        int perCoreBatchSize = contents.length() / numThreads;
        int coreNum = 0;
        long before = System.currentTimeMillis();

        for (int i = 0; i < contents.length() - 1; i += perCoreBatchSize) {
            ReverseThread myReverse = null;
            if (coreNum == numThreads) {
                myReverse = new ReverseThread(contents.substring(i));
            } else {
                myReverse = new ReverseThread(contents.substring(i, i + perCoreBatchSize));
            }
            results.put(i, myReverse);
            Thread t = new Thread(myReverse);
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

        String res = "";
        for (Map.Entry<Integer, ReverseThread> entry : results.entrySet()) {
            res = entry.getValue().getResult() + res;
        }
        long after = System.currentTimeMillis();

        System.out.println("Time for " + numThreads + " threads: " + (after-before));

        return res;
    }
}


class ReverseThread implements Runnable {
    String text;
    String result;

    public ReverseThread(String parameter) {
        text = parameter;
    }

    public void run() {
        String[] parts = text.split(" ");
        String out = "";
        for (int i = parts.length - 1; i > 0; i--) {
            out += parts[i] + " ";
        }
        result = out + parts[0];
    }

    public String getResult() {
        return result;
    }
}


class TranslateThread implements Runnable {
    String text;
    String result;

    public TranslateThread(String parameter) {
        text = parameter;
    }

    public void run() {
        try {
            Translate translate = TranslateOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\dh961_000\\Certs\\Creds.json"))).build().getService();

            Translation translation = translate.translate(text, Translate.TranslateOption.sourceLanguage("en"), Translate.TranslateOption.targetLanguage("ru"));
            result = translation.getTranslatedText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}