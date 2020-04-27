import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProcessMessenger {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public ProcessMessenger(String ip, int port, Player slavePlayer, int rounds) {
        try {
            socket = new Socket(ip, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }

        String line = "";
        int count = 0;
        while (count < rounds) {
            try {
                slavePlayer.GeneratePlay();
                line = slavePlayer.playerID + " " + slavePlayer.play + " " + (count+1);

                out.writeUTF(line);
            } catch (IOException i) {
                System.out.println(i);
            }
            count++;
        }

        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

}
