import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientThread extends Thread {
    protected Socket socket;
    int numRounds;
    protected Player player;
    private DataInputStream in =  null;
    LinkedBlockingQueue<Message> playHistory;

    public ClientThread(Socket clientSocket, Player processPlayer, LinkedBlockingQueue<Message> plays, int rounds) {
        socket = clientSocket;
        player = processPlayer;
        playHistory = plays;
        numRounds = rounds;
    }

    public void run() {
        try {
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";

            int count = 0;
            while (count < numRounds) {
                try {
                    line = in.readUTF();
                    String[] message = line.split(" ");
                    Message clientMessage = new Message(Integer.valueOf(message[0]), message[1], Integer.valueOf(message[2]));
                    playHistory.add(clientMessage);
                    System.out.println(line);

                } catch (IOException i) {
                    System.out.println(i);
                }
                count++;
            }
            System.out.println("Closing connection");

            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}