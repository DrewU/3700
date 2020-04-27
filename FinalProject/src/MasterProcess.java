import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MasterProcess {
    private static MasterProcess single_instance = null;
    int numRounds;
    Player player;
    LinkedBlockingQueue<Message> playHistory;
    int messageOneScore = 0;
    int messageTwoScore = 0;
    int messageThreeScore = 0;
    private int portNumber;
    private InetAddress ipAddress;

    private MasterProcess(int port, InetAddress ip, int rounds, int i) {
        portNumber = port;
        ipAddress = ip;
        numRounds = rounds;
        player = new Player(rounds);
        playHistory = new LinkedBlockingQueue<Message>();
    }

    public static MasterProcess getInstance(int port, InetAddress ip, int rounds, int i) {
        if (single_instance == null)
            single_instance = new MasterProcess(port, ip, rounds, i);

        return single_instance;
    }

    public static Process createProcess(int port, int numRounds, int id) {
        Process p = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./FinalProject.jar", String.valueOf(numRounds), String.valueOf(id));
            p = pb.start();
            System.out.println("Created Process");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return p;
    }

    public void playRound() {
        ProcessServer.getInstance(portNumber, numRounds, player, playHistory);
        determineWinner();
    }

    public void determineWinner() {
        ArrayList<Message> currentRound;

        for (int i = 1; i <= numRounds; i++) {
            currentRound = new ArrayList<>();
            for (Message m : playHistory) {
                if (m.roundNum == i) {
                    currentRound.add(m);
                }
            }
            System.out.println("Round " + i + "!");
            determineScores(currentRound);
        }

        System.out.println("Final Scores!");
        System.out.println("Player 1 final score: " + messageOneScore);
        System.out.println("Player 1 final score: " + messageTwoScore);
        System.out.println("Player 1 final score: " + messageThreeScore);
    }

    private void determineScores(ArrayList<Message> messages) {

        System.out.println("Player 1 plays: " + messages.get(0).play);
        System.out.println("Player 2 plays: " + messages.get(1).play);
        System.out.println("Player 3 plays: " + messages.get(2).play);


        if ((messages.get(0).play.equals("paper") && messages.get(1).play.equals("rock") && messages.get(2).play.equals("rock")) ||
                ((messages.get(0).play.equals("rock") && messages.get(1).play.equals("scissors") && messages.get(2).play.equals("scissors"))) ||
                (messages.get(0).play.equals("scissors") && messages.get(1).play.equals("paper") && messages.get(2).play.equals("paper"))) {
            messageOneScore += 2;
            System.out.println("Player 1 wins 2 points!");
        } else if ((messages.get(1).play.equals("paper") && messages.get(0).play.equals("rock") && messages.get(2).play.equals("rock")) ||
                ((messages.get(1).play.equals("rock") && messages.get(0).play.equals("scissors") && messages.get(2).play.equals("scissors"))) ||
                (messages.get(1).play.equals("scissors") && messages.get(0).play.equals("paper") && messages.get(2).play.equals("paper"))) {
            messageTwoScore += 2;
            System.out.println("Player 2 wins 2 points!");
        } else if ((messages.get(2).play.equals("paper") && messages.get(0).play.equals("rock") && messages.get(1).play.equals("rock")) ||
                ((messages.get(2).play.equals("rock") && messages.get(0).play.equals("scissors") && messages.get(1).play.equals("scissors"))) ||
                (messages.get(2).play.equals("scissors") && messages.get(0).play.equals("paper") && messages.get(1).play.equals("paper"))) {
            messageThreeScore += 2;
            System.out.println("Player 3 wins 2 points!");
        } else if ((messages.get(0).play.equals("paper") && messages.get(1).play.equals("paper") && messages.get(2).play.equals("rock")) ||
                (messages.get(0).play.equals("rock") && messages.get(1).play.equals("rock") && messages.get(2).play.equals("scissors")) ||
                (messages.get(0).play.equals("scissors") && messages.get(1).play.equals("scissors") && messages.get(2).play.equals("paper"))) {
            messageOneScore++;
            messageTwoScore++;
            System.out.println("Player 1 wins 1 point!");
            System.out.println("Player 2 wins 1 point!");
        } else if ((messages.get(0).play.equals("paper") && messages.get(2).play.equals("paper") && messages.get(1).play.equals("rock")) ||
                (messages.get(0).play.equals("rock") && messages.get(2).play.equals("rock") && messages.get(1).play.equals("scissors")) ||
                (messages.get(0).play.equals("scissors") && messages.get(2).play.equals("scissors") && messages.get(1).play.equals("paper"))) {

            messageOneScore++;
            messageThreeScore++;
            System.out.println("Player 1 wins 1 point!");
            System.out.println("Player 3 wins 1 point!");
        } else if ((messages.get(1).play.equals("paper") && messages.get(2).play.equals("paper") && messages.get(0).play.equals("rock")) ||
                (messages.get(1).play.equals("rock") && messages.get(2).play.equals("rock") && messages.get(0).play.equals("scissors")) ||
                (messages.get(1).play.equals("scissors") && messages.get(2).play.equals("scissors") && messages.get(0).play.equals("paper"))) {

            messageThreeScore++;
            messageTwoScore++;
            System.out.println("Player 2 wins 1 point!");
            System.out.println("Player 3 wins 1 point!");
        } else {
            System.out.println("No points awarded!");
        }

        System.out.println("\n");
    }
}
