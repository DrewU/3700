import java.net.InetAddress;

public class SlaveProcess {
    private static SlaveProcess single_instance = null;
    private int portNumber;
    private String ipAddress;
    int numRounds;
    Player slavePlayer;

    private SlaveProcess(int rounds, int id){
        portNumber = 5000;
        ipAddress = "127.0.0.1";
        numRounds = rounds;
        slavePlayer = new Player(id);
    }

    public static SlaveProcess getInstance(int rounds, int id) {
        if (single_instance == null)
            single_instance = new SlaveProcess(rounds, id);

        return single_instance;
    }

    public void startMessenger(){
        ProcessMessenger messenger = new ProcessMessenger(ipAddress, portNumber, slavePlayer, numRounds);
    }
}
