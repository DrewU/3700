import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessServer {
    private static ProcessServer single_instance = null;

    private ProcessServer(int port, int numRounds, Player processPlayer, LinkedBlockingQueue<Message> plays) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        System.out.println("Starting server");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("Waiting for clients...");
        int count = 0;
        while (count < 2) {
            try {
                socket = serverSocket.accept();


            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Found client");
            new ClientThread(socket, processPlayer, plays, numRounds).start();
            count++;
        }

        try{
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        for(int i = 0; i < numRounds; i++){
            processPlayer.GeneratePlay();
            plays.add(new Message(processPlayer.playerID, processPlayer.getPlay(), i+1));
        }
        try{
            System.out.println("Closing server");
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static HashMap<String, String> getPlays(HashMap<String, String> plays){
        return plays;
    }


    public static ProcessServer getInstance(int port, int rounds, Player procplayer, LinkedBlockingQueue<Message> plays) {
        if (single_instance == null)
            single_instance = new ProcessServer(port, rounds, procplayer, plays);

        return single_instance;
    }
}
