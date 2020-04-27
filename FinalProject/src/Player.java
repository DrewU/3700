import java.util.Random;

public class Player {
    int playerID;
    String play;

    public Player(int id){
        playerID = id;
    }

    public void GeneratePlay() {
        Random random = new Random();
        int tmp = random.nextInt(3);
        switch (tmp) {
            case 0:
                play = "paper";
                break;
            case 1:
                play = "rock";
                break;
            case 2:
                play = "scissors";
                break;
            default:
                System.err.println("Error");
                break;
        }
    }

    public String getPlay() {
        return play;
    }

    public int getPlayerID(){return playerID;}
}
