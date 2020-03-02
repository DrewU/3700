import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RockPaperScissorsMemo {
    public static ConcurrentHashMap<String, Integer> playerScores = new ConcurrentHashMap<String, Integer>();
    public static ArrayList<Player> players = new ArrayList<Player>();
    public static int minScore = Integer.MAX_VALUE;

    public static void main(String[] args) {
        generatePlayers(1000);
        long before = System.currentTimeMillis();
        while(players.size() > 1)
            playRound();
        long after = System.currentTimeMillis();
        System.out.println(after-before);
    }

    public static void generatePlayers(int numPlayers){
        for(int i = 0; i < numPlayers; i++){
            players.add(new RockPaperScissorsMemo().new Player(i));
        }
    }

    public static void playRound(){
        boolean paperFound = false;
        boolean rockFound = false;
        boolean scissorsFound = false;
        ArrayList<Player> playerthreads = new ArrayList<>();
        for(Player player : players) {
            if (paperFound && rockFound && scissorsFound) {
                break;
            } else {
                if (player.play == Play.Scissors && !paperFound) {
                    paperFound = true;
                    playerthreads.add(player);
                    player.run();
                } else if(player.play == Play.Rock && !rockFound){
                    playerthreads.add(player);
                    rockFound = true;
                    player.run();
                } else if(!scissorsFound){
                    playerthreads.add(player);
                    scissorsFound = true;
                    player.run();
                }
            }
        }

        int rockWins = 0;
        int paperWins = 0;
        int scissorsWins = 0;

        for(Player player : playerthreads){
            try {
                player.join();
                if(player.play == Play.Scissors){
                    scissorsWins = playerScores.get(player.id);
                } else if(player.play == Play.Rock){
                    rockWins = playerScores.get(player.id);
                } else{
                    paperWins = playerScores.get(player.id);
                }
            } catch (Exception e) {
            }
        }


        for(Player player : players){
            if(player.play == Play.Rock){
                if(!playerScores.containsKey(player.id)){
                    playerScores.put(player.id, rockWins);
                }
            } else if(player.play == Play.Scissors){
                if(!playerScores.containsKey(player.id)){
                    playerScores.put(player.id, scissorsWins);
                }
            } else{
                if(!playerScores.containsKey(player.id)){
                    playerScores.put(player.id, paperWins);
                }
            }
        }

        if(minScore != 0){
            winner win = new RockPaperScissorsMemo().new winner();
            win.run();
        }

        for(Player player : players)
            player.GeneratePlay();


    }
    class winner implements Runnable{
        public void run(){
            System.err.println("Lowest Value: " + minScore);
            System.err.println("The list of values");

            String key = "";
            for(ConcurrentHashMap.Entry<String,Integer> tmp : playerScores.entrySet()){
                if(tmp.getValue() == minScore){
                    key = tmp.getKey();
                    break;
                }
            }
            playerScores.remove(key);
            for(int i = 0; i < players.size(); i++){
                if(players.get(i).id.equals(key)){
                    players.remove(players.get(i));
                    break;
                }
            }


            System.err.println(playerScores.size());
            System.err.println("\nPlayerList");

            for(Player player : players){
                System.err.print(player.id);
                System.err.println(" "+ player.play);
            }

            minScore = Integer.MAX_VALUE;
        }
    }
    public enum Play {
        Rock,
        Paper,
        Scissors
    }

    class Player extends Thread{
        Play play;
        String id;

        public Player(int val){
            GeneratePlay();
            id = ("StringNo" + val);
        }

        public void run(){
            int score = 0;
            for (Player player: players) {
                if(play == Play.Paper){
                    if(player.play == Play.Rock){
                        score++;
                    } else if (player.play == Play.Scissors){
                        score--;
                    }
                } else if(play == Play.Rock){
                    if(player.play == Play.Scissors){
                        score++;
                    } else if (player.play == Play.Paper){
                        score--;
                    }
                } else{
                    if(player.play == Play.Paper){
                        score++;
                    } else if (player.play == Play.Rock){
                        score--;
                    }
                }
            }
            if(score < minScore){
                minScore = score;
            }
            playerScores.put(id, score);
        }

        public void GeneratePlay(){
            Random random = new Random();
            int tmp = random.nextInt(3);
            switch(tmp){
                case 0:
                    play = Play.Paper;
                    break;
                case 1:
                    play = Play.Rock;
                    break;
                case 2:
                    play = Play.Scissors;
                    break;
                default:
                    System.err.println("Error");
                    break;
            }
        }
    }
}
