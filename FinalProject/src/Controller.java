import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Controller {
    public static void main(String[] args) {
        Process p1 = null;
        Process p2 = null;
        boolean isMaster = false;
        if(args.length == 1){
            MasterProcess masterProcess = MasterProcess.getInstance(5000, generateInetAddress("127.0.0.1"), Integer.valueOf(args[0]), 1);
            p1 = masterProcess.createProcess(5000, Integer.valueOf(args[0]), 2);
            p2 = masterProcess.createProcess(5000, Integer.valueOf(args[0]), 3);
            isMaster = true;
            masterProcess.playRound();

        } else{
            try{
                SlaveProcess slaveProcess = SlaveProcess.getInstance(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
                slaveProcess.startMessenger();
            } catch(Exception e){
                e.printStackTrace();
            }

        }

        if(isMaster){
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            p1.destroyForcibly();
            p2.destroyForcibly();
        }


    }

    private static InetAddress generateInetAddress(final String ip) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException u) {
            u.printStackTrace();
        }
        return ipAddress;
    }

}
