import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RockPaperScissorsv2 {
    public static ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    public static AtomicInteger numPlayers = new AtomicInteger();

    public static void main(String[] args) {
        System.out.print("How many players will be playing: ");
        Scanner in = new Scanner(System.in);
        int numP =in.nextInt();

        generatePlayers(numP);
        numPlayers.getAndAdd(players.size());
        playRound();

        players = new ConcurrentHashMap<>();
        numPlayers = new AtomicInteger();
        generatePlayers(numP);
        numPlayers.getAndAdd(players.size());
        playRoundExecutor();
    }

    public static void generatePlayers(int numPlayers){
        for(int i = 0; i < numPlayers; i++){
            Player tmp = new RockPaperScissorsv2().new Player(i);
            players.put(tmp.id, tmp);
        }
    }

    public static Player getRandomEntry(){
        Object[] crunchifyKeys = players.keySet().toArray();
        Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
        return players.remove(key);
    }

    public static void playRound(){
        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        while (numPlayers.get() > 1) {
            Player player1 = getRandomEntry();
            player1.setOpponent(getRandomEntry());
            player1.run();
        }

        after = System.currentTimeMillis();
        System.out.println("Time to execute: " + (after-before));

        for(Map.Entry<String,Player> player : players.entrySet()){
            System.out.println("Winner: ");
            System.out.println(player.getKey());
        }
    }

    public static void playRoundExecutor(){
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        long before = 0;
        long after = 0;
        before = System.currentTimeMillis();
        while (numPlayers.get() > 1) {
            if(!(players.size() < 2)) {
                Player player1 = getRandomEntry();
                player1.setOpponent(getRandomEntry());
                executor.execute(player1);
            } else if(numPlayers.equals(1)){
                break;
            }
        }



        awaitTerminationAfterShutdown(executor);
        executor.shutdown();
        after = System.currentTimeMillis();
        System.out.println("Time to execute: " + (after-before));

        for(Map.Entry<String,Player> player : players.entrySet()){
            System.out.println("Winner: ");
            System.out.println(player.getKey());
        }
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

    enum Playv2 {
        Rock,
        Paper,
        Scissors
    }

    class Player implements Runnable{
        Playv2 play;
        Player oponent;
        String id;
        boolean hasWon;

        public Player(int val){
            GeneratePlay();
            id = ("StringNo" + val);
        }

        public void setOpponent(Player op){
            oponent = op;
        }

        public void run(){
            boolean tie = false;
            System.err.println("Player 1: " + id + " Play: " + play);
            System.err.println("Player 2: " + oponent.id + " Play: " + oponent.play);
            do {
                if(tie == true){
                    this.GeneratePlay();
                    oponent.GeneratePlay();
                }

                if (play == Playv2.Paper) {
                    if (oponent.play == Playv2.Rock) {
                        hasWon = true;
                        tie = false;
                    } else if (oponent.play == Playv2.Scissors) {
                        hasWon = false;
                        tie = false;
                    } else {
                        tie = true;
                    }
                } else if (play == Playv2.Rock) {
                    if (oponent.play == Playv2.Scissors) {
                        hasWon = true;
                        tie = false;
                    } else if (oponent.play == Playv2.Paper) {
                        hasWon = false;
                        tie = false;
                    } else {
                        tie = true;
                    }
                } else {
                    if (oponent.play == Playv2.Paper) {
                        hasWon = true;
                        tie = false;
                    } else if (oponent.play == Playv2.Rock) {
                        hasWon = false;
                        tie = false;
                    } else {
                        tie = true;
                    }
                }
            }while (tie == true);
            if (this.hasWon) {
                System.err.println("Removed player 2");
                players.put(this.id, this);
            } else {
                System.err.println("Removed player 1");
                players.put(this.oponent.id, this.oponent);
            }
            numPlayers.addAndGet(-1);
        }

        public void GeneratePlay(){
            Random random = new Random();
            int tmp = random.nextInt(3);

            switch(tmp){
                case 0:
                    play = Playv2.Paper;
                    break;
                case 1:
                    play = Playv2.Rock;
                    break;
                case 2:
                    play = Playv2.Scissors;
                    break;
                default:
                    System.err.println("Error");
                    break;
            }
        }
    }
}