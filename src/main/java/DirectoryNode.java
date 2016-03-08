import com.google.gson.Gson;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DirectoryNode {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Directory IP: " + getLocalNetworkIp());

        int n = Integer.parseInt(args[0]);
        Directory directory = new Directory(n);

        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");

        ZMQ.Socket pull = context.createSocket(ZMQ.PULL);
        pull.bind("tcp://*:5554");

        System.out.println("Waiting to receive connections");
        for (int i = 0; i < n; i++) {
            String messageReceived = pull.recvStr();
            int index = Integer.parseInt(messageReceived.split("%")[0]);
            String ip = messageReceived.split("%")[1];
            directory.nodes[i] = new NodeDCNET(index, ip);
        }

        System.out.println("Creating JSON with {index,ip}");
        Gson gson = new Gson();
        String directoryJson = gson.toJson(directory);
        System.out.println(directoryJson);

        for (int i = 0; i < 50; i++) {
            publisher.send(directoryJson);
            System.out.println("Sent JSON to the nodes: #" + (i+1));
            Thread.sleep(1000);
        }

        System.out.println("Finished");

    }

    private static String getLocalNetworkIp() {
        String networkIp = "";
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            networkIp = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return networkIp;
    }

}
