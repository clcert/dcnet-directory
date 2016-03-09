import com.google.gson.Gson;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.net.UnknownHostException;

/*
    This application work as the Directory node for the DC-NET room, and is necessary for the nodes
    to receive information about the other nodes (ip address and index) in order to establish
    subsequently connections with them to run the anonymous DC-NET protocol.
*/

public class DirectoryNode {

    // Usage: ./gradlew run -PappArgs=[<numberOfNodes>]
    public static void main(String[] args) throws InterruptedException {
        // Print Directory IP address
        System.out.println("Directory IP: " + getLocalNetworkIp());

        // Variable to store the number of nodes admitting in the room controlled by this directory node
        int n = Integer.parseInt(args[0]);

        // Create object Directory with the total number of nodes
        Directory directory = new Directory(n);

        // Create context where to run the sockets
        ZContext context = new ZContext();

        // Create the PUB socket and bind it to the port 5555
        ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");

        // Create the PULL socket and bind it to the port 5554
        ZMQ.Socket pull = context.createSocket(ZMQ.PULL);
        pull.bind("tcp://*:5554");

        // Wait to receive <numberOfNodes> connections from each node that wants to send a message in this room
        System.out.println("Waiting to receive connections");
        for (int i = 0; i < n; i++) {
            // Receive a message from the PULL socket, which corresponds to the IP address of this node
            String messageReceived = pull.recvStr();
            // Assign an index to this node and store it in the directory with his correspondent IP address
            directory.nodes[i] = new NodeInfo(i+1, messageReceived);
        }

        // Create a Json message with all the information of the directory: every pair {index,ip}
        System.out.println("Creating JSON with {index,ip}");
        Gson gson = new Gson();
        String directoryJson = gson.toJson(directory);
        System.out.println(directoryJson);

        // Send broadcast through the PUB socket to all the nodes with the Json message created before
        // TODO: Review if the re-sending of this broadcast is working or not
        for (int i = 0; i < 50; i++) {
            publisher.send(directoryJson);
            System.out.println("Sent JSON to the nodes: #" + (i+1));
            Thread.sleep(1000);
        }

        // The task of the directory is over
        System.out.println("Finished");

    }

    // Get the LAN IP address of the node
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
