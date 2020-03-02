import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

public class RockPaperScissorsv1 {
    public static ConcurrentHashMap<String, Integer> playerScores = new ConcurrentHashMap<String, Integer>();
    public static ArrayList<Player> players = new ArrayList<Player>();
    public static int minScore = Integer.MAX_VALUE;

    public static void main(String[] args) {

        System.out.print("How many players will be playing: ");
        Scanner in = new Scanner(System.in);
        int numPlayers =in.nextInt();
        generatePlayers(numPlayers);
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        while(players.size() > 1)
            playRound();
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after-before));

        players = new ArrayList<Player>();
        minScore = Integer.MAX_VALUE;

        generatePlayers(numPlayers);
        before = 0;
        after = 0;
        before = System.currentTimeMillis();
        while(players.size() > 1)
            playRoundExecutor();
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after-before));

        players = new ArrayList<Player>();
        minScore = Integer.MAX_VALUE;

        generatePlayers(numPlayers);
        before = 0;
        after = 0;
        before = System.currentTimeMillis();
        while(players.size() > 1)
            playRoundExecutor();
        after = System.currentTimeMillis();
        System.err.println("Time taken: " + (after-before));


        System.exit(0);

    }

    public static void playRound(){
        for (Player play: players) {
            play.run();
            try {
                play.join();
            } catch(Exception e){
                System.err.println("error");
            }
        }
        if(minScore != 0){
            winner win = new RockPaperScissorsv1().new winner();
            win.run();
        }


        for(Player player : players)
            player.GeneratePlay();
    }

    public static void playRoundExecutor(){
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        for (Player play: players) {
            executor.execute(play);
            try {
            } catch(Exception e){
            }
        }
        for (Player play: players) {
            try {
                play.join();
            } catch (Exception e) {
            }
        }
        if(minScore != 0){
            winner win = new RockPaperScissorsv1().new winner();
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
            System.err.println("\nRemoving Thread: " + key);
            playerScores.remove(key);
            for(int i = 0; i < players.size(); i++){
                if(players.get(i).id.equals(key)){
                    players.remove(players.get(i));
                    break;
                }
            }


            System.err.println("Num players left: " + playerScores.size());
            System.err.println("\nPlayerList");

            for(Player player : players){
                System.err.print(player.id);
                System.err.println(" "+ player.play);
            }

            minScore = Integer.MAX_VALUE;
        }
    }

    public static void generatePlayers(int numPlayers){
        for(int i = 0; i < numPlayers; i++){
            players.add(new RockPaperScissorsv1().new Player(i));
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
